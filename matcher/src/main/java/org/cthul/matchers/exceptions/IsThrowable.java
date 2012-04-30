package org.cthul.matchers.exceptions;

import org.cthul.matchers.InstanceThat;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;

/**
 *
 * @author Arian Treffer
 */
public class IsThrowable {
        
    @Factory
    public static <T> Matcher<T> throwable() {
        return throwable(Throwable.class);
    }
    
    @Factory
    public static <T> Matcher<T> throwable(Class<? extends Throwable> clazz) {
        return IsInstanceOf.instanceOf(clazz);
    }
    
    @Factory
    public static <T> Matcher<T> throwable(String message) {
        return throwable(Throwable.class, message);
    }
    
    @Factory
    public static <T> Matcher<T> throwable(Class<? extends Throwable> clazz, String message) {
        return InstanceThat.instanceThat(clazz, 
                                 ExceptionMessage.messageContains(message));
    }
    
    @Factory
    public static <T> Matcher<T> throwable(Matcher<? super Throwable> matcher) {
        return throwable(Throwable.class, matcher);
    }

    @Factory
    public static <T, T2 extends Throwable> Matcher<T> throwable(Class<T2> clazz, Matcher<? super T2> matcher) {
        return InstanceThat.instanceThat(clazz, matcher);
    }

    @Factory
    @SuppressWarnings("unchecked")
    public static <T, T2 extends Throwable> Matcher<T> throwable(Class<T2> clazz, String message, Matcher<? super T2> matcher) {
        return InstanceThat.instanceThat(clazz,
                                 ExceptionMessage.messageContains(message),
                                 matcher);
    }
    
    @Factory
    public static <T> Matcher<T> exception() {
        return exception(Exception.class);
    }
    
    @Factory
    public static <T> Matcher<T> exception(Class<? extends Exception> clazz) {
        return throwable(clazz);
    }
    
    @Factory
    public static <T> Matcher<T> exception(String message) {
        return exception(Exception.class,
                         ExceptionMessage.messageContains(message));
    }
    
    @Factory
    public static <T> Matcher<T> exception(Matcher<? super Exception> matcher) {
        return exception(Exception.class, matcher);
    }

    @Factory
    public static <T> Matcher<T> exception(Class<? extends Exception> clazz, String message) {
        return throwable(clazz, message);
    }
    
    @Factory
    public static <T, T2 extends Exception> Matcher<T> exception(Class<T2> clazz, Matcher<? super T2> matcher) {
        return throwable(clazz, matcher);
    }

    @Factory
    public static <T, T2 extends Exception> Matcher<T> exception(Class<T2> clazz, String message, Matcher<? super T2> matcher) {
        return throwable(clazz, message, matcher);
    }
    
}
