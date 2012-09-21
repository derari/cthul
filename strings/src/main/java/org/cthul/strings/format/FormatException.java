package org.cthul.strings.format;

import java.util.*;

/**
 *
 * @author Arian Treffer
 */
public class FormatException {

    public static DuplicateFormatFlagsException duplicateFormatFlags(String flags) {
        return new DuplicateFormatFlagsException(flags);
    }
    
    /**
     * 
     * @param flags flags that are not allowed for the format
     * @param format
     * @return FormatFlagsConversionMismatchException
     */
    public static FormatFlagsConversionMismatchException formatFlagsMismatch(String flags, String format) {
        return new CustomFormatFlagsMismatchException(flags, format);
    }
    
    /**
     * 
     * @param flags illegal combination of flags
     * @param format
     * @return IllegalFormatFlagsException
     */
    public static IllegalFormatFlagsException illegalFlags(String flags, String format) {
        return new CustomIllegalFormatFlagsException(flags, format);
    }
    
    public static UnknownFormatConversionException unknownFormat(char format) {
        return new UnknownFormatConversionException(String.valueOf(format));
    }
    
    public static UnknownFormatConversionException unknownFormat(String format) {
        return new UnknownFormatConversionException(format);
    }
    
    public static IllegalFormatConversionException illegalFormat(String format, Class<?> arg) {
        return new CustomIllegalFormatException(format, arg);
    }

    public static IllegalFormatPrecisionException illegalPrecision(String format, int p) {
        return new CustomIllegalPrecisionException(format, p);
    }
    
    public static IllegalFormatPrecisionException illegalPrecision(String format, int p, Integer min, Integer max) {
        return new CustomIllegalPrecisionException(format, p, min, max);
    }
    
    public static IllegalFormatPrecisionException precisionTooHigh(String format, int p, Integer max) {
        return new CustomIllegalPrecisionException(format, p, null, max);
    }
    
    public static IllegalFormatWidthException illegalWidth(String format, int w) {
        return new CustomIllegalWidthException(format, w);
    }
    
    public static IllegalFormatWidthException illegalWidth(String format, int w, Integer min, Integer max) {
        return new CustomIllegalWidthException(format, w, min, max);
    }
    
    public static class ShortFormatFlagsConversionMismatchException extends FormatFlagsConversionMismatchException {

        private final char format;
        
        public ShortFormatFlagsConversionMismatchException(String flags, char shortKey, char format) {
            super(flags, shortKey);
            this.format = format;
        }

        public char getFormat() {
            return format;
        }

        @Override
        public String getMessage() {
            return "Conversion = " + getConversion() + getFormat() + 
                    ", Flags = " + getFlags();
        }
        
    }
    
    public static class CustomFormatFlagsMismatchException extends FormatFlagsConversionMismatchException {

        private final String format;
        
        public CustomFormatFlagsMismatchException(String flags, String format) {
            super(flags, FormatStringParser.CUSTOM_LONG);
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        @Override
        public String getMessage() {
            return "Format = " + getFormat() + ", Flags = '" + getFlags() + "'";
        }
        
    }
    
    public static class CustomIllegalFormatFlagsException extends IllegalFormatFlagsException {

        private final String format;

        public CustomIllegalFormatFlagsException(String flags, String format) {
            super(flags);
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        @Override
        public String getMessage() {
            return "Format = " + getFormat() + ", Flags = '" + getFlags() + "'";
        }
        
    }
    
    public static class CustomIllegalFormatException extends IllegalFormatConversionException {

        private final String format;

        public CustomIllegalFormatException(String format, Class<?> arg) {
            super(FormatStringParser.CUSTOM_LONG, arg);
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        @Override
        public String getMessage() {
            return "Format = " + getFormat() + 
                    ", Argument = " + getArgumentClass().getName();
        }
        
    }
    
    public static class CustomIllegalPrecisionException extends IllegalFormatPrecisionException {

        private final String format;
        private final Integer min;
        private final Integer max;

        public CustomIllegalPrecisionException(String format, int p) {
            this(format, p, null, null);
        }

        public CustomIllegalPrecisionException(String format, int p, Integer min, Integer max) {
            super(p);
            this.format = format;
            this.min = min;
            this.max = max;
        }

        public String getFormat() {
            return format;
        }

        public Integer getMax() {
            return max;
        }

        public Integer getMin() {
            return min;
        }

        @Override
        public String getMessage() {
            final StringBuilder msg = new StringBuilder();
            msg.append("Format = ");
            msg.append(getFormat());
            msg.append(", Precision = ");
            msg.append(getPrecision());
            if (min != null || max != null) {
                msg.append(", Expected ");
                if (min != null) msg.append(min).append(" <= ");
                msg.append("p");
                if (max != null) msg.append(" <= ").append(max);
            }
            return msg.toString();
        }
        
    }
    
    public static class CustomIllegalWidthException extends IllegalFormatWidthException {

        private final String format;
        private final Integer min;
        private final Integer max;

        public CustomIllegalWidthException(String format, int w) {
            this(format, w, null, null);
        }

        public CustomIllegalWidthException(String format, int p, Integer min, Integer max) {
            super(p);
            this.format = format;
            this.min = min;
            this.max = max;
        }

        public String getFormat() {
            return format;
        }

        public Integer getMax() {
            return max;
        }

        public Integer getMin() {
            return min;
        }

        @Override
        public String getMessage() {
            final StringBuilder msg = new StringBuilder();
            msg.append("Format = ");
            msg.append(getFormat());
            msg.append(", Width = ");
            msg.append(getWidth());
            if (min != null || max != null) {
                msg.append(", Expected ");
                if (min != null) msg.append(min).append(" <= ");
                msg.append("w");
                if (max != null) msg.append(" <= ").append(max);
            }
            return msg.toString();
        }
        
    }
    
}
