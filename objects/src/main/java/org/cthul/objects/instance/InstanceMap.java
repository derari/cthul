package org.cthul.objects.instance;

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
        Inject inject = clazz.getAnnotation(Inject.class);
        if (inject != null) return new InjectFactory<>(clazz, inject);
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
            for (Init i: config.injects) i.init(this);
            for (Init i: config.inits) i.init(this);
            if (!config.lateInits.isEmpty() || 
                    !config.lateInjects.isEmpty()) {
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
            for (Init i: oc.lateInjects) i.init(this);
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
        final Class<?> clazz = o.getClass();
        final Inject classInject = defaultInject(clazz);
        for (Field f: clazz.getFields()) {
            Inject inject = f.getAnnotation(Inject.class);
            Initialize init = f.getAnnotation(Initialize.class);
            if (inject != null) {
                if (init != null) inject = merge(inject, NO_UPDATE);
                Init i = collectField(config, o, f, merge(inject,classInject));
                addInject(i, config, inject.late());
            } else if (init != null) {
                Init i = collectField(config, o, f, init);
                addInit(i, config, init.late());
            }
        }
    }

    protected Init collectField(ObjectConfig config, Object o, Field f, Inject inject) {
        String key = propertyKey(inject.value(), f.getType());
        Property prop = new FieldProperty(o, f);
        if (inject.autoUpdate()) addAutoUpdated(key, prop);
        return new PropertyInit(prop, inject, f.getType());
    }
    
    protected Init collectField(ObjectConfig config, Object o, Field f, Initialize init) {
        return new FieldValueInit(o, f);
    }
    
    protected void collectMethods(ObjectConfig config, Object o) {
        final Class<?> clazz = o.getClass();
        final Inject classInject = defaultInject(clazz);
        for (Method m: clazz.getMethods()) {
            Inject inject = m.getAnnotation(Inject.class);
            if (inject != null) {
                collectMethod(config, o, m, merge(inject, classInject));
            }
            Initialize init = m.getAnnotation(Initialize.class);
            if (init != null) {
                collectMethod(config, o, m, init, classInject);
            }
        }
    }

    protected void collectMethod(ObjectConfig config, Object o, Method m, Inject inject) {
        SetterInit init = collectParams(o, m, inject, false);
        addInject(init, config, inject.late());
    }
    
    protected void collectMethod(ObjectConfig config, Object o, Method m, Initialize initialize, Inject classInject) {
        SetterInit init = collectParams(o, m, merge(NO_UPDATE, classInject), true);
        addInit(init, config, initialize.late());
    }
    
    protected SetterInit collectParams(Object o, Method m, Inject mInject, boolean initResult) {
        final SetterInit init = new SetterInit(o, m, initResult);
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
        init.addParameter(prop, type, inject);
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

    protected void addInject(Init init, ObjectConfig config, boolean late) {
        if (late) {
            config.lateInjects.add(init);
        } else {
            config.injects.add(init);
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
    
    public class InjectFactory<T> implements Factory<T> {
        
        protected final Class<T> clazz;
        protected final Inject inject;

        public InjectFactory(Class<T> clazz, Inject inject) {
            this.clazz = clazz;
            this.inject = inject;
        }

        @Override
        public T create(InstanceMap map) {
            if (inject.late()) throw unsupportedOption("late");
            if (inject.create()) throw unsupportedOption("create");
            if (!inject.autoUpdate()) throw unsupportedOption("autoUpdate");
            String key = inject.value();
            if (key != null) {
                T t = (T) map.get(key);
                if (t != null) return t;
            }
            Class<T> impl = (Class) inject.impl();
            if (impl == Void.class) impl = clazz;
            String f = inject.factory();
            if (f.isEmpty()) {
                return new ReflectiveFactory<>(impl).create(map);
            } else {
                return new ReflectiveProxyFactory<>(impl, f).create(map);
            }
        }
        
        protected IllegalArgumentException unsupportedOption(String name) {
            return new IllegalArgumentException(
                    "Unsupported option in " + clazz.getName() + 
                    ": @Inject(" + name + ")");
        }
    }
    
    public class ReflectiveFactory<T> implements Factory<T> {

        protected final Constructor<T> constructor;
        protected final InjectedParameters ip;

        public ReflectiveFactory(Constructor<T> c, InjectedParameters ip) {
            this.constructor = c;
            this.ip = ip;
        }
        
        public ReflectiveFactory(Class<T> clazz) {
            constructor = selectConstructor(clazz);
            ip = initializeParameters(constructor);
        }
        
        private Constructor<T> selectConstructor(Class<T> clazz) {
            Constructor<T> choice = null;
            Constructor<T> ambiguous = null;
            boolean choiceIsAnnotated = false;
            int choiceParamC = -1;
            for (Constructor<T> c: (Constructor<T>[]) clazz.getConstructors()) {
                // c is always public
//                if ((c.getModifiers() & Modifier.PUBLIC) == 0) continue;
                if (c.getAnnotation(Inject.class) != null) {
                    if (choiceIsAnnotated) {
                        throw ambiguous(choice, c);
                    }
                    choice = c;
                    choiceIsAnnotated = true;
                } else {
                    if (choiceIsAnnotated) continue;
                    if (choice == null) {
                        choice = c;
                        choiceParamC = c.getParameterTypes().length;
                    } else if (c.getParameterTypes().length == 0) {
                        choice = c;
                        choiceParamC = 0;
                    } else {
                        ambiguous = c;
                    }
                }
            }
            if (choiceParamC > 0 && ambiguous != null && !choiceIsAnnotated) {
                throw ambiguous(choice, ambiguous);
            }
            if (choice == null) {
                throw new IllegalArgumentException(
                        "No suitable constructor for: " + clazz);
            }
            return choice;
        }
        
        private IllegalArgumentException ambiguous(Constructor<?> c1, Constructor<?> c2) {
            return new IllegalArgumentException(
                    "Multiple possible constructors: " + c1 + " and " + c2);
        }
        
        private InjectedParameters initializeParameters(Constructor<T> c) {
            final Class<?>[] pTypes = c.getParameterTypes();
            if (pTypes.length == 0) return null;
            final Annotation[][] pAts = c.getParameterAnnotations();
            final Inject classInject = defaultInject(c.getDeclaringClass());
            final Inject cInject = merge(c.getAnnotation(Inject.class), classInject);
            final InjectedParameters params = new InjectedParameters();
            for (int i = 0; i < pTypes.length; i++) {
                Inject pInject = null;
                for (Annotation at: pAts[i]) {
                    if (at instanceof Inject) pInject = (Inject) at;
                }
                pInject = merge(pInject, cInject);
                params.addParameter(pTypes[i], pInject);
            }
            return params;
        }
        
        @Override
        public T create(InstanceMap map) {
            try {
                Object[] args = ip != null ? ip.getArguments(map) : null;
                return constructor.newInstance(args);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
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
        
        protected final List<Init> injects = new ArrayList<>();
        
        protected final List<Init> inits = new ArrayList<>();
        
        protected final List<Init> lateInjects = new ArrayList<>();
        
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
    
    protected static class FieldValueInit extends Init {

        protected final Object o;
        protected final Field f;

        public FieldValueInit(Object o, Field f) {
            this.o = o;
            this.f = f;
        }
        
        @Override
        public void init(InstanceMap map) {
            try {
                map.initialize(f.get(o));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    protected static class InjectedParameters {
        
        protected final List<Inject> paramInjects = new ArrayList<>();
        protected final List<Class<?>> paramTypes = new ArrayList<>();

        protected void addParameter(Class<?> type, Inject inject) {
            paramTypes.add(type);
            paramInjects.add(inject);
        }
        
        public Object[] getArguments(InstanceMap map) {
            int paramCount = paramTypes.size();
            if (paramCount == 0) return null;
            Object[] args = new Object[paramCount];
            for (int i = 0; i < paramCount; i++) {
                args[i] = getArgument(map, i);
            }
            return args;
        }
    
        public Object getArgument(InstanceMap map, int i) {
            return map.get(paramTypes.get(i), paramInjects.get(i));
        }
        
    }
    
    protected static class SetterInit extends Init {
        
        protected final List<ParamProperty> params = new ArrayList<>();
        protected final InjectedParameters ip = new InjectedParameters();
        protected final Object instance;
        protected final Method method;
        protected final boolean initResult;

        public SetterInit(Object instance, Method method, boolean initResult) {
            this.instance = instance;
            this.method = method;
            this.initResult = initResult;
        }

        protected void addParameter(ParamProperty pp, Class<?> type, Inject inject) {
            params.add(pp);
            ip.addParameter(type, inject);
        }

        @Override
        public void init(InstanceMap map) {
            Object[] args = ip.getArguments(map);
            try {
                Object r = method.invoke(instance, args);
                if (r != null && initResult) {
                    map.initialize(r);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            for (Property pp: params) pp.initialized = true;
        }
        
    }
    
    protected Inject defaultInject(Class<?> clazz) {
        Inject classInject = null;
        DefaultInject def = clazz.getAnnotation(DefaultInject.class);
        if (def != null) classInject = def.value();
        return classInject;
    }

    protected static Inject merge(final Inject inject, final Inject base) {
        if (base == null || base == DEFAULT_INJECT) {
            if (inject == null) return DEFAULT_INJECT;
            return inject;
        }
        if (inject == null || inject == DEFAULT_INJECT) return base;
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
 
    protected static final Inject DEFAULT_INJECT = new Inject() {
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
        public boolean autoUpdate() { return true; }
        @Override
        public Class<? extends Annotation> annotationType() { return Inject.class; }
    };
    
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
