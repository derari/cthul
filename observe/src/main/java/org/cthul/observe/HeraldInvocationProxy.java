package org.cthul.observe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@SuppressWarnings("java:S112")
public class HeraldInvocationProxy {

    private static final BiFunction<Herald, Class<?>, ?> CAST_OR_PROXY = HeraldInvocationProxy::castOrProxy;
    private static final BiFunction<Herald, Class<?>, ?> CAST_OR_PROXY_WO_DEFAULT = HeraldInvocationProxy::castOrProxyWithoutDefaults;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> BiFunction<Herald, Class<T>, T> castOrProxy() {
        return (BiFunction) CAST_OR_PROXY;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> BiFunction<Herald, Class<T>, T> castOrProxyWithoutDefaults() {
        return (BiFunction) CAST_OR_PROXY_WO_DEFAULT;
    }

    public static <T> T castOrProxy(Herald herald, Class<T> clazz) {
        return castOrProxy(herald, clazz, HeraldInvocationProxy.WithDefaults.class);
    }

    public static <T> T castOrProxyWithoutDefaults(Herald herald, Class<T> clazz) {
        return castOrProxy(herald, clazz, HeraldInvocationProxy.WithoutDefaults.class);
    }

    private static <T> T castOrProxy(Herald herald, Class<T> clazz, Class<? extends HeraldInvocationProxy> proxy) {
        if (clazz.isInstance(herald)) return clazz.cast(herald);
        if (clazz.isAssignableFrom(Observer.class)) return clazz.cast(new HeraldingObserver(herald));
        if (clazz.isAssignableFrom(HeraldInvocationProxy.WithDefaults.class)) return clazz.cast(new HeraldInvocationProxy.WithDefaults(herald));
        if (clazz.isAssignableFrom(HeraldInvocationProxy.WithoutDefaults.class)) return clazz.cast(new HeraldInvocationProxy.WithoutDefaults(herald));
        if (!clazz.isInterface()) return null;
        return herald.as(proxy, castOrProxy()).as(clazz);
    }

    private final Herald herald;
    private final boolean useDefaults;

    public HeraldInvocationProxy(Herald herald, boolean useDefaults) {
        this.herald = herald;
        this.useDefaults = useDefaults;
    }

    public <T> T as(Class<T> intf) {
        var proxy = Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{ intf }, this::herald);
        return intf.cast(proxy);
    }

    private Object herald(Object proxy, Method method, Object[] args) throws Exception {
        if (method.getDeclaringClass().isInstance(herald)) {
            return invoke(herald, method, args);
        }
        if (useDefaults && method.isDefault()) {
            return invokeDefaultMethod(proxy, method, args);
        }
        return herald(method.getReturnType(), method, args);
    }

    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Exception {
        var declaringClass = method.getDeclaringClass();
        var lookup = MethodHandles.privateLookupIn(declaringClass, MethodHandles.lookup());
        try {
            return lookup.unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
        } catch (Throwable t) {
            if (t instanceof Exception ex) throw ex;
            if (t instanceof Error err) throw err;
            throw new InvocationTargetException(t);
        }
    }

    private <R> R herald(Class<R> returnType, Method method, Object[] args) throws Exception {
        if (returnType == void.class || returnType == Void.class) {
            herald.announce(method.getDeclaringClass(), announcement(method, args));
            return null;
        }
        return herald.inquire(method.getDeclaringClass(), returnType, inquiry(returnType, method, args));
    }

    private <T> Event.Announcement<T, Exception> announcement(Method method, Object[] args) {
        return target -> invoke(target, method, args);
    }

    private <T, R> Event.Inquiry<T, R, Exception> inquiry(Class<R> returnType, Method method, Object[] args) {
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
        return "HeraldInvocationProxy"
                + (useDefaults ? ".WithDefaults" : ".WithoutDefaults")
                + "@" + Integer.toHexString(hashCode()) + "(" + herald + ")";
    }

    public static class WithDefaults extends HeraldInvocationProxy {

        public WithDefaults(Herald herald) {
            super(herald, true);
        }
    }

    public static class WithoutDefaults extends HeraldInvocationProxy {

        public WithoutDefaults(Herald herald) {
            super(herald, false);
        }
    }
}
