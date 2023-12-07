package org.cthul.observe;

import java.lang.reflect.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class EventProxy implements InvocationHandler {
    
    public static <T> Function<Herald, T> factory(Class<T> intf) {
        if (!intf.isInterface()) throw new IllegalArgumentException("interface required, got " + intf);
        return herald -> new EventProxy(herald).as(intf);
    }

    private final Herald herald;

    protected EventProxy(Herald herald) {
        this.herald = herald;
    }

    public <T> T as(Class<T> intf) {
        var proxy = Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{ intf }, this);
        return intf.cast(proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        return invoke(method.getReturnType(), method, args);
    }

    private <R> Object invoke(Class<R> returnType, Method method, Object[] args) throws Exception {
        if (returnType == void.class || returnType == Void.class) {
            herald.announce(method.getDeclaringClass(), event(method, args));
            return null;
        }
        return herald.enquire(method.getDeclaringClass(), returnType, event(returnType, method, args));
    }
    
    private <T> Event.C0<T, Exception> event(Method method, Object[] args) {
        return subject -> {
            try {
                method.invoke(subject, args);
            } catch (InvocationTargetException e) {
                throw asThrownException(e, method);
            }
        };
    }

    private <T, R> Event.F0<T, R, Exception> event(Class<R> returnType, Method method, Object[] args) {
        return subject -> {
            try {
                return returnType.cast(method.invoke(subject, args));
            } catch (InvocationTargetException e) {
                throw asThrownException(e, method);
            }
        };
    }

    private Exception asThrownException(InvocationTargetException ite, Method method) {
        var cause = ite.getCause();
        if (cause instanceof RuntimeException || isDeclaredException(method, cause)) {
            return (Exception) cause;
        }
        if (cause instanceof Error err) {
            throw err;
        }
        return ite;
    }

    private boolean isDeclaredException(Method method, Throwable cause) {
        return cause instanceof Exception
                && Stream.of(method.getExceptionTypes()).anyMatch(t -> t.isInstance(cause));
    }
}
