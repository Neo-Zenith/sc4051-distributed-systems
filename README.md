## SC4051: Distributed Systems Socket Programming

## Client

To build and run on windows:

At the `client` directory, run

```
g++ -o client.exe src\*.cpp -lws2_32
client <server_addr> <freshness_interval> <packet_loss_frequency>
```

## Server

To build and run:

At the `server` directory, run:

```
javac src/*.java -d bin
cd bin
java src/Server [at-most-once|at-least-once] [every-2-requests|every-4-requests|never|random]
```

To build javadoc:

At the `server` directory, run:

```
javadoc src src.controller src.comms src.marshaller src.services  -d doc/Javadoc -author -version -noqualifier all
```
