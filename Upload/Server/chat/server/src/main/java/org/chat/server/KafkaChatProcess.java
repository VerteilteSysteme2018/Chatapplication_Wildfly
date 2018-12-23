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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This is the core class of the server where the incoming Kafka chat requests arrive
 */
public class KafkaChatProcess {

  private TraceRepository traceRepository = null;
  private CountRepository countRepository = null;
  private static KafkaChatProcess kCP = null;

  public static KafkaChatProcess getInstance() {
    return kCP;
  }

  KafkaConsumer kafkaConsumer;
  ChatMessage chatMessage;

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
            chatMessage = message.value();
            System.out.print(chatMessage.getMessage() + " die message");
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    thread.start();
  }

  private void initiateServerProcess(ChatMessage chatMessage) throws NamingException {
    System.out.println("update tracedb");
    Trace trace = new Trace();
    trace.setClientThread(chatMessage.getClientThread());
    trace.setUsername(chatMessage.getUserName());
    trace.setMessage(chatMessage.getMessage());
    trace.setServerthread(chatMessage.getServerThread());
    connect();

    //update tracedb
    System.out.println("update tracedb");
    traceRepository.create(trace);


    //update countdb
    System.out.println("update countdb");
    countRepository.updateCount(chatMessage.getUserName());

    //send to topic
    sendMessageToKafkaTopic(chatMessage);

  }

  private void connect() throws NamingException {
    InitialContext ic = new InitialContext();
    this.traceRepository = (TraceRepository) ic.lookup("java:global/server-1.0-SNAPSHOT/TraceJPA!org.chat.databases.TraceRepository");
    this.countRepository = (CountRepository) ic.lookup("java:global/server-1.0-SNAPSHOT/CountJPA!org.chat.databases.CountRepository");
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