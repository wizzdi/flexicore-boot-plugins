package com.wizzdi.basic.iot.service.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.IOTMessageSubscriber;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Configuration
public class TestEntities {

    @Value("${basic.iot.nats.url:nats://localhost:5222}")
    private String natsUrl;


    @Bean
    public Connection testConnection() throws IOException, InterruptedException {
        return Nats.connect(new Options.Builder().server(natsUrl).noEcho().build());
    }
    @Bean
    public BasicIOTClient testBasicIOTClient(Connection testConnection, ObjectProvider<IOTMessageSubscriber> iotMessageSubscribers, ObjectMapper objectMapper) throws IOException, InterruptedException {
        return new BasicIOTClient(testConnection,objectMapper,iotMessageSubscribers.stream().collect(Collectors.toList()));
    }

    @Bean
    public String jsonSchema() throws IOException {
        return IOUtils.resourceToString("light.schema.json", StandardCharsets.UTF_8,TestEntities.class.getClassLoader());
    }


}
