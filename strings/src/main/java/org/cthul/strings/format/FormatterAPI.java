package org.cthul.strings.format;

import java.io.IOException;
import java.util.Locale;
import org.cthul.strings.Formatter;

/**
 * A special interface the {@link Formatter} publishes to 
 * {@link FormatConversion} implementations.
 * 
 * @author Arian Treffer
 */
public interface FormatterAPI extends Appendable {
    
    /** {@inheritDoc} */
    @Override
    FormatterAPI append(CharSequence csq) throws IOException;
    
    /** {@inheritDoc} */
    @Override
    FormatterAPI append(CharSequence csq, int start, int end) throws IOException;
    
    /** {@inheritDoc} */
    @Override
    FormatterAPI append(char c) throws IOException;
    
    /**
     * Formats the given string using the arguments of the current
     * formatter call.
     * @param format format string
     * @throws IOException 
     */
    void format(String format) throws IOException;
    
    /**
     * Formats the specified part of the given string, using the arugments
     * of the current formatter call.
     * @param format format string
     * @param start start of range
     * @param end end of range
     * @return end of consumend input (can be greater than {@code end} if the
     *         last format consumend additional input)
     * @throws IOException 
     */
    int format(String format, int start, int end) throws IOException;
    
    Locale locale();
    
}
