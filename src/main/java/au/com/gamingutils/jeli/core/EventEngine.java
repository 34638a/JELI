package au.com.gamingutils.jeli.core;

import au.com.gamingutils.jeli.annotation.EventListener;
import au.com.gamingutils.jeli.core.exception.IllegalEventCallbackMethod;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class EventEngine {

    //Temp Threadsafe Write Buffers
    private static List<DynamicEventListener> temp_dynamicEventListenerList = Collections.synchronizedList(new ArrayList<>());
    private static ConcurrentHashMap<Class<? extends Event>, List<StaticEventCallbackMethod>> temp_staticCallbacks = new ConcurrentHashMap<>();

    //Thread Locks
    private static final StaticEventCallbackMethod[] EMPTYCALLBACKLIST = new StaticEventCallbackMethod[]{};
    private static final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static boolean locked;


    //Usable values
    private static DynamicEventListener[] dynamicEventListenerList;
    private static final HashMap<Class<? extends Event>, StaticEventCallbackMethod[]> staticCallbacks = new HashMap<>();



    /**
     * Lock the EventEngine and commit to the loaded data in order to save memory.
     */
    public void lock() {
        readWriteLock.writeLock().lock();
        if (!locked) {
            locked = true;
            temp_staticCallbacks.forEach((e, k) -> {
                StaticEventCallbackMethod[] callbacks = k.toArray(new StaticEventCallbackMethod[]{});
                Arrays.sort(callbacks, Comparator.comparing(o -> o.priority));
                staticCallbacks.put(e, callbacks);
            });
            dynamicEventListenerList = temp_dynamicEventListenerList.toArray(new DynamicEventListener[]{});
            temp_staticCallbacks = null;
            temp_dynamicEventListenerList = null;
        }
        readWriteLock.writeLock().unlock();
    }


    /**
     * Register an event listener that will handle callbacks from given events.
     * @param method Method to register.
     */
    public void registerStaticEventListener(Method method) {
        if (method == null) return;
        readWriteLock.readLock().lock();
        if (!locked) {
            StaticEventCallbackMethod staticEventCallbackMethod = new StaticEventCallbackMethod(method);
            temp_staticCallbacks.putIfAbsent(
                    (Class<? extends Event>) method.getParameterTypes()[0],
                    Collections.synchronizedList(new ArrayList<>())
            ).add(staticEventCallbackMethod);
        }
        readWriteLock.readLock().unlock();
    }

    /**
     * Register an event listener that will handle callbacks from given events.
     * @param dynamicEventListener DynamicEventListener to register.
     */
    public void registerDynamicEventListener(DynamicEventListener dynamicEventListener) {
        if (dynamicEventListener == null) return;
        readWriteLock.readLock().lock();
        if (!locked) {
            temp_dynamicEventListenerList.add(dynamicEventListener);
        }
        readWriteLock.readLock().unlock();
    }


    /**
     * Broadcast a given event to all relevant listeners.
     * @param event Event to broadcast.
     */
    static void broadcastEvent(Event event) {

        for (StaticEventCallbackMethod method : staticCallbacks.getOrDefault(event.getClass(), EMPTYCALLBACKLIST)) {
            method.invoke(event);
        }
        for (DynamicEventListener dynamicEventListener : dynamicEventListenerList) {
            dynamicEventListener.broadcastEvent(event);
        }
    }

    @Getter
    private static final class StaticEventCallbackMethod {
        private final boolean acceptCancelled;
        private final EventPriority priority;
        private final Method callback;

        public StaticEventCallbackMethod(Method callback) {
            if (callback.getParameterCount() != 1 || callback.getParameterTypes()[0].isAssignableFrom(Event.class)) {
                throw new IllegalEventCallbackMethod("Unable to load a callback method that has more than 1 parameter and parameter that does not extend Event.class", callback);
            }
            this.callback = callback;
            EventListener listener = callback.getAnnotation(EventListener.class);
            if (listener != null) {
                this.acceptCancelled = listener.listenForCancelled();
                this.priority = listener.priority();
            } else {
                this.acceptCancelled = false;
                this.priority = EventPriority.NORMAL;
            }
        }

        @SneakyThrows
        public void invoke(Event event) {
            if (!event.isCancelled() || acceptCancelled) callback.invoke(null, event);
        }
    }
}
