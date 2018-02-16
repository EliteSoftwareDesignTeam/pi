package com.teamness.smane.bluetooth;

import com.teamness.smane.Pair;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

public class BluetoothServer<T> {

    public final String name;
    public final UUID uuid;
    private List<Pair<Function<String, T>, Consumer<T>>> handlers = new ArrayList<>();
    private PrintWriter writer;
    private boolean connected = false;

    public BluetoothServer(String name, String uuid) {
        this.name = name;
        this.uuid = new UUID(uuid, false);
    }

    public void start() throws IOException {
        final String conString = String.format("btspp://localhost:%s;name=%s", uuid, name);
        StreamConnectionNotifier conNotifier = (StreamConnectionNotifier) Connector.open(conString);

        Thread thread = new Thread(() -> {
            StreamConnection con = null;
            try {
                con = conNotifier.acceptAndOpen();
                RemoteDevice device = RemoteDevice.getRemoteDevice(con);
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.openDataInputStream()));
                writer = new PrintWriter(new OutputStreamWriter(con.openDataOutputStream()));
                connected = true;

                String line;
                while((line = reader.readLine()) != null) {
                    handle(line);
                }
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
            }
        });
        thread.start();
    }

    public boolean isConnected() {
        return connected;
    }

    public void send(String data) {
        if(writer != null && isConnected()) writer.write(data);
    }

    public void addHandler(Function<String, T> converter, Consumer<T> handler) {
        handlers.add(new Pair<>(converter, handler));
    }

    private void handle(String data) {
        handlers.forEach(p -> p.second.accept(p.first.apply(data)));
    }

    public static void main(String[] args) throws IOException {
        URL propertiesURL = Pair.class.getClassLoader().getResource("bluetooth.properties");
        Properties props = new Properties();
        //props.load(propertiesURL.openStream());
        props.setProperty("name", "CanePi");
        props.setProperty("uuid", "94f39d297d6d437d973bfba39e49d4ee");
        BluetoothServer<String> server = new BluetoothServer<>(props.getProperty("name"), props.getProperty("uuid"));
        server.addHandler(Function.identity(), System.out::println);
        server.start();
    }

}
