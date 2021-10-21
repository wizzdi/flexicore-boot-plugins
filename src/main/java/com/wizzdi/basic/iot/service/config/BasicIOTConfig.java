package com.wizzdi.basic.iot.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.IOTMessageSubscriber;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.pf4j.Extension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.stream.Collectors;

@Extension
@Configuration
public class BasicIOTConfig implements Plugin {

    @Value("${basic.iot.nats.url:nats://localhost:5222}")
    private String natsUrl;

    @Bean
    public Connection connection() throws IOException, InterruptedException {
        return Nats.connect(new Options.Builder().server(natsUrl).noEcho().build());
    }
    @Bean
    public BasicIOTClient basicIOTClient(Connection connection, ObjectProvider<IOTMessageSubscriber> iotMessageSubscribers, ObjectMapper objectMapper) throws IOException, InterruptedException {
        return new BasicIOTClient(connection,objectMapper,iotMessageSubscribers.stream().collect(Collectors.toList()));
    }
}
