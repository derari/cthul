package org.cthul.observe;

import java.lang.reflect.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class EventProxy implements InvocationHandler {
    
    public static <T> Function<Herald, T> factory(Class<T> intf) {
        if (!intf.isInterface()) throw new IllegalArgumentException("interface required, got " + intf);
        return herald -> intf.cast(Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{ intf }, new EventProxy(herald)));
    }
    
    private final Herald herald;

    protected EventProxy(Herald herald) {
        this.herald = herald;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        return invoke(method.getDeclaringClass(), method.getReturnType(), method, args);
    }

    private <T, R> Object invoke(Class<T> declaringClass, Class<R> returnType, Method method, Object[] args) throws Exception {
        if (returnType == void.class || returnType == Void.class) {
            herald.announce(declaringClass, event(declaringClass, Object.class, method, args).discardResult());
            return null;
        }
        return herald.enquire(declaringClass, returnType, event(declaringClass, returnType, method, args));
    }
    
    private <T, R> Event.F0<T, R, ?> event(Class<T> declaringClass, Class<R> returnType, Method method, Object[] args) {
        return subject -> {
            try {
                return returnType.cast(method.invoke(subject, args));
            } catch (Throwable e) {
                throw asThrownException(e, method);
            }
        };
    }

    private Exception asThrownException(Throwable throwable, Method method) {
        var cause = throwable.getCause();
        if (cause instanceof Exception ex) {
            if (cause instanceof RuntimeException || Stream.of(method.getExceptionTypes()).anyMatch(t -> t.isInstance(ex))) {
                return ex;
            }
        }
        if (throwable instanceof Error err) {
            throw err;
        }
        return new IllegalArgumentException(throwable);
    }
}
