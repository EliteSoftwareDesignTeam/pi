package com.teamness.smane.adapter;

import com.teamness.smane.Converter;
import com.teamness.smane.Handler;
import com.teamness.smane.event.Event;

import java.io.IOException;
import java.util.Arrays;

public class UltrasonicAdapter extends ProcessAdapter<Integer> {

    Handler<Integer> distanceHandler;

    @Override
    public void init() throws IOException {
        start(Arrays.asList("sudo", "python", "python/obstacles.py"));
        process.addHandler(t -> Integer.parseInt(new String(t)), integer -> {
            if(distanceHandler != null) {
                distanceHandler.handle(integer);
                distanceHandler = null;
            }
        });
    }

    public void getDistance(Handler<Integer> handler) {
        distanceHandler = handler;
        process.send("get_distance\n");
    }

}
