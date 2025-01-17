package org.cthul.observe;

import java.lang.reflect.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class HeraldInvocationProxy {

    private static final BiFunction<Herald, Class<HeraldInvocationProxy>, HeraldInvocationProxy> NEW = (h, c) -> new HeraldInvocationProxy(h);
    private static final BiFunction<Herald, Class<?>, ?> CAST_OR_PROXY = HeraldInvocationProxy::castOrProxy;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> BiFunction<Herald, Class<T>, T> castOrProxy() {
        return (BiFunction) CAST_OR_PROXY;
    }

    public static <T> Function<Herald, T> castOrProxy(Class<T> clazz) {
        return herald -> castOrProxy(herald, clazz);
    }

    public static <T> T castOrProxy(Herald herald, Class<T> clazz) {
        if (clazz.isInstance(herald)) return clazz.cast(herald);
        if (clazz.isAssignableFrom(Observer.class)) return clazz.cast(new HeraldingObserver(herald));
        return herald.as(HeraldInvocationProxy.class, NEW).as(clazz);
    }

    private final Herald herald;

    public HeraldInvocationProxy(Herald herald) {
        this.herald = herald;
    }

    public <T> T as(Class<T> intf) {
        var proxy = Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{ intf }, this::herald);
        return intf.cast(proxy);
    }

    private Object herald(Object proxy, Method method, Object[] args) throws Exception {
        if (method.getDeclaringClass().isInstance(herald)) {
            return invoke(herald, method, args);
        }
        return herald(method.getReturnType(), method, args);
    }

    private <R> R herald(Class<R> returnType, Method method, Object[] args) throws Exception {
        if (returnType == void.class || returnType == Void.class) {
            herald.announce(method.getDeclaringClass(), event(method, args));
            return null;
        }
        return herald.inquire(method.getDeclaringClass(), returnType, event(returnType, method, args));
    }

    private <T> Event.Announcement<T, Exception> event(Method method, Object[] args) {
        return target -> invoke(target, method, args);
    }

    private <T, R> Event.Inquiry<T, R, Exception> event(Class<R> returnType, Method method, Object[] args) {
        return target -> returnType.cast(invoke(target, method, args));
    }

    private Object invoke(Object instance, Method method, Object[] args) throws Exception {
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException e) {
            throw getThrownException(method, e);
        }
    }

    private Exception getThrownException(Method method, InvocationTargetException ite) {
        var cause = ite.getCause();
        if (cause instanceof RuntimeException re) {
            return re;
        }
        if (cause instanceof Exception ex && isThrown(method, ex)) {
            return ex;
        }
        if (cause instanceof Error err) {
            throw err;
        }
        return ite;
    }

    private boolean isThrown(Method method, Exception ex) {
        return Stream.of(method.getExceptionTypes()).anyMatch(t -> t.isInstance(ex));
    }

    @Override
    public String toString() {
        return "HeraldInvocationProxy@" + Integer.toHexString(hashCode()) + "(" + herald + ")";
    }
}
