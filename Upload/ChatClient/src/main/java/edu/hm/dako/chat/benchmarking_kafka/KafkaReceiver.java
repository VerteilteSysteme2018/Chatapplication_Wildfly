package edu.hm.dako.chat.benchmarking_kafka;

import edu.hm.dako.chat.common.ChatMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaReceiver {
    KafkaConsumer kafkaConsumer;
    ChatMessage m;
    ArrayList<Long> rtts = new ArrayList<>();


    public static void main(String[] args) {
        KafkaReceiver kRC = new KafkaReceiver();
        kRC.initializeConsumer();
        kRC.startRecievingMessages();

    }

    public void initializeConsumer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "edu.hm.dako.chat.common.ChatMessageDeserializer");
        properties.put("group.id", "chef");

        kafkaConsumer = new KafkaConsumer(properties);
        List topics = new ArrayList();
        topics.add("responseTopic");
        kafkaConsumer.subscribe(topics);
    }

    public void startRecievingMessages() {
        Thread thread = new Thread(() -> {
            try {

                while (true) {
                    ConsumerRecords<String, ChatMessage> messages = kafkaConsumer.poll(100);
                    for (ConsumerRecord<String, ChatMessage> message : messages) {
                        System.out.println("Message received " + message.value().toString());
                        m = message.value();

                        ChatMessage msg = message.value();
                        long RTT = System.currentTimeMillis() - msg.getTimestamp();
                        rtts.add(RTT);
                        System.out.println(rtts.toString());

                        if(rtts.size() == 1000) {
                            long sum=0;
                            for (Long rtt : rtts) {
                                sum=sum+rtt;

                            }
                            System.out.println("Schnitt: "+sum/1000);
                        }


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
