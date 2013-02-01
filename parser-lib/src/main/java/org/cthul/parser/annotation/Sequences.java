package org.cthul.parser.annotation;

import java.lang.annotation.*;
import org.cthul.parser.annotation.scan.ProcessedBy;

/**
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@ProcessedBy(SequenceProcessor.class)
public @interface Sequences {

	public Sequence[] value();

}
