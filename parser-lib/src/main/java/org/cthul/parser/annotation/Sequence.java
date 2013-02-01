package org.cthul.parser.annotation;

import java.lang.annotation.*;
import org.cthul.parser.annotation.scan.ProcessedBy;
import org.cthul.parser.sequence.ArrayListSequence;
import org.cthul.parser.sequence.SequenceBuilder;

/**
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@ProcessedBy(SequenceProcessor.class)
public @interface Sequence {

	String item();
        
        boolean flatten() default false;
        
        String separator() default "";

        boolean allowEmpty() default true;
        
        int minSteps() default 0;
        
        int stepSize() default 1;
        
        Class<? extends SequenceBuilder> type() default ArrayListSequence.class;
        
}
