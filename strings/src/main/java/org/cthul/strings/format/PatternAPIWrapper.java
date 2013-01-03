package org.cthul.strings.format;

/**
 *
 * @author Arian Treffer
 */
public class PatternAPIWrapper implements PatternAPI {
    
    protected final PatternAPI base;

    public PatternAPIWrapper(PatternAPI base) {
        if (base == null) throw new NullPointerException();
        this.base = base;
    }

    @Override
    public PatternAPI append(char c) {
        base.append(c);
        return this;
    }

    @Override
    public PatternAPI append(CharSequence csq) {
        base.append(csq);
        return this;
    }

    @Override
    public PatternAPI append(CharSequence csq, int start, int end) {
        base.append(csq, start, end);
        return this;
    }

    @Override
    public PatternAPI addedCapturingGroup() {
        base.addedCapturingGroup();
        return this;
    }

    @Override
    public PatternAPI addedCapturingGroups(int i) {
        base.addedCapturingGroups(i);
        return this;
    }

    @Override
    public Object putMemento(Object newMemento) {
        base.putMemento(newMemento);
        return this;
    }

    @Override
    public PatternData parse(PatternAPI api, String format) {
        return base.parse(api, format);
    }

    @Override
    public PatternData parse(PatternAPI api, String format, int start, int end) {
        return base.parse(api, format, start, end);
    }

}
