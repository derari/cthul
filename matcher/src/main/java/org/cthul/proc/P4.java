package org.cthul.proc;

/**
 * A {@link ProcBase ProcBase} with four parameters.
 * @author Arian Treffer
 */
public class P4<A, B, C, D> extends ProcBase<P4<A, B, C, D>> implements Proc4<A, B, C, D> {

    protected P4(ProcBase source, Object[] args) {
        super(source, args);
    }

    public P4(ProcBase source, A a, B b, C c, D d) {
        super(source, a, b, c, d);
    }

    public P4(ProcBase source) {
        super(source);
    }

    public P4(A a, B b, C c, D d) {
        super(a, b, c, d);
    }

    public P4() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected P4<A, B, C, D> createCopy(Object[] args) {
        return new P4<>(this, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object run(Object[] args) throws Throwable {
        assertArgCount(args, 4);
        return run((A)args[0], (B)args[1], (C)args[2], (D)args[3]);
    }

    /**
     * Executes the proc.
     * @param a
     * @param b
     * @param c
     * @param d
     * @return result
     * @throws Throwable
     */
    protected Object run(A a, B b, C c, D d) throws Throwable {
        throw notImplemented("run(A, B, C, D)");
    }

    protected class Runner {
        public Object doRun(Object a, Object b, Object c, Object d) throws Throwable {
            return proxySourceOrRun(a, b, c, d);
        }
    }

    @Override
    public P4 call(A a, B b, C c, D d) {
        return copy(a, b, c, d);
    }

    public P4 withArgs(A a, B b, C c, D d) {
        return call(a, b, c, d);
    }

    @Override
    public Proc3<B, C, D> curry(A a) {
        return curry((Object) a).asProc3();
    }

    @Override
    public Proc2<C, D> curry(A a, B b) {
        return curry((Object) a, (Object) b).asProc2();
    }

    @Override
    public Proc1<D> curry(A a, B b, C c) {
        return curry((Object) a, (Object) b, (Object) c).asProc1();
    }

    @Override
    public Proc0 curry(A a, B b, C c, D d) {
        return curry((Object) a, (Object) b, (Object) c, (Object) d).asProc0();
    }

}
