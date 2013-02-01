package org.cthul.parser.annotation;

import java.lang.annotation.*;
import org.cthul.parser.annotation.scan.ProcessedBy;

/**
 * Defines the patterns that will be used for matching tokens.
 * <p/>
 * {@link Group} to select a group of the pattern
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@ProcessedBy(MatchProcessor.class)
public @interface Match {

	public String[] value();
    
}
