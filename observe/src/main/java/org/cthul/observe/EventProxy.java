package org.cthul.observe;

import java.lang.reflect.*;
import java.util.function.Function;

public class EventProxy implements InvocationHandler {
    
    public static <T> Function<Herald, T> factory(Class<T> intf) {
        if (!intf.isInterface()) throw new IllegalArgumentException("interface required, got " + intf);
        return herald -> (T) Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{ intf }, new EventProxy(herald));
    }
    
    private final Herald herald;

    protected EventProxy(Herald herald) {
        this.herald = herald;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var returnType = method.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            herald.announce(method.getDeclaringClass(), event(method, args).discardResult());
            return null;
        }
        return herald.enquire(method.getDeclaringClass(), returnType, event(method, args));
    }
    
    private Event.F0 event(Method method, Object[] args) {
        return subject -> {
            try {
                return method.invoke(subject, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                var cause = e.getCause();
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                } 
                if (cause instanceof Error) {
                    throw (Error) cause;
                } 
                throw e;
            }
        };
    }
}
