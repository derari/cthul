package org.cthul.proc;

import org.hamcrest.SelfDescribing;

/**
 * A proc is a piece of code that can be executed.
 * For implementations, see {@link ProcBase}.
 * For quick implementations in anonymous classes, see {@link P0}, {@link P1},
 * {@link P2}, {@link P3}, {@link P4}, and {@link PN}.
 * <p/>
 * A proc is generally executed only once. The result or the exception
 * that was thrown are cached.
 * The only way to reset the state of a proc is with {@link #retry()}.
 * <p/>
 * If a proc is {@link #call(java.lang.Object[]) called} a new instance is 
 * created that will execute the code of the original proc, but may use 
 * different arguments.
 * 
 * @author Arian Treffer
 */
public interface Proc extends SelfDescribing {
    
    /**
     * The arguments that were used for the execution of this Proc.
     */
    Object[] getArgs();
    
    /**
     * Resets the Proc.
     * The cached values are cleared, so it will be executed again.
     * @return this object.
     */
    Proc retry();
    
    /**
     * Creates a new Proc that invokes the code of this Proc 
     * and uses the same arguments.
     * @return new Proc
     */
    Proc callAgain();
    
    /**
     * Creates a new Proc that invokes the code of this Proc with {@code args}.
     * @param args
     * @return new Proc
     */
    Proc call(Object... args);
    
    /**
     * Alias for {@link #call(java.lang.Object[])}.
     * @see Proc#call(java.lang.Object[]) 
     */
    Proc with(Object... args);
    
    /**
     * Returns true iff executing this Proc caused no exceptions.
     * <p/>
     * If true, {@link #getResult()} can be called to retrieve the result of 
     * the execution. Otherwise, {@link #getException()} will return the 
     * exception that was thrown.
     * @return if this Proc returned.
     */
    boolean hasResult();
    
    /**
     * Returns the result of the execution,
     * or {@code null} if an exception was thrown.
     * @return result
     */
    Object getResult();
    
    /**
     * Returns the exception that was thrown during execution,
     * or {@code null} if the proc completed without error.
     * @return exception
     */
    Throwable getException();
    
    Proc curry(Object... args);
    
    Proc curryAt(int i, Object... args);
    
    Proc0 asProc0();

    <A> Proc1<A> asProc1();

    <A, B> Proc2<A, B> asProc2();

    <A, B, C> Proc3<A, B, C> asProc3();

    <A, B, C, D> Proc4<A, B, C, D> asProc4();
}
