package org.cthul.resolve;

/**
 *
 * @author Arian Treffer
 */
public class ResolvingException extends RuntimeException {

    public ResolvingException() {
    }

    public ResolvingException(String message) {
        super(message);
    }

    public ResolvingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolvingException(Throwable cause) {
        super(cause);
    }
    
    public RuntimeException asRuntimeException() {
        Throwable c = getResolvingCause();
        if (c instanceof RuntimeException) {
            return (RuntimeException) c;
        }
        return this;
    }
    
    public <T1 extends Throwable> 
                RuntimeException againAs(Class<T1> t1) 
                throws T1 {
        return againAs(t1, NULL_EX, NULL_EX, NULL_EX);
    }
    
    public <T1 extends Throwable, T2 extends Throwable> 
                RuntimeException againAs(Class<T1> t1, Class<T2> t2) 
                throws T1, T2 {
        return againAs(t1, t2, NULL_EX, NULL_EX);
    }
    
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> 
                RuntimeException againAs(Class<T1> t1, Class<T2> t2, Class<T3> t3) 
                throws T1, T2, T3 {
        return againAs(t1, t2, t3, NULL_EX);
    }
    
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, T4 extends Throwable> 
                RuntimeException againAs(Class<T1> t1, Class<T2> t2, Class<T3> t3, Class<T4> t4) 
                throws T1, T2, T3, T4 {
        Throwable c = getResolvingCause();
        if (t1 != null && t1.isInstance(c)) {
            throw t1.cast(c);
        }
        if (t2 != null && t2.isInstance(c)) {
            throw t2.cast(c);
        }
        if (t3 != null && t3.isInstance(c)) {
            throw t3.cast(c);
        }
        if (t4 != null && t4.isInstance(c)) {
            throw t4.cast(c);
        }
        if (c instanceof RuntimeException) {
            return (RuntimeException) c;
        }
        return this;
    }
                
    public <T1 extends Throwable> 
                RuntimeException throwIf(Class<T1> t1) 
                throws T1 {
        return againAs(t1, NULL_EX, NULL_EX, NULL_EX);
    }
    
    public <T1 extends Throwable, T2 extends Throwable> 
                RuntimeException throwIf(Class<T1> t1, Class<T2> t2) 
                throws T1, T2 {
        return againAs(t1, t2, NULL_EX, NULL_EX);
    }
    
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> 
                RuntimeException throwIf(Class<T1> t1, Class<T2> t2, Class<T3> t3) 
                throws T1, T2, T3 {
        return againAs(t1, t2, t3, NULL_EX);
    }
    
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, T4 extends Throwable> 
                RuntimeException throwIf(Class<T1> t1, Class<T2> t2, Class<T3> t3, Class<T4> t4) 
                throws T1, T2, T3, T4 {
        return againAs(t1, t2, t3, t4);
    }
                
    public RuntimeException rethrow() {
        throw asRuntimeException();
    }
                
    public <T1 extends Throwable> 
                RuntimeException rethrow(Class<T1> t1) 
                throws T1 {
        throw againAs(t1, NULL_EX, NULL_EX, NULL_EX);
    }
    
    public <T1 extends Throwable, T2 extends Throwable> 
                RuntimeException rethrow(Class<T1> t1, Class<T2> t2) 
                throws T1, T2 {
        throw againAs(t1, t2, NULL_EX, NULL_EX);
    }
    
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> 
                RuntimeException rethrow(Class<T1> t1, Class<T2> t2, Class<T3> t3) 
                throws T1, T2, T3 {
        throw againAs(t1, t2, t3, NULL_EX);
    }
    
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, T4 extends Throwable> 
                RuntimeException rethrow(Class<T1> t1, Class<T2> t2, Class<T3> t3, Class<T4> t4) 
                throws T1, T2, T3, T4 {
        throw againAs(t1, t2, t3, t4);
    }
    
    public Throwable getResolvingCause() {
        Throwable t = this, c = this;
        while (c instanceof ResolvingException) {
            t = c; c = t.getCause();
        }
        return c == null ? t : c;
    }
    
    private static final Class<NoEx> NULL_EX = null;
    private static class NoEx extends RuntimeException {}
}
