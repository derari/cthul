package org.cthul.parser.annotation;

/**
 *
 * @author Arian Treffer
 */
public class EmptyProduction {
    
    @Production("p ::= ")
    public void p() {}
    
}
