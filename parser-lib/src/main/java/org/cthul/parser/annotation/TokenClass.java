package org.cthul.parser.annotation;

import java.lang.annotation.*;

/**
 *
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface TokenClass {

    public Class<?> value();
    
    public String factoryField() default "FACTORY";
    
}
