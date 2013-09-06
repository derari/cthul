package org.cthul.matchers.diagnose;

/**
 *
 */
public class MatchResultSuccess implements MatchResult<Object> {

    private static final MatchResultSuccess INSTANCE = new MatchResultSuccess();
    
    public static <T> MatchResult<T> instance() {
        return (MatchResult) INSTANCE;
    }
    
    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public Mismatch<Object> getMismatch() {
        return null;
    }
    
}
