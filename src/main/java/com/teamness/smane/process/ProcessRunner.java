package com.teamness.smane.process;

import com.teamness.smane.Pair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProcessRunner<T> {

    private List<Pair<Function<byte[], T>, Consumer<T>>> handlers = new ArrayList<>();
    private Process process;

    public void start(List<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Thread thread = new Thread(() -> {
            try {
                process = pb.start();
                InputStream is = process.getInputStream();
                /*
                String s;
                BufferedReader stdout = new BufferedReader (new InputStreamReader(p.getInputStream()));
                while ((s = stdout.readLine()) != null) {
                    handle(s);
                }
                */
                while (true) {
                    int bytesAvailable = is.available();
                    if(bytesAvailable <= 0) continue;
                    System.out.printf("Bytes available: %d%n", bytesAvailable);
                    byte[] buffer = new byte[bytesAvailable];
                    is.read(buffer);
                    handle(buffer);
                }
                //process.destroy();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.start();
    }

    private boolean isAlive() {
        return process.isAlive();
    }

    public void addHandler(Function<byte[], T> converter, Consumer<T> handler) {
        handlers.add(new Pair<>(converter, handler));
    }

    private void handle(byte[] data) {
        handlers.forEach(p -> p.second.accept(p.first.apply(data)));
    }

    public static void main(String[] args) {
        ProcessRunner<String> server = new ProcessRunner<>();

    }

}
