package org.cthul.strings.format.pattern;

import java.util.Locale;
import org.cthul.strings.format.PatternAPI;

/**
 *
 * @author Arian Treffer
 */
public abstract class PatternAlignmentBase extends FormatPatternBase {
    
    private static final char[] F_ALL = flags(F_JUSTIFICATION + F_PADDING);
    private static final char[] F_PAD = flags(F_PADDING);
    
    protected char[] getValidFlags() {
        return F_ALL;
    }
    
    protected char[] getPaddingFlags() {
        return F_PAD;
    }
    
    protected char getDefaultPaddingChar() {
        return ' ';
    }
    
    protected char getPaddingChar(String flags) {
        char p = selectFlag(flags, getPaddingFlags());
        if (p != 0) return p;
        return getDefaultPaddingChar();
    }

    protected void appendPadding(final PatternAPI pattern, final char padding) {
        pattern.append("[");
        if (padding == ']') pattern.append('\\');
        pattern.append(padding);
        pattern.append("]*");
    }
    
    protected void appendPadding(final PatternAPI pattern, final char padding, int w) {
        pattern.append("[");
        if (padding == ']') pattern.append('\\');
        pattern.append(padding);
        pattern.append("]{");
        pattern.append(String.valueOf(w));
        pattern.append("}");
    }
    
    protected boolean matchExactWidth(String flags, int width) {
        return false;
    }

    @Override
    public int toRegex(PatternAPI pattern, Locale locale, String flags, int width, int precision, String formatString, int position) {
        ensureValidFlags(flags, getValidFlags());
        Justification j = getJustification(flags, width);
        char padding = j == Justification.None ? 0 : getPaddingChar(flags);
        if (j != Justification.None && matchExactWidth(flags, width)) {
            return toExactWidthRegex(pattern, locale, flags, j, padding, width, precision, formatString, position);
        }
        switch (j) {
            case Center:
            case Right:
                appendPadding(pattern, padding);
        }
        int result = toRegex(pattern, locale, flags, padding, precision, formatString, position);
        switch (j) {
            case Left:
            case Center:
                appendPadding(pattern, padding);
        }
        return result;
    }
    
    protected abstract int toRegex(PatternAPI regex, Locale locale, String flags, char padding, int precision, String formatString, int position);
    
    protected int toExactWidthRegex(PatternAPI pattern, Locale locale, String flags, Justification j, char padding, int width, int precision, String formatString, int position) {
        AlignmentMemento memento = new AlignmentMemento(pattern, width+2);
        memento.open();

        int result = appendExactWidthRegex(memento, locale, flags, Justification.None, padding, -1, precision, formatString, position);
        for (int w = 0; w <= width; w++) {
            memento.next();
            int r = appendExactWidthRegex(memento, locale, flags, j, padding, w, precision, formatString, position);
            if (r > -1) result = r;
        }

        memento.close();
        return result;
    }
    
    protected int appendExactWidthRegex(AlignmentMemento pattern, Locale locale, String flags, Justification j, char padding, int width, int precision, String formatString, int position) {
        pattern.beginSubgroup();
        if (j == Justification.Right) appendPadding(pattern, padding, width);
        if (j == Justification.Center) appendPadding(pattern, padding, (width)/2);
        int r = toRegex(pattern, locale, flags, padding, width, precision, formatString, position);
        if (j == Justification.Left) appendPadding(pattern, padding, width);
        if (j == Justification.Center) appendPadding(pattern, padding, (width+1)/2);

        if (r < 0) {
            pattern.append("todo no-match");
        }
        pattern.endSubgroup();
        return r;
    }

    protected int toRegex(PatternAPI regex, Locale locale, String flags, char padding, int width, int precision, String formatString, int position) {
        throw new UnsupportedOperationException("Explicit width matching not supported.");
    }
    
    public static class AlignmentMemento implements PatternAPI {
        
        private final PatternAPI api;
        private final int[] groups;
        private int groupIndex = -1;
        private int groupOffset = 0;
        private final Object[] mementos = null;

        public AlignmentMemento(PatternAPI api, int groupCount) {
            this.api = api;
            this.groups = new int[groupCount];
        }
        
        @Override
        public PatternAPI append(char c) {
            api.append(c);
            return this;
        }

        @Override
        public PatternAPI append(CharSequence csq) {
            api.append(csq);
            return this;
        }

        @Override
        public PatternAPI append(CharSequence csq, int start, int end) {
            api.append(csq, start, end);
            return this;
        }

        @Override
        public PatternAPI addedCapturingGroup() {            
            api.addedCapturingGroup();
            groupOffset++;
            return this;
        }

        @Override
        public PatternAPI addedCapturingGroups(int i) {
            api.addedCapturingGroups(i);
            groupOffset += i;
            return this;
        }

        @Override
        public Object putMemento(Object newMemento) {
            Object old = mementos[groupIndex];
            mementos[groupIndex] = newMemento;
            return old;
        }
        
        public void open() {
            append("(?");
        }

        public void beginSubgroup() {
            append('(');
            addedCapturingGroup();
            groupIndex++;
            groups[groupIndex] = groupOffset;
        }

        public void endSubgroup() {
            append(')');
        }
        
        public void next() {
            append('|');
        }
        
        public void close() {
            append(")");
        }

    }

}
