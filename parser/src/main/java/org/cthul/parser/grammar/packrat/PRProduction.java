package org.cthul.parser.grammar.packrat;

/**
 *
 * @author Arian Treffer
 */
public class PRProduction {
    
    private final String key;
    private final Part[] parts;

    public PRProduction(String key, Part[] parts) {
        this.key = key;
        this.parts = parts;
    }
    
    public static class Part {
        
        public int index;
        public Part next;
        public Part alt;
        
        public String key;
        
    }
    
}
