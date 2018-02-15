package com.teamness.bluetooth;

import com.teamness.smane.Pair;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class BluetoothServer<T> {

    public final String name;
    public final UUID uuid;
    List<Pair<Function<String, T>, Consumer<T>>> handlers = new ArrayList<>();
    private BufferedWriter writer;
    private boolean connected = false;

    public BluetoothServer(String name, String uuid) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
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
                writer = new BufferedWriter(new OutputStreamWriter(con.openDataOutputStream()));
                connected = true;

                String line;
                while((line = reader.readLine()) != null) {
                    handle(line);
                }
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public boolean isConnected() {
        return connected;
    }

    public void send(String data) {
        if(writer != null && connected) {
            try {
                writer.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addHandler(Function<String, T> converter, Consumer<T> handler) {
        handlers.add(new Pair<>(converter, handler));
    }

    private void handle(String data) {
        handlers.forEach(p -> p.second.accept(p.first.apply(data)));
    }

    public static void main(String[] args) {
        BluetoothServer<String> server = new BluetoothServer<>("", "");
    }

}
