## ClientGUI

 - Start der JMS / Kafka Client GUI über ClientStart
 - Nachrichten können wahlweise via JMS oder Kafka verschickt werden (DropDown Menü zum auswählen)


## Benchmark
 
 - Start des Benchmarks über `BenchmarkingClientUserInterfaceSimulation` 
 - Parameter (Anzahl der Clients, Anzahl Messages & Typ des Servers (TCP, JMS, Kafka) können in der Klasse angepasst werden (Methode doWork())

## 1. Testversuch: 
# 1 Client
*** Meldung: Alle Clients-Threads beendet ***
Testende: 21.12.18 11:22:54:459
Testdauer in s: 13
Gesendete Requests: 100
Anzahl Responses: 100
Anzahl verlorener Responses: 0
Mittlere RTT in ms: 107.51250465000003
Maximale RTT in ms: 582.631829
Minimale RTT in ms: 87.862273
Mittlere Serverbearbeitungszeit in ms: 1.545387
Maximale Heap-Belegung in MByte: 48
Maximale CPU-Auslastung in %: 0.12699206173419952
*** Meldung: JMS-Implementation: Benchmark beendet ***

# 10 Clients
*** Meldung: Alle Clients-Threads beendet *** Testende: 21.12.18 11:00:51:438 Testdauer in s: 32 Gesendete Requests: 1000 Anzahl Responses: 1000 Anzahl verlorener Responses: 0 Mittlere RTT in ms: 220.5495390450013 Maximale RTT in ms: 1376.695004 Minimale RTT in ms: 84.918836 Mittlere Serverbearbeitungszeit in ms: 1.545386 Maximale Heap-Belegung in MByte: 78 Maximale CPU-Auslastung in %: 0.1987442970275879 *** Meldung: JMS-Implementation: Benchmark beendet ***

# 20 Clients
*** Meldung: Alle Clients-Threads beendet *** Testende: 21.12.18 11:03:45:161 Testdauer in s: 25 Gesendete Requests: 2000 Anzahl Responses: 2000 Anzahl verlorener Responses: 0 Mittlere RTT in ms: 201.63439191800066 Maximale RTT in ms: 605.500934 Minimale RTT in ms: 124.644542 Mittlere Serverbearbeitungszeit in ms: 1.545386 Maximale Heap-Belegung in MByte: 124 Maximale CPU-Auslastung in %: 0.2924070954322815 *** Meldung: JMS-Implementation: Benchmark beendet ***

# 30 Clients

*** Meldung: Alle Clients-Threads beendet *** Testende: 21.12.18 11:06:05:064 Testdauer in s: 31 Gesendete Requests: 3000 Anzahl Responses: 3000 Anzahl verlorener Responses: 0 Mittlere RTT in ms: 251.33139241499913 Maximale RTT in ms: 1094.132762 Minimale RTT in ms: 106.206524 Mittlere Serverbearbeitungszeit in ms: 1.545386 Maximale Heap-Belegung in MByte: 233 Maximale CPU-Auslastung in %: 0.35310789942741394 *** Meldung: JMS-Implementation: Benchmark beendet ***

# 40 Cleints

*** Meldung: Alle Clients-Threads beendet *** Testende: 21.12.18 11:08:21:899 Testdauer in s: 34 Gesendete Requests: 4000 Anzahl Responses: 4000 Anzahl verlorener Responses: 0 Mittlere RTT in ms: 268.9692535522513 Maximale RTT in ms: 861.285125 Minimale RTT in ms: 125.829096 Mittlere Serverbearbeitungszeit in ms: 1.545386 Maximale Heap-Belegung in MByte: 206 Maximale CPU-Auslastung in %: 0.33482977747917175 *** Meldung: JMS-Implementation: Benchmark beendet ***

# 50 Clients

*** Meldung: Alle Clients-Threads beendet *** Laufzeitzaehler: 44 Testende: 21.12.18 11:10:19:888 Testdauer in s: 44 Gesendete Requests: 5000 Anzahl Responses: 5000 Anzahl verlorener Responses: 0 Mittlere RTT in ms: 346.90472619840443 Maximale RTT in ms: 1326.121178 Minimale RTT in ms: 117.100056 Mittlere Serverbearbeitungszeit in ms: 1.545386 Maximale Heap-Belegung in MByte: 256 Maximale CPU-Auslastung in %: 0.2911187708377838 *** Meldung: JMS-Implementation: Benchmark beendet ***



## 2. Testversuch:

### Ergebnisse: 
# 1 Client
*** Meldung: Alle Clients-Threads beendet ***
Testende: 21.12.18 11:22:10:900
Testdauer in s: 13
Gesendete Requests: 100
Anzahl Responses: 100
Anzahl verlorener Responses: 0
Mittlere RTT in ms: 99.93333127000001
Maximale RTT in ms: 1136.194084
Minimale RTT in ms: 55.76004
Mittlere Serverbearbeitungszeit in ms: 1.545387
Maximale Heap-Belegung in MByte: 52
Maximale CPU-Auslastung in %: 0.15676714479923248
*** Meldung: JMS-Implementation: Benchmark beendet ***


## 10 Clients
*** Meldung: Alle Clients-Threads beendet ***
Testende: 21.12.18 11:13:01:114
Testdauer in s: 21
Gesendete Requests: 1000
Anzahl Responses: 1000
Anzahl verlorener Responses: 0
Mittlere RTT in ms: 168.17943159200073
Maximale RTT in ms: 577.618204
Minimale RTT in ms: 77.666608
Mittlere Serverbearbeitungszeit in ms: 1.545387
Maximale Heap-Belegung in MByte: 90
Maximale CPU-Auslastung in %: 0.26193296909332275
*** Meldung: JMS-Implementation: Benchmark beendet ***

## 20 Clients
*** Meldung: Alle Clients-Threads beendet ***
Laufzeitzaehler: 23
Testende: 21.12.18 11:14:10:676
Testdauer in s: 23
Gesendete Requests: 2000
Anzahl Responses: 2000
Anzahl verlorener Responses: 0
Mittlere RTT in ms: 189.05216990350038
Maximale RTT in ms: 479.773401
Minimale RTT in ms: 113.380773
Mittlere Serverbearbeitungszeit in ms: 1.545387
Maximale Heap-Belegung in MByte: 158
Maximale CPU-Auslastung in %: 0.31278499960899353
*** Meldung: JMS-Implementation: Benchmark beendet ***
## 30 Clients
*** Meldung: Alle Clients-Threads beendet ***
Testende: 21.12.18 11:15:08:058
Testdauer in s: 27
Gesendete Requests: 3000
Anzahl Responses: 3000
Anzahl verlorener Responses: 0
Mittlere RTT in ms: 221.95562180533224
Maximale RTT in ms: 501.894555
Minimale RTT in ms: 125.833461
Mittlere Serverbearbeitungszeit in ms: 1.545387
Maximale Heap-Belegung in MByte: 203
Maximale CPU-Auslastung in %: 0.3428846299648285
*** Meldung: JMS-Implementation: Benchmark beendet ***
## 40 Cleints
*** Meldung: Alle Clients-Threads beendet ***
Testende: 21.12.18 11:17:42:598
Testdauer in s: 32
Gesendete Requests: 4000
Anzahl Responses: 4000
Anzahl verlorener Responses: 0
Mittlere RTT in ms: 266.9876975600019
Maximale RTT in ms: 793.812342
Minimale RTT in ms: 132.7973
Mittlere Serverbearbeitungszeit in ms: 1.545387
Maximale Heap-Belegung in MByte: 251
Maximale CPU-Auslastung in %: 0.32257625460624695
*** Meldung: JMS-Implementation: Benchmark beendet ***
## 50 Clients
*** Meldung: Alle Clients-Threads beendet ***
Testende: 21.12.18 11:21:10:579
Testdauer in s: 39
Gesendete Requests: 5000
Anzahl Responses: 5000
Anzahl verlorener Responses: 0
Mittlere RTT in ms: 322.14020047319565
Maximale RTT in ms: 1748.06597
Minimale RTT in ms: 107.10547
Mittlere Serverbearbeitungszeit in ms: 1.545387
Maximale Heap-Belegung in MByte: 289
Maximale CPU-Auslastung in %: 0.33662667870521545
*** Meldung: JMS-Implementation: Benchmark beendet ***
