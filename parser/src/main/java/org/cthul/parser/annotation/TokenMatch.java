package org.cthul.parser.annotation;

import java.lang.annotation.*;
import org.cthul.parser.annotation.processing.ProcessedBy;

/**
 * Defines the patterns that will be used for matching tokens.
 * <p/>
 * {@link Group} to select a group of the pattern
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ProcessedBy(TokenMatchReader.class)
public @interface TokenMatch {

	public String[] value();
    
}
