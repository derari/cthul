package org.cthul.strings.format;

import java.io.IOException;
import org.cthul.strings.Romans;

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
public class RomansFormat extends FormatAlignmentBase {

    public static final RomansFormat INSTANCE = new RomansFormat();
    
    private static final long MaxVal = 4000;
    private static final Romans r0 = new Romans(new String[]{"i", "v", "x", "l", "c", "d", "m"}, "N", Romans.DIGITS_SIMPLE);
    private static final Romans r = new Romans(new String[]{"i", "v", "x", "l", "c", "d", "m"});
    
    /**
     * Registers this format as {@code %ir} and {@code %jRomans}.
     * @param conf 
     */
    public void register(FormatConfiguration conf) {
        conf.setShortFormat('r', this);
        conf.setLongFormat("Romans", this);
    }
    
    @Override
    protected int format(Appendable a, Object value, java.util.Locale locale, String flags, int precision, String formatString, int position) throws IOException {
        if (precision > 2) {
            throw FormatException.precisionTooHigh(getConversionName(), precision, 2);
        }
        int i = ((Number) value).intValue();
        while (i > MaxVal) {
            a.append('m');
            i -= 1000;
        }
        final StringBuilder sb = (a instanceof StringBuilder) ? 
                                    (StringBuilder) a : 
                                    new StringBuilder();
        switch (precision) {
            case 0:
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
