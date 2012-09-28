package org.cthul.strings;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Converts integers to roman numbers.
 * @author Arian Treffer
 */
public class Romans implements Serializable {

    private static final long serialVersionUID = 8284062658638767444L;
    
    /**
     * Alternative letters for generating roman numbers: <p/>
     *
     * {@code "I", "V", "X", "L", "C", "D", "M", "ↁ", "ↂ"}
     */
    public static final String[] LETTERS_ALT =
            {"I", "V", "X", "L", "C", "D", "M", "ↁ", "ↂ"};

    public static final int LETTERS_ALT_MAXL = MaxRomanLetterValue(LETTERS_ALT);

    /**
     * Default letters used for generating roman numbers: <p/>
     *
     * I V X L C D M V\u0305 X\u0305 L\u0305 C\u0305 D\u0305 M\u0305
     */
    public static final String[] LETTERS =
            {"I", "V", "X", "L", "C", "D", "M", "V\u0305", "X\u0305",
             "L\u0305", "C\u0305", "D\u0305", "M\u0305"};

    public static final int LETTERS_MAXL = MaxRomanLetterValue(LETTERS);

    /**
     * Default value for zero.
     * <p>
     * Actual value is {@code "N"}.
     */
    public static final String ZERO = "N";

    /**
     * Defines roman numbers with the subtraction rule.
     * The resulting numbers will be I, II, III, IV, V, VI, VII, VIII, IX, X.
     * <p>
     * Actual value is
     * {@code {{},{0},{0,0},{0,0,0},{0,1},{1},{1,0},{1,0,0},{1,0,0,0},{0,2}}}
     * @see Romans#DIGITS_SIMPLE
     */
    public static final int[][] DIGITS = {
        {},{0},{0,0},{0,0,0},{0,1},{1},
        {1,0},{1,0,0},{1,0,0,0},{0,2}};

    /**
     * Defines roman numbers without the subtraction rule.
     * The resulting numbers will be I, II, III, IIII, V, VI, VII, VIII, VIIII, X.
     * <p>
     * Actual value is
     * {@code {{},{0},{0,0},{0,0,0},{0,0,0,0},{1},{1,0},{1,0,0},{1,0,0,0},{1,0,0,0,0}}}
     * @see Romans#DIGITS
     */
    public static final int[][] DIGITS_SIMPLE = {
        {},{0},{0,0},{0,0,0},{0,0,0,0},{1},
        {1,0},{1,0,0},{1,0,0,0},{1,0,0,0,0}};

    public static final Romans DEFAULT = new Romans();

    /**
     * Given an array of letters, returns the value of the highest letter.
     * @param letters
     * @return value of highest
     */
    public static int MaxRomanLetterValue(final String... letters) {
        int v = 1;
        for (int i = 1; i < letters.length; i++) {
            v *= i%2==0 ? 2 : 5;
        }
        return v;
    }

    /**
     * Converts an integer into a roman number.
     * @param number
     * @return roman number
     */
    public static String ToRoman(int number) {
        return DEFAULT.toRoman(number);
    }
    
    /**
     * Converts an integer into a roman number.
     * @param number
     * @return roman number
     */
    public static String ToRoman2(int number) {
        return DEFAULT.toRoman2(number);
    }
    
    /**
     * Converts a roman number into an integer.
     * @param number
     * @return integer value of {@code number}
     */
    public static int FromRoman(String number) {
        return DEFAULT.fromRoman(number);
    }

    /**
     * Converts an integer into a roman number.
     * 
     * @param number    the value to convert
     * @param digits    digit codes,
     *          i.e. {@link Romans#DIGITS DIGITS} or
     *          {@link Romans#DIGITS_SIMPLE DIGITS_SIMPLE}
     * @param zero      representation of zero or {@code null}
     * @param letters   letters to use for roman number
     * @throws IllegalArgumentException if {@code number} is negative or
     *          too large to be represented with the given letters or if
     *          {@code number} is 0 and {@code zero} is null.
     */
    public static String ToRoman(final int number, final int[][] digits,
                               final String zero, final String[] letters) {
        StringBuilder sb = new StringBuilder();
        int maxVal = MaxRomanLetterValue(letters);
        ToRoman(number, sb, digits, zero, letters, maxVal);
        return sb.toString();
    }

    /**
     * Converts an integer into a roman number. See {@link Romans#ToRoman(int,
     * int[][], java.lang.String, java.lang.String[]) ToRoman\4}
     * for more documentation.
     * 
     * @param number    the value to convert
     * @param target    the string builder that will contain the result
     * @param digits    digit codes
     * @param zero      representation of zero or {@code null}
     * @param letters   letters to use for roman number
     * @param maxLetter integer value of the highest letter,
     *          can be computed with
     *          {@link Romans#MaxRomanLetterValue MaxRomanLetterValue}
     * @throws IllegalArgumentException if {@code number} is invalid.
     * @see Romans#ToRoman(int, int[][], java.lang.String, java.lang.String[])
     */
    public static void ToRoman(final int number, final StringBuilder target,
                               final int[][] digits, final String zero,
                               final String[] letters, final int maxLetter) {
        final boolean maxLetterIs5 = letters.length%2 == 0;
        final int maxVal = maxLetterIs5 ? 9*maxLetter/5 : 4*maxLetter;
        if (number < 0 || number >= maxVal) throw new IllegalArgumentException(
                number + " is not between 0 and " + (maxVal-1));
        if (number == 0) {
            if (zero == null) throw new IllegalArgumentException(
                    "Zero is not allowed");
            target.append(zero);
        } else {
            // process number by decimal digits
            // v: value of current digit, e.g. 1000
            // i: `letters` index of current digit, e.g. 6 for 1000 (letters[6] == "M")
            int v = maxLetterIs5 ? maxLetter * 2 : maxLetter;
            int i = maxLetterIs5 ? letters.length : letters.length-1;
            for (; v > 0; v/= 10, i -= 2) {
                // select and append encoding for current digit
                int[] d = digits[(number/v)%10];
                for (int n: d) target.append(letters[i+n]);
            }
        }
    }
    
    /**
     * @see #toRoman2(int) 
     */
    public static void ToRoman2(int number, final StringBuilder target,
                                final String zero, final String[] letters,
                                final int maxLetter) {
        final boolean maxLetterIs5 = letters.length%2 == 0;
        final int maxVal = maxLetterIs5 ? 9*maxLetter/5 : 4*maxLetter;
        if (number < 0 || number >= maxVal) throw new IllegalArgumentException(
                number + " is not between 0 and " + (maxVal-1));
        if (number == 0) {
            if (zero == null) throw new IllegalArgumentException(
                    "Zero is not allowed");
            target.append(zero);
        } else {
            // process number by roman digits
            // v: value of current roman digit, e.g. 500
            // i: `letters` index of current digit, e.g. 5 for 500 (letters[5] == "D")
            int v = maxLetter;
            int i = letters.length - 1;
            for (;number > 0; v /= (is5(i) ? 5 : 2), i -= 1) {
                // append current digit as often as possible
                while (number >= v) {
                    target.append(letters[i]);
                    number -= v;
                }
                // try to find a smaller digit that is a power of 10 and, when
                // added to `number`, allows appending current digit once more
                int i2 = 0;
                int v2 = 1;
                while (i2 < i) {
                    if (number + v2 >= v) {
                        // smaller digit found, append subtraction construct
                        target.append(letters[i2]);
                        target.append(letters[i]);
                        number -= v - v2;
                        break;
                    }
                    i2 += 2;
                    v2 *= 10;
                }
            }
        }
    }
    
    /**
     * @return true iif {@code i} is the index of a 5-digit (V, L, D, ...)
     */
    private static boolean is5(int i) {
        return i % 2 != 0;
    }
    
    /**
     * Parses a roman number.
     * <p>
     * See {@link #fromRoman(java.lang.String)} for an explanation of the 
     * semantics.
     * 
     * @param number
     * @param letters
     * @param zero
     * @return integer value of {@code number}
     */
    public static int FromRoman(final String number, final String[] letters, final String zero) {
        if (number.equals(zero)) {
            return 0;
        }
        int total = 0;
        int lastValue = 0;
        int lastTotal = 0;
        int i = 0;
        while (i < number.length()) {
            int value = 0;
            int v = 1;
            int vLen = 0;
            for (int j = 0; j < letters.length; j++) {
                final String l = letters[j];
                if (vLen < l.length() && number.startsWith(l, i)) {
                    vLen = l.length();
                    value = v;
                }
                v *= is5(j) ? 2 : 5;
            }
            if (value == 0) {
                throw new IllegalArgumentException(
                        "Could not match digit at: " + number.substring(i));
            }
            if (lastValue == value) {
                lastTotal += value;
            } else {
                if (lastValue < value) {
                    total -= lastTotal;
                } else {
                    total += lastTotal;
                }
                lastTotal = lastValue = value;
            }
            assert vLen != 0 : "Digit should be matched";
            i += vLen;
        }
        total += lastTotal;
        return total;
    }

    private final String[] letters;
    private final int max;
    private final String zero;
    private final int[][] digits;

    /**
     * Creates a new int-to-romans converter.
     * @param letters
     * @param zero
     * @param digits
     */
    public Romans(String[] letters, String zero, int[][] digits) {
        this.letters = Arrays.copyOf(letters, letters.length);
        this.zero = zero;
        this.digits = new int[10][];
        for (int i = 0; i < 10; i++)
            this.digits[i] = Arrays.copyOf(digits[i], digits[i].length);
        this.max = MaxRomanLetterValue(this.letters);
    }

    public Romans(String... letters) {
        this(letters, ZERO, DIGITS);
    }

    public Romans() {
        this(LETTERS, ZERO, DIGITS);
    }

    public void toRoman(int number, StringBuilder target) {
        ToRoman(number, target, digits, zero, letters, max);
    }

    /**
     * Converts an integer into a roman number.
     * <p>
     * Uses the converter's digits to print the decimal places.
     * For instance, using {@link #DIGITS}, 4 will be converted to IV, 
     * using {@link #DIGITS_SIMPLE}, it will be IIII.
     * @param number
     * @return a roman number
     */
    public String toRoman(int number) {
        StringBuilder sb = new StringBuilder();
        toRoman(number, sb);
        return sb.toString();
    }
    
    /**
     * @see #toRoman2(int) 
     */
    public void toRoman2(int number, StringBuilder target) {
        ToRoman2(number, target, zero, letters, max);
    }

    /**
     * Converts an integer into a roman number.
     * <p>
     * Tries to shorten the result by using subtractions more freely.
     * For instance, 2992 will be converted to MMXMII, instead of MMCMXCII.
     * 
     * @param number
     * @return a roman number
     */
    public String toRoman2(int number) {
        StringBuilder sb = new StringBuilder();
        toRoman2(number, sb);
        return sb.toString();
    }
    
    /**
     * Parses a roman number.
     * <p>
     * Does not check whether the number is in a valid format, instead uses
     * a simple subtraction rule. If a sequence of the same letter is followed
     * by a letter of higher value, they are subtracted, otherwise added.
     * For instance, the following values are parsed as {@code 6}:
     * VI, IVII, IIIIX, IIIIII, VXI
     * @param number
     * @return integer value of {@code number}
     */
    public int fromRoman(String number) {
        return FromRoman(number, letters, zero);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Romans(");
        JavaNames.Join(letters, " ", sb);
        sb.append(")");
        return sb.toString();
    }

    public String[] getLetters() {
        return letters.clone();
    }

}
