package org.cthul.strings.format;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 * The result of parsing a format pattern.
 * <p>
 * Manages conversion patterns and mementos, and counts capturing group.
 * @author Arian Treffer
 */
public class PatternData {
    
    private final int maxConversionCount;
    protected int[] offsets;
    protected Object[] mementos;
    protected Object[] argIds;
    protected ConversionPattern[] patterns;
    private int capturingOffset = 0;
    private int lastConversionId = -1;

    public PatternData(int conversionCount) {
        initArrays(conversionCount);
        maxConversionCount = conversionCount;
    }
    
    public PatternData() {
        initArrays(16);
        maxConversionCount = -1;
    }
    
    private void initArrays(final int len) {
        offsets = new int[len];
        mementos = new Object[len];
        argIds = new Object[len];
        patterns = new ConversionPattern[len];
    }
    
    private void checkCapacity() {
        if (maxConversionCount == -1 && lastConversionId >= offsets.length) {
            final int len = offsets.length*2;
            mementos = Arrays.copyOf(mementos, len);
            offsets = Arrays.copyOf(offsets, len);
            argIds = Arrays.copyOf(argIds, len);
            patterns = Arrays.copyOf(patterns, len);
        }
    }
    
    public void addConversion(ConversionPattern pattern, Object memento, Object argId) {
        lastConversionId++;
        checkCapacity();
        patterns[lastConversionId] = pattern;
        offsets[lastConversionId] = capturingOffset;
        argIds[lastConversionId] = argId;
        mementos[lastConversionId] = memento;
    }
    
    public void apply(MatcherAPI matcherAPI, Matcher matcher, int capturingBase, MatchResults results) {
        final int max = lastConversionId+1;
        for (int i = 0; i < max; i++) {
            int c = offsets[i] + capturingBase;
            Object argId = argIds[i];
            Object arg = MatchResults.Arg.get(results, argId);
            Object memento = mementos[i];
            arg = patterns[i].parse(matcherAPI, matcher, c, memento, arg);
            MatchResults.Arg.put(results, argId, arg);
        }
    }
    
    public static class Builder extends PatternAPIWrapper {

        protected int curGroupId = -1;
        protected PatternAPI api;
        protected final PatternData data;
        
        public Builder(PatternAPI baseApi, int groupCount) {
            this(baseApi, new PatternData(groupCount));
        }
        
        public Builder(PatternAPI baseApi) {
            this(baseApi, new PatternData());
        }
        
        protected Builder(PatternAPI baseApi, PatternData data) {
            super(baseApi);
            this.api = new PatternAPIWrapper(this);
            this.data = data;
        }
        
        public PatternData data() {
            return data;
        }
        
        protected PatternAPI api() {
            if (api == null) api = new PatternAPIWrapper(this);
            return api;
        }

        public void addConversion(ConversionPattern pattern, Object memento, Object argId) {
            data.addConversion(pattern, memento, argId);
        }

        public int addConversion(ConversionPattern pattern, Object argId, Locale locale, String flags, int width, int precision, String formatString, int position) {
            append('(');
            addedCapturingGroup();
            addConversion(pattern, null, argId);
            curGroupId = data.lastConversionId;
            int r = pattern.toRegex(api(), locale, flags, width, precision, formatString, position);
            append(')');
            return r;
        }

        @Override
        public PatternAPI addedCapturingGroup() {
            data.capturingOffset++;
            return super.addedCapturingGroup();
        }

        @Override
        public PatternAPI addedCapturingGroups(int i) {
            data.capturingOffset += i;
            return super.addedCapturingGroups(i);
        }

        @Override
        public Object putMemento(Object newMemento) {
            Object old = data.mementos[curGroupId];
            data.mementos[curGroupId] = newMemento;
            return old;
        }
        
    }
        
}
