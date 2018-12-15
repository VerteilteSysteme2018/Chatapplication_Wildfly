package org.chat.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;

import java.util.Map;

public class ChatMessageSerializer implements org.apache.kafka.common.serialization.Serializer { //TODO add <ChatMessage> to serializer

    @Override
    public void configure(Map map, boolean b) {
    }

    @Override public byte[] serialize(String arg0, Object arg1) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(arg1).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public void close() {
    }
}
