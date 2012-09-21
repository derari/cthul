package org.cthul.strings.format;

import java.io.IOException;
import java.util.Locale;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;

/**
 *
 * @author Arian Treffer
 */
public class FormatterAPIStub implements FormatterAPI {
    
    public static Proc formatCall = Procs.invoke("format", 8);
    
    public static String format(FormatConversion conv, Object value, Locale locale, String flags, int width, int precision, String formatString, int position) {
        FormatterAPIStub api = new FormatterAPIStub();
        try {
            conv.format(api, value, locale, flags, width, precision, formatString, position);
        } catch (IOException e) {
            throw new RuntimeException(api.toString(), e);
        }
        return api.toString();
    }
    
    public static Proc formatCall(FormatConversion conv, Object value, Locale locale, String flags, int width, int precision, String formatString, int position) {
        return formatCall.call(conv, value, locale, flags, width, precision, formatString, position);
    }

    private final StringBuilder sb = new StringBuilder();

    @Override
    public FormatterAPI append(CharSequence csq) {
        sb.append(csq);
        return this;
    }

    @Override
    public FormatterAPI append(CharSequence csq, int start, int end) {
        sb.append(csq, start, end);
        return this;
    }

    @Override
    public FormatterAPI append(char c) {
        sb.append(c);
        return this;
    }

    @Override
    public void format(String format) {
        sb.append(format);
    }

    @Override
    public int format(String format, int start, int end) {
        sb.append(format, start, end);
        return end;
    }

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
    
}
