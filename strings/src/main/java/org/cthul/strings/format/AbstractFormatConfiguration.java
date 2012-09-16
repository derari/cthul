package org.cthul.strings.format;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Manages short and long formats.
 * 
 * @author Arian Treffer
 */
public abstract class AbstractFormatConfiguration<Format> {
    
    private final AbstractFormatConfiguration<Format> parent;
    private Locale locale = null;
    private final Format[] shortFormats;
    private final Map<String, Format> longFormats = new ConcurrentSkipListMap<>();

    public AbstractFormatConfiguration(AbstractFormatConfiguration parent, Class<Format> formatClass) {
        this.parent = parent;
        shortFormats = (Format[]) Array.newInstance(formatClass, 2*26);
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public Locale locale() {
        if (locale == null) {
            if (parent == null) {
                return Locale.getDefault();
            } else {
                return parent.locale();
            }
        }
        return locale;
    }
    
    protected abstract AbstractFormatConfiguration createChildConfig();
    
    public AbstractFormatConfiguration forLocale(Locale locale) {
        AbstractFormatConfiguration c = createChildConfig();
        c.setLocale(locale);
        return c;
    }
    
    private int index(char c) {
        if (c < 'A' || c > 'z' || (c > 'Z' && c < 'a')) {
            throw new IndexOutOfBoundsException(String.valueOf(c));
        }
        return c < 'a' ? c - 'A' : 26 + c - 'a';
    }

    public Format getShortFormat(char c) {
        return shortFormats[index(c)];
    }
    
    public void setShortFormat(char c, Format format) {
        shortFormats[index(c)] = format;
    }
    
    public void removeShortFormat(char c) {
        setShortFormat(c, null);
    }
    
    protected abstract Format nullFormat();
    
    public void clearShortFormat(char c) {
        setShortFormat(c, nullFormat());
    }
    
    private Format shortFormat(int i) {
        Format f = shortFormats[i];
        if (f == nullFormat()) return null;
        if (f != null) return f;
        if (parent == null) return null;
        return parent.shortFormat(i);
    }
    
    public Format shortFormat(char c) {
        return shortFormat(index(c));
    }
    
    public Format getLongFormat(String key) {
        return longFormats.get(key);
    }
    
    public void setLongFormat(String key, Format format) {
        longFormats.put(key, format);
    }
    
    public void removeLongFormat(String key) {
        setLongFormat(key, null);
    }
    
    public void clearLongFormat(String key) {
        setLongFormat(key, nullFormat());
    }
    
    public Format longFormat(String key) {
        Format f = longFormats.get(key);
        if (f == NullFormat.INSTANCE) return null;
        if (f != null) return f;
        if (parent == null) return null;
        return parent.longFormat(key);
    }
    
}
