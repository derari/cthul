package org.cthul.proc;

/**
 * A {@link ProcBase PN} with parameters.
 * @author Arian Treffer
 */
public class PN extends ProcBase<PN> {

    private int argC = -1;
    
    public PN(ProcBase source, Object[] args) {
        super(source, args);
    }

    public PN(ProcBase source) {
        super(source);
    }

    public PN(Object[] args) {
        super(args);
    }

    public PN() {
    }

    public PN(ProcBase source, Object[] args, int argC) {
        super(source, args);
        this.argC = argC;
    }

    public PN(ProcBase source, int argC) {
        super(source);
        this.argC = argC;
    }

    public PN(Object[] args, int argC) {
        super(args);
        this.argC = argC;
    }

    public PN(int argC) {
        this.argC = argC;
    }

    @Override
    protected PN createCopy(Object[] args) {
        return new PN(this, args, argC);
    }

    @Override
    protected final Object run(Object[] args) throws Throwable {
        if (argC > -1) assertArgCount(args, argC);
        return runN(args);
    }
    
    protected Object runN(Object[] args) throws Throwable {
        throw notImplemented("runN(Object[])");
    }
    
}
