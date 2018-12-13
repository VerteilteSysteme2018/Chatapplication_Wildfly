package org.chat.common;

import java.io.Closeable;
import java.util.Map;

public interface KafkaDeserializer extends Closeable {
    void configure(Map<String, ?> var1, boolean var2);

    ChatMessage deserialize(String var1, byte[] var2);

    void close();
}