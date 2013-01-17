package org.cthul.parser.util;

import java.util.regex.MatchResult;

public class MutableMatchResult implements MatchResult {

    protected MatchResult match;
    protected String string;
    protected int start, end;

    public void setMatch(MatchResult match) {
        this.match = match;
        string = null;
    }

    public void setString(String string, int start) {
        setString(string, start, start + string.length());
    }
    
    public void setString(String string, int start, int end) {
        this.string = string;
        this.start = start;
        this.end = end;
        match = null;
    }

    @Override
    public int start() {
        if (match != null) {
            return match.start();
        }
        return start;
    }

    @Override
    public int start(int i) {
        if (match != null) {
            return match.start(i);
        } else if (i == 0) {
            return start;
        } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }
    }

    @Override
    public int end() {
        if (match != null) {
            return match.end();
        }
        return end;
    }

    @Override
    public int end(int i) {
        if (match != null) {
            return match.end(i);
        } else if (i == 0) {
            return end;
        } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }
    }

    @Override
    public String group() {
        if (match != null) {
            return match.group();
        } else {
            return string;
        }
    }

    @Override
    public String group(int i) {
        if (match != null) {
            return match.group(i);
        } else if (i == 0) {
            return string;
        } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }
    }

    @Override
    public int groupCount() {
        if (match != null) {
            return match.groupCount();
        } else {
            return 0;
        }
    }
}
