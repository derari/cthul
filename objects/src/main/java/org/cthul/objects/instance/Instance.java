package org.cthul.objects.instance;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Instance {

    String key() default "";
    
    Class<?> impl() default void.class;
    
    String factory() default "";
}
