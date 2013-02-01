package org.cthul.strings.format;

import java.util.*;
import java.util.Map.Entry;

/**
 * Format args, by index, char, or key.
 * @author Arian Treffer
 */
public class SimpleArgs extends AbstractArgs implements FormatArgs {
    
    protected static final Object[] EMPTY = {};
    protected static final String[] EMPTY_S = {};
    
    protected static void fill(Map<Object, Object> args, List<Object> intArgs, List<Object> charArgs, Map<String, Object> stringArgs) {
        for (Map.Entry<Object, Object> a: args.entrySet()) {
            Object key = a.getKey();
            if (key instanceof Number) {
                if (intArgs == null) throw unexpectedArgument("integer", a);
                Object o = insert(intArgs, ((Number) key).intValue(), a.getValue());
                if (o != null) throw duplicateArgument("integer", a, o);
            } else if (key instanceof Character) {
                if (charArgs == null) throw unexpectedArgument("char", a);
                Object o = insert(charArgs, cToI((Character) key), a.getValue());
                if (o != null) throw duplicateArgument("char", a, o);
            } else if (key instanceof String) {
                if (stringArgs == null) throw unexpectedArgument("string", a);
                Object o = stringArgs.put((String) key, a.getValue());
                if (o != null) throw duplicateArgument("string", a, o);
            } else {
                throw new IllegalArgumentException(
                        "Invalid key " + key + ", "
                        + "expected int, char, or String");
            }
        }
    }

    protected static Object insert(List<Object> intArgs, int intValue, Object value) {
        while (intArgs.size() <= intValue) intArgs.add(null);
        return intArgs.set(intValue, value);
    }

    protected static void fill(Map<String, Object> args, String[] stringKeys, Object[] stringValues) {
        int i = 0;
        for (Map.Entry<String, Object> a: args.entrySet()) {
            stringKeys[i] = a.getKey();
            stringValues[i] = a.getValue();
            i++;
        }
    }
    
    protected static IllegalArgumentException unexpectedArgument(String type, Entry<Object, Object> a) {
        return new IllegalArgumentException(
                "Unexpected " + type + " argument " + 
                a.getKey() + " -> " + a.getValue());
    }
    
    protected static IllegalArgumentException duplicateArgument(String type, Entry<Object, Object> a, Object other) {
        return new IllegalArgumentException(
                "Duplicate " + type + " argument " + 
                a.getKey() + " -> " + a.getValue() + ", " + other);
    }
    
    private static Object[] copy(Object[] ary) {
        return ary != null && ary.length > 0 ? ary.clone() : EMPTY;
    }

    private static String[] copy(String[] ary) {
        return ary != null && ary.length > 0 ? ary.clone() : EMPTY_S;
    }
    
    private static <K,V> Map<K,V> copy(Map<K,V> map) {
        return map != null && !map.isEmpty() ?
                new HashMap<>(map) : Collections.<K,V>emptyMap();
    }

    protected final Object[] intArgs;
    protected final Object[] charArgs;
    protected final Map<String, Object> stringArgs;
    protected final String[] stringKeys;
    protected final Object[] stringValues;

    public SimpleArgs(Object... args) {
        this(args, null, null);
    }
    
    public SimpleArgs(Object[] intArgs, Object[] charArgs, Map<String, Object> stringArgs) {
        this.intArgs = copy(intArgs);
        this.charArgs = copy(charArgs);
        final int strSize = stringArgs != null ? stringArgs.size() : 9999;
        if (strSize < 16) {
            this.stringArgs = null;
            this.stringKeys = new String[strSize];
            this.stringValues = new Object[strSize];
            fill(stringArgs, stringKeys, stringValues);
        } else {
            this.stringArgs = copy(stringArgs);
            this.stringKeys = null;
            this.stringValues = null;
        }
    }
    
    public SimpleArgs(Object[] intArgs, Object[] charArgs, String[] strKeys, Object[] strValues) {
        this.intArgs = copy(intArgs);
        this.charArgs = copy(charArgs);
        if (strKeys.length != strValues.length) {
            throw new IllegalArgumentException(
                    "String keys and values length must match, but was "
                    + strKeys.length + " and " + strValues.length);
        }
        this.stringArgs = null;
        this.stringKeys = copy(strKeys);
        this.stringValues = copy(strValues);
    }
    
    public SimpleArgs(Map<Object, Object> args) {
        List<Object> iArgs = new ArrayList<>();
        List<Object> cArgs = new ArrayList<>();
        this.stringArgs = new HashMap<>();
        this.stringKeys = null;
        this.stringValues = null;
        fill(args, iArgs, cArgs, stringArgs);
        this.intArgs = iArgs.toArray();
        this.charArgs = cArgs.toArray();
    }

    public SimpleArgs(Object[] intArgs, Map<Object, Object> args) {
        List<Object> cArgs = new ArrayList<>();
        this.stringArgs = new HashMap<>();
        this.stringKeys = null;
        this.stringValues = null;
        fill(args, null, cArgs, stringArgs);
        this.intArgs = copy(intArgs);
        this.charArgs = cArgs.toArray();
    }

    public SimpleArgs(String[] strKeys, Object[] strValues) {
        if (strKeys.length != strValues.length) {
            throw new IllegalArgumentException(
                    "String keys and values length must match, but was "
                    + strKeys.length + " and " + strValues.length);
        }
        this.intArgs = copy(strValues);
        this.charArgs = null;
        this.stringArgs = null;
        this.stringKeys = copy(strKeys);
        this.stringValues = intArgs;
    }
    
    @Override
    public Object get(int i) {
        return intArgs[i];
    }

    @Override
    public Object get(char c) {
        return charArgs[cToI(c)];
    }

    @Override
    public Object get(String s) {
        if (stringArgs != null) {
            return stringArgs.get(s);
        }
        for (int i = 0; i < stringKeys.length; i++) {
            if (stringKeys[i].equals(s)) {
                return stringValues[i];
            }
        }
        return null;
    }

}
