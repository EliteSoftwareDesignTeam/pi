package com.teamness.smane;

import com.teamness.smane.event.Event;
import com.teamness.smane.event.EventChannel;
import com.teamness.smane.process.ProcessRunner;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static EventChannel localEvents = new EventChannel(), bluetoothEvents = new EventChannel(), allEvents = new EventChannel(EventChannel.EventPriority.MEDIUM, EventChannel.EventPriority.HIGH, localEvents, bluetoothEvents);
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
        bluetoothEvents.onAny(EventChannel.EventPriority.HIGH, "sendEvent", null);
    }

    public static void sendEvent(Event e) throws IOException {
        btServer.send(Serialisation.fromObject(e));
    }

    public static void receiveEvent(Event e) {
        bluetoothEvents.trigger(e);
    }



}
