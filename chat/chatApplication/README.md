# Verteilte Systeme | Studienarbeit WS18/19
## vorgelegt von Johannes Knippel | Marvin Staudt | Andreas Westhoff | Anja Wolf

## Ordnerstruktur:
*  *chatApplikation* Chat-Vorlage Dako
*  *chat* Projekt für die Anwendung. Enthält:
    * *AdminClient* Enthält den AdminClient in Form eines Angular-Projekts. Holt sich LiveDaten vom Server und Chatverlauf
    * *client* Modul für die Implementierung des Chatclients
    * *common* Modul für gemeinsame Dateien wie z.B. die Chat-PDUs
    * *server* Modul für die Implementierung des Servers
*  *Wildfly-config* Konfigurationsdateien für den Wildfly-Server mit Application- und Management-User sowie den benötigten JMS-Einstellungen im Server. Einfach in den Wildfly-Ordner kopieren (vorher backup von standalone.xml anlegen)

_______________________________________________________________________________________________________
_______________________________________________________________________________________________________

## TO DO 05.12.2018:
* Benchmariking Clients + Last Test     -> Anja    
* Kafka                                 -> Flo
* Serverthread                           -> ????
* beide Server laufen  + TCP?            -> ????
* Update README.md für Configuration
* Ordnerstruktur updaten
* jeder seinen teil schreiben für Ausarbeitung:
** Anja: Grundalgen + Login REST
** Johannes: Angular
** Andi: Widlfly Config JMS
** Marvin: DB + GraphQL
** fehlt noch: Kafka, XA Transaktion, Benchmark

_______________________________________________________________________________________________________
_______________________________________________________________________________________________________
_______________________________________________________________________________________________________

## Setup local Wildfly 13.0.0.Final, DB Anbindung, JMS & Kafka

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

         
