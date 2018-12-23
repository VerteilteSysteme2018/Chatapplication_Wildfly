package edu.hm.dako.chat.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class ChatMessageDeserializer implements Deserializer<ChatMessage> {

    @Override public void close() {

    }

    @Override public void configure(Map map, boolean b) {

    }

    @Override
    public ChatMessage deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        ChatMessage chatMessage = null;
        System.out.println("Holladiewaldfee"+arg0+"   "+arg1);

        try {
            chatMessage = mapper.readValue(arg1, ChatMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessage;
    }

}
