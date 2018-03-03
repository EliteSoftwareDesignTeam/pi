package com.teamness.smane;

import com.teamness.smane.event.CaneEvents;
import com.teamness.smane.event.Event;
import com.teamness.smane.event.EventChannel;
import com.teamness.smane.process.ProcessRunner;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    private static ProcessRunner<Event> btServer;

    public static void main(String[] args) {
        btServer = new ProcessRunner<>();
        btServer.addHandler(b -> {
            String str = new String(b);
            if(str.charAt(str.length()-1) == '\n') str = str.substring(0, str.length()-1);
            try {
                return (Event) Serialisation.toObject(str);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }, Main::receiveEvent);
        btServer.start(Arrays.asList("sudo", "python", "python/cane-bluetooth.py"));
        CaneEvents.BT_OUT.onAny(EventChannel.EventPriority.HIGH, "sendEvent", Main.class);
    }

    public static void sendEvent(Event e) throws IOException {
        btServer.send(Serialisation.fromObject(e));
    }

    public static void receiveEvent(Event e) {
        CaneEvents.BT_IN.trigger(e);
    }

}
