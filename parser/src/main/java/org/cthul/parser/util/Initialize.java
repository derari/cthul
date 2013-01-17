package org.cthul.parser.util;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Initialize {
    
    boolean late() default false;
    
}
