package org.cthul.parser.grammar;

import java.util.ArrayList;
import java.util.List;
import org.cthul.parser.annotation.Channel;
import org.cthul.parser.annotation.Match;
import org.cthul.parser.annotation.Key;
import org.cthul.parser.annotation.Production;

/**
 *
 * @author Arian Treffer
 */
public class ListGrammar {
    
    @Match("[a,{}]")
    public void token() {}
    
    @Key("WS")
    @Match("[\\s]+")
    @Channel(Channel.Whitespace)
    public void ws() {}
    
    @Production("count ::= { list }")
    public int count(Void open, List<Integer> list, Void close) {
        int c = 0;
        for (Integer i: list) c += i;
        return c;
    }
    
    @Key("list")
    @Production({"list_EMPTY", "list_STEP"})
    public List<Integer> list(List<Integer> list) {
        return list;
    }
    
    @Production("list_EMPTY ::= ")
    public List<Integer> listEmpty() {
        return new ArrayList<Integer>();
    }
    
    @Production("list_STEP ::= list_STEP , a")
    public List<Integer> listStep(List<Integer> list, Void c, Object a) {
        list.add(1);
        return list;
    }
    
    @Production("list_STEP ::= list_EMPTY a")
    public List<Integer> listInit(List<Integer> list, Object a) {
        list.add(1);
        return list;
    }
    
}
