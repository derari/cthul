package org.cthul.parser.rule;

public class StringMatchRule extends Rule {
    
    protected final String string;

    public StringMatchRule(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
    
}
