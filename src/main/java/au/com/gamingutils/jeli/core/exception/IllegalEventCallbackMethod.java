package au.com.gamingutils.jeli.core.exception;

import java.lang.reflect.Method;

public class IllegalEventCallbackMethod extends RuntimeException {

    private final Method attemptedToRegister;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public IllegalEventCallbackMethod(String message, Method attemptedToRegister) {
        super(message);
        this.attemptedToRegister = attemptedToRegister;
    }
}
