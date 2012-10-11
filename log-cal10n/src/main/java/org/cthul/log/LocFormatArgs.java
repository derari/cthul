package org.cthul.log;

import java.util.List;
import org.cthul.strings.format.FormatArgs;

/**
 *
 * @author Arian Treffer
 */
public class LocFormatArgs implements FormatArgs {
    
    private final Object e;
    private final List<String> names;
    private final Object[] values;

    public LocFormatArgs(Object e, List<String> names, Object[] values) {
        this.e = e;
        this.names = names;
        this.values = values;
    }

    @Override
    public Object get(int i) {
        return values[i-1];
    }

    @Override
    public Object get(char c) {
        if (c == 'E' && e != null) return e;
        final int len = names.size();
        for (int i = 0; i < len; i++) {
            String n = names.get(i);
            if (n.length() > 0 && n.charAt(0) == c) {
                return values[i];
            }
        }
        return null;
    }

    @Override
    public Object get(String s) {
        final int len = names.size();
        for (int i = 0; i < len; i++) {
            String n = names.get(i);
            if (n.equals(s)) {
                return values[i];
            }
        }
        return null;
    }
    
}
