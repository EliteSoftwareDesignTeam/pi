package com.teamness.smane.process;

import com.teamness.smane.Handleable;
import com.teamness.smane.Pair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProcessRunner<T> extends Handleable<byte[], T> {

    private Process process;
    private PrintWriter writer;

    public void start(List<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Thread thread = new Thread(() -> {
            try {
                process = pb.start();
                InputStream is = process.getInputStream();
                writer = new PrintWriter(process.getOutputStream());
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

    public void send(String s) {
        if(writer != null) writer.println(s);
    }

}
