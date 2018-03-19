package com.teamness.smane.adapter;

import java.io.IOException;
import java.util.Arrays;

public class BuzzerAdapter extends ProcessAdapter<String> {

    public void buzz(Direction direction, int milliseconds) {
        process.send(String.format("%s,%d%n", direction.str, milliseconds));
    }

    @Override
    public void init() throws IOException {
        start(Arrays.asList("sudo", "python", "python/buzzers.py"));
    }

    public enum Direction {

        LEFT("left"), RIGHT("right");

        public final String str;

        Direction(String str) {
            this.str = str;
        }
    }

}
