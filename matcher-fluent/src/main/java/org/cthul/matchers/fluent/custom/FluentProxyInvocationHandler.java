package org.cthul.matchers.fluent.custom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import org.cthul.matchers.fluent.Fluent;
import org.hamcrest.Matcher;

/**
 *
 * @author derari
 */
public class FluentProxyInvocationHandler implements InvocationHandler {
    
    public static <T> T createCustomFluent(Fluent<?> impl, ClassLoader cl, Class<T> api) {
        Class[] interfaces = new Class[]{api};
        Class<?> proxyClass = Proxy.getProxyClass(cl, interfaces);
        ConcurrentHashMap<Method, Method> proxyMap = getProxyMap(proxyClass);
        InvocationHandler handler = new FluentProxyInvocationHandler(impl, proxyMap);
        return (T) Proxy.newProxyInstance(cl, interfaces, handler);
    }
    
    private static final ConcurrentHashMap<Class, ConcurrentHashMap<Method, Method>> classMap = new ConcurrentHashMap<>();
    
    protected static ConcurrentHashMap<Method, Method> getProxyMap(Class<?> proxyClass) {
        ConcurrentHashMap<Method, Method> proxyMap = classMap.get(proxyClass);
        if (proxyMap == null) {
            proxyMap = new ConcurrentHashMap<>();
            classMap.putIfAbsent(proxyClass, proxyMap);
            return classMap.get(proxyClass);
        }
        return proxyMap;
    }

    // =========================================================================
    
    private final Fluent<?> fluent;
    private final ConcurrentHashMap<Method, Method> proxyMap;

    public FluentProxyInvocationHandler(Fluent<?> fluent, ConcurrentHashMap<Method, Method> proxyMap) {
        this.fluent = fluent;
        this.proxyMap = proxyMap;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method target = getTargetMethod(method);
        try {
            Object result = target.invoke(fluent, args);
            if ((target.getModifiers() & Modifier.STATIC) != 0) {
                fluent.is((Matcher) result);
                return proxy;
            }
            if (result == fluent) {
                return proxy;
            } else {
                return result;
            }
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Method getTargetMethod(Method method) {
        Method m = proxyMap.get(method);
        if (m == null) {
            m = findTargetMethod(method);
            proxyMap.put(method, m);
        }
        return m;
    }

    private Method findTargetMethod(Method method) {
        try {
            Implementation impl = method.getAnnotation(Implementation.class);
            Class implClass;
            if (impl == null) {
                impl = method.getDeclaringClass().getAnnotation(Implementation.class);
            }
            implClass = impl != null ? impl.value() : fluent.getClass();
            
            return implClass.getMethod(method.getName(), method.getParameterTypes());
            
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    
}
