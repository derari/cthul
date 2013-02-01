package org.cthul.parser.annotation.scan;

import java.lang.annotation.*;

/**
 * When the {@link AnnotationScanner} finds an annotation whose type
 * is annotated with {@code ProcessedBy}, the specified 
 * {@link AnnotationProcessor reader} is invoked.
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessedBy {
    
    Class<? extends AnnotationProcessor> value();
    
}
