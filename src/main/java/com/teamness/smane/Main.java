package com.teamness.smane;

import com.teamness.smane.adapter.ButtonAdapter;
import com.teamness.smane.adapter.BuzzerAdapter;
import com.teamness.smane.adapter.UltrasonicAdapter;
import com.teamness.smane.event.*;
import com.teamness.smane.process.ProcessRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Function;

public class Main {

    private static ProcessRunner<Event> btServer = new ProcessRunner<>();
    private static BuzzerAdapter buzzers = new BuzzerAdapter();
    private static UltrasonicAdapter ultrasonic = new UltrasonicAdapter();
    private static ButtonAdapter button = new ButtonAdapter();
    private static Handler<Integer> distanceHandler = distance -> CaneEvents.LOCAL.trigger(new DistanceEvent(distance));

    public static void main(String[] args) throws IOException {
        Serialisation.base64Provider = new Base64Provider() {
            @Override
            public byte[] decode(String base64Str) {
                return Base64.getDecoder().decode(base64Str);
            }

            @Override
            public String encode(byte[] bytes) {
                return Base64.getEncoder().encodeToString(bytes);
            }
        };
        btServer.addHandler(b -> {
            String str = new String(b);
            while(str.charAt(str.length()-1) == '\n') str = str.substring(0, str.length()-1);
            try {
                return (Event) Serialisation.toObject(str);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }, Main::receiveEvent);
        btServer.setRedirectError(false);
        btServer.setErrHandler(s -> System.out.printf("Got error from pi: %s%n", s));
        btServer.start(Arrays.asList("sudo", "python", "python/cane-bluetooth.py"));
        CaneEvents.BT_OUT.onAny(EventChannel.EventPriority.HIGH, "sendEvent", Main.class);

        buzzers.init();
        CaneEvents.BT_IN.on(BuzzerEvent.class, "buzz", Main.class);

        CaneEvents.LOCAL.on(EventChannel.EventPriority.LOW, DistanceEvent.class, "getDistance", Main.class);
        getDistance(null);

        button.init();
        button.addHandler(Converter.identity(), s -> CaneEvents.BT_OUT.trigger(new ButtonEvent(ButtonEvent.ButtonAction.PRESSED)));
    }

    private static void getDistance(DistanceEvent event) {
        try {
            // Sleep a little to avoid overloading the sensor
            Thread.sleep(100);
            ultrasonic.getDistance(distanceHandler);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sendEvent(Event e) throws IOException {
        btServer.send(Serialisation.fromObject(e));
    }

    public static void buzz(BuzzerEvent e) {
        if(e.direction > 0) buzzers.buzz(BuzzerAdapter.Direction.LEFT, 3);
        else if(e.direction < 0) buzzers.buzz(BuzzerAdapter.Direction.RIGHT, 3);
    }

    public static void receiveEvent(Event e) {
        CaneEvents.BT_IN.trigger(e);
    }

}
