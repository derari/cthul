package org.cthul.strings;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import org.cthul.strings.Formatter;
import org.cthul.strings.format.*;
import org.cthul.strings.format.conversion.*;

/**
 * A reimplementation of {@link java.util.Formatter} that allows to use
 * custom {@link FormatConversion} implementations.
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
 *   ia and jAlpha: {@link AlphaIndexConversion}
 *   iC and jClass: {@link ClassNameConversion}
 *   if and jCase: {@link ConditionalConversion}
 *   iP and jPlural: {@link PluralConversion}
 *   ir and jRomans: {@link RomansConversion}
 *   iS and jSingular: {@link SingularConversion}
 * </pre>
 * 
 * @author Arian Treffer
 */
public class Formatter implements Flushable, AutoCloseable {
    
    private final Appendable out;
    private final FormatterConfiguration conf;
    private IOException lastIOException = null;
    private boolean closed = false;
    
    private java.util.Formatter util = null;;
    private Object[] tmp = null;

    public Formatter() {
        this(null, (FormatterConfiguration) null);
    }

    public Formatter(Appendable out) {
        this(out, (FormatterConfiguration) null);
    }
    
    public Formatter(FormatterConfiguration conf) {
        this(null, conf);
    }
    
    public Formatter(Appendable out, FormatterConfiguration conf) {
        this.out = out != null ? out : new StringBuilder();
        this.conf = conf != null ? conf : FormatterConfiguration.getDefault();
    }
    
    public Formatter(Locale l) {
        this(FormatterConfiguration.getDefault().forLocale(l));
    }
    
    public Formatter(Appendable out, Locale l) {
        this(out, FormatterConfiguration.getDefault().forLocale(l));
    }
    
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
     * <p> The {@link #format(java.util.Locale, java.lang.Object, java.lang.Object[]) format} method
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
        ensureOpen();
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
        ensureOpen();
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
     * @see #format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public Formatter format(Object format, Object... args) {
        return format(null, null, null, format, 0, -1, args);
    }
    
    /**
     * @see #format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public Formatter format(Locale locale, Object format, Object... args) {
        return format(null, locale, null, format, 0, -1, args);
    }
    
    /**
     * @see #format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public Formatter format(Throwable e, Object format, Object... args) {
        return format(null, null, e, format, 0, -1, args);
    }
    
    /**
     * @see #format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public Formatter format(FormatterConfiguration conf, Object format, Object... args) {
        return format(conf, null, format, 0, -1, args);
    }

    /**
     * @see #format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public Formatter format(FormatterConfiguration conf, Throwable e, Object format, Object... args) {
        return format(conf, e, format, 0, -1, args);
    }
    
    /**
     * @see #format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public Formatter format(FormatterConfiguration conf, Object e, Object format, int start, int end, Object... args) {
        return format(conf, null, e, format, start, end, args);
    }
    
    /**
     * @see #format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, int, int, org.cthul.strings.format.FormatArgs) 
     */
    public Formatter format(Object format, FormatArgs args) {
        return format(null, null, format, 0, -1, args);
    }
    
    /**
     * @see #format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, int, int, org.cthul.strings.format.FormatArgs) 
     */
    public Formatter format(FormatterConfiguration conf, Object format, FormatArgs args) {
        return format(conf, null, format, 0, -1, args);
    }
    
    /**
     * @see #format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, int, int, org.cthul.strings.format.FormatArgs) 
     */
    public Formatter format(FormatterConfiguration conf, Object format, int start, int end, FormatArgs args) {
        return format(conf, null, format, start, end, args);
    }
    
    /**
     * Writes a formatted string to this object's destination using the
     * specified format string and arguments. 
     * 
     * @param  conf
     *         The formatter configuration, provides additional formats and
     *         the default locale. If {@code null}, the configuration specified
     *         at the construction of this formatter is used.
     * 
     * @param  locale
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting. If {@code null}, the configuration will be used to
     *         obtain a locale.
     * 
     * @param  e
     *         The object that is used as exception argument {@code E}.
     *
     * @param  format
     *         A format string as described in Format string syntax. If the
     *         object does not implement {@link Localizable}, it is converted
     *         into a string.
     * 
     * @param  start 
     *         The index where parsing the format string begins.
     * 
     * @param  end 
     *         The index where parsing the format string ends 
     *         (-1 for the entire string).
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
    protected Formatter format(FormatterConfiguration conf, Locale locale, Object e, Object format, int start, int end, Object... args) {
        try {
            if (conf == null) conf = this.conf;
            if (locale == null) locale = conf.locale();
            if (locale == null) locale = Locale.ROOT;
            String fString = toFormatString(format, locale);
            if (end < 0) end = fString.length() - end - 1;
            new Parser(conf, locale, e, args).parse(fString, start, end);
        } catch (IOException ex) {
            lastIOException = ex;
        }
        return this;
    }

    /**
     * Writes a formatted string to this object's destination using the
     * specified format string and arguments. 
     * 
     * @param  conf
     *         The formatter configuration, provides additional formats and
     *         the default locale. If {@code null}, the configuration specified
     *         at the construction of this formatter is used.
     * 
     * @param  locale
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting. If {@code null}, the configuration will be used to
     *         obtain a locale.
     * 
     * @param  format
     *         A format string as described in Format string syntax. If the
     *         object does not implement {@link Localizable}, it is converted
     *         into a string.
     * 
     * @param  start 
     *         The index where parsing the format string begins.
     * 
     * @param  end 
     *         The index where parsing the format string ends 
     *         (-1 for the entire string).
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
    protected Formatter format(FormatterConfiguration conf, Locale locale, Object format, int start, int end, FormatArgs args) {
        try {
            if (conf == null) conf = this.conf;
            if (locale == null) locale = conf.locale();
            if (locale == null) locale = Locale.ROOT;
            String fString = toFormatString(format, locale);
            if (end < 0) end = fString.length() - end - 1;
            new Parser(conf, locale, args).parse(fString, start, end);
        } catch (IOException ex) {
            lastIOException = ex;
        }
        return this;
    }

    protected String toFormatString(Object format, Locale locale) {
        if (format instanceof Localizable) {
            return ((Localizable) format).toString(locale);
        } else {
            return String.valueOf(format);
        }
    }
    
    protected class Parser extends FormatStringParser<IOException> implements FormatArgs {
        
        protected final FormatterConfiguration conf;
        protected final Locale locale;
        protected final FormatArgs fArgs;
        protected final Object e;
        protected final Object[] args;
        protected int ucCounter = 0;
        private API api;

        private Parser(FormatterConfiguration conf, Locale locale, Object e, Object[] args, FormatArgs fArgs) {
            this.conf = conf;
            this.locale = locale != null ? locale : conf.locale();
            this.e = e;
            this.args = args;
            this.fArgs = fArgs != null ? fArgs : this;
        }
        
        public Parser(FormatterConfiguration conf, Locale locale, Object e, Object[] args) {
            this(conf, locale, e, args, null);
        }

        public Parser(FormatterConfiguration conf, Locale locale, FormatArgs fArgs) {
            this(conf, locale, null, null, fArgs);
        }

        @Override
        public Object get(int i) {
            return args[i];
        }

        @Override
        public Object get(char c) {
            if (c == 'E') return e;
            throw new IllegalArgumentException("Invalid index '" + c + "'");
        }

        @Override
        public Object get(String s) {
            throw new UnsupportedOperationException();
        }
        
        protected boolean uppercase() {
            return ucCounter > 0;
        }
        
        protected API api() {
            if (api == null) api = new API(this);
            return api;
        }

        @Override
        protected Object getArg(int i) {
            // careful! format strings use one-based indices
            return fArgs.get(i-1);
        }

        @Override
        protected Object getArg(char c) {
            return fArgs.get(c);
        }

        @Override
        protected Object getArg(String s) {
            return fArgs.get(s);
        }
        
        @Override
        protected void appendText(CharSequence csq, int start, int end) throws IOException {
            if (uppercase()) {
                append(Formatter.this.uppercase(csq, start, end, locale));
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
        protected int customShortFormat(char formatId, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws IOException {
            FormatConversion f = conf.shortFormat(formatId);
            if (f == null) {
                throw FormatException.unknownFormat("i" + formatId);
            }
            return format(f, arg, flags, width, precision, formatString, lastPosition, uppercase);
        }

        @Override
        protected int customLongFormat(String formatId, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws IOException {
            FormatConversion f = conf.longFormat(formatId);
            if (f == null) {
                throw FormatException.unknownFormat("j" + formatId);
            }
            return format(f, arg, flags, width, precision, formatString, lastPosition, uppercase);
        }

        @Override
        protected int standardFormat(Matcher matcher, String fId, CharSequence formatString) throws IOException {
            final String format;
            if (matcher.start(G_ARG_ID) < 0) {
                format = matcher.group();
            } else {
                final StringBuilder sb = new StringBuilder(matcher.end()-matcher.start());
                sb.append('%');
                sb.append(formatString, matcher.end(G_ARG_ID), matcher.end());
                format = sb.toString();
            }
            Object arg = getArg(formatString, matcher, G_ARG_ID);
            standardFormat(locale, format, arg);
            return matcher.end();
        }
        
        @Override
        protected void standardFormat(String formatId, Object arg, String flags, int width, int precision) {
            throw new UnsupportedOperationException();
        }

        protected int format(FormatConversion f, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws IOException {
            if (uppercase) ucCounter++;
            try {
                return f.format(api(), arg, locale, flags, width, precision, formatString.toString(), lastPosition);
            } finally {
                if (uppercase) ucCounter--;
            }
        }
        
    }
    
    protected class API implements FormatterAPI {
        
        protected final Parser parser;
        protected final Locale locale;

        public API(Parser parser) {
            this.parser = parser;
            this.locale = parser.locale;
        }

        @Override
        public API append(CharSequence csq) throws IOException {
            if (parser.uppercase()) {
                Formatter.this.append(uppercase(csq, locale));
            } else {
                Formatter.this.append(csq);
            }
            return this;
        }

        @Override
        public API append(CharSequence csq, int start, int end) throws IOException {
            if (parser.uppercase()) {
                Formatter.this.append(uppercase(csq, start, end, locale));
            } else {
                Formatter.this.append(csq, start, end);
            }
            return this;
        }

        @Override
        public API append(char c) throws IOException {
            if (parser.uppercase()) {
                Formatter.this.append(uppercase(c, locale));
            } else {
                Formatter.this.append(c);
            }
            return this;
        }

        @Override
        public void format(String formatString) throws IOException {
            parser.parse(formatString);
        }

        @Override
        public int format(String formatString, int start, int end) throws IOException {
            return parser.parse(formatString, start, end);
        }

        @Override
        public Locale locale() {
            return locale;
        }
        
    }
    
}
