package org.cthul.parser.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.annotation.scan.AnnotationProcessor;
import org.cthul.parser.annotation.scan.AnnotationScanner;
import org.cthul.parser.reflect.FieldValue;
import org.cthul.parser.reflect.MethodExecutor;
import org.cthul.parser.util.Inject;
import org.cthul.parser.util.InstanceMap;

public class IncludeProcessor implements AnnotationProcessor<Object, Object> {

    @Inject
    public AnnotationScanner scanner;
    
    @Inject
    public InstanceMap iMap;
    
    @Override
    public void process(AnnotatedElement ae, Object impl, Annotation at, ParserBuilder<? super Object, ? extends Object> pb) {
        Object include = getInclude(impl, ae);
        scanner.process(include);
    }

    protected Object getInclude(Object impl, AnnotatedElement ae) {
        if (ae instanceof Method) {
            return new MethodExecutor<>(impl, (Method) ae, MethodExecutor.DEFAULT_MAPPER, null).invoke(iMap, null, null);
        } else if (ae instanceof Field) {
            return FieldValue.create(impl, (Field) ae).get(iMap);
        } else {
            throw new IllegalArgumentException("" + ae);
        }
    }
    
}
