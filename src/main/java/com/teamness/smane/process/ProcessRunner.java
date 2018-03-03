package com.teamness.smane.process;

import com.teamness.smane.Handleable;
import com.teamness.smane.Handler;
import com.teamness.smane.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProcessRunner<T> extends Handleable<byte[], T> {

    private Process process;
    private PrintWriter writer;

    private Handler<String> errHandler;
    private boolean redirectError = false;

    public void start(List<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if(redirectError) pb.redirectErrorStream(true);
        Thread thread = new Thread(() -> {
            try {
                process = pb.start();
                InputStream is = process.getInputStream();
                writer = new PrintWriter(process.getOutputStream());
                while (true) {
                    int bytesAvailable = is.available();
                    if(bytesAvailable <= 0) continue;
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

        if(errHandler != null && !redirectError) {
            Thread errThread = new Thread(() -> {
                InputStream is = process.getErrorStream();
                while (true) {
                    try {
                        int bytesAvailable = is.available();
                        if (bytesAvailable <= 0) continue;
                        byte[] buffer = new byte[bytesAvailable];
                        is.read(buffer);
                        errHandler.handle(new String(buffer));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            errThread.start();
        }
    }

    public void setErrHandler(Handler<String> errHandler) {
        this.errHandler = errHandler;
    }

    private boolean isAlive() {
        return process.isAlive();
    }

    public void send(String s) {
        if(writer != null) {
            writer.println(s);
            writer.flush();
        }
    }

    public boolean isRedirectingError() {
        return redirectError;
    }

    public void setRedirectError(boolean redirectError) {
        this.redirectError = redirectError;
    }
}
