package org.cthul.parser.impl;

/**
 *
 * @author Arian Treffer
 */
public class MatchImpl {

    private int i;
    
    public MatchImpl() {
        i = -1;
    }

    public MatchImpl(int i) {
        this.i = i;
    }

    public String integer(String s) {
        if (i < 0) {
            return s;
        } else {
            return String.valueOf(i);
        }
    }
    
}
