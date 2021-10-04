package au.com.gamingutils.jeli.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public final class EventQueue {
    private final List<Event> eventList = new ArrayList<>();

    /**
     * Add an event to the queue.
     * @param event Event to queue up.
     */
    public void addEvent(Event event) {
        synchronized (eventList) {
            eventList.add(event);
        }
    }

    /**
     * Process this event queue on the current thread.
     */
    public void processEventQue() {
        processEventQue(null);
    }

    /**
     * Process this event queue on a specific thread.
     * @param executor Thread to process this event queue on. If null this queue is executed on the current thread.
     */
    public void processEventQue(Executor executor) {

        List<Event> copyEventList = new ArrayList<>();
        synchronized (eventList) {
            copyEventList.addAll(eventList);
            eventList.clear();
        }
        if (executor != null) {
            executor.execute(()->copyEventList.forEach( Event::broadcast ));
        } else copyEventList.forEach( Event::broadcast );
    }
}
