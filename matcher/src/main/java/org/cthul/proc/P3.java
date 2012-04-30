package org.cthul.proc;

/**
 * A {@link ProcBase ProcBase} with three parameters.
 * @author Arian Treffer
 */
public class P3<A, B, C> extends ProcBase<P3<A, B, C>> implements Proc3<A, B, C> {

    protected P3(ProcBase source, Object[] args) {
        super(source, args);
    }

    public P3(ProcBase source, A a, B b, C c) {
        super(source, a, b, c);
    }

    public P3(ProcBase source) {
        super(source);
    }

    public P3(A a, B b, C c) {
        super(a, b, c);
    }

    public P3() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected P3<A, B, C> createCopy(Object[] args) {
        return new P3<A, B, C>(this, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object run(Object[] args) throws Throwable {
        assertArgCount(args, 3);
        return run((A)args[0], (B)args[1], (C)args[2]);
    }

    /**
     * Executes the asProc.
     * @param a
     * @param b
     * @return result
     * @throws Throwable
     */
    protected Object run(A a, B b, C c) throws Throwable {
        throw notImplemented("run(A, B, C)");
    }

    /**
     * Creates a new asProc.
     * @param a
     * @param b
     * @return
     */
    public P3 call(A a, B b, C c) {
        return copy(a, b, c);
    }

    public P3 withArgs(A a, B b, C c) {
        return call(a, b, c);
    }

}
