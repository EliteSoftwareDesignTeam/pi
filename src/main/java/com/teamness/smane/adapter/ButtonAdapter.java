package com.teamness.smane.adapter;

import java.io.IOException;
import java.util.Arrays;

public class ButtonAdapter extends ProcessAdapter<String> {
    @Override
    public void init() throws IOException {
        start(Arrays.asList("sudo", "python", "python/sensors/emergency_button.py"));
        process.addHandler(String::new, s -> {
            if(s.equals("PRESSED")) handle(s);
        });
    }
}
