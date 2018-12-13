package org.chat.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ChatMessageDeserializer implements KafkaDeserializer {

    @Override public void close() {

    }

    @Override public void configure(Map<String, ?> arg0, boolean arg1) {

    }

    @Override
    public ChatMessage deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        ChatMessage chatMessage = null;
        try {
            chatMessage = mapper.readValue(arg1, ChatMessage.class);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return chatMessage;
    }

}
