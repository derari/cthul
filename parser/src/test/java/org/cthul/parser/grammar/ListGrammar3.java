package org.cthul.parser.grammar;

import java.util.List;
import org.cthul.parser.annotation.Channel;
import org.cthul.parser.annotation.Match;
import org.cthul.parser.annotation.Key;
import org.cthul.parser.annotation.Production;
import org.cthul.parser.annotation.Sequence;

/**
 *
 * @author Arian Treffer
 */
public class ListGrammar3 {
    
    @Match("[a,{}]")
    public void token() {}
    
    @Key("WS")
    @Match("[\\s]+")
    @Channel(Channel.Whitespace)
    public void ws() {}
    
    @Production("a")
    public Integer x() {
        return 1;
    }
    
    @Production("{ list }")
    public int count(Void open, List<Integer> list, Void close) {
        int c = 0;
        for (Integer i: list) c += i;
        return c;
    }
    
    @Sequence(item="x", separator=",")
    public List<Integer> list(List<Integer> list) {
        return list;
    }
    
}
