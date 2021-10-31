package com.wizzdi.basic.iot.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.IOTMessageSubscriber;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.nats.client.*;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.stream.Collectors;

@Extension
@Configuration
public class BasicIOTConfig implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(BasicIOTClient.class);

    @Value("${basic.iot.nats.url:nats://localhost:5222}")
    private String natsUrl;

    @Bean
    public Connection connection() throws IOException, InterruptedException {
        Connection connect = Nats.connect(new Options.Builder().traceConnection().server(natsUrl).noEcho().build());
        Dispatcher dispatcher = connect.createDispatcher();
        dispatcher.subscribe("$SYS.REQ.ACCOUNT.*.CLAIMS.LOOKUP",this::handleSubscriptionMessage);
        return connect;
    }
    @Bean
    public BasicIOTClient basicIOTClient(Connection connection, ObjectProvider<IOTMessageSubscriber> iotMessageSubscribers, ObjectMapper objectMapper) throws IOException, InterruptedException {
        return new BasicIOTClient(connection,objectMapper,iotMessageSubscribers.stream().collect(Collectors.toList()));
    }


    private void handleSubscriptionMessage(Message message) {
        String data = new String(message.getData(), StandardCharsets.UTF_8);
        logger.info("received message"+ data);
    }
}
