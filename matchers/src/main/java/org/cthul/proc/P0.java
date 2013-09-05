package org.cthul.proc;

/**
 * A {@link ProcBase ProcBase} with no parameters.
 */
public class P0 extends ProcBase<P0> implements Proc0 {

    protected P0(ProcBase source, Object[] args) {
        super(source, args);
    }

    public P0(ProcBase source) {
        super(source);
    }

    public P0() {
    }

    @Override
    protected P0 createCopy(Object[] args) {
        return new P0(this, args);
    }

    @Override
    protected Object run(Object[] args) throws Throwable {
        assertArgCount(args, 0);
        return run();
    }

    protected Object run() throws Throwable {
        throw notImplemented("run()");
    }
    
    @Override
    public P0 call() {
        return copy(NO_ARGS);
    }

}
