package com.teamness.smane.adapter;

import com.teamness.smane.Converter;
import com.teamness.smane.Handler;
import com.teamness.smane.event.Event;

import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class UltrasonicAdapter extends ProcessAdapter<Integer> {

    Queue<Handler<Integer>> distanceHandlers = new LinkedBlockingQueue<>();

    @Override
    public void init() throws IOException {
        start(Arrays.asList("sudo", "python", "python/obstacles.py"));
        process.addHandler(t -> Integer.parseInt(new String(t)), integer -> {
            if(!distanceHandlers.isEmpty()) distanceHandlers.remove().handle(integer);
            // If there are distance handlers waiting, trigger a distance reading with the next handler
            if(!distanceHandlers.isEmpty()) triggerDistanceReading();
        });
    }

    public void getDistance(Handler<Integer> handler) {
        distanceHandlers.add(handler);
        // If there are no distance handlers currently waiting (except this one), then trigger a distance reading
        if(distanceHandlers.size() == 1) triggerDistanceReading();
    }

    private void triggerDistanceReading() {
        process.send("get_distance\n");
    }

}
