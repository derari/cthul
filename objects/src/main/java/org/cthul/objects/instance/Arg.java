package org.cthul.objects.instance;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {
    
    String key() default "";
    
    boolean[] x() default {};
    
    byte[] b() default {};
    
    char[] c() default {};
    
    double[] d() default {};
    
    float[] f() default {};
    
    int[] i() default {};
    
    long[] l() default {};
    
    short[] s() default {};
    
    String[] str() default {};
    
    Instance[] o() default {};
    
    Class<?> arrayOf() default void.class;
    
    Class<?> type() default void.class;
}
