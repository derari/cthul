package org.cthul.strings.plural;

import java.util.*;
import java.util.ResourceBundle.Control;
import org.cthul.strings.Pluralizer;

/**
 * Manages {@linkplain Pluralizer pluralizers} for different 
 * {@linkplain Locale locales}.
 * 
 * @author Arian Treffer
 */
public class PluralizerRegistry {

    public static PluralizerRegistry INSTANCE;

    private static ResourceBundle.Control defControl = new ResourceBundle.Control() { };
    private static String PLURALIZER_CLASS = Pluralizer.class.getName();

    static {
        PluralizerRegistry reg = new PluralizerRegistry();
        
        Pluralizer pEn = new DefaultEnglishPluralizer();
        reg.register(Locale.ROOT, pEn);
        reg.register(Locale.ENGLISH, pEn);
        
        INSTANCE = reg;
    }

    private final Map<Locale, Pluralizer> pluralizers = new HashMap<>();
    private final ResourceBundle.Control control;

    public PluralizerRegistry() {
        this(defControl);
    }

    public PluralizerRegistry(Control c) {
        this.control = c;
    }

    /**
     * Registers a pluralizer
     * @param l
     * @param p
     * @return the pluralizer previously registered for that locale, 
     *         or {@code null}
     */
    public synchronized Pluralizer register(Locale l, Pluralizer p) {
        return pluralizers.put(l, p);
    }

    /**
     * Finds a pluralizer for a locale.
     * @param l
     * @return a pluralizer
     * @see #getExact(java.util.Locale) 
     */
    public synchronized Pluralizer get(Locale l) {
        while (l != null) {
            List<Locale> candidates = control
                                    .getCandidateLocales(PLURALIZER_CLASS, l);
            for (Locale c: candidates) {
                Pluralizer p = pluralizers.get(c);
                if (p != null) return p;
            }
            l = control.getFallbackLocale(PLURALIZER_CLASS, l);
        }
        return null;
    }

    /**
     * Returns the pluralizer that is registered for a locale, or {@code null}.
     * @param l
     * @return a pluralizer
     * @see #get(java.util.Locale)
     */
    public synchronized Pluralizer getExact(Locale l) {
        return pluralizers.get(l);
    }

    /**
     * Creates a shallow copy of this registry.
     * @return a new registry
     */
    public synchronized PluralizerRegistry copy() {
        PluralizerRegistry r = new PluralizerRegistry(control);
        r.pluralizers.putAll(pluralizers);
        return r;
    }

}
