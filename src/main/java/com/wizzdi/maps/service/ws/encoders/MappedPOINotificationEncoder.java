package com.wizzdi.maps.service.ws.encoders;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wizzdi.flexicore.boot.rest.views.Views;
import com.wizzdi.maps.service.ws.messages.MappedPOINotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Writer;


/**
 * Created by Asaf on 12/02/2017.
 */
public class MappedPOINotificationEncoder<T extends MappedPOINotification> implements Encoder.TextStream<T> {

    private static ObjectMapper objectMapper;
    private static final Logger logger= LoggerFactory.getLogger(MappedPOINotificationEncoder.class);

    @Override
    public void init(EndpointConfig config) {

        objectMapper= new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);



    }



    @Override
    public void destroy() {

    }

    @Override
    public void encode(T object, Writer writer) throws IOException {
            objectMapper.writeValue(writer,object);




    }
}
