package com.wizzdi.basic.iot.client;

public interface IOTMessageSubscriber<T extends IOTMessage> {

    void onIOTMessage(T iotMessage);
}
