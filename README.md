# balboa-backend-java

![Java CI](https://github.com/satta/balboa-backend-java/workflows/Java%20CI/badge.svg)

This is a balboa backend connector for Java. It takes care of the boring msgpack
deserialization and validation, and provides a pluggable interface for backends
to receive input and answer queries.

Interaction with the frontend(s) is done via code implementing the `InputProcessor`
interface, which is called for each incoming message.
The methods to be implemented refer to the various message types:

```Java
public interface InputProcessor {
    public abstract void handle(Observation o) throws BalboaException;
    public abstract void handle(DumpRequest d) throws BalboaException;
    public abstract void handle(BackupRequest b) throws BalboaException;
    public abstract void handle(Query q, List<Observation> obs) throws BalboaException;
    public abstract void close();
}
```

Here's the simplest forking server that starts a new processing engine for each new incoming connection:

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
