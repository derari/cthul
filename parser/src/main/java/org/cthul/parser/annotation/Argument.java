package org.cthul.parser.annotation;

import java.lang.annotation.*;

/**
 * @author Arian Treffer
 * @see Match
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Argument {
    
}
