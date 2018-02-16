package com.teamness.smane;

import com.teamness.smane.event.Event;
import com.teamness.smane.process.ProcessRunner;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        ProcessRunner<String> btServer = new ProcessRunner<>();
        btServer.addHandler(String::new, System.out::println);
        btServer.start(Arrays.asList("python", "python/example_server.py"));
    }

}
