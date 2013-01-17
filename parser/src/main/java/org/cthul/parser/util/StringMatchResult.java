package org.cthul.parser.util;

import java.util.regex.MatchResult;

public class StringMatchResult implements MatchResult {
    
    protected final String string;
    protected final int start, end;

    public StringMatchResult(String s, int start) {
        this.string = s;
        this.start = start;
        this.end = start + s.length();
    }

    @Override
    public int start() {
        return start;
    }

    @Override
    public int start(int group) {
        if (group == 0) {
            return start;
        } else {
            throw new IndexOutOfBoundsException(String.valueOf(group));
        }
    }

    @Override
    public int end() {
        return end;
    }

    @Override
    public int end(int group) {
        if (group == 0) {
            return end;
        } else {
            throw new IndexOutOfBoundsException(String.valueOf(group));
        }
    }

    @Override
    public String group() {
        return string;
    }

    @Override
    public String group(int group) {
        if (group == 0) {
            return string;
        } else {
            throw new IndexOutOfBoundsException(String.valueOf(group));
        }
    }

    @Override
    public int groupCount() {
        return 0;
    }

    @Override
    public String toString() {
        return "\"" + string + "\"";
    }
    
}
