package org.cthul.parser.util;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
    
    String value() default "";
    
    boolean create() default false;
    
    Class<?> impl() default Void.class;
    
    String factory() default "";
    
    boolean late() default false;
    
    boolean autoUpdate() default true;
    
}
