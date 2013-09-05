package org.cthul.proc;

/**
 *
 */
public class CurryProc extends PN {
    
    private final int i;
    private final Object[] curry;

    public CurryProc(ProcBase source, Object[] curry) {
        this(source, 0, curry);
    }

    public CurryProc(ProcBase source, int i, Object[] curry) {
        super(source);
        this.i = i;
        this.curry = curry != null ? curry : new Object[0];
    }

    @Override
    protected Object executeProc(Object[] args) throws Throwable {
        if (args.length == 0) {
            args = curry;
        } else if (curry.length > 0) {
            Object[] newArgs = new Object[args.length + curry.length];
            System.arraycopy(args, 0, newArgs, 0, i);
            System.arraycopy(curry, 0, newArgs, i, curry.length);
            System.arraycopy(args, i, newArgs, i+curry.length, args.length-i);
            args = newArgs;
        }
        return super.executeProc(args);
    }
    
}
