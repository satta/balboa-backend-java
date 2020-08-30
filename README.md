# balboa-backend-java

![Java CI](https://github.com/satta/balboa-backend-java/workflows/Java%20CI/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.satta/balboa-backend-java/badge.svg?)](https://maven-badges.herokuapp.com/maven-central/com.github.satta/balboa-backend-java)

This is a balboa backend connector for Java. It takes care of the boring msgpack
deserialization and validation, and provides a pluggable interface for backends
to receive input and answer queries.

Interaction with the frontend(s) is done via code implementing the `InputProcessor`
interface, which is called for each incoming message.
The methods to be implemented refer to the various message types:

```Java
@FunctionalInterface
interface ObservationStreamConsumer
{
    void submit(Observation o) throws IOException;
}

public interface InputProcessor {
    public abstract void handle(Observation o) throws BalboaException;
    public abstract void handle(DumpRequest d) throws BalboaException;
    public abstract void handle(BackupRequest b) throws BalboaException;
    public abstract void handle(Query q, ObservationStreamConsumer submitResult) throws BalboaException, IOException;
    public abstract void close();
}
```

Here's the simplest forking server that starts a new processing engine for each new incoming connection and just prints incoming mesages:

```Java
public class Main {
    public static void main(String[] args) {
        ServerSocket server = new ServerSocket(4242);
        do {
            Socket socket = server.accept();
            new Thread(new BackendWorker(socket, new PrintProcessor())).start();
        } while (true);
    }
}
```
