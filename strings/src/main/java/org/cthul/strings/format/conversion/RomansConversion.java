package org.cthul.strings.format.conversion;

import java.io.IOException;
import java.util.Locale;
import org.cthul.strings.Romans;
import org.cthul.strings.format.FormatException;
import org.cthul.strings.format.FormatterConfiguration;

/**
 * {@link Romans} format, lower case by default.
 * {@linkplain FormatAlignmentBase Width} is supported,
 * flags are not.
 * <p>
 * The precision value can be used to select the output format: <br/>
 * {@code 0} uses {@linkplain Romans#DIGITS_SIMPLE no subtraction rule}, <br/>
 * {@code 1} (default) uses the {@linkplain Romans#DIGITS default subtraction rule}, <br/>
 * {@code 2} subtracts {@linkplain Romans#toRoman2(int) more freely} to create shorter output.
 * 
 * @author Arian Treffer
 */
public class RomansConversion extends FormatAlignmentBase {
    private static final Romans R0 = new Romans(new String[]{"i", "v", "x", "l", "c", "d", "m"}, "n", Romans.DIGITS_SIMPLE);
    private static final Romans R  = new Romans(new String[]{"i", "v", "x", "l", "c", "d", "m"}, "n", Romans.DIGITS);
    
    public static final RomansConversion INSTANCE = new RomansConversion();
    
    private final String maxL;
    private final long maxVal;
    private final Romans r;
    private final Romans r0;

    public RomansConversion() {
        this(R, R0);
    }

    public RomansConversion(Romans r) {
        this(r, null);
    }
    
    public RomansConversion(Romans r, Romans r0) {
        this.r = r;
        this.r0 = r0;
        String[] letters = r.getLetters();
        if (r0 != null && letters.length != r0.getLetters().length) {
            throw new IllegalArgumentException(
                    "Both romans need same number of letters, but was " +
                    letters.length + " and " + r0.getLetters().length);
        }
        maxVal = Romans.MaxRomanLetterValue(letters);
        maxL = letters[letters.length-1];
    }
    
    /**
     * Registers this format as {@code %ir} and {@code %jRomans}.
     * @param conf 
     */
    public void register(FormatterConfiguration conf) {
        conf.setShortFormat('r', this);
        conf.setLongFormat("Romans", this);
    }
    
    @Override
    protected int format(Appendable a, Object value, Locale locale, String flags, int precision, String formatString, int position) throws IOException {
        if (precision > 2) {
            throw FormatException.precisionTooHigh(getFormatName(), precision, 2);
        }
        int i = cast(value, Number.class).intValue();
        while (i > maxVal) {
            a.append(maxL);
            i -= maxVal;
        }
        final StringBuilder sb = (a instanceof StringBuilder) ? 
                                    (StringBuilder) a : new StringBuilder();
        switch (precision) {
            case 0:
                if (r0 == null)
                    throw FormatException.illegalPrecision(
                            getFormatName() + "(zero-precision not configured)", 
                            0, 1, 2);
                r0.toRoman(i, sb);
                break;
            case 2:
                r.toRoman2(i, sb);
                break;
            default:
                r.toRoman(i, sb);
        }
        if (sb != a) a.append(sb);
        return 0;
    }
    
}
