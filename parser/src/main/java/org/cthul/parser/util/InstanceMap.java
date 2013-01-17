package org.cthul.parser.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class InstanceMap {
    
    protected static final Ambiguous AMBIGUOUS = new Ambiguous();
    public static final Class<?> ANY_INTERFACE = AnyInterface.class;
    public static final Class[] NO_LIMIT = null;
    public static final Class[] LIMIT_NO_INTERFACES = {Object.class};
    public static final Class[] LIMIT_INTERFACES_ONLY = {ANY_INTERFACE};

    private final Map<String, Object> map = new HashMap<>();
    private final Map<String, List<Property>> properties = new HashMap<>();
    private final Set<Object> initializing = new HashSet<>();
    private final List<ObjectConfig> lateInits = new ArrayList<>();

    @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
    public InstanceMap() {
        put(getClass(), this, InstanceMap.class, ANY_INTERFACE);
    }
    
    @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
    protected InstanceMap(InstanceMap source) {
        this.map.putAll(source.map);
        this.properties.putAll(source.properties);
        replace(getClass(), this, InstanceMap.class, ANY_INTERFACE);
    }
    
    /**
     * Associates the value with the key.
     * @param key
     * @param value
     * @return old value or null
     */
    public Object put(String key, Object value) {
        Object old = map.put(key, value);
        updateProperties(key, value);
        return old;
    }
    
    @SuppressWarnings("unchecked")
    protected final <T> T t_put(String key, Object value) {
        return (T) put(key, value);
    }
    
    protected void updateProperties(String key, Object value) {
        List<Property> l = properties.get(key);
        if (l != null && !l.isEmpty()) {
            if (isAmbiguous(value)) {
                throw new IllegalArgumentException(
                    "Auto-update failed: value for '" + key + "' is ambiguous");
            }
            for (Property p: l) p.update(value);
        }
    }
    
    protected void addAutoUpdated(String key, Property p) {
        List<Property> l = properties.get(key);
        if (l == null) {
            l = new ArrayList<>();
            properties.put(key, l);
        }
        l.add(p);
    }
    
    /**
     * @param clazz
     * @param value
     * @param limit
     * @return {@code value} if {@code clazz} is not in limit,
     *         old value if inserted successfully, or
     *         {@link #AMBIGUOUS} if key was already in use
     */
    public Object put(Class<?> clazz, Object value, Class... limit) {
        if (!checkLimit(clazz, limit)) return value;
        
        put(clazz.getSuperclass(), value, limit);
        for (Class<?> iface: clazz.getInterfaces())
            put(iface, value, limit);
        
        String key = key(clazz);
        Object old = unsafeGet(key);
        if (old != null && old != value) {
            if (!isAmbiguous(old)) 
                put(key, AMBIGUOUS);
            return AMBIGUOUS;
        }
        
        return put(key, value);
    }
    
    public Object put(Class<?> clazz, Object value) {
        return put(clazz, value, NO_LIMIT);
    }
    
    /**
     * @param clazz
     * @param value
     * @param limit
     * @return {@code null} if {@code clazz} is not in limit,
     *         old value if inserted successfully
     */
    public <T> T replace(Class<T> clazz, Object value, Class... limit) {
        if (!checkLimit(clazz, limit)) return null;
                
        replace(clazz.getSuperclass(), value, limit);
        for (Class<?> i: clazz.getInterfaces())
            replace(i, value, limit);

        String key = key(clazz);
        return t_put(key, value);
    }
    
    public <T> T replace(Class<T> clazz, Object value) {
        return replace(clazz, value, NO_LIMIT);
    }
    
    protected boolean checkLimit(Class<?> clazz, Class[] limit) {
        if (clazz == null) return false;
        if (limit == null) return true;
        final boolean isInterface = clazz.isInterface();
        for (Class<?> l: limit) {
            if (isInterface) {
                if (l == Object.class) continue;
                if (l == AnyInterface.class) return true;
            }
            if (l.isAssignableFrom(clazz)) return true;
        }
        return false;
    }
    
    public Object get(String key) {
        Object v = unsafeGet(key);
        if (isAmbiguous(v)) {
            throw new IllegalArgumentException(
                    "Value for '" + key + "' is ambiguous");
        }
        ensureNotInitializing(v);
        return v;
    }
    
    @SuppressWarnings("unchecked")
    protected final <T> T t_get(String key) {
        return (T) get(key);
    }
    
    protected Object unsafeGet(String key) {
        return map.get(key);
    }
    
    public <T> T get(Class<T> clazz) {
        return t_get(key(clazz));
    }
    
    public <T> T putNew(String key, Factory<? extends T> factory) {
        T t = factory.create(this);
        put(key, t);
        initialize(t);
        return t;
    }
    
    public <T> T putNew(Class<?> clazz, Factory<? extends T> factory) {
        T t = factory.create(this);
        put(clazz, t);
        initialize(t);
        return t;
    }
    
    public <T> T getOrCreate(String key, Factory<T> factory) {
        T t = t_get(key);
        if (t == null) {
            t = putNew(key, factory);
        }
        return t;
    }
    
    public <T> T getOrCreate(String key, Class<T> impl) {
        return getOrCreate(key, f(impl));
    }
    
    public <T> T getOrCreate(Class<T> clazz, Factory<? extends T> factory) {
        T t = get(clazz);
        if (t == null) {
            t = putNew(clazz, factory);
        }
        return t;
    }
    
    public <T> T getOrCreate(Class<T> clazz, Class<? extends T> impl) {
        T t = get(clazz);
        if (t == null) {
            t = putNew(clazz, f(impl));
        }
        return t;
    }
    
    public <T> T getOrCreate(Class<T> clazz) {
        return getOrCreate(clazz, clazz);
    }
    
    public <T> T get(Class<T> clazz, Inject inject) {
        String key = propertyKey(inject.value(), clazz);
        T t = t_get(key);
        if (t != null) return t;
        if (!propertyCreate(inject)) return null;
        Class<T> impl = propertyImpl(inject.impl(), clazz);
        Factory<T> f = propertyFactory(impl, inject.factory());
        if (inject.value().isEmpty()) {
            return putNew(clazz, f);
        } else {
            return putNew(key, f);
        }
    }
    
    public Object remove(String key) {
        return put(key, null);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T remove(Class<T> clazz) {
        return remove(clazz, NO_LIMIT);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T remove(Class<T> clazz, Class... limit) {
        T old = get(clazz);
        removeValue(old, clazz, limit);
        return old;
    }
    
    protected void removeValue(Object value, Class<?> clazz, Class... limit) {
        if (!checkLimit(clazz, limit)) return;
        
        removeValue(value, clazz.getSuperclass(), limit);
        for (Class<?> iface: clazz.getInterfaces())
            removeValue(value, iface, limit);
        
        String key = key(clazz);
        if (unsafeGet(key) == value) put(key, null);
    }
    
    protected String key(Class<?> clazz) {
        if (clazz == null) return null;
        return clazz.getCanonicalName();
    }
    
    protected <T> Factory<T> f(Class<T> clazz) {
        return new ReflectiveFactory<>(clazz);
    }
    
    protected boolean isAmbiguous(Object v) {
        return v instanceof Ambiguous;
    }
    
    public <T> T initialize(T o) {
        if (!initializing.add(o)) {
            throw new IllegalArgumentException(
                    "Circular dependency at " + o + ", "
                    + "try defining some properties as `late`");
        }
        ObjectConfig config;
        try {
            config = config(o);
            for (Init i: config.propInits) i.init(this);
            for (Init i: config.inits) i.init(this);
            if (!config.lateInits.isEmpty() || 
                    !config.latePropInits.isEmpty()) {
                lateInits.add(config);
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().endsWith(IS_INITIALIZING)) {
                throw new IllegalArgumentException(
                        "Circular dependency at " + o + ", "
                        + "try defining some properties as `late`");                
            }
            throw e;
        } finally {
            initializing.remove(o);
            if (initializing.isEmpty()) lateInitialize();
        }
        return o;
    }
    
    protected void lateInitialize() {
        for (ObjectConfig oc: lateInits) {
            for (Init i: oc.latePropInits) i.init(this);
            for (Init i: oc.lateInits) i.init(this);
        }
        lateInits.clear();
    }
    
    protected static final String IS_INITIALIZING = " is currently initializing";
    
    protected void ensureNotInitializing(Object o) {
        if (initializing.contains(o)) {
            throw new IllegalArgumentException(o + IS_INITIALIZING);            
        }
    }

    protected ObjectConfig config(Object o) {
        ObjectConfig config = new ObjectConfig();
        collectFields(config, o);
        collectMethods(config, o);
        return config;
    }

    protected void collectFields(ObjectConfig config, Object o) {
        Class<?> clazz = o.getClass();
        for (Field f: clazz.getFields()) {
            Inject inject = f.getAnnotation(Inject.class);
            if (inject != null) {
                Init i = collectField(config, o, f, inject);
                addProp(i, config, inject.late());
            }
            Initialize init = f.getAnnotation(Initialize.class);
            if (init != null) {
                Init i = collectField(config, o, f, NO_UPDATE);
                addProp(i, config, init.late());
            }
        }
    }

    protected Init collectField(ObjectConfig config, Object o, Field f, Inject inject) {
        String key = propertyKey(inject.value(), f.getType());
        Property prop = new FieldProperty(o, f);
        if (inject.autoUpdate()) addAutoUpdated(key, prop);
        return new PropertyInit(prop, inject, f.getType());
    }
    
    protected void collectMethods(ObjectConfig config, Object o) {
        Class<?> clazz = o.getClass();
        for (Method m: clazz.getMethods()) {
            Inject inject = m.getAnnotation(Inject.class);
            if (inject != null) {
                collectMethod(config, o, m, inject);
            }
            Initialize init = m.getAnnotation(Initialize.class);
            if (init != null) {
                collectMethod(config, o, m, init);
            }
        }
    }

    protected void collectMethod(ObjectConfig config, Object o, Method m, Inject inject) {
        SetterInit init = collectParams(o, m, inject);
        addProp(init, config, inject.late());
    }
    
    protected void collectMethod(ObjectConfig config, Object o, Method m, Initialize initialize) {
        SetterInit init = collectParams(o, m, NO_UPDATE);
        addInit(init, config, initialize.late());
    }
    
    protected SetterInit collectParams(Object o, Method m, Inject mInject) {
        final SetterInit init = new SetterInit(o, m);
        final Annotation[][] paramAts = m.getParameterAnnotations();
        final Class[] paramCls = m.getParameterTypes();
        for (int i = 0; i < paramAts.length; i++) {
            Inject pInject = null;
            for (Annotation at: paramAts[i])
                if (at instanceof Inject) pInject = (Inject) at;
            if (pInject == null) pInject = mInject;
            else pInject = paramMerge(pInject, mInject, m, i);
            collectParam(init, o, m, pInject, i, paramAts[i], paramCls[i]);
        }
        return init;
    }
    
    protected Inject paramMerge(Inject param, Inject method, Method m, int pId) {
        if (param.late() && !method.late()) {
            throw new IllegalArgumentException(
                    "Parameter " + pId + " of " + m + " is flagged as lazy, "
                    + "but method is not.");
        }
        return merge(param, method);
    }
    
    protected void collectParam(SetterInit init, Object o, Method m, Inject inject, int paramId, Annotation[] ats, Class<?> type) {
        String key = propertyKey(inject.value(), type);
        ParamProperty prop = new ParamProperty(o, m, paramId);
        init.params.add(prop);
        init.classes.add(type);
        init.injects.add(inject);
        if (inject.autoUpdate()) addAutoUpdated(key, prop);
    }

    protected String propertyKey(String key, Class<?> clazz) {
        if (key != null && !key.isEmpty()) return key;
        return key(clazz);
    }
    
    protected boolean propertyCreate(Inject inject) {
        return inject.create() || 
                inject.impl() != Void.class  ||
                !inject.factory().isEmpty();
    }
    
    @SuppressWarnings("unchecked")
    protected <T> Class<T> propertyImpl(Class<?> impl, Class<?> clazz) {
        if (impl != Void.class) return (Class) impl;
        return (Class) clazz;
    }
    
    protected <T> Factory<T> propertyFactory(Class<T> impl, String factory) {
        if (factory == null || factory.isEmpty()) {
            return new ReflectiveFactory<>(impl);
        } else 
            return new ReflectiveProxyFactory<>(impl, factory);
    }

    protected void addProp(Init init, ObjectConfig config, boolean late) {
        if (late) {
            config.latePropInits.add(init);
        } else {
            config.propInits.add(init);
        }
    }
    
    protected void addInit(Init init, ObjectConfig config, boolean late) {
        if (late) {
            config.lateInits.add(init);
        } else {
            config.inits.add(init);
        }
    }

    protected static class Ambiguous implements Serializable {
    }
    
    public interface AnyInterface {
    }
    
    public interface Factory<T> {
        
        T create(InstanceMap map);
        
    }
    
    public class ReflectiveFactory<T> implements Factory<T> {

        protected final Class<T> clazz;

        public ReflectiveFactory(Class<T> clazz) {
            this.clazz = clazz;
        }
        
        @Override
        public T create(InstanceMap map) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    public class ReflectiveProxyFactory<T> implements Factory<T> {

        protected final Class<T> clazz;
        protected final String factory;

        public ReflectiveProxyFactory(Class<T> clazz, String factory) {
            this.clazz = clazz;
            this.factory = factory;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T create(InstanceMap map) {
            Object o = getFactory(clazz, factory);
            if (o instanceof Factory) {
                return ((Factory<T>) o).create(map);
            } else {
                return (T) o;
            }
        }
    }
    
    public static Object getFactory(Class<?> clazz, String factory) {
        try {
            Field f = clazz.getField(factory);
            checkAccess(f.getModifiers(), factory);
            return f.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "Error accessing " + clazz + "#" + factory, e);
        } catch (NoSuchFieldException e) {
        }
        try {
            Method m = clazz.getMethod(factory);
            checkAccess(m.getModifiers(), factory);
            return m.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "Error invoking " + clazz + "#" + factory + "()", e);
        } catch (NoSuchMethodException e) {
        }
        throw new IllegalArgumentException(clazz + " has no member " + factory);
    }
    
    private static void checkAccess(int modifier, String factory) throws IllegalArgumentException {
        if ((modifier & Modifier.PUBLIC) == 0) {
            throw new IllegalArgumentException(
                    factory + " is not public");
        }
        if ((modifier & Modifier.STATIC) == 0) {
            throw new IllegalArgumentException(
                    factory + " is not static");
        }
    }
    
    protected class ObjectConfig {
        
        protected final List<Init> propInits = new ArrayList<>();
        
        protected final List<Init> inits = new ArrayList<>();
        
        protected final List<Init> latePropInits = new ArrayList<>();
        
        protected final List<Init> lateInits = new ArrayList<>();
        
    }
    
    protected static abstract class Property {
        
        private boolean initialized = false;

        protected boolean isInitialized() {
            return initialized;
        }
        
        public abstract void update(Object newValue);
        
    }
    
    protected static class FieldProperty extends Property {
        
        protected final Object instance;
        protected final Field field;

        public FieldProperty(Object instance, Field field) {
            this.instance = instance;
            this.field = field;
        }

        @Override
        public void update(Object newValue) {
            if (!isInitialized()) return;
            try {
                field.set(instance, newValue);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    protected static class ParamProperty extends Property {
        
        protected final Object instance;
        protected final Method method;
        protected final int paramCount;
        protected final int paramId;

        public ParamProperty(Object instance, Method method, int paramId) {
            this.instance = instance;
            this.method = method;
            this.paramId = paramId;
            this.paramCount = method.getParameterTypes().length;
        }

        @Override
        public void update(Object newValue) {
            if (!isInitialized()) return;
            try {
                Object[] args = new Object[paramCount];
                args[paramId] = newValue;
                method.invoke(instance, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    protected static abstract class Init {
        
        public abstract void init(InstanceMap map);
        
    }
    
    protected static class PropertyInit extends Init {
        
        protected final Property prop;
        protected final Inject inject;
        protected final Class<?> clazz;

        public PropertyInit(Property prop, Inject inject, Class<?> clazz) {
            this.prop = prop;
            this.inject = inject;
            this.clazz = clazz;
        }

        @Override
        public void init(InstanceMap map) {
            Object value = getInitialValue(map);
            prop.initialized = true;
            if (value != null) prop.update(value);
        }

        protected Object getInitialValue(InstanceMap map) {
            return map.get(clazz, inject);
        }
        
    }
    
    protected static class SetterInit extends Init {
        
        protected final List<ParamProperty> params = new ArrayList<>();
        protected final List<Inject> injects = new ArrayList<>();
        protected final List<Class<?>> classes = new ArrayList<>();
        protected final Object instance;
        protected final Method method;

        public SetterInit(Object instance, Method method) {
            this.instance = instance;
            this.method = method;
        }

        @Override
        public void init(InstanceMap map) {
            int paramCount = method.getParameterTypes().length;
            Object[] args = new Object[paramCount];
            for (int i = 0; i < paramCount; i++) {
                args[i] = getInitialValue(map, i);
            }
            try {
                method.invoke(instance, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            for (Property pp: params) pp.initialized = true;
        }
        
        protected Object getInitialValue(InstanceMap map, int i) {
            return map.get(classes.get(i), injects.get(i));
        }
        
    }
    
    protected static Inject merge(final Inject inject, final Inject base) {
        return new Inject() {
            private boolean isValue(String s) {
                return s != null && !s.isEmpty();
            }
            @Override
            public String value() {
                String v = inject.value();
                return isValue(v) ? v : base.value();
            }
            @Override
            public boolean create() {
                return inject.create() || base.create();
            }
            @Override
            public Class<?> impl() {
                Class<?> i = inject.impl();
                return i != Void.class ? i : base.impl();
            }
            @Override
            public String factory() {
                String f = inject.factory();
                return isValue(f) ? f : base.factory();
            }
            @Override
            public boolean late() {
                return inject.late() || base.late();
            }
            @Override
            public boolean autoUpdate() {
                return inject.autoUpdate() && base.autoUpdate();
            }
            @Override
            public Class<? extends Annotation> annotationType() {
                return Inject.class;
            }
        };
    }
 
//    protected static final Inject DEFAULT_INJECT = new Inject() {
//        @Override
//        public String value() { return ""; }
//        @Override
//        public boolean putNew() { return false; }
//        @Override
//        public Class<?> impl() { return Void.class; }
//        @Override
//        public String factory() { return ""; }
//        @Override
//        public boolean late() { return false; }
//        @Override
//        public boolean autoUpdate() { return true; }
//        @Override
//        public Class<? extends Annotation> annotationType() { return Inject.class; }
//    };
    
    protected static final Inject NO_UPDATE = new Inject() {
        @Override
        public String value() { return ""; }
        @Override
        public boolean create() { return false; }
        @Override
        public Class<?> impl() { return Void.class; }
        @Override
        public String factory() { return ""; }
        @Override
        public boolean late() { return false; }
        @Override
        public boolean autoUpdate() { return false; }
        @Override
        public Class<? extends Annotation> annotationType() { return Inject.class; }
    };
    
}
