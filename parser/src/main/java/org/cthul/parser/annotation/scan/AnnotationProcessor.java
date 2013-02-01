package org.cthul.parser.annotation.scan;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.cthul.parser.ParserBuilder;

public interface AnnotationProcessor<Token, Match> {
    
    /**
     * Call when an annotation with a {@link ProcessedBy} annotation was found.
     * @param ae the annotated element
     * @param impl the object that owns the element; may be an instance, a class, or null
     * @param at the annotation that referenced this reader
     * @param pb the parser builder
     */
    void process(AnnotatedElement ae, Object impl, Annotation at, ParserBuilder<? super Token, ? extends Match> pb);
    
}
