package org.chat.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
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
            chatMessage = mapper.readValue(arg1, org.chat.common.ChatMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessage;
    }

}
