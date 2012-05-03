package org.cthul.strings;

/**
 *
 * @author Arian Treffer
 */
public class PluralizerTestBase<P extends Pluralizer> {
    
    protected P instance;
    
    protected String pluralOf(String s) {
        return instance.pluralize(s);
    }
    
    protected String singularOf(String s) {
        return instance.singularize(s);
    }
    
}
