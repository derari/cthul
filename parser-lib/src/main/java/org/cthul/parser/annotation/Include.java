package org.cthul.parser.annotation;

import java.lang.annotation.*;
import org.cthul.parser.annotation.scan.ProcessedBy;

/**
 * Defines a production of a grammar.
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@ProcessedBy(IncludeProcessor.class)
public @interface Include {

}
