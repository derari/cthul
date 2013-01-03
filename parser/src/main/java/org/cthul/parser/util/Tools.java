package org.cthul.parser.util;

import java.util.List;

public class Tools {

    public static String[] strings(String... strings) {
        return strings;
    }
    
    public static String[] strings(List<String> strings) {
        return strings.toArray(new String[strings.size()]);
    }
    
    public static int[] ints(int... priorities) {
        return priorities;
    }
    
    public static int[] ints(List<Integer> priorities) {
        final int[] result = new int[priorities.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = priorities.get(i);
        }
        return result;
    }


    
}
