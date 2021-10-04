package au.com.gamingutils.jeli.annotation;

import au.com.gamingutils.jeli.core.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
    boolean listenForCancelled() default false;
    EventPriority priority() default EventPriority.NORMAL;
}
