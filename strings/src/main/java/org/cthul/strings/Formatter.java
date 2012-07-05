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
public class Formatter implements Flushable, Closeable {
    
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
        return Format(null, formatString, args);
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
     * @param t error argument
     * @param formatString a format string
     * @param args Arguments referenced by the format specifiers in the format
     *         string.
     * @return A formatted string
     * @see java.util.Formatter
     */
    public static String Format(FormatConfiguration c, Throwable t, String formatString, Object... args) {
        return new Message(c, t, formatString, args).toString();
    }

    /**
     * Formats a message.
     * 
     * @param message
     * @param appendable 
     */
    public static void Format(Message message, Appendable appendable) {
        new Formatter(message, appendable).run();
    }
    
    private final API api = new API();
    
    private final Message message;
    private final Appendable appendable;
    private final FormatConfiguration conf;
    private final Locale locale;
    
    private final String formatString;
    
    private Locale currentLocale = null;
    private Object[] currentArgs = null;
    private int autoIndex = 1;
    private int lastIndex = 0;
    private int ucaseCount = 0;
    private boolean uppercase = false;
    
    private boolean closed = false;
    
    private IOException lastIOException = null;
    
    private final Object[] tmp = new Object[1];
    private final java.util.Formatter util;

    /**
     * Constructs a new formatter with the specified destination and configuration.
     *
     * @param  appendable
     *         Destination for the formatted output.  If {@code appendable} is
     *         {@code null} then a {@link StringBuilder} will be created.
     *
     * @param  conf
     *         The {@linkplain FormatConfiguration configuration} used for
     *         formatting.
     */
    public Formatter(Appendable appendable, FormatConfiguration conf) {
        this(null, conf, appendable);
    }
    
    /**
     * Constructs a new formatter with the specified destination and locale.
     *
     * @param  appendable
     *         Destination for the formatted output.  If {@code appendable} is
     *         {@code null} then a {@link StringBuilder} will be created.
     *
     * @param  locale
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting.  If {@code locale} is {@code null} then no localization
     *         is applied.
     */
    public Formatter(Appendable appendable, Locale locale) {
        this(null, FormatConfiguration.getDefault().forLocale(locale), appendable);
    }
    
    protected Formatter(Message message, Appendable appendable) {
        this(message, message.getConfiguration(), appendable);
    }
    
    private Formatter(Message message, FormatConfiguration conf, Appendable appendable) {
        this.message = message;
        this.appendable = appendable != null ? appendable : new StringBuilder();
        this.conf = conf;
        this.locale = conf.locale();
        formatString = message != null ? message.getMessage() : "";
        util = new java.util.Formatter(appendable, locale);
    }

    protected Message getMessage() {
        return message;
    }

    protected String getFormatString() {
        return formatString;
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
        if (appendable instanceof Closeable) {
            closed = true;
            ((Closeable) appendable).close();
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
        if (appendable instanceof Flushable) {
            ((Flushable) appendable).flush();
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
        return locale;
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
        return appendable;
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
        return appendable.toString();
    }

    protected Formatter append(char c) throws IOException {
        ensureOpen();
        if (uppercase) {
            appendable.append(Character.toString(c).toUpperCase(getLocale()));
        } else {
            appendable.append(c);
        }
        return this;
    }

    protected Formatter append(CharSequence csq) throws IOException {
        ensureOpen();
        if (uppercase) {
            appendable.append(csq.toString().toUpperCase(getLocale()));
        } else {
            appendable.append(csq);
        }
        return this;
    }

    protected Formatter append(CharSequence csq, int start, int end) throws IOException {
        ensureOpen();
        csq = csq.subSequence(start, end);
        if (uppercase) {
            appendable.append(csq.toString().toUpperCase(getLocale()));
        } else {
            appendable.append(csq);
        }
        return this;
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
        return format(locale, format, args);
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
        final Object[] tmpArgs = currentArgs;
        final int tmpAutoIndex = autoIndex;
        final int tmpLastIndex = lastIndex;
        final Locale tmpLocale = currentLocale;
        try {
            currentArgs = args;
            autoIndex = 1;
            lastIndex = 0;
            currentLocale = locale;
            format(format);
        } catch (IOException e) {
            lastIOException = e;
        } finally {
            currentArgs = tmpArgs;
            autoIndex = tmpAutoIndex;
            lastIndex = tmpLastIndex;
            currentLocale = tmpLocale;
        }
        return this;
    }
    
    /**
     * Formats the string.
     * @see FormatterAPI#format(java.lang.String) 
     */
    protected void format(final String format) throws IOException {
        format(format, 0, format.length());
    }
    
    /**
     * Formats the specified range of the format string,
     * using the stored argument values.
     * 
     * @see FormatterAPI#format(java.lang.String, int, int) 
     */
    protected int format(final String formatString, final int start, 
                         final int end) throws IOException {
        final Matcher matcher = PATTERN.matcher(formatString);
        int lastPosition = start;
        while (matcher.find(lastPosition) && matcher.end() <= end) {
            append(formatString, lastPosition, matcher.start());
            lastPosition = matcher.end();

            final String fId = matcher.group(N_FORMAT_ID);
            final char f0 = fId.charAt(0);

            if (f0 == '%') {
                append('%');
            } else if (f0 == 'n') {
                append("\n");
            } else {
                final String argId = matcher.group(N_ARG_ID);
                final int index = getArgIndex(argId);                
                if (f0 == CUSTOM_SHORT || f0 == CUSTOM_SHORT_UC || 
                        f0 == CUSTOM_LONG || f0 == CUSTOM_LONG_UC) {
                    int n = customFormat(matcher, index, fId, 
                                        formatString, lastPosition);
                    lastPosition += n;
                } else {
                    stringFormat(matcher, argId, index);
                }
            }

        }
        if (lastPosition < end) {
            append(formatString, lastPosition, end);
            lastPosition = end;
        }
        return lastPosition;
    }

    /** @return argument for one-based index */
    private Object getArg(final int index) {
        return currentArgs != null ? currentArgs[index-1] : message.getArg(index-1);
    }
    
    /** @return current locale */
    private Locale getLocale() {
        return currentLocale != null ? currentLocale : locale;
    }

    /** @return short or long format from the format configuration */
    private Format getFormat(final String f) throws FormatException {
        if (f.length() < 2) {
            throw new FormatException("Expected format Id after " + f);
        }
        final Format format;
        switch (f.charAt(0)) {
            case CUSTOM_SHORT:
            case CUSTOM_SHORT_UC:
                final char cfId = f.charAt(1);
                format = conf.shortFormat(cfId);
                break;
            case CUSTOM_LONG:
            case CUSTOM_LONG_UC:
                final int semicolon = f.endsWith(";") ? 1 : 0;
                final String fmtId = f.substring(1, f.length() - semicolon);
                format = conf.longFormat(fmtId);
                break;
            default:
                format = null;
        }
        if (format == null) {
            throw new FormatException("Unknown custom format " + f);
        }
        return format;
    }

    /**
     * Invokes {@link #format(java.lang.String)} with the 
     * {@linkplain #getFormatString() format string}.
     * Can be published by subclasses.
     */
    protected void run() {
        try {
            format(formatString);
        } catch (IOException e) {
            throw new FormatException(e);
        }
    }
    
    private int getArgIndex(String argId) {
        if (argId == null || argId.isEmpty()) {
            lastIndex = autoIndex++;
        } else {
            switch (argId.charAt(0)) {
                case 'E':
                    lastIndex = 0;
                    break;
                case '`':
                case '<':
                    break;
                case '´':
                case '>':
                    lastIndex++;
                    break;
                case '$':
                case '.':
                    lastIndex = autoIndex++;
                    break;
                default:
                    lastIndex = Integer.parseInt(argId.substring(0, argId.length()-1));
            }
        }
        return lastIndex;
    }
    
    private void stringFormat(Matcher matcher, String argId, int argIndex) throws IOException {
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
        tmp[0] = getArg(argIndex);
        util.format(format, tmp);
        IOException e = util.ioException();
        if (e != null) throw e;
    }
    
    private int customFormat(final Matcher matcher, final int index, 
                             final String f,
                             String input, int position) throws IOException {
        final boolean forceUppercase = f.charAt(0) < 'a';
        final Format format = getFormat(f);
        final Object value = getArg(index);
        final String flags = getFlags(matcher);
        final int width = getIntGroup(matcher, N_WIDTH);
        final int precision = getIntGroup(matcher, N_PRECISION);
        return execFormat(forceUppercase, format, value, flags, width, precision, input, position);
    }
    
    private int getIntGroup(Matcher matcher, int groupId) {
        final String group = matcher.group(groupId);
        if (group == null || group.isEmpty()) {
            return -1;
        } else {
            return Integer.parseInt(group);
        }
    }
    
    private String getFlags(Matcher matcher) {
        final String flags = matcher.group(N_FLAGS);
        if (flags == null || flags.isEmpty()) return null;
        return flags;
    }
    
    private int execFormat(final boolean uppercase, final Format format, 
                           final Object value, final String flags, 
                           final int width, final int precision, String input, 
                           int position) throws IOException {
        if (uppercase) {
            ucaseCount++;
            this.uppercase = true;
        }
        final int result;
        try {
            result = format.format(api, value, getLocale(), flags, width, 
                                   precision, input, position);
        } finally {
            if (uppercase) {
                ucaseCount--;
                this.uppercase = ucaseCount > 0;
            }
        }
        return result;
    }
    
    private class API implements FormatterAPI {

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            Formatter.this.append(csq);
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            Formatter.this.append(csq, start, end);
            return this;
        }

        @Override
        public Appendable append(char c) throws IOException {
            Formatter.this.append(c);
            return this;
        }

        @Override
        public void format(String formatString) throws IOException {
            Formatter.this.format(formatString);
        }

        @Override
        public int format(String formatString, int start, int end) throws IOException {
            return Formatter.this.format(formatString, start, end);
        }

        @Override
        public Formatter formatter() {
            return Formatter.this;
        }
        
    }
    
    public static final char CUSTOM_SHORT = 'i';
    public static final char CUSTOM_LONG = 'j';
    
    public static final char CUSTOM_SHORT_UC = 'I';
    public static final char CUSTOM_LONG_UC = 'J';
    
    private static final String ARG_ID = "(\\d+\\$|E\\$|\\.?\\$|[<>`´]\\$?)?";
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
