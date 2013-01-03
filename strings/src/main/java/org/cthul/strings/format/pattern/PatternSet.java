package org.cthul.strings.format.pattern;

import java.util.Arrays;
import java.util.regex.Matcher;
import org.cthul.strings.format.PatternAPI;
import org.cthul.strings.format.PatternAPIWrapper;

/**
 *
 * @author Arian Treffer
 */
public class PatternSet {
    
    private final int maxGroupCount;
    private PatternAPI baseAPI;
    private SubAPI subAPI;
    protected int[] offsets;
    protected Object[] mementos;
    private int capturingOffset = 0;
    private int groupId = -1;
    private boolean opened = false;
    private boolean initialized = false;
    private boolean closed = false;
    private boolean subgroupOpened = false;
    private boolean subgroupInitialized = false;
    private boolean subgroupIgnored = false;

    public PatternSet(PatternAPI api, int groupCount) {
        this.baseAPI = api;
        offsets = new int[groupCount];
        mementos = new Object[groupCount];
        maxGroupCount = groupCount;
    }
    
    public PatternSet(PatternAPI api) {
        this.baseAPI = api;
        offsets = new int[16];
        mementos = new Object[16];
        maxGroupCount = -1;
    }
    
    private void checkCapacity() {
        if (maxGroupCount == -1 && groupId >= offsets.length) {
            mementos = Arrays.copyOf(mementos, offsets.length*2);
            offsets = Arrays.copyOf(offsets, offsets.length*2);
        }
    }
    
    private void checkOpen() {
        if (!opened) throw new IllegalStateException("not open");
        if (closed) throw new IllegalStateException("closed");
    }
    
    private void checkSubgroupOpen() {
        checkOpen();
        if (!subgroupOpened) throw new IllegalStateException("no open subgroup");
    }
    
    private void initSubgroup() {
        checkSubgroupOpen();
        if (subgroupInitialized) return;
        subgroupInitialized = true;
        if (!initialized) {
            beforeFirstSubpattern();
            initialized = true;
        } else {
            nextSubpattern();
        }
        beforeSubpattern();
        baseAPI.addedCapturingGroup();
        baseAPI.append('(');
        capturingOffset++;
        offsets[groupId] = capturingOffset;
    }
    
    protected void beforeFirstSubpattern() {}
    
    protected void nextSubpattern() {}
    
    protected void beforeSubpattern() {}
    
    protected void afterSubpattern() {}
    
    protected void afterLastSubpattern() {}
    
    protected PatternAPI baseAPI() {
        return baseAPI;
    }
    
    public PatternAPI api() {
        if (subAPI == null) subAPI = createSubAPI();
        return subAPI;
    }
    
    public void open() {
        if (opened) throw new IllegalStateException("already opened");
        opened = true;
    }
    
    public void close() {
        checkOpen();
        if (subgroupOpened) throw new IllegalStateException("Subpattern still open");
        closed = true;
        if (initialized) {
            afterLastSubpattern();
        }
        baseAPI = null;
        subAPI = null;
    }
    
    public void beginSubpattern() {
        checkOpen();
        if (subgroupOpened) throw new IllegalStateException("Subpattern already opened");
        subgroupOpened = true;
        subgroupInitialized = false;
        subgroupIgnored = false;
        groupId++;
        checkCapacity();
    }
    
    public void endSubpattern() {
        if (!subgroupIgnored) checkSubgroupOpen();
        subgroupOpened = false;
        if (subgroupInitialized) {
            baseAPI.append(')');
            afterSubpattern();
        }
    }
    
    public void ignoreSubpattern() {
        checkSubgroupOpen();
        if (subgroupInitialized) throw new IllegalStateException("Subpattern already initialized");
        subgroupIgnored = true;
        subgroupOpened = false;
    }
    
    public void allowEmptySubpattern() {
        initSubgroup();
    }
    
    public int getMatchingPattern(Matcher matcher, int capturingBase) {
        return getMatchingPattern(matcher, capturingBase, 0);
    }
    
    public int getMatchingPattern(Matcher matcher, int capturingBase, int i) {
        for (; i <= groupId; i++) {
            if (offsets[i] != 0 &&
                    matcher.start(capturingBase + offsets[i]) > -1) {
                return i;
            }
        }
        return -1;
    }
    
    public int getCapturingOffset(int i) {
        return offsets[i];
    }
    
    public Object getMemento(int i) {
        return mementos[i];
    }
    
    protected SubAPI createSubAPI() {
        return new SubAPI(baseAPI);
    }

    protected class SubAPI extends PatternAPIWrapper {

        public SubAPI(PatternAPI base) {
            super(base);
        }

        @Override
        public PatternAPI append(char c) {
            initSubgroup();
            return super.append(c);
        }

        @Override
        public PatternAPI append(CharSequence csq) {
            initSubgroup();
            return super.append(csq);
        }

        @Override
        public PatternAPI append(CharSequence csq, int start, int end) {
            initSubgroup();
            return super.append(csq, start, end);
        }

        @Override
        public PatternAPI addedCapturingGroup() {
            capturingOffset++;
            return super.addedCapturingGroup();
        }

        @Override
        public PatternAPI addedCapturingGroups(int i) {
            capturingOffset += i;
            return super.addedCapturingGroups(i);
        }

        @Override
        public Object putMemento(Object newMemento) {
            Object old = mementos[groupId];
            mementos[groupId] = newMemento;
            return old;
        }
        
    }
    
}
