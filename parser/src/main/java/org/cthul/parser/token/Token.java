package org.cthul.parser.token;

import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.api.InputMatch;

/**
 *
 * @author Arian Treffer
 */
public interface Token<V> extends InputMatch<V> {
    
    @Override
    public V eval(Object arg);
    
    boolean match(RuleKey key);
    
    int getChannel();
    
    int getInputIndex();
    
    StringBuilder toString(StringBuilder sb);
    
    Class<? extends V> getValueType();
    
}
