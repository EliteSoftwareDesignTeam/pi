package com.teamness.smane;

import com.teamness.smane.event.CaneEvents;
import com.teamness.smane.event.Event;
import com.teamness.smane.event.EventChannel;
import com.teamness.smane.event.TestEvent;
import com.teamness.smane.process.ProcessRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class Main {

    private static ProcessRunner<Event> btServer;

    public static void main(String[] args) {
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
        btServer = new ProcessRunner<>();
        btServer.addHandler(b -> {
            String str = new String(b);
            System.out.printf("Got %s from pi%n", str);
            if(str.charAt(str.length()-1) == '\n') str = str.substring(0, str.length()-1);
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
    }

    public static void sendEvent(Event e) throws IOException {
        System.out.println("Sending");
        btServer.send(Serialisation.fromObject(e));
    }

    public static void receiveEvent(Event e) {
        System.out.printf("Got event %s%n", e);
        CaneEvents.BT_IN.trigger(e);
    }

}
