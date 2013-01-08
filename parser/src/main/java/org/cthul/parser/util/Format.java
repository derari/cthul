package org.cthul.parser.util;

import java.util.Arrays;

public class Format {
    
    public static String productionKey(String symbol, int priority) {
        StringBuilder sb = new StringBuilder();
        productionKey(sb, symbol, priority, 0);
        return sb.toString();
    }
    
    private static void productionKey(StringBuilder sb, String symbol, int priority, int defaultPriority) {
        sb.append(symbol);
        if (priority != defaultPriority)
            sb.append('[').append(priority).append(']');
    }

    public static String production(String left, int priority, String[] right, int[] priorities) {
        return production(left, priority, "::=", right, priorities);
    }
    
    public static String production(String left, int priority, String productionOp, String[] right, int[] priorities) {
        StringBuilder sb = new StringBuilder();
        productionKey(sb, left, priority, 0);
        sb.append(' ').append(productionOp);
        if (right == null) return sb.toString();
        for (int i = 0; i < right.length; i++) {
            sb.append(' ');
            productionKey(sb, right[i], priorities[i], priority);
        }
        return sb.toString();
    }
    
    public static String productionState(String left, int priority, String[] right, int[] priorities, int position) {
        StringBuilder sb = new StringBuilder();
        productionKey(sb, left, priority, 0);
        sb.append(' ').append("::=");
        for (int i = 0; i < right.length; i++) {
            if (i == position) sb.append(" •");
            sb.append(' ');
            productionKey(sb, right[i], priorities[i], priority);
        }
        if (right.length == position) sb.append(" •");
        return sb.toString();
    }
    
    public static String join(String begin, String sep, String end, Iterable<?>... values) {
        StringBuilder sb = new StringBuilder();
        sb.append(begin);
        boolean first = true;
        for (Iterable<?> it: values) {
            for (Object o: it) {
                if (first) first = false;
                else sb.append(sep);
                sb.append(o);
            }
        }
        return sb.append(end).toString();
    }
    
    public static String join(String begin, String sep, String end, Object[] values) {
        return join(begin, sep, end, Arrays.asList(values));
    }
    
    public static String join(String begin, String sep, String end, Iterable<?> values) {
        return join(begin, sep, end, new Iterable[]{values});
    }
    public static String join(String begin, String sep, String end, int maxLen, String cutEnd, Iterable<?>... values) {
        maxLen -= sep.length() + Math.max(end.length(), cutEnd.length());
        StringBuilder sb = new StringBuilder();
        sb.append(begin);
        boolean first = true;
        for (Iterable<?> it: values) {
            for (Object o: it) {
                if (first) first = false;
                else sb.append(sep);
                String s = String.valueOf(o);
                if (sb.length() + s.length() > maxLen) {
                    return sb.append(cutEnd).toString();
                }
                sb.append(s);
            }
        }
        return sb.append(end).toString();
    }
    
    public static String join(String begin, String sep, String end, int maxLen, String cutEnd, Iterable<?> values) {
        return join(begin, sep, end, maxLen, cutEnd, new Iterable[]{values});
    }
    
    public static String join(String begin, String sep, String end, int maxLen, String cutEnd, Object[] values) {
        return join(begin, sep, end, maxLen, cutEnd, Arrays.asList(values));
    }

    
}
