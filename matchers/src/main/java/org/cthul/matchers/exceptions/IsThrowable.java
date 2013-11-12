package org.cthul.matchers.exceptions;

import org.cthul.matchers.object.InstanceOf;
import org.cthul.matchers.object.InstanceThat;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 *
 */
public class IsThrowable {
        
    @Factory
    public static InstanceOf<Throwable> throwable() {
        return throwable(Throwable.class);
    }
    
    @Factory
    public static <T extends Throwable> InstanceOf<T> throwable(Class<T> clazz) {
        return InstanceOf.instanceOf(clazz);
    }
    
    @Factory
    public static Matcher<Object> throwable(String message) {
        return throwable(Throwable.class, message);
    }
    
    @Factory
    public static Matcher<Object> throwable(Class<? extends Throwable> clazz, String message) {
        return InstanceThat.instanceThat(clazz, 
                                 ExceptionMessage.messageContains(message));
    }
    
    @Factory
    public static Matcher<Object> throwable(Matcher<? super Throwable> matcher) {
        return throwable(Throwable.class, matcher);
    }

    @Factory
    public static <T extends Throwable> Matcher<Object> throwable(Class<T> clazz, Matcher<? super T> matcher) {
        return InstanceThat.instanceThat(clazz, matcher);
    }

    @Factory
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> Matcher<Object> throwable(Class<T> clazz, String message, Matcher<? super T> matcher) {
        return InstanceThat.instanceThat(clazz,
                                 ExceptionMessage.messageContains(message),
                                 matcher);
    }
    
    @Factory
    public static InstanceOf<Exception> exception() {
        return exception(Exception.class);
    }
    
    @Factory
    public static <T extends Exception> InstanceOf<T> exception(Class<T> clazz) {
        return throwable(clazz);
    }
    
    @Factory
    public static Matcher<Object> exception(String message) {
        return exception(Exception.class,
                         ExceptionMessage.messageContains(message));
    }
    
    @Factory
    public static Matcher<Object> exception(Matcher<? super Exception> matcher) {
        return exception(Exception.class, matcher);
    }

    @Factory
    public static Matcher<Object> exception(Class<? extends Exception> clazz, String message) {
        return throwable(clazz, message);
    }
    
    @Factory
    public static <T extends Exception> Matcher<Object> exception(Class<T> clazz, Matcher<? super T> matcher) {
        return throwable(clazz, matcher);
    }

    @Factory
    public static <T extends Exception> Matcher<Object> exception(Class<T> clazz, String message, Matcher<? super T> matcher) {
        return throwable(clazz, message, matcher);
    }
    
}
