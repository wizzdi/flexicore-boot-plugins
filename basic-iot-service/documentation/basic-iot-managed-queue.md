# Basic IoT Managed Queue

## Purpose
This document describes how the Basic IoT service manages inbound MQTT message processing capacity and where the queue size is reported.

## Inbound Message Flow
Inbound MQTT messages are configured in `BasicIOTConfig.serverInputIntegrationFlowHolder`.

The flow receives messages from `MqttPahoMessageDrivenChannelAdapter` and dispatches them through an executor channel:

```java
.channel(MessageChannels.executor("mqtt-in-executor", new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())))
```

Each message is then parsed, verified, and dispatched to Basic IoT subscribers and handlers.

## Queue / Backpressure Mechanism
Basic IoT does not use a Spring Integration `QueueChannel` for the managed inbound processing queue. The effective queue is created by threads waiting on the `virtualThreadsLogicSemaphore`.

For each inbound message:

1. A virtual thread starts handling the message.
2. The handler calls `virtualThreadsLogicSemaphore.acquire()` before verification and subscriber processing.
3. If all semaphore permits are already in use, the virtual thread waits.
4. After processing completes, the permit is released in the `finally` block.

This means the managed queue size is the number of virtual threads currently waiting to acquire the semaphore, not the size of a `QueueChannel` buffer.

## Queue Size Reporting
Queue size is reported as a Micrometer gauge in `SemaphoreMetrics`:

```java
Gauge.builder("iot.queue.length", virtualThreadsLogicSemaphore, s -> s.getQueueLength())
    .description("Number of threads waiting to acquire")
    .register(meterRegistry);
```

The related available-capacity metric is also registered there:

```java
Gauge.builder("iot.available", virtualThreadsLogicSemaphore, Semaphore::availablePermits)
    .description("Available permits")
    .register(meterRegistry);
```

## Log Reporting
There is no direct log statement that periodically prints the Basic IoT queue size.

The queue size is exposed through metrics as `iot.queue.length`. It will appear in logs only if the runtime environment is configured to export or log Micrometer metrics. Otherwise, it should be observed through the configured metrics backend or actuator/monitoring integration.

Basic IoT also logs aggregated message processing time periodically. The log includes the number of processed MQTT messages, latest message type, latest processing time, minimum processing time, maximum processing time, average processing time, and total system CPU load percentage. The cadence is controlled by `basic.iot.message.processing.logInterval` and defaults to every 1000 processed messages. Setting it to `0` or a negative value disables this summary log.

## Backpressure and MQTT Bursts
High `iot.queue.length` can also happen after a communication issue between the service and the MQTT broker. When the connection recovers, the broker may deliver many queued QoS 1 messages at once. Because the inbound flow uses a virtual-thread executor before the semaphore, that burst can create many virtual-thread handlers quickly, and they then wait on `virtualThreadsLogicSemaphore` until processing capacity is available.

## Related Timing Metrics
`BasicIOTConfig` records processing timers around message parsing, semaphore waiting, signature verification, subscriber processing, and total handling time. The semaphore waiting timer can help identify queue pressure together with `iot.queue.length` and `iot.available`.
