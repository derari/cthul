package org.cthul.proc;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;

/**
 * Impements the basic contract of the {@link Proc} interface,
 * but has no behavior.
 * @param <This> 
 * @see P0
 * @see P1
 * @see P2
 * @see P3
 * @see P4
 * @see PN
 */
public abstract class ProcBase<This extends ProcBase<This>> implements Proc {

    protected static final Object[] NO_ARGS = new Object[0];

    protected final ProcBase source;

    private Object[] args = NO_ARGS;

    private String name = "";

    private boolean isRun = false;
    private Object result;
    private Throwable exception;

    protected ProcBase() {
        this.source = null;
    }

    protected ProcBase(Object... args) {
        this();
        args(args);
    }

    /**
     * Creates a ProcBase that uses the run-method of
     * {@code source} instead of its own.
     * @param source
     */
    protected ProcBase(ProcBase source) {
        name(source.name);
        //while (source.source != null) source = source.source;
        this.source = source;
    }

    protected ProcBase(ProcBase source, Object... args) {
        this(source);
        args(args);
    }

    /**
     * Sets the arguments, for internal use only.
     * @param args
     */
    protected final void args(Object... args) {
        if (args == null) args = NO_ARGS;
        this.args = args;
        retry();
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getArgs() {
        return args;
    }

    /**
     * Sets the name of this proc.
     * It will be used as description and has no functional purpose.
     * @param name
     */
    protected final void name(String name) {
        if (name == null) name = "";
        this.name = name;
    }

    /**
     * Throws a ProcError exception if {@code args.length != count}
     * @param args
     * @param count
     */
    protected void assertArgCount(Object[] args, int count) {
        if (args.length != count) {
            throw illegalArgumentException(String.format(
                    "Wrong number of arguments, expected %d got %d", 
                    count, args.length));
        }
    }

    protected ProcError notImplemented(String method) {
        return unsupportedOperationException(method + " is not implemented");
    }

    /**
     * Execute the proc.
     * @return result
     * @throws Throwable
     */
    protected abstract Object run(Object[] args) throws Throwable;

    protected This copy() {
        return createCopy(args);
    }

    protected This copy(Object... args) {
        return createCopy(args);
    }

    protected abstract This createCopy(Object[] args);

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public This retry() {
        isRun = false;
        return (This) this;
    }

    /**
     * Execute the proc if not done so already.
     */
    private synchronized void ensureIsRun() {
        if (!isRun) {
            isRun = true;
            try {
                result = executeProc(args);
                exception = null;
            } catch (ProcError e) {
                throw e.asRuntimeException();
            } catch (Throwable t) {
                exception = t;
                result = null;
            }
        }
    }

    protected Object executeProc(Object... args) throws Throwable {
        if (source != null) return source.executeProc(args);
        return run(args);
    }

    /** {@inheritDoc} */
    @Override
    public This callAgain() {
        return copy();
    }

    /** {@inheritDoc} */
    @Override
    public This call(Object... args) {
        return copy(args);
    }

    /** {@inheritDoc} */
    @Override
    public This with(Object... args) {
        return call(args);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasResult() {
        ensureIsRun();
        return exception == null;
    }

    /** {@inheritDoc} */
    @Override
    public Object getResult() {
        ensureIsRun();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public Throwable getException() {
        ensureIsRun();
        return exception;
    }

    /** {@inheritDoc} */
    @Override
    public P0 asProc0() {
        return new P0(this, args);
    }

    /** {@inheritDoc} */
    @Override
    public <A> P1<A> asProc1() {
        return new P1<>(this, args);
    }

    /** {@inheritDoc} */
    @Override
    public <A, B> P2<A, B> asProc2() {
        return new P2<>(this, args);
    }

    /** {@inheritDoc} */
    @Override
    public <A, B, C> P3<A, B, C> asProc3() {
        return new P3<>(this, args);
    }

    /** {@inheritDoc} */
    @Override
    public <A, B, C, D> P4<A, B, C, D> asProc4() {
        return new P4<>(this, args);
    }

    @Override
    public Proc curry(Object... args) {
        return new CurryProc(this, args);
    }

    @Override
    public Proc curryAt(int i, Object... args) {
        return new CurryProc(this, i, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void describeTo(Description description) {
        describeKeyTo(description);
        if (isRun) {
            if (hasResult()) {
                description.appendText(" = ")
                           .appendValue(getResult());
            } else {
                description.appendText("-> ")
                           .appendValue(getException());
            }
        }
    }
    
    protected void describeKeyTo(Description description) {
        description.appendText("{");
        describeNameTo(description);
        description.appendText("}(");
        describeArgsTo(description);
        description.appendText(")");
    }
    
    protected void describeNameTo(Description description) {
        if (name != null) {
            description.appendText(name);
        } else if (source != null) {
            source.describeNameTo(description);
        }
    }

    /**
     * Describes the arguments that are used for execution this asProc.
     * @param description
     */
    protected int describeArgsTo(Description description) {
        for (int i = 0; i < args.length; i++) {
            if (i > 0) description.appendText(", ");
            description.appendValue(args[i]);
        }
        return args.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return StringDescription.toString(this);
    }
    
    protected ProcError exception(Class<? extends RuntimeException> reClazz, String message, Throwable cause) {
        return new ProcError(reClazz, message, cause);
    }
    
    protected ProcError runtimeException(String message) {
        return exception(RuntimeException.class, message, null);
    }

    protected ProcError runtimeException(String message, Throwable cause) {
        return exception(RuntimeException.class, message, cause);
    }

    protected ProcError illegalArgumentException(String message) {
        return exception(IllegalArgumentException.class, message, null);
    }

    protected ProcError unsupportedOperationException(String message) {
        return exception(UnsupportedOperationException.class, message, null);
    }

}
