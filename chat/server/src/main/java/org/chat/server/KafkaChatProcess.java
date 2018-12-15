package org.chat.server;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.chat.common.ChatMessage;
import org.chat.databases.CountRepository;
import org.chat.databases.Trace;
import org.chat.databases.TraceRepository;

import javax.ejb.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Singleton
public class KafkaChatProcess {

    @EJB
    private TraceRepository traceRepository;

    @EJB
    private CountRepository countRepository;

    private static KafkaChatProcess kCP = null;

    public static KafkaChatProcess getInstance() {
        return kCP;
    }

    KafkaConsumer kafkaConsumer;
    ChatMessage m;

    public static void main(String[] args) {
        KafkaChatProcess kRC = new KafkaChatProcess();
        kRC.initializeConsumer();
        kRC.startRecievingMessages();

    }

    public void initializeConsumer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.chat.common.ChatMessageDeserializer");
        properties.put("group.id", "test-group");

        kafkaConsumer = new KafkaConsumer(properties);
        List topics = new ArrayList();
        topics.add("requestTopic");
        kafkaConsumer.subscribe(topics);
    }

    public void startRecievingMessages() {
        Thread thread = new Thread(() -> {
          try {

        while (true) {
            ConsumerRecords<String, ChatMessage> messages = kafkaConsumer.poll(100);
            for (ConsumerRecord<String, ChatMessage> message : messages) {
                if(message.value() != null) {
                    initiateServerProcess(message.value());
                }
                System.out.println("Message received " + message.value().toString());
                m = message.value();
                System.out.print(m.getMessage() + " die message");
            }
        }
          } catch (Exception e) {
                e.printStackTrace();
            }
            /**     try {
             while (true) {
             ConsumerRecords records = kafkaConsumer.poll(10);
             for (Object record: records){

             System.out.println(String.format(record.toString()));
             }
             }
             } catch (Exception e){
             System.out.println(e.getMessage());
             } finally {
             kafkaConsumer.close();
             }*/
        });
        thread.start();
    }

    private void initiateServerProcess(ChatMessage chatMessage) {
        System.out.println("update tracedb");
        Trace trace = new Trace();
        trace.setClientThread(chatMessage.getClientThread());
        trace.setUsername(chatMessage.getUserName());
        trace.setMessage(chatMessage.getMessage());
        trace.setServerthread(chatMessage.getServerthread());
        //traceRepository.create(trace); //TODO Marvin

        //update countdb
        System.out.println("update countdb");
        //countRepository.updateCount(chatMessage.getUserName());

        //send to topic
        sendMessageToKafkaTopic(chatMessage);

    }


private void sendMessageToKafkaTopic(ChatMessage message) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.chat.common.ChatMessageSerializer");
        KafkaProducer kafkaProducer = new KafkaProducer<String, String>(properties);
        try {
        System.out.println(message.toString());
        kafkaProducer.send(new ProducerRecord<>("responseTopic", message.getUserName(), message));
        } catch (Exception e) {
        e.printStackTrace();
        } finally {
        kafkaProducer.close();
        }
        }

}