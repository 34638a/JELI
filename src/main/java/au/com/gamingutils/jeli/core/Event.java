package au.com.gamingutils.jeli.core;

import lombok.Getter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Getter
public abstract class Event {

    private static final DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss.SSS z");
    static { formatter.setTimeZone(TimeZone.getTimeZone("UTC")); }
    private final long timestamp;
    private boolean cancelled = false;
    private boolean sent = false;

    /**
     * Create an event.
     */
    public Event() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Can this event be cancelled.
     * @return true if this event can be cancelled.
     */
    public abstract boolean canThisEventBeCancelled();

    /**
     * Cancel this event if it can be cancelled.
     */
    public final void cancelEvent() {
        this.cancelled = canThisEventBeCancelled();
    }

    /**
     * Get the name for the event.
     * @return Event name as String.
     */
    public final String getEventName() {
        return this.getClass().getName();
    }

    /**
     * Get the time stamp formatted to a String.
     * @return Time stamp as String.
     */
    public final String getTimeStampFormatted() {
        return formatter.format(new Date(timestamp));
    }

    /**
     * Broadcast this event to all listeners.
     */
    public final void broadcast() {
        synchronized (this) {
            if (this.sent) {
                return;
            }
            this.sent = true;
            EventEngine.broadcastEvent(this);
        }
    }

    @Override
    public String toString() {
        return String.format("%s, %s", getTimeStampFormatted(), getEventName());
    }
}
