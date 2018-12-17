# Verteilte Systeme | Studienarbeit WS18/19
## vorgelegt von Johannes Knippel | Marvin Staudt | Andreas Westhoff | Anja Wolf

## Ordnerstruktur:
*  **chat** Projekt für die Anwendung. Enthält:
    * **AdminClient** Enthält den AdminClient in Form eines Angular-Projekts. Holt sich LiveDaten vom Server und stellt diese dar.
    * **chatApplication** enthält die Vorlage inkl. unserer Erweiterungen. Wurde in ein Maven-Projekt umgewandelt.
    * **client** Modul für die Implementierung des Chatclients
    * **common** Modul für gemeinsame Dateien wie z.B. die Chat-PDUs
    * **lib** Libraries 
    * **server** Modul für die Implementierung des Servers. Enthält: 
      * Datenbanken
      * ReST
      * GraphQL
      * ChatProcess JMS & Kafka
*  **Docs** beinhaltet die Ausarbeitung als pdf
*  **README** beschreibt die Konfiguration von Widlfly, Kafka, JMS und die Datenbankanbindung
*  **Wildfly-config** Konfigurationsdateien für den Wildfly-Server mit Application- und Management-User sowie den benötigten JMS-Einstellungen im Server. Einfach in den Wildfly-Ordner kopieren (vorher backup von standalone.xml anlegen)

_______________________________________________________________________________________________________

## TO DO 17.12.2018:
* Benchmariking Clients + Last Test     -> Anja    
* Kafka                                 -> Flo
* Serverthread            + Timestamp  
* Docker Container
* Update README.md für Configuration
* Ordnerstruktur updaten
* jeder seinen teil schreiben für Ausarbeitung:
  * Anja: Grundalgen + Login REST
  * Johannes: Angular
  * Andi: Widlfly Config JMS
  * Marvin: DB + GraphQL
  * fehlt noch: Kafka, XA Transaktion, Benchmark

_______________________________________________________________________________________________________

## Setup local Wildfly 13.0.0.Final, DB Anbindung, JMS, Kafka, Angular AdminClient & die Nutzung mehrerer localhosts

## 1. Application Server in IDE einrichten (Wir haben mit InteliJ gearbeitet, als Projektstruktur wurde Maven verwendet) 

### 1.1 User anlegen 

???

### 1.2 config Dateien einfügen

standalone.xml, mgmt-groups.properties, mgmt-users.properties, application-roles.properties, application-users.properties aus Directory Wildfly-config in `../wildfly-13.0.0.Final/standalone/configuration` einfügen

### 1.3 Server starten

* `localhost:8080`
* `localhost:8080/server-1.0-SNAPSHOT/`
* REST- Schnittstelle:  `localhost:8080/server-1.0-SNAPSHOT/rest`
* GraphQL-Schnittstelle: `localhost:8080/server-1.0-SNAPSHOT/graphql`

### 1.4 Queue und Topic anlegen

??????? 

### 1.5 GraphQl Service

* GET TraceDB: `localhost:8080/server-1.0-SNAPSHOT/graphql?query={allTrace{id username clientthread message serverthread}}`
* GET CountDB: `localhost:8080/server-1.0-SNAPSHOT/graphql?query={allCount{id username counting}}`
* DELETE TraceDB: POST `localhost:8080/server-1.0-SNAPSHOT/graphql` + Query: `{"query":"mutation{clearTrace}"}`
* DELETE CountDB: POST `localhost:8080/server-1.0-SNAPSHOT/graphql` + Query: `{"query":"mutation{clearCount}“}`

### 1.6 Rest Service 

* GET: `localhost:8080/server-1.0-SNAPSHOT/rest/users/currentusers`
* POST: `localhost:8080/server-1.0-SNAPSHOT/rest/users/login/beispielname`
* DELETE: `localhost:8080/server-1.0-SNAPSHOT/rest/users/logout/beispielname`

## 2. Datenbanken aufsetzen & anbinden

### 2.1 MariaDB Config Dateien aus dem Directory `../Wildfly-config/main` in `../wildfly-13.0.0.Final/modules/system/layers/base/org/mariadb/jdbc/main`einfügen (Ordner mariadb muss erstellt werden)

### 2.2 Es wurden 2 Datenbanken verwendet: Countdb und Tracedb
* Diese wurden mit den jeweiligen IP-Adressen und Ports in der `standalone.xml` und der `persistence.xml` Datei hinterlegt
* Um die beiden Docker Container zu starten muss Docker installiert werden (Docker Desktop -v 2.0.0.0-mac81 Community)
* in das folgende Verzeichnis navigieren im Terminal : `../wildfly-13.0.0.Final/modules/system/layers/base/org/mariadb/jdbc/main` und den Befehl `docker-compose up`ausführen. Dies startet die beiden Dockercontainer. Nun muss noch der Remote Access in den beiden DBs aktiviert werden. 
mit dem Befehl  `docker ps` kann dies überprüft werden:
CONTAINER ID        IMAGE                    COMMAND                  CREATED             STATUS              PORTS                    NAMES
c3b81170daca        bitnami/mariadb:latest   "/entrypoint.sh /run…"   2 weeks ago         Up 16 seconds       0.0.0.0:3306->3306/tcp   main_tracedb_1_968bdbafc000
7b8ad9bbb77b        bitnami/mariadb:latest   "/entrypoint.sh /run…"   2 weeks ago         Up 1 second         0.0.0.0:3310->3306/tcp   main_countdb_1_51881ee5a5f8
Die tracedb läuft auf dem Port 3306 und countdb auf dem Port 3310. Dies wurde jeweils in dem `standalone.xml` und der `persistence.xml` mit den jeweiligen Zugangsdaten hinterlegt. 
* Test der Datenbank Connection in Management-Console: `Configuration`>`Subsystems`>`Datasources & Drivers`>`Datasources`-> countDS und traceDS -> Test Connection
??

## 3. JMS

## 4. Kafka
* Um Kafka nutzen zu können muss sowohl ein ZooKeeper- als auch ein Kafka-Server laufen, beides erhältlich unter: https://kafka.apache.org/downloads
* Nach dem entpacken in der Konsole in den Kafka Ordner wechseln
* Zookeeper und Kafka installieren (z.B. per Homebrew)
* bei Bedarf dafür Gradle installieren und `./gradlew jar -PscalaVersion=2.11.12` im Kafka-ordner ausführen
* ZooKeeper mit `bin/zookeeper-server-start.sh config/zookeepeproperties` starten
* Kafka mit `bin/kafka-server-start.sh config/server.properties` starten
* Kafka-Topics anlegen (auch aus dem Kafka-Ordner) per: 
 `bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic requestTopic`
 `bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic responseTopic`      

## 5. Angular AdminClient
* Für die Nutzung von Angular muss zunächst sichergestellt sein, dass Node.js und der AngularCLI auf dem Rechner global installiert sind. Node.js: https://nodejs.org/en/
* Für den AngularCLI nun in der Windows-Console folgenden Befehl ausführen: `npm install -g @angular/cli`
* In den Projektordner `../AdminClient/` wechseln und `npm install` ausführen, um die notwendigen node_modules (Dependencies) zu installieren
* bei eventuellen `vulnerabilities` folgenden Befehl ausführen `npm audit fix -force`
* `npm start` um den Client unter http://localhost:4200/ aufzurufen

## 6. Nutzung mehrerer localhosts
* Unter den Brwoserherstellern ist es üblich gleichzeitige Verbindungen von localhosts zu unterbinden, um Sicherheitslücken zu präventieren
* Damit die Kommunikation zwischen dem Backend (ChatApplication) und dem Frontend (AdminClient) zu ermöglichen, muss unter Wondows eine ungesicherte Instanz des Browsers gestartet werden. Unter Windows und Chrome funktioniert dies so: `Windows+R` drücken und folgenden Befehl ausführen `chrome.exe --user-data-dir="C:/Chrome dev session" --disable-web-security`