package org.cthul.proc;

/**
 * A {@link ProcBase ProcBase} with two parameters.
 * @author Arian Treffer
 */
public class P2<A, B> extends ProcBase<P2<A, B>> implements Proc2<A, B> {

    protected P2(ProcBase source, Object[] args) {
        super(source, args);
    }

    public P2(ProcBase source, A a, B b) {
        super(source, a, b);
    }

    public P2(ProcBase source) {
        super(source);
    }

    public P2(A a, B b) {
        super(a, b);
    }

    public P2() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected P2<A, B> createCopy(Object[] args) {
        return new P2<A, B>(this, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object run(Object[] args) throws Throwable {
        assertArgCount(args, 2);
        return run((A)args[0], (B)args[1]);
    }

    /**
     * Executes the asProc.
     * @param a
     * @param b
     * @return result
     * @throws Throwable
     */
    protected Object run(A a, B b) throws Throwable {
        throw notImplemented("run(A, B)");
    }

    /**
     * Creates a new ProcBase.
     * @param a
     * @param b
     * @return
     */
    public P2 call(A a, B b) {
        return copy(a, b);
    }

    public P2 withArgs(A a, B b) {
        return call(a, b);
    }

    @Override
    public Proc1<B> curry(A a) {
        return curry((Object) a).asProc1();
    }

    @Override
    public Proc0 curry(A a, B b) {
        return curry((Object) a, (Object) b).asProc0();
    }

}
