/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cthul.parser.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.cthul.parser.Token;
import org.cthul.parser.grammar.earley.Production;

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
