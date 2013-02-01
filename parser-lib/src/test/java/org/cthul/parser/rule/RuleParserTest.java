package org.cthul.parser.rule;

import org.cthul.parser.api.RuleKey;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.CthulMatchers.*;


public class RuleParserTest {

    public <T> T cast(Object o, Class<T> clazz) {
        assertThat(o, _isA(clazz));
        return (T) o;
    }
    
    public static RuleKey rk(String s, int p) {
        return new RuleKey(s, p);
    }
    
    @Test
    public void test_string_production() {
        Rule r = new RuleParser("'foo\\'' \"bar'\"", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        StringMatchRule rFoo = cast(cr.getRules().get(0), StringMatchRule.class);
        assertThat(rFoo.getString(), is("foo'"));
        
        StringMatchRule rBar = cast(cr.getRules().get(1), StringMatchRule.class);
        assertThat(rBar.getString(), is("bar'"));
    }
    
    @Test
    public void test_key_production() {
        Rule r = new RuleParser("foo `'bar`", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        
        KeyMatchRule rFoo = cast(cr.getRules().get(0), KeyMatchRule.class);
        assertThat(rFoo.getKey(), is(rk("foo", 5)));
        
        KeyMatchRule rBar = cast(cr.getRules().get(1), KeyMatchRule.class);
        assertThat(rBar.getKey(), is(rk("'bar", 0)));
    }
    
    @Test
    public void test_key_production_priority() {
        Rule r = new RuleParser("foo^ foo# foo. foo~ bar^ bar# bar. bar~ foo[1] bar[-2] baz[+3]", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        RuleKey[] expected = {
            rk("foo", 6), rk("foo", 0), rk("foo", 5), rk("foo", 5),
            rk("bar", 6), rk("bar", 0), rk("bar", 5), rk("bar", 0),
            rk("foo", 1), rk("bar", -2), rk("baz", 3)};
        
        for (int i = 0; i < expected.length; i++) {
            KeyMatchRule m = cast(cr.getRules().get(i), KeyMatchRule.class);
            assertThat(i + ":", m.getKey(), is(expected[i]));
        }
    }
    
    @Test
    public void test_group() {
        Rule r = new RuleParser("a (b c) d", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        cast(cr.getRules().get(0), KeyMatchRule.class);
        cast(cr.getRules().get(2), KeyMatchRule.class);
        ProductionRule gr = cast(cr.getRules().get(1), ProductionRule.class);
        
        KeyMatchRule rB = cast(gr.getRules().get(0), KeyMatchRule.class);
        assertThat(rB.getKey(), is(rk("b", 0)));        
        KeyMatchRule rC = cast(gr.getRules().get(1), KeyMatchRule.class);
        assertThat(rC.getKey(), is(rk("c", 0)));
    }
    
    @Test
    public void test_anti_single() {
        Rule r = new RuleParser("a !b c", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        cast(cr.getRules().get(0), KeyMatchRule.class);
        cast(cr.getRules().get(2), KeyMatchRule.class);
        AntiMatchRule amr = cast(cr.getRules().get(1), AntiMatchRule.class);
        
        KeyMatchRule rB = cast(amr.getRules().get(0), KeyMatchRule.class);
        assertThat(rB.getKey(), is(rk("b", 0)));        
    }

    @Test
    public void test_anti_group() {
        Rule r = new RuleParser("a !(b c) d", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        cast(cr.getRules().get(0), KeyMatchRule.class);
        cast(cr.getRules().get(2), KeyMatchRule.class);
        AntiMatchRule amr = cast(cr.getRules().get(1), AntiMatchRule.class);
        
        KeyMatchRule rB = cast(amr.getRules().get(0), KeyMatchRule.class);
        assertThat(rB.getKey(), is(rk("b", 0)));        
        KeyMatchRule rC = cast(amr.getRules().get(1), KeyMatchRule.class);
        assertThat(rC.getKey(), is(rk("c", 0)));
    }
    
    @Test
    public void test_look_ahead_single() {
        Rule r = new RuleParser("a &b c", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        cast(cr.getRules().get(0), KeyMatchRule.class);
        cast(cr.getRules().get(2), KeyMatchRule.class);
        LookAheadRule lar = cast(cr.getRules().get(1), LookAheadRule.class);
        
        KeyMatchRule rB = cast(lar.getRules().get(0), KeyMatchRule.class);
        assertThat(rB.getKey(), is(rk("b", 0)));        
    }

    @Test
    public void test_look_ahead_group() {
        Rule r = new RuleParser("a & ( b c ) d", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        cast(cr.getRules().get(0), KeyMatchRule.class);
        cast(cr.getRules().get(2), KeyMatchRule.class);
        LookAheadRule lar = cast(cr.getRules().get(1), LookAheadRule.class);
        
        KeyMatchRule rB = cast(lar.getRules().get(0), KeyMatchRule.class);
        assertThat(rB.getKey(), is(rk("b", 0)));        
        KeyMatchRule rC = cast(lar.getRules().get(1), KeyMatchRule.class);
        assertThat(rC.getKey(), is(rk("c", 0)));
    }

    @Test
    public void test_no_result_single() {
        Rule r = new RuleParser("a : b c", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        cast(cr.getRules().get(0), KeyMatchRule.class);
        cast(cr.getRules().get(2), KeyMatchRule.class);
        NoResultRule lar = cast(cr.getRules().get(1), NoResultRule.class);
        
        KeyMatchRule rB = cast(lar.getRules().get(0), KeyMatchRule.class);
        assertThat(rB.getKey(), is(rk("b", 0)));        
    }

    @Test
    public void test_no_result_group() {
        Rule r = new RuleParser("a :(b c) d", "foo", 5).parse();
        CompositeRule cr = cast(r, CompositeRule.class);
        cast(cr.getRules().get(0), KeyMatchRule.class);
        cast(cr.getRules().get(2), KeyMatchRule.class);
        NoResultRule lar = cast(cr.getRules().get(1), NoResultRule.class);
        
        KeyMatchRule rB = cast(lar.getRules().get(0), KeyMatchRule.class);
        assertThat(rB.getKey(), is(rk("b", 0)));        
        KeyMatchRule rC = cast(lar.getRules().get(1), KeyMatchRule.class);
        assertThat(rC.getKey(), is(rk("c", 0)));
    }

}
