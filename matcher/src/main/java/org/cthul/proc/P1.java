package org.cthul.proc;

/**
 * A {@link ProcBase ProcBase} with one parameter.
 * @author Arian Treffer
 */
public class P1<A> extends ProcBase<P1<A>> implements Proc1<A> {

    protected P1(ProcBase source, Object[] args) {
        super(source, args);
    }

    public P1(ProcBase source, A arg) {
        super(source, arg);
    }

    public P1(ProcBase source) {
        super(source);
    }

    public P1(A arg) {
        super(arg);
    }

    public P1() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected P1<A> createCopy(Object[] args) {
        return new P1<>(this, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object run(Object[] args) throws Throwable {
        assertArgCount(args, 1);
        return run((A)args[0]);
    }

    /**
     * Executes the asProc.
     * @param a
     * @return result
     * @throws Throwable
     */
    protected Object run(A a) throws Throwable {
        throw notImplemented("run(A)");
    }
    
    /**
     * Creates a new asProc.
     * @param a
     * @return
     */
    @Override
    public P1<A> call(A a) {
        return copy(a);
    }

    @Override
    public P1<A> with(A a) {
        return call(a);
    }

    @Override
    public Proc0 curry(A a) {
        return curry((Object) a).asProc0();
    }

}
