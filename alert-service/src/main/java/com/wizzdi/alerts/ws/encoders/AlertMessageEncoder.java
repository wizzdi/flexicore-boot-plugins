package com.wizzdi.alerts.ws.encoders;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wizzdi.alerts.ws.messages.AlertMessage;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;


/**
 * Created by Asaf on 12/02/2017.
 */
public class AlertMessageEncoder implements Encoder.TextStream<AlertMessage> {

    private static ObjectMapper objectMapper;
    private static final Logger logger= LoggerFactory.getLogger(AlertMessageEncoder.class);

    @Override
    public void init(EndpointConfig config) {

        objectMapper= new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);



    }



    @Override
    public void destroy() {

    }

    @Override
    public void encode(AlertMessage object, Writer writer) throws IOException {
            objectMapper.writeValue(writer,object);




    }
}
