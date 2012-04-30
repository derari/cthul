package org.cthul.log;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import org.cthul.log.format.ClassName;
import org.cthul.log.format.Format;
import org.cthul.log.format.NullFormat;

/**
 *
 * @author Arian Treffer
 */
public class CLogConfiguration {
    
    private static final CLogConfiguration Default = new CLogConfiguration(null);

    public static CLogConfiguration getDefault() {
        return Default;
    }
    
    static {
        ClassName.register(Default);
    }

    private CLogConfiguration parent;
    private Locale locale = null;
    private Format[] shortFormats = new Format[26*2];
    private Map<String, Format> longFormats = new ConcurrentSkipListMap<>();

    public CLogConfiguration() {
        this(getDefault());
    }

    public CLogConfiguration(CLogConfiguration parent) {
        this.parent = parent;
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
    
    public void clearShortFormat(char c) {
        setShortFormat(c, NullFormat.INSTANCE);
    }
    
    private Format shortFormat(int i) {
        Format f = shortFormats[i];
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
        setLongFormat(key, NullFormat.INSTANCE);
    }
    
    public Format longFormat(String key) {
        Format f = longFormats.get(key);
        if (f != null) return f;
        if (parent == null) return null;
        return parent.longFormat(key);
    }
    
    
}
