### Netty "no such method" Fehler

HornetQ in 2.4.0.Final einbinden (bei neueren Versionen scheint sich eine Methode geändert zu haben...)

### Wildfly Quickstart - HelloWorld JMS Client

[github.com/wildfly/quickstart/tree/8.x/helloworld-jms](https://github.com/wildfly/quickstart/tree/8.x/helloworld-jms)

### Package `test`

- `HelloWorldJMSClient` Beispiel das Nachricht in Queue stellt und anschließend wieder aus dieser Queue liest
- `JmsTopicPublisher` and `JmsTopicReciever` Reciever fragt in Endlosschleife Nachrichten vom Topic ab, Publisher sendet Nachrichten ans Topic. Publisher sollte natürlich später nicht blockend Nachrichten empfangen...


 
 Benchmark
 
 - Start über `BenchmarkingClientUserInterfaceSimulation` 
 - Parameter wie Anzahl der Clients und Messages können in dieser Klasse angepasst werden