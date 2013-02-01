package org.cthul.parser.annotation.scan;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.util.InstanceMap;

public class AnnotationScanner {
    
    public static void apply(ParserBuilder<?,?> pb, Class<?>... classes) {
        new AnnotationScanner(pb).process(classes);
    }
    
    public static void apply(ParserBuilder<?,?> pb, Object... impls) {
        new AnnotationScanner(pb).process(impls);
    }
    
    protected final ParserBuilder<?,?> pb;
    protected final InstanceMap instances = new InstanceMap();
    protected final Map<Class<? extends Annotation>, AnnotationProcessor<?,?>> readers = new HashMap<>();

    @SuppressWarnings("LeakingThisInConstructor")
    public AnnotationScanner(ParserBuilder<?,?> pb) {
        this.pb = pb;
        instances.put(getClass(), this);
        instances.initialize(pb);
    }
    
    public void process(Object... impls) {
        for (Object o: impls)
            process(o);
    }
    
    public void process(Object impl) {
        process(impl.getClass(), impl);
    }
    
    public void process(Class<?>... classes) {
        for (Class<?> clazz: classes)
            process(clazz);
    }
    
    public void process(Class<?> clazz) {
        process(clazz, null);
    }
    
    public void process(Class<?> clazz, Object impl) {
        if (clazz == null) return;
        if (impl == null) impl = clazz;
        process(clazz.getSuperclass(), impl);
        for (Field f: clazz.getDeclaredFields()) {
            if ((f.getModifiers() & Modifier.PUBLIC) == 0) continue;
            processElement(f, impl);
        }
        for (Method m: clazz.getDeclaredMethods()) {
            if ((m.getModifiers() & Modifier.PUBLIC) == 0) continue;
            processElement(m, impl);
        }
        for (Class c: clazz.getDeclaredClasses()) {
            int mod = c.getModifiers();
            if ((mod & Modifier.PUBLIC) == 0) continue;
            if ((mod & Modifier.STATIC) == 0) continue;
            if ((mod & Modifier.ABSTRACT) != 0) continue;
            process(c);
        }
    }
    
    protected AnnotationProcessor<?,?> getReader(Class<? extends Annotation> clazz) {
        AnnotationProcessor<?,?> ar = readers.get(clazz);
        if (ar == null) {
            ar = findReader(clazz);
            readers.put(clazz, ar);
        }
        return (ar == NULL ? null : ar);
    }
    
    protected AnnotationProcessor<?,?> findReader(Class<? extends Annotation> clazz) {
        ProcessedBy pBy = clazz.getAnnotation(ProcessedBy.class);
        if (pBy == null) return NULL;
        return instances.getOrCreate(pBy.value());
    }

    @SuppressWarnings("unchecked")
    protected void processElement(AnnotatedElement ae, Object impl) {
        final Annotation[] ats = ae.getAnnotations();
        for (int i = 0; i < ats.length; i++) {
            AnnotationProcessor<?,?> ar = getReader(ats[i].annotationType());
            if (ar != null) {
                ar.process(ae, impl, ats[i], (ParserBuilder) pb);
            }
        }
    }

    protected static final AnnotationProcessor<Object,Object> NULL = new AnnotationProcessor<Object,Object>() {
        @Override
        public void process(AnnotatedElement ae, Object impl, Annotation at, ParserBuilder<? super Object, ? extends Object> pb) {
            throw new UnsupportedOperationException();
        }
    };
    
    
}
