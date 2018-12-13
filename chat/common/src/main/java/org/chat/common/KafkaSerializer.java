package org.chat.common;

import java.io.Closeable;
import java.util.Map;

public interface KafkaSerializer extends Closeable {
    void configure(Map<String, ?> var1, boolean var2);

    byte[] serialize(String var1, ChatMessage var2);

    void close();
}
