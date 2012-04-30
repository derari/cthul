package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public class Priority {

    public static final int LOW = 0x100;
    public static final int DEFAULT = 0x200;
    public static final int HIGH = 0x300;

    /**
     * Indicates that a symbol in the production has to have a higher priority
     * that the production itself.
     * <p>
     * The value is '!'.
     * <p>
     * Example: {@code term ::= term - !term} 
     * defines subtraction to be left-deep.
     * @see Priority#C_AUTO
     */
    public static final char C_HIGHER = '!';
    
    /**
     * Indicates that a symbol has to have the same priority as the production
     * itself. If the symbol is different from the production's symbol, by
     * default it could have any priority.
     * <p>
     * The value is '.'
     * <p>
     * Example: {@code foo ::= .bar} matches only bar rules with the same
     * priority as foo.
     */
    public static final char C_SAME = '.';
    public static final char C_ANY = '?';
    public static final char C_EXPLICIT = '(';
    public static final char C_EXPLICIT_CLOSE = ')';
    public static final char C_AUTO = '~';
    
    public static final String[] NO_SYMBOLS = {};
    public static final int[] NO_PRIORITIES = {};
    
    public static String[] split(String production) {
        if (production == null || production.trim().isEmpty()) {
            return NO_SYMBOLS;
        } else {
            return production.split("\\s+", 0);
        }
    }
    
    public static int[] createPriorityArray(String[] symbols) {
        if (symbols.length == 0) {
            return NO_PRIORITIES;
        } else {
            return new int[symbols.length];
        }
    }
    
    public static void parse(final String[] symbols, 
                             final int[] priorities, 
                             final String defaultSymbol, 
                             final int defaulPriority) {
        if (symbols.length != priorities.length) {
            throw new IllegalArgumentException(
                    String.format("Symbol and priority array must have "
                                  + "same size, but was %d and %d",
                                  symbols.length, priorities.length));
        }
        for (int i = 0; i < symbols.length; i++) {
            parse(i, symbols, priorities, defaultSymbol, defaulPriority);
        }
    }
    
    private static void parse(final int i, final String[] symbols, 
                              final int[] priorities, 
                              final String defaultSymbol, 
                              final int defaultPriority) {
        final String s = symbols[i];
        final int priority;
        final char first = s.length() > 1 ? s.charAt(0) : C_AUTO;
        int cut = s.length() > 1 ? 1 : 0;
        switch (first) {
            case C_HIGHER:
                priority = defaultPriority+1;
                break;
            case C_SAME:
                priority = defaultPriority;
                break;
            case C_ANY:
                priority = 0;
                break;
            case C_EXPLICIT:
                final String prioString;
                cut = s.indexOf(C_EXPLICIT_CLOSE, 2);
                if (cut > 1) {
                    prioString = s.substring(1, cut);
                    cut++;
                } else {
                    cut = 1;
                    while (cut < s.length() && Character.isDigit(s.charAt(cut))) {
                        cut++;
                    }
                    prioString = s.substring(1, cut);
                }
                if (cut < 2 || cut >= s.length()) {
                    throw new IllegalArgumentException(
                            "Could not parse priority of " + s);
                }
                priority = Integer.parseInt(prioString);
                break;
            case C_AUTO:
                cut++;
            default:
                cut--;
                // default: if same name -> same priority, else 0
                priority = s.equals(defaultSymbol) ? defaultPriority : 0;
        }
        priorities[i] = priority;
        symbols[i] = s.substring(cut);
    }

    protected Priority() {
    }
    
}
