package org.cthul.parser.annotation.processing;

import java.lang.annotation.*;

@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessedBy {
    
    Class<? extends AnnotationReader> value();
    
}
