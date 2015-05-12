package org.cthul.resolve;

import java.util.List;

/**
 * Wraps exceptions that occurred during resource lookup.
 * Especially intended for checked exceptions.
 * <p>
 * Can be used as a return value to indicate non-critical exceptions. 
 * However, in most cases it will be more efficient to 
 * {@linkplain RRequest#noResultResponse() obtain} a {@link ResponseBuilder}.
 */
public class ResolvingException extends RuntimeException implements RResponse {
    
    /**
     * Wraps e in an resolving exception, if it isn't one already.
     * @param e
     * @return resolving exception
     */
    public static ResolvingException wrap(Exception e) {
        return wrap(null, e);
    }
    
    /**
     * Wraps e in an resolving exception, if it isn't one already.
     * @param request
     * @param e
     * @return resolving exception
     */
    public static ResolvingException wrap(RRequest request, Exception e) {
        if (e instanceof ResolvingException) return (ResolvingException) e;
        return new ResolvingException(request, e);
    }
    
    private final RRequest request;
    RResult result = null;
    ResponseBuilder.WarningsList warningsList = null;

    public ResolvingException() {
        this(null, null, null);
    }

    public ResolvingException(String message) {
        this(null, message, null);
    }

    public ResolvingException(Throwable cause) {
        this(null, null, cause);
    }

    public ResolvingException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public ResolvingException(RRequest request) {
        this.request = request;
    }

    public ResolvingException(RRequest request, String message) {
        super(message);
        this.request = request;
    }

    public ResolvingException(RRequest request, String message, Throwable cause) {
        super(message, cause);
        this.request = request;
    }

    public ResolvingException(RRequest request, Throwable cause) {
        super(cause);
        this.request = request;
    }

    @Override
    public RRequest getRequest() {
        return request;
    }

    @Override
    public boolean hasResult() {
        return result != null;
    }

    @Override
    public RResult getResult() {
        if (hasResult()) {
            return result;
        } else {
            throw asException();
        }
    }

    @Override
    public ResolvingException asException() {
        return this;
    }

    @Override
    public List<Exception> getWarningsLog() {
        if (warningsList == null) {
            warningsList = new ResponseBuilder.WarningsList();
            addWarning(getCause());
            for (Throwable t: getSuppressed()) {
                addWarning(t);
            }
        }
        return warningsList.asReadOnly();
    }
    
    private void addWarning(Throwable t) {
        if (t == null) return;
        if (t instanceof Exception) {
            warningsList.add((Exception) t);
        } else {
            warningsList.add(t.getMessage());
        }
    }
    
    /**
     * If the {@linkplain #getResolvingCause() cause} is a RuntimeException, 
     * returns the cause, otherwise returns this.
     * @return runtime exception.
     */
    public RuntimeException asRuntimeException() {
        Throwable c = getResolvingCause();
        if (c instanceof RuntimeException) {
            return (RuntimeException) c;
        }
        return this;
    }
    
    /**
     * Throws the {@linkplain #getResolvingCause() cause} if it is the 
     * specified type, otherwise returns a 
     * {@linkplain #asRuntimeException() runtime exception}.
     * <p>
     * Intended to be written as {@code throw e.againAs(IOException.class)}.
     * @param <T1>
     * @param t1
     * @return
     * @throws T1 
     */
    public <T1 extends Throwable> 
                RuntimeException againAs(Class<T1> t1) 
                throws T1 {
        return againAs(t1, NULL_EX, NULL_EX, NULL_EX);
    }
    
    /**
     * Throws the {@linkplain #getResolvingCause() cause} if it is one of the 
     * specified types, otherwise returns a 
     * {@linkplain #asRuntimeException() runtime exception}.
     * <p>
     * Intended to be written as {@code throw e.againAs(IOException.class)}.
     * @param <T1>
     * @param <T2>
     * @param t1
     * @param t2
     * @return runtime exception
     * @throws T1
     * @throws T2 
     */
    public <T1 extends Throwable, T2 extends Throwable> 
                RuntimeException againAs(Class<T1> t1, Class<T2> t2) 
                throws T1, T2 {
        return againAs(t1, t2, NULL_EX, NULL_EX);
    }
    
    /**
     * Throws the {@linkplain #getResolvingCause() cause} if it is one of the 
     * specified types, otherwise returns a 
     * {@linkplain #asRuntimeException() runtime exception}.
     * <p>
     * Intended to be written as {@code throw e.againAs(IOException.class)}.
     * @param <T1>
     * @param <T2>
     * @param <T3>
     * @param t1
     * @param t2
     * @param t3
     * @return runtime exception
     * @throws T1
     * @throws T2
     * @throws T3 
     */
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> 
                RuntimeException againAs(Class<T1> t1, Class<T2> t2, Class<T3> t3) 
                throws T1, T2, T3 {
        return againAs(t1, t2, t3, NULL_EX);
    }
    
    /**
     * Throws the {@linkplain #getResolvingCause() cause} if it is one of the 
     * specified types, otherwise returns a 
     * {@linkplain #asRuntimeException() runtime exception}.
     * <p>
     * Intended to be written as {@code throw e.againAs(IOException.class)}.
     * @param <T1>
     * @param <T2>
     * @param <T3>
     * @param <T4>
     * @param t1
     * @param t2
     * @param t3
     * @param t4
     * @return runtime exception
     * @throws T1
     * @throws T2
     * @throws T3
     * @throws T4 
     */
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, T4 extends Throwable> 
                RuntimeException againAs(Class<T1> t1, Class<T2> t2, Class<T3> t3, Class<T4> t4) 
                throws T1, T2, T3, T4 {
        return throwIf(t1, t2, t3, t4);
    }
    
    /**
     * Throws the {@linkplain #getResolvingCause() cause} if it is the 
     * specified type, otherwise returns a 
     * {@linkplain #asRuntimeException() runtime exception}.
     * @param <T1>
     * @param t1
     * @return runtime exception
     * @throws T1 
     */
    public <T1 extends Throwable> 
                RuntimeException throwIf(Class<T1> t1) 
                throws T1 {
        return throwIf(t1, NULL_EX, NULL_EX, NULL_EX);
    }
    
    /**
     * Throws the {@linkplain #getResolvingCause() cause} if it is one of the 
     * specified types, otherwise returns a 
     * {@linkplain #asRuntimeException() runtime exception}.
     * @param <T1>
     * @param <T2>
     * @param t1
     * @param t2
     * @return runtime exception
     * @throws T1
     * @throws T2 
     */
    public <T1 extends Throwable, T2 extends Throwable> 
                RuntimeException throwIf(Class<T1> t1, Class<T2> t2) 
                throws T1, T2 {
        return throwIf(t1, t2, NULL_EX, NULL_EX);
    }
    
    /**
     * Throws the {@linkplain #getResolvingCause() cause} if it is one of the 
     * specified types, otherwise returns a 
     * {@linkplain #asRuntimeException() runtime exception}.
     * @param <T1>
     * @param <T2>
     * @param <T3>
     * @param t1
     * @param t2
     * @param t3
     * @return runtime exception
     * @throws T1
     * @throws T2
     * @throws T3 
     */
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> 
                RuntimeException throwIf(Class<T1> t1, Class<T2> t2, Class<T3> t3) 
                throws T1, T2, T3 {
        return throwIf(t1, t2, t3, NULL_EX);
    }
    
    /**
     * Throws the {@linkplain #getResolvingCause() cause} if it is one of the 
     * specified types, otherwise returns a 
     * {@linkplain #asRuntimeException() runtime exception}.
     * @param <T1>
     * @param <T2>
     * @param <T3>
     * @param <T4>
     * @param t1
     * @param t2
     * @param t3
     * @param t4
     * @return runtime exception
     * @throws T1
     * @throws T2
     * @throws T3
     * @throws T4 
     */
    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, T4 extends Throwable> 
                RuntimeException throwIf(Class<T1> t1, Class<T2> t2, Class<T3> t3, Class<T4> t4) 
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
                
//    public RuntimeException rethrow() {
//        throw asRuntimeException();
//    }
//                
//    public <T1 extends Throwable> 
//                RuntimeException rethrow(Class<T1> t1) 
//                throws T1 {
//        throw againAs(t1, NULL_EX, NULL_EX, NULL_EX);
//    }
//    
//    public <T1 extends Throwable, T2 extends Throwable> 
//                RuntimeException rethrow(Class<T1> t1, Class<T2> t2) 
//                throws T1, T2 {
//        throw againAs(t1, t2, NULL_EX, NULL_EX);
//    }
//    
//    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> 
//                RuntimeException rethrow(Class<T1> t1, Class<T2> t2, Class<T3> t3) 
//                throws T1, T2, T3 {
//        throw againAs(t1, t2, t3, NULL_EX);
//    }
//    
//    public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, T4 extends Throwable> 
//                RuntimeException rethrow(Class<T1> t1, Class<T2> t2, Class<T3> t3, Class<T4> t4) 
//                throws T1, T2, T3, T4 {
//        throw againAs(t1, t2, t3, t4);
//    }
    
    /**
     * Returns the underlying cause of this resolving exceptions.
     * Unpacks nested resolving exceptions if necessary.
     * @return cause
     */
    public Throwable getResolvingCause() {
        Throwable t = this, c = this;
        while (c instanceof ResolvingException) {
            t = c; c = t.getCause();
        }
        return c == null ? t : c;
    }
    
    private static final Class<RuntimeException> NULL_EX = null;
//    private static class NoEx extends RuntimeException {}
}
