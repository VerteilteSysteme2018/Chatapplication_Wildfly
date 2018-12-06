# Verteilte Systeme WS 2018

## Ordnerstruktur:
*  *chatApplikation* Chat-Vorlage Dako
*  *chat* Projekt für die Anwendung. Enthält:
    * *client* Modul für die Implementierung des Chatclients
    * *common* Modul für gemeinsame Dateien wie z.B. die Chat-PDUs
    * *server* Modul für die Implementierung des Servers
*  *Wildfly-config* Konfigurationsdateien für den Wildfly-Server mit Application- und Management-User sowie den benötigten JMS-Einstellungen im Server. Einfach in den Wildfly-Ordner kopieren (vorher backup von standalone.xml anlegen)

## TO DO 05.12.2018: 
* Topic für User integrieren            -> Andi
* Benchmariking Clients + Last Test     -> Anja    
* Kafka                                 -> Flo
* Angular Admin Oberfläche              -> Johannes
* Chat GUI + Login Prozess               -> Anja
* XA Transaktion                         -> Andi
* Clientthread einbinden                 -> Marvin
* Serverthread                           -> ????
* beide Server laufen  + TCP?            -> ????
* jeder seinen teil schreiben für Ausarbeitung: 
         Anja: Grundalgen + Login REST
         Johannes: Angular
         Andi: Widlfly Config JMS
         Marvin: DB + GraphQL 
         fehlt noch: Kafka, XA Transaktion, Benchmark
          
