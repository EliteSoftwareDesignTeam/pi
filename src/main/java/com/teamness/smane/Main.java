package com.teamness.smane;

import com.teamness.smane.event.Event;
import com.teamness.smane.process.ProcessRunner;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args)  {
        ProcessRunner<Event> bluetoothProcess = new ProcessRunner<>();
        bluetoothProcess.addHandler(bytes -> {
            System.out.println("pls");
            try {
                System.out.println("Trying!");
                return (Event) Serialisation.toObject(new String(bytes));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }, s -> System.out.printf("Got event %s%n", s));
        bluetoothProcess.start("sudo", "python", "python/cane-bluetooth.py");
    }

}
