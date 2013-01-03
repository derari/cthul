package org.cthul.strings.format;

/**
 *
 * @author Arian Treffer
 */
public interface MatchResults {
    
    Object get(int i);
    
    Object get(char c);
    
    Object get(String s);
    
    void put(int i, Object o);
    
    void put(char c, Object o);
    
    void put(String s, Object o);
    
    public static class Arg {
        
        public static Object get(MatchResults mr, Object argId) {
            if (argId instanceof Integer) {
                return mr.get((Integer) argId);
            } else if (argId instanceof Character) {
                return mr.get((Character) argId);
            } else if (argId instanceof String) {
                return mr.get((String) argId);
            } else {
                throw new IllegalArgumentException(
                    "Expected Integer, Character, or String; got " + argId);
            }
        }
        
        public static void put(MatchResults mr, Object argId, Object value) {
            if (argId instanceof Integer) {
                mr.put((Integer) argId, value);
            } else if (argId instanceof Character) {
                mr.put((Character) argId, value);
            } else if (argId instanceof String) {
                mr.put((String) argId, value);
            } else {
                throw new IllegalArgumentException(
                    "Expected Integer, Character, or String; got " + argId);
            }
        }
        
    }
    
}
