# Databases

## neuen Ordner anlegen:
'/wildfly-13.0.0.final/modules/system/layers/base/org/mariadb/jdbc/main'

## die 3 Dateien aus diesem Ordner main einfügen

## zum starten:
1. Docker installieren
2. cd in das obengenante Verzeichnis und mit dem Befehl `docker-compose up` ausführen
3. mit `docker ps` überprüfen ob beide Docker richtig laufen :
Marvins-MBP:~ Marvin$ docker ps
CONTAINER ID        IMAGE                    COMMAND                  CREATED             STATUS              PORTS                    NAMES
c3b81170daca        bitnami/mariadb:latest   "/entrypoint.sh /run…"   30 hours ago        Up 16 seconds       0.0.0.0:3306->3306/tcp   main_tracedb_1_968bdbafc000
7b8ad9bbb77b        bitnami/mariadb:latest   "/entrypoint.sh /run…"   30 hours ago        Up 1 second         0.0.0.0:3310->3306/tcp   main_countdb_1_51881ee5a5f8
4. mit mySQL workbench countdb auf localhost mit port 3310 mit user: root und passwort: password verbinden. folgende 2 Befehle durchführen: 
`GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'password' WITH GRANT OPTION;'`
'`GRANT ALL PRIVILEGES ON *.* TO 'countuser'@'%' IDENTIFIED BY 'countuser' WITH GRANT OPTION;'`
5. mit mySQL workbench countdb auf localhost mit port 3306 mit user: root und passwort: password verbinden. folgende 2 Befehle durchführen: 
`GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'password' WITH GRANT OPTION;`
`GRANT ALL PRIVILEGES ON *.* TO 'traceuser'@'%' IDENTIFIED BY 'traceuser' WITH GRANT OPTION;`


Während Server läuft in MySQL Workbench überprüfen ob beide Tabellen in den beiden DB angelegt wurden: 
`show tables;`
`select * from countdata;'`
`show tables;`
`select * from trace;`
