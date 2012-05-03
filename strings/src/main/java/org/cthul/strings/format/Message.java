package org.cthul.strings.format;

import org.cthul.strings.Formatter;

/**
 *
 * @author Arian Treffer
 */
public class Message {
    
    private final FormatConfiguration conf;
    private final Throwable throwable;
    private final Object message;
    private final Object[] args;
    private final Object arg0;
    private final Object arg1;
    private final Object arg2;
    private final Object arg3;
    private final Object arg4;

    public Message(FormatConfiguration conf, Object message, Object... args) {
        this(conf, null, message, args, null, null, null, null, null);
    }

    public Message(FormatConfiguration conf, Object message, Object arg0) {
        this(conf, null, message, null, arg0, null, null, null, null);
    }

    public Message(FormatConfiguration conf, Object message, Object arg0, Object arg1) {
        this(conf, null, message, null, arg0, arg1, null, null, null);
    }

    public Message(FormatConfiguration conf, Object message, Object arg0, Object arg1, Object arg2) {
        this(conf, null, message, null, arg0, arg1, arg2, null, null);
    }

    public Message(FormatConfiguration conf, Object message, Object arg0, Object arg1, Object arg2, Object arg3) {
        this(conf, null, message, null, arg0, arg1, arg2, arg3, null);
    }

    public Message(FormatConfiguration conf, Object message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        this(conf, null, message, null, arg0, arg1, arg2, arg3, arg4);
    }

    public Message(FormatConfiguration conf, Throwable throwable, Object message) {
        this(conf, throwable, message, null, null, null, null, null, null);
    }

    public Message(FormatConfiguration conf, Throwable throwable, Object message, Object[] args) {
        this(conf, throwable, message, args, null, null, null, null, null);
    }

    public Message(FormatConfiguration conf, Throwable throwable, Object message, Object arg0) {
        this(conf, throwable, message, null, arg0, null, null, null, null);
    }

    public Message(FormatConfiguration conf, Throwable throwable, Object message, Object arg0, Object arg1) {
        this(conf, throwable, message, null, arg0, arg1, null, null, null);
    }

    public Message(FormatConfiguration conf, Throwable throwable, Object message, Object arg0, Object arg1, Object arg2) {
        this(conf, throwable, message, null, arg0, arg1, arg2, null, null);
    }

    public Message(FormatConfiguration conf, Throwable throwable, Object message, Object arg0, Object arg1, Object arg2, Object arg3) {
        this(conf, throwable, message, null, arg0, arg1, arg2, arg3, null);
    }

    public Message(FormatConfiguration conf, Throwable throwable, Object message, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        this(conf, throwable, message, null, arg0, arg1, arg2, arg3, arg4);
    }

    private Message(FormatConfiguration conf, Throwable throwable, Object message, Object[] args, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        this.conf = conf;
        this.throwable = throwable;
        this.message = message;
        this.args = args;
        this.arg0 = arg0;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
    }

    public String getMessage() {
        return message.toString();
    }

    public FormatConfiguration getConfiguration() {
        return conf;
    }
    
    public Object getArg(int i) {
        if (i == -1) {
            return throwable;
        }
        if (args != null) {
            return args[i];
        } else {
            switch (i) {
                case 0: return arg0;
                case 1: return arg1;
                case 2: return arg2;
                case 3: return arg3;
                case 4: return arg4;
                default:
                    throw new IndexOutOfBoundsException(String.valueOf(i));
            }
        }
    }

    public void toString(Appendable appendable) {
        Formatter.Format(this, appendable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }

}
