package org.cthul.parser.token;

import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.api.TokenMatch;

/**
 *
 * @author Arian Treffer
 */
public interface Token<V> extends TokenMatch<V> {
    
    @Override
    public V eval();
    
    boolean match(RuleKey key);
    
    int getChannel();
    
    int getInputIndex();
    
    StringBuilder toString(StringBuilder sb);
    
}
