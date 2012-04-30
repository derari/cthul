package org.cthul.parser.lexer;

import java.util.ArrayList;
import java.util.List;
import org.cthul.parser.annotation.Channel;
import org.cthul.parser.annotation.Match;
import org.cthul.parser.annotation.Key;
import org.cthul.parser.annotation.Production;

/**
 * right-deep list grammar.
 * @author Arian Treffer
 */
public class ListGrammar2 {
    
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
    @Production({"list_", "list_EMPTY"})
    public List<Integer> list(List<Integer> list) {
        return list;
    }
    
    @Production("list_EMPTY ::= ")
    public List<Integer> listEmpty() {
        return new ArrayList<Integer>();
    }
    
    @Production("list_STEP ::= , a list_STEP")
    public List<Integer> listStep(Void c, Object a, List<Integer> list) {
        list.add(1);
        return list;
    }
    
    @Production({"list_STEP ::= list_EMPTY"})
    public List<Integer> lastStep(List<Integer> list) {
        return list;
    }
    
    @Production("list_ ::= a list_STEP")
    public List<Integer> listInit(Object a, List<Integer> list) {
        list.add(1);
        return list;
    }
    
}
