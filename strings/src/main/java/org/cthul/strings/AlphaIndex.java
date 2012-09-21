package org.cthul.strings;

/**
 * Converts between integers and excel style column IDs.
 * <pre>
 *   0 <->   A
 *   1 <->   B
 *  25 <->   Z
 *  26 <->  AA
 *  27 <->  AB
 * 702 <-> AAA
 * etc.
 * </pre>
 * @author Arian Treffer
 */
public class AlphaIndex {

    /**
     * Converts a number into an alpha-index.
     * <pre>
     *   0 ->   A
     *   1 ->   B
     *  25 ->   Z
     *  26 ->  AA
     *  27 ->  AB
     * 702 -> AAA
     * etc.
     * </pre>
     * Maximum value is {@code Long.MAX_VALUE-1}
     * @param i number to convert
     * @return aplha-index of {@code i}
     * @see Long#MAX_VALUE
     */
    public static String ToAlpha(long i) {
        return ToAlpha(i, true);
    }
    
    /**
     * Like {@link #ToAlpha(long)}, but allows to choose lower case.
     * @param i number to convert
     * @param uppercase whether output will be in upper case
     * @return aplha-index of {@code i}
     */
    public static String ToAlpha(long i, boolean uppercase) {
        final int A = (uppercase ? 'A' : 'a') - 1;
        if (i < 0)
            throw new IllegalArgumentException(
                    "Argument must be non-negative");
        if (i == Long.MAX_VALUE)
            throw new IllegalArgumentException(
                    "Argument must be smaller than Long.MAX_VALUE");
        i++;
        StringBuilder sb = new StringBuilder();
        while (i > 0) {
            long d = i%26;
            if (d == 0) d = 26;
            sb.append((char)(A + d));
            i = (i-d)/26;
        }
        return sb.reverse().toString();
    }

    /**
     * Converts an alpha-index into a number.
     * <pre>
     *   A ->   0
     *   B ->   1
     *   Z ->  25
     *  AA ->  26
     *  AB ->  27
     * AAA -> 702
     * </pre>
     * Maximum value is {@code "CRPXNLSKVLJFHG"} (or {@code Long.MAX_VALUE-1})
     * <p>
     * All characters of {@code s} have to be between {@code 'A'} and
     * {@code 'Z'}, in upper or lower case.
     * @param s alpha-index
     * @return value of {@code s}
     * @see Long#MAX_VALUE
     */
    public static long FromAlpha(String s) {
        s = s.trim().toUpperCase();
        if (s.isEmpty()) throw new IllegalArgumentException(
                "Empty string not allowed");
        long result = 0;
        for (char c: s.toCharArray()) {
            if (c < 'A' || c > 'Z')
                throw new IllegalArgumentException(
                        "Invalid character '" + c + "'");
            result = result*26 + (c - 'A' + 1);
            if (result < 0)
                throw new IllegalArgumentException(
                        "\"" + s + "\" is too large.");
        }
        return result-1;
    }

}
