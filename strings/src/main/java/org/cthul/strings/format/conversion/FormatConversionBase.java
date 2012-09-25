package org.cthul.strings.format.conversion;

import java.io.IOException;
import org.cthul.strings.format.FormatConversion;
import org.cthul.strings.format.FormatImplBase;

/**
 * Provides some utility methods for implementing a {@link FormatConversion}
 * @author Arian Treffer
 */
public abstract class FormatConversionBase
                extends FormatImplBase
                implements FormatConversion {
    /**
     * Appends {@code csq} to {@code a}, left-justified.
     * @param a
     * @param csq
     * @param pad padding character padding character
     * @param width minimum of characters that will be written
     * @throws IOException 
     */
    protected static void justifyLeft(Appendable a, CharSequence csq, char pad, int width) throws IOException {
        final int padLen = width - csq.length();
        a.append(csq);
        for (int i = 0; i < padLen; i++) a.append(pad);
     }
    
    /**
     * Left-justifies {@code csq}.
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written minimum of characters that will be written
     * @return justified string
     */
    protected static String justifyLeft(CharSequence csq, char pad, int width) {
        try {
            StringBuilder sb = new StringBuilder(width);
            justifyLeft(sb, csq, pad, width);
            return sb.toString();
        } catch (IOException e) {
            throw new AssertionError("StringBuilder failed", e);
        }
    }
    
    /**
     * Appends {@code csq} to {@code a}, right-justified.
     * @param a
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written
     * @throws IOException 
     */
    protected static void justifyRight(Appendable a, CharSequence csq, char pad, int width) throws IOException {
        final int padLen = width - csq.length();
        for (int i = 0; i < padLen; i++) a.append(pad);
        a.append(csq);
    }
    
    /**
     * Right-justifies {@code csq}.
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written
     * @return justified string
     */
    protected static String justifyRight(CharSequence csq, char pad, int width) {
        try {
            StringBuilder sb = new StringBuilder(width);
            justifyRight(sb, csq, pad, width);
            return sb.toString();
        } catch (IOException e) {
            throw new AssertionError("StringBuilder failed", e);
        }
    }
    
    /**
     * Appends {@code csq} to {@code a}, centered.
     * @param a
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written
     * @throws IOException 
     */
    protected static void justifyCenter(Appendable a, CharSequence csq, char pad, int width) throws IOException {
        final int padLen = width - csq.length();
        for (int i = 0; i < padLen/2; i++) a.append(pad);
        a.append(csq);
        for (int i = 0; i < (padLen+1)/2; i++) a.append(pad);
    }
    
    /**
     * Centers {@code csq}.
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written
     * @return justified string
     */
    protected static String justifyCenter(CharSequence csq, char pad, int width) {
        try {
            StringBuilder sb = new StringBuilder(width);
            justifyCenter(sb, csq, pad, width);
            return sb.toString();
        } catch (IOException e) {
            throw new AssertionError("StringBuilder failed", e);
        }
    }

}
