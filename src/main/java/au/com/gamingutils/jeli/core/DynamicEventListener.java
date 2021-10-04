package au.com.gamingutils.jeli.core;

public abstract class DynamicEventListener {

    /**
     * process the event that was broadcast.
     * @param eventToBroadcast Event to broadcast.
     */
    public abstract void broadcastEvent(Event eventToBroadcast);
}
