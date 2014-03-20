package org.cthul.objects.reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.cthul.objects.instance.UniqueValueCache;

public class VirtualSwitch {
    
    private static final UniqueValueCache<Class, VirtualSwitch> INSTANCES = new UniqueValueCache<Class, VirtualSwitch>() {
        @Override
        protected VirtualSwitch newValue(Class clazz) {
            return new VirtualSwitch(clazz);
        }
    };
    
    public static VirtualSwitch instance(Class clazz) {
        return INSTANCES.get(clazz);
    }
    
    private final Class clazz;
    private final Map<Signature, VirtualMethod<?>> methods = new ConcurrentHashMap<>();

    public VirtualSwitch(Class clazz) {
        this.clazz = clazz;
    }
    
    public <R> R invoke(Object self, String name, Object... args) throws IllegalAccessException, InvocationTargetException {
        VirtualMethod<R> vms = get(name);
        return vms.invoke(self, args);
    }

    public <R> R invokeStatic(String name, Object... args) throws IllegalAccessException, InvocationTargetException {
        VirtualMethod<R> vms = get(name);
        return vms.invokeStatic(args);
    }

    public <R> R _invoke(Object self, String name, Object... args) {
        VirtualMethod<R> vms = get(name);
        return vms._invoke(self, args);
    }

    public <R> R _invokeStatic(String name, Object... args) {
        VirtualMethod<R> vms = get(name);
        return vms._invokeStatic(args);
    }

    public <R> VirtualMethod<R> get(String name) {
        return get(name, 0, true, (Class[]) null);
    }
    
    public <R> VirtualMethod<R> get(String name, Class... paramTypes) {
        return get(name, 0, true, paramTypes);
    }
    
    public <R> VirtualMethod<R> get(String name, boolean varArgSwitch, Class... paramTypes) {
        return get(name, 0, varArgSwitch, paramTypes);
    }
    
    public <R> VirtualMethod<R> get(String name, int switchParamCount, Class... moreParams) {
        return get(name, switchParamCount, true, moreParams);
    }
    
    public <R> VirtualMethod<R> get(String name, int switchParamCount, boolean varArgSwitch, Class... moreParams) {
        Signature sig = new Signature(name, switchParamCount, varArgSwitch, moreParams);
        VirtualMethod<?> vms = methods.get(sig);
        if (vms == null) {
            vms = new VirtualMethod<>(clazz, name, switchParamCount, varArgSwitch, moreParams);
            methods.put(sig, vms);
        }
        return (VirtualMethod<R>) vms;
    }
    
    protected static class Signature {
        final String name;
        final int switchParamCount;
        final boolean varArgSwitch;
        final Class[] moreParams;

        public Signature(String name, int switchParamCount, boolean varArgSwitch, Class[] moreParams) {
            this.name = name;
            this.switchParamCount = switchParamCount;
            this.varArgSwitch = varArgSwitch;
            this.moreParams = moreParams;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode(this.name);
            hash = 29 * hash + this.switchParamCount;
            hash = 29 * hash + (this.varArgSwitch ? 1 : 0);
            hash = 29 * hash + Arrays.deepHashCode(this.moreParams);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Signature other = (Signature) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (this.switchParamCount != other.switchParamCount) {
                return false;
            }
            if (this.varArgSwitch != other.varArgSwitch) {
                return false;
            }
            if (!Arrays.deepEquals(this.moreParams, other.moreParams)) {
                return false;
            }
            return true;
        }
    }
    
}
