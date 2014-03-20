package org.cthul.objects.instance;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class UniqueValueCache<K, V> {
    
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final ConcurrentMap<KeyRef, Reference<V>> cache = new ConcurrentHashMap<>();
    
    public V get(K key) {
        cleanUp();
        
        @SuppressWarnings("element-type-mismatch")
        Reference<V> ref = cache.get(new Key(key));
        V value = ref != null ? ref.get() : null;
        
        if (value == null) {
            V newValue = newValue(key);
            while (value == null) {
                KeyRef cr = new KeyRef(key, queue);
                ref = newReference(cr, newValue, queue);
                cr.instanceRef = ref;
                Reference<V> r2 = cache.putIfAbsent(cr, ref);
                value = r2 == null ? newValue : r2.get();
            }
        }
        
        return value;
    }
    
    private void cleanUp() {
        Reference<?> r;
        while ((r = queue.poll()) != null) {
            KeyRef cr = ((GetKeyRef) r).getKeyRef();
            cache.remove(cr, cr.instanceRef);
        }
    }
    
    protected Reference<V> newReference(KeyRef keyRef, V value, ReferenceQueue<? super V> q) {
        return new WeakValueRef<>(keyRef, value, q);
    }

    protected abstract V newValue(K key);

    protected static interface GetKeyRef {
        
        KeyRef getKeyRef();
    }
    
    private static class Key<K> {
        private K key;

        public Key(K key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof KeyRef) {
                obj = ((KeyRef) obj).get();
            } else if (obj instanceof Key) {
                obj = ((Key) obj).key;
            }
            return key.equals(obj);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }
    
    protected static class KeyRef<K> extends SoftReference<K> implements GetKeyRef {
        private final int hash;
        private Reference<?> instanceRef = null;

        public KeyRef(K referent, ReferenceQueue<? super K> q) {
            super(referent, q);
            hash = referent.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            K k = get();
            if (k == null) {
                return obj == null;
            } else if (obj instanceof KeyRef) {
                obj = ((KeyRef) obj).get();
            } else if (obj instanceof Key) {
                obj = ((Key) obj).key;
            }
            return k.equals(obj);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public KeyRef getKeyRef() {
            return this;
        }
    }
    
    protected static class WeakValueRef<T> extends WeakReference<T> implements GetKeyRef {

        private final KeyRef keyRef;

        public WeakValueRef(KeyRef keyRef, T referent, ReferenceQueue<? super T> q) {
            super(referent, q);
            this.keyRef = keyRef;
        }
        
        @Override
        public KeyRef getKeyRef() {
            return keyRef;
        }
    }
    
    protected static class SoftValueRef<T> extends SoftReference<T> implements GetKeyRef {

        private final KeyRef keyRef;

        public SoftValueRef(KeyRef keyRef, T referent, ReferenceQueue<? super T> q) {
            super(referent, q);
            this.keyRef = keyRef;
        }
        
        @Override
        public KeyRef getKeyRef() {
            return keyRef;
        }
    }
}
