package org.cthul.parser.annotation;

import java.lang.annotation.*;
import org.cthul.parser.Token;

/**
 *
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TokenClass {

    public Class<? extends Token<?>> value();
    
    public String factoryField() default "FACTORY";
    
}
