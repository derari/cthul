package org.cthul.strings;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.cthul.strings.format.*;

/**
 * A reimplementation of {@link java.util.Formatter} that allows to use
 * custom {@link Format} implementations.
 * <p>
 * Custom formats must be registered and can be identified either by
 * {@code %iX}, where X is their short ID, or {@code %jXxx}, where Xxx is their
 * long ID. Format IDs are case sensitive. Using {@code I} or {@code J} selects 
 * the same format, but converts the format's output into upper case. 
 * For better integration in text, long format IDs can be terminated 
 * with {@code ;}.
 * <p>
 * A format is specified as 
 * {@code %(ARG_ID$)(FLAGS)(WIDTH)(.PRECISION)(FORMAT_ID)}
 * <p>
 * ARG_ID References the value for formatting
 * <pre>
 *   3$ -> third argument
 *   E$ -> exception argument
 *   $ or .$ -> auto index (default)
 *   <$, <, `$ or ` -> last value
 *   >$, >, ´$ or ´ -> next value
 * </pre>
 * If you want to use $, <, >, ` or ´ as a flag, specify $ as argument id to
 * enforce auto indexing.
 * <p>
 * FLAGS a set of characters, effects depend on format. <br/>
 * The following flags are recommended for formats that support the WIDTH option:
 * <pre>
 *   - -> left-justify output
 *   | -> center output
 *        (default: right-justified)
 *   _ -> padding with underscore
 *   0 -> padding with zeros
 *        (default: space)
 * </pre>
 * <p>
 * WIDTH Minimum number of characters that will be written to output.
 * <p>
 * PRECISION Format specific
 * <p>
 * FORMAT_ID The format. <br/>
 * See {@link java.util.Formatter} for default formats;
 * custom formats are explained above.
 * By default, the following formats are registered:
 * <pre>
 *   ia and jAlpha: {@link AlphaIndexFormat}
 *   iC and jClass: {@link ClassNameFormat}
 *   iP and jPlural: {@link PluralFormat}
 *   ir and jRomans: {@link RomansFormat}
 *   iS and jSingular: {@link SingularFormat}
 * </pre>
 * 
 * @author Arian Treffer
 */
public class Formatter implements Flushable, AutoCloseable {
    
    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     * 
     * @param formatString a format string
     * @param args Arguments referenced by the format specifiers in the format
     *         string.
     * @return A formatted string
     * @see java.util.Formatter
     */
    public static String Format(String formatString, Object... args) {
        return Format((FormatConfiguration) null, formatString, args);
    }
    
    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     * 
     * @param t error argument
     * @param formatString a format string
     * @param args Arguments referenced by the format specifiers in the format
     *         string.
     * @return A formatted string
     * @see java.util.Formatter
     */
    public static String Format(Throwable t, String formatString, Object... args) {
        return Format(FormatConfiguration.getDefault(), t, formatString, args);
    }

    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     * 
     * @param c A {@linkplain FormatConfiguration format configuration}.
     * @param formatString a format string
     * @param args Arguments referenced by the format specifiers in the format
     *         string.
     * @return A formatted string
     * @see java.util.Formatter
     */
    public static String Format(FormatConfiguration c, String formatString, Object... args) {
        return Format(c, null, formatString, args);
    }
    
    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     * 
     * @param c A {@linkplain FormatConfiguration format configuration}.
     * @param t error argument
     * @param formatString a format string
     * @param args Arguments referenced by the format specifiers in the format
     *         string.
     * @return A formatted string
     * @see java.util.Formatter
     */
    public static String Format(FormatConfiguration c, Throwable t, String formatString, Object... args) {
        return new Formatter(c).format(t, formatString, args).toString();
    }
    
    private final Appendable out;
    private final FormatConfiguration conf;
    private IOException lastIOException = null;
    private boolean closed = false;
    
    private java.util.Formatter util = null;;
    private Object[] tmp = null;

    public Formatter() {
        this(null, (FormatConfiguration) null);
    }

    public Formatter(Appendable out) {
        this(out,(FormatConfiguration) null);
    }
    
    public Formatter(FormatConfiguration conf) {
        this(null, conf);
    }
    
    public Formatter(Appendable out, FormatConfiguration conf) {
        this.out = out != null ? out : new StringBuilder();
        this.conf = conf != null ? conf : FormatConfiguration.getDefault();
    }
    
    public Formatter(Locale l) {
        this(FormatConfiguration.getDefault().forLocale(l));
    }
    
    public Formatter(Appendable out, Locale l) {
        this(out, FormatConfiguration.getDefault().forLocale(l));
    }
    
    
//    /**
//     * Constructs a new formatter with the specified destination and configuration.
//     *
//     * @param  appendable
//     *         Destination for the formatted output.  If {@code appendable} is
//     *         {@code null} then a {@link StringBuilder} will be created.
//     *
//     * @param  conf
//     *         The {@linkplain FormatConfiguration configuration} used for
//     *         formatting.
//     */
//    public Formatter(Appendable appendable, FormatConfiguration conf) {
//        this(null, conf, appendable);
//    }
//    
//    /**
//     * Constructs a new formatter with the specified destination and locale.
//     *
//     * @param  appendable
//     *         Destination for the formatted output.  If {@code appendable} is
//     *         {@code null} then a {@link StringBuilder} will be created.
//     *
//     * @param  locale
//     *         The {@linkplain java.util.Locale locale} to apply during
//     *         formatting.  If {@code locale} is {@code null} then no localization
//     *         is applied.
//     */
//    public Formatter(Appendable appendable, Locale locale) {
//        this(null, FormatConfiguration.getDefault().forLocale(locale), appendable);
//    }
//
//    public Formatter(Appendable appendable, FormatConfiguration conf, Locale) {
//        this.appendable = appendable;
//        this.conf = conf;
//        this.locale = locale;
//    }
//
//
//    
//    protected Formatter(Message message, Appendable appendable) {
//        this(message, message.getConfiguration(), appendable);
//    }

    private void ensureOpen() {
        if (closed) {
            throw new FormatterClosedException();
        }
    }

    /**
     * Returns the {@code IOException} last thrown by this formatter's {@link
     * Appendable}.
     *
     * <p> If the destination's {@code append()} method never throws
     * {@code IOException}, then this method will always return {@code null}.
     *
     * @return  The last exception thrown by the Appendable or {@code null} if
     *          no such exception exists.
     */
    public IOException ioException() {
        return lastIOException;
    }
    
    /**
     * Closes this formatter.  If the destination implements the {@link
     * java.io.Closeable} interface, its {@code close} method will be invoked.
     *
     * <p> Closing a formatter allows it to release resources it may be holding
     * (such as open files).  If the formatter is already closed, then invoking
     * this method has no effect.
     *
     * <p> Attempting to invoke any methods except {@link #ioException()} in
     * this formatter after it has been closed will result in a {@link
     * FormatterClosedException}.
     */
    @Override
    public void close() throws IOException {
        if (out instanceof Closeable) {
            closed = true;
            ((Closeable) out).close();
        }
    }

    /**
     * Flushes this formatter.  If the destination implements the {@link
     * java.io.Flushable} interface, its {@code flush} method will be invoked.
     *
     * <p> Flushing a formatter writes any buffered output in the destination
     * to the underlying stream.
     *
     * @throws  FormatterClosedException
     *          If this formatter has been closed by invoking its {@link
     *          #close()} method
     */
    @Override
    public void flush() throws IOException {
        ensureOpen();
        if (out instanceof Flushable) {
            ((Flushable) out).flush();
        }
    }
    
    /**
     * Returns the locale set by the construction of this formatter.
     *
     * <p> The {@link #format(java.util.Locale,String,Object...) format} method
     * for this object which has a locale argument does not change this value.
     *
     * @return  {@code null} if no localization is applied, otherwise a
     *          locale
     *
     * @throws  FormatterClosedException
     *          If this formatter has been closed by invoking its {@link
     *          #close()} method
     */
    public Locale locale() {
        return conf.locale();
    }
    
    /**
     * Returns the destination for the output.
     *
     * @return  The destination for the output
     *
     * @throws  FormatterClosedException
     *          If this formatter has been closed by invoking its {@link
     *          #close()} method
     */
    public Appendable out() {
        return out;
    }

    /**
     * Returns the result of invoking {@code toString()} on the destination
     * for the output.  For example, the following code formats text into a
     * {@link StringBuilder} then retrieves the resultant string:
     *
     * <blockquote><pre>
     *   Formatter f = new Formatter();
     *   f.format("Last reboot at %tc", lastRebootDate);
     *   String s = f.toString();
     *   // -&gt; s == "Last reboot at Sat Jan 01 00:00:00 PST 2000"
     * </pre></blockquote>
     *
     * <p> An invocation of this method behaves in exactly the same way as the
     * invocation
     *
     * <pre>
     *     out().toString() </pre>
     *
     * <p> Depending on the specification of {@code toString} for the {@link
     * Appendable}, the returned string may or may not contain the characters
     * written to the destination.  For instance, buffers typically return
     * their contents in {@code toString()}, but streams cannot since the
     * data is discarded.
     *
     * @return  The result of invoking {@code toString()} on the destination
     *          for the output
     *
     * @throws  FormatterClosedException
     *          If this formatter has been closed by invoking its {@link
     *          #close()} method
     */
    @Override
    public String toString() {
        return out.toString();
    }
    
    protected void append(char c) throws IOException {
        ensureOpen();
        out.append(c);
    }

    protected void append(CharSequence csq) throws IOException {
        ensureOpen();
        out.append(csq);
    }

    protected void append(CharSequence csq, int start, int end) throws IOException {
        ensureOpen();
        out.append(csq, start, end);
    }
    
    protected String uppercase(char c, Locale l) {
        return uppercase(new String(new char[]{c}), l);
    }
    
    protected String uppercase(CharSequence csq, Locale l) {
        return csq.toString().toUpperCase(l);
    }
    
    protected String uppercase(CharSequence csq, int start, int end, Locale l) {
        return uppercase(csq.subSequence(start, end), l);
    }
    
    protected void standardFormat(Locale l, String format, Object arg) throws IOException {
        if (util == null) {
            util = new java.util.Formatter(out, conf.locale());
            tmp = new Object[1];
        }
        tmp[0] = arg;
        util.format(l, format, tmp);
        IOException e = util.ioException();
        if (e != null) throw e;
    }
    
    /**
     * Writes a formatted string to this object's destination using the
     * specified format string and arguments.  The locale used is the one
     * defined during the construction of this formatter.
     *
     * @param  format
     *         A format string as described in Format string
     *         syntax.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.
     *
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.
     *
     * @throws  FormatterClosedException
     *          If this formatter has been closed by invoking its {@link
     *          #close()} method
     *
     * @return  This formatter
     * @see java.util.Formatter#format(java.lang.String, java.lang.Object[])
     */
    public Formatter format(final String format, final Object... args) {
        return format(conf, format, args);
    }
    
    /**
     * Writes a formatted string to this object's destination using the
     * specified locale, format string, and arguments.
     *
     * @param  locale
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting.
     *
     * @param  format
     *         A format string as described in Format string
     *         syntax.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.
     *
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.
     *
     * @throws  FormatterClosedException
     *          If this formatter has been closed by invoking its {@link
     *          #close()} method
     *
     * @return  This formatter
     * @see java.util.Formatter#format(java.util.Locale, java.lang.String, java.lang.Object[]) 
     */
    public Formatter format(final Locale locale, final String format, final Object... args) {
        return format(conf.forLocale(locale), format, args);
    }
    
    /**
     * Writes a formatted string to this object's destination using the
     * specified format string and arguments.  The locale used is the one
     * defined during the construction of this formatter.
     *
     * @param  format
     *         A format string as described in Format string
     *         syntax.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.
     *
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.
     *
     * @throws  FormatterClosedException
     *          If this formatter has been closed by invoking its {@link
     *          #close()} method
     *
     * @return  This formatter
     * @see java.util.Formatter#format(java.lang.String, java.lang.Object[])
     */
    public Formatter format(final Object e, final String format, final Object... args) {
        return format(conf, e, format, args);
    }
    
    /**
     * Writes a formatted string to this object's destination using the
     * specified locale, format string, and arguments.
     *
     * @param  locale
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting.
     *
     * @param  format
     *         A format string as described in Format string
     *         syntax.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.
     *
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.
     *
     * @throws  FormatterClosedException
     *          If this formatter has been closed by invoking its {@link
     *          #close()} method
     *
     * @return  This formatter
     * @see java.util.Formatter#format(java.util.Locale, java.lang.String, java.lang.Object[]) 
     */
    public Formatter format(final FormatConfiguration conf, final String format, final Object... args) {
        return format(conf, null, format, args);
    }

    /**
     * Writes a formatted string to this object's destination using the
     * specified locale, format string, and arguments.
     *
     * @param  locale
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting.
     *
     * @param  format
     *         A format string as described in Format string
     *         syntax.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.
     *
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.
     *
     * @throws  FormatterClosedException
     *          If this formatter has been closed by invoking its {@link
     *          #close()} method
     *
     * @return  This formatter
     * @see java.util.Formatter#format(java.util.Locale, java.lang.String, java.lang.Object[]) 
     */
    public Formatter format(final FormatConfiguration conf, final Object e, final String format, final Object... args) {
        return format(conf, e, format, 0, format.length(), args);
    }
    
    public Formatter format(final FormatConfiguration conf, final Object e, final String format, int start, int end, final Object... args) {
        try {
            new Parser(conf, e, args, false).parse(format, start, end);
        } catch (IOException ex) {
            lastIOException = ex;
        }
        return this;
    }
    
    protected class Parser extends FormatPatternParser<IOException> {
        
        protected final FormatConfiguration conf;
        protected final Locale locale;
        protected final Object e;
        protected final Object[] args;
        protected final boolean uppercase;
        private API api;
        private API apiUc;
        private Parser parserUc;

        public Parser(FormatConfiguration conf, Object e, Object[] args, boolean uppercase) {
            this.conf = conf;
            this.locale = conf.locale();
            this.e = e;
            this.args = args;
            this.uppercase = uppercase;
        }
        
        protected API api(boolean uppercase) {
            if (uppercase | this.uppercase) {
                if (apiUc == null) {
                    apiUc = new API(this, true);
                }
                return apiUc;                
            } else {
                if (api == null) {
                    api = new API(this, false);
                }
                return api;
            }
        }
        
        protected Object arg(int i) {
            if (i == 0) return e;
            return args[i-1];
        }

        @Override
        protected int parseCharIndex(char c) {
            if (c == 'E') return 0;
            throw new IllegalArgumentException("Invalid index '" + c + "'");
        }
        
        @Override
        protected void appendText(CharSequence csq, int start, int end) throws IOException {
            if (uppercase) {
                append(uppercase(csq, start, end, locale));
            } else {
                append(csq, start, end);
            }
        }

        @Override
        protected void appendPercent() throws IOException {
            append("%");
        }

        @Override
        protected void appendNewLine() throws IOException {
            append("\n");
        }

        @Override
        protected int customShortFormat(char formatId, int argId, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws IOException {
            Format f = conf.shortFormat(formatId);
            return f.format(api(uppercase), arg(argId), locale, flags, width, precision, formatString.toString(), lastPosition);
        }

        @Override
        protected int customLongFormat(String formatId, int argId, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws IOException {
            Format f = conf.longFormat(formatId);
            return f.format(api(uppercase), arg(argId), locale, flags, width, precision, formatString.toString(), lastPosition);
        }

        @Override
        protected void standardFormat(Matcher matcher, String fId) throws IOException {
            final String argId = matcher.group(G_ARG_ID);
            final String group = matcher.group();
            final String format;
            if (argId == null || argId.isEmpty()) {
                format = group;
            } else {
                final StringBuilder sb = new StringBuilder(group.length());
                sb.append('%');
                sb.append(group, 1 + argId.length(), group.length());
                format = sb.toString();
            }
            Object arg = arg(parseArgIndex(argId));
            standardFormat(locale, format, arg);
        }
        
        @Override
        protected void standardFormat(String formatId, int argId, String flags, int width, int precision) {
            throw new UnsupportedOperationException();
        }
        
        protected Parser getParser(boolean uppercase) {
            if (uppercase == this.uppercase) return this;
            if (parserUc == null) {
                parserUc = createParser(uppercase);
            }
            return parserUc;
        }

        protected Parser createParser(boolean uppercase) {
            return new Parser(conf, e, args, uppercase);
        }
        
    }
    
    protected class API implements FormatterAPI {
        
        protected final Parser parser;
        protected final Locale locale;
        protected final boolean uppercase;

        public API(Parser parser, boolean uppercase) {
            this.parser = parser;
            this.locale = parser.locale;
            this.uppercase = uppercase;
        }

        @Override
        public API append(CharSequence csq) throws IOException {
            if (uppercase) {
                Formatter.this.append(uppercase(csq, locale));
            } else {
                Formatter.this.append(csq);
            }
            return this;
        }

        @Override
        public API append(CharSequence csq, int start, int end) throws IOException {
            if (uppercase) {
                Formatter.this.append(uppercase(csq, start, end, locale));
            } else {
                Formatter.this.append(csq, start, end);
            }
            return this;
        }

        @Override
        public API append(char c) throws IOException {
            if (uppercase) {
                Formatter.this.append(uppercase(c, locale));
            } else {
                Formatter.this.append(c);
            }
            return this;
        }

        @Override
        public void format(String formatString) throws IOException {
            parser.getParser(uppercase).parse(formatString);
        }

        @Override
        public int format(String formatString, int start, int end) throws IOException {
            return parser.getParser(uppercase).parse(formatString, start, end);
        }

        @Override
        public Locale locale() {
            return locale;
        }
        
    }
    
    public static final char CUSTOM_SHORT = 'i';
    public static final char CUSTOM_LONG = 'j';
    
    public static final char CUSTOM_SHORT_UC = 'I';
    public static final char CUSTOM_LONG_UC = 'J';
    
    private static final String ARG_ID = "(\\d+\\$|[a-zA-Z]\\$|\\.?\\$|[<>`´]\\$?)?";
    private static final String FLAGS = "([^.1-9a-zA-Z%]+)?"; // [-#+ 0,(\\<]
    private static final String WIDTH_PRECISION = "(\\d+)?(\\.\\d+)?";
    private static final String FORMAT_ID = "(([jJ][_a-zA-Z0-9]+[;]?)|([tTiI]?[a-zA-Z%]))";
    private static final Pattern PATTERN;
    
    private static final int N_ARG_ID    = 1;
    private static final int N_FLAGS     = 2;
    private static final int N_WIDTH     = 3;
    private static final int N_PRECISION = 4;
    private static final int N_FORMAT_ID = 5;
    
    static {
        try {
            PATTERN = Pattern.compile(
                      "%" + ARG_ID + FLAGS + WIDTH_PRECISION + FORMAT_ID);
        } catch (PatternSyntaxException e) {
            System.err.println(e);
            throw e;
        }
    }
    
}
