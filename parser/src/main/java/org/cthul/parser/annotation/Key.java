package org.cthul.parser.annotation;

import java.lang.annotation.*;

/**
 * Explicitly sets the name of tokens, productions and sequence productions.
 * @author Arian Treffer
 * @see Token
 * @see Match
 * @see Production
 * @see Sequence
 * @see Sequences
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Key {

	public String value() default "";

}
