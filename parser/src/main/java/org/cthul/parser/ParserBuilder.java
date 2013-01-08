package org.cthul.parser;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.KeySet;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.grammar.GrammarBuilder;
import org.cthul.parser.grammar.GrammarBuilder.ComplexRule;
import org.cthul.parser.grammar.GrammarBuilder.MultiAntiMatch;
import org.cthul.parser.grammar.GrammarBuilder.MultiLookAhead;
import org.cthul.parser.grammar.GrammarBuilder.SingleAntiMatch;
import org.cthul.parser.grammar.GrammarBuilder.SingleLookAhead;
import org.cthul.parser.grammar.api.NullRuleEval;
import org.cthul.parser.grammar.api.ProxyRuleEval;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.lexer.Lexer;
import org.cthul.parser.lexer.LexerBuilder;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.rule.*;

public class ParserBuilder<Token> {
    
    protected static final int TOKEN_PRIORITY = 0;

    protected final KeySet keySet = new KeySet();
    protected final LexerBuilder<Token, ?> lexerBuilder;
    protected final GrammarBuilder<?> grammarBuilder;
    protected final SingleLookAhead<?> gbSingleLookAhead;
    protected final MultiLookAhead<?> gbMultiLookAhead;
    protected final SingleAntiMatch<?> gbSingleAntiMatch;
    protected final MultiAntiMatch<?> gbMultiAntiMatch;
    protected final ComplexRule<?> gbComplexRule;

    public <I extends Input<?>> ParserBuilder(LexerBuilder<Token, I> lexerBuilder, GrammarBuilder<I> grammarBuilder) {
        this.lexerBuilder = lexerBuilder;
        this.grammarBuilder = grammarBuilder;
        gbSingleLookAhead = cast(SingleLookAhead.class, grammarBuilder);
        gbMultiLookAhead = cast(MultiLookAhead.class, grammarBuilder);
        gbSingleAntiMatch = cast(SingleAntiMatch.class, grammarBuilder);
        gbMultiAntiMatch = cast(MultiAntiMatch.class, grammarBuilder);
        gbComplexRule = cast(ComplexRule.class, grammarBuilder);
    }

    public <I extends Input<?>> ParserBuilder(
                         LexerBuilder<Token, I> lexerBuilder, 
                         GrammarBuilder<I> grammarBuilder, 
                         SingleLookAhead<?> gbSingleLookAhead, 
                         MultiLookAhead<?> gbMultiLookAhead, 
                         SingleAntiMatch<?> gbSingleAntiMatch, 
                         MultiAntiMatch<?> gbMultiAntiMatch,
                         ComplexRule<?> gbComplexRule) {
        this.lexerBuilder = lexerBuilder;
        this.grammarBuilder = grammarBuilder;
        this.gbSingleLookAhead = gbSingleLookAhead;
        this.gbMultiLookAhead = gbMultiLookAhead;
        this.gbSingleAntiMatch = gbSingleAntiMatch;
        this.gbMultiAntiMatch = gbMultiAntiMatch;
        this.gbComplexRule = gbComplexRule;
    }
    
    protected static <T> T cast(Class<T> clazz, Object o) {
        if (clazz.isInstance(o)) {
            return (T) o;
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked") // generics are checked in constructor
    public <Parser> Parser createParser(ParserFactory<Parser> factory) {
        GrammarBuilder tmp = grammarBuilder.copy();
        tmp.setTokenMatchers(lexerBuilder.getTokenMatchers());
        Lexer lexer = lexerBuilder.createLexer();
        Grammar grammar = tmp.createGrammar();
        return (Parser) factory.create(lexer, grammar);
    }
    
    protected RuleKey sanatized(RuleKey key) {
        return keySet.sanatized(key);
    }
    
    protected RuleKey[] sanatized(final RuleKey... keys) {
        final RuleKey[] result = new RuleKey[keys.length];
        for (int i = 0; i < keys.length; i++)
            result[i] = sanatized(keys[i]);
        return result;
    }
    
    protected RuleKey rk(String symbol, int priority) {
        return new RuleKey(symbol, priority);
    }
    
    protected RuleKey internRk(String symbol, int priority) {
        return keySet.cached(symbol, priority);
    }
    
    protected RuleKey genRk(String symbol, String suffix) {
        String sym = keySet.generateUniqueSymbol(symbol, suffix);
        return internRk(sym, 0);
    }
    
    protected String generateSymbolForToken(String... values) {
        String s = values.length == 0 ? "epsilon" : values[0];
        return generateSymbolForToken(s, 
                values.length > 1 ? "+" + values.length : null);
    }
    
    protected String generateSymbolForToken(String value) {
        return generateSymbolForToken(value, null);
    }
    
    protected String generateSymbolForToken(String value, String suffix) {
        if (value == null || value.isEmpty()) value = "epsilon";
        for (int i = 0; i < value.length() && i < 10; i++) {
            char c = value.charAt(i);
            if (Character.isWhitespace(c)) {
                value = value.substring(0, i) + "#" + ((int) c) + value.substring(i+1);
            }
        }
        if (value.length() > 10) value = value.substring(0, 10);
        if (suffix != null) value += suffix;
        value = "$" + value;
        return keySet.generateUniqueSymbol(value, "_");
    }
    
    protected RuleKey internAddStringToken(RuleKey key, InputEval<? extends Token, ? super String> eval, String string) {
        lexerBuilder.addStringToken(key, eval, string);
        return key;
    }

    protected RuleKey internAddStringToken(RuleKey key, InputEval<? extends Token, ? super String> eval, String... strings) {
        lexerBuilder.addStringToken(key, eval, strings);
        return key;
    }

    protected RuleKey internAddRegexToken(RuleKey key, InputEval<? extends Token, ? super MatchResult> eval, Pattern pattern) {
        lexerBuilder.addRegexToken(key, eval, pattern);
        return key;
    }

    public RuleKey addStringToken(RuleKey key, InputEval<? extends Token, ? super String> eval, String string) {
        return internAddStringToken(sanatized(key), eval, string);
    }

    public RuleKey addStringToken(RuleKey key, InputEval<? extends Token, ? super String> eval, String... strings) {
        return internAddStringToken(sanatized(key), eval, strings);
    }
    
    public RuleKey addRegexToken(RuleKey key, InputEval<? extends Token, ? super MatchResult> eval, Pattern pattern) {
        return internAddRegexToken(sanatized(key), eval, pattern);
    }
    
    public RuleKey addStringToken(String symbol, InputEval<? extends Token, ? super String> eval, String string) {
        return addStringToken(rk(symbol, TOKEN_PRIORITY), eval, string);
    }

    public RuleKey addStringToken(String symbol, InputEval<? extends Token, ? super String> eval, String... strings) {
        return addStringToken(rk(symbol, TOKEN_PRIORITY), eval, strings);
    }
    
    public RuleKey addRegexToken(String symbol, InputEval<? extends Token, ? super MatchResult> eval, Pattern pattern) {
        return addRegexToken(rk(symbol, TOKEN_PRIORITY), eval, pattern);
    }
    
    public RuleKey addRegexToken(String symbol, InputEval<? extends Token, ? super MatchResult> eval, String pattern) {
        return addRegexToken(symbol, eval, Pattern.compile(pattern));
    }
    
    public RuleKey addStringToken(InputEval<? extends Token, ? super String> eval, String string) {
        String sym = generateSymbolForToken(string);
        return internAddStringToken(internRk(sym, TOKEN_PRIORITY), eval, string);
    }

    public RuleKey addStringToken(InputEval<? extends Token, ? super String> eval, String... strings) {
        String sym = generateSymbolForToken(strings);
        return internAddStringToken(internRk(sym, TOKEN_PRIORITY), eval, strings);
    }
    
    protected RuleKey addProxy(String prefix, String symbol, RuleKey[] rule, RuleEval eval) {
        return addProxy(prefix+symbol, rule, eval);
    }
    
    protected RuleKey addProxy(String symbol, RuleKey[] rule, RuleEval eval) {
        symbol = keySet.generateUniqueSymbol(symbol, "_");
        RuleKey key = internRk(symbol, 0);
        internAddProduction(key, rule, eval);
        return key;
    }
    
    protected RuleEval proxyEval() {
        return ProxyRuleEval.instance();
    }
    
    protected RuleEval nullEval() {
        return NullRuleEval.instance();
    }
    
    protected RuleKey internAddProduction(RuleKey key, RuleKey[] rule, RuleEval eval) {
        grammarBuilder.addProduction(key, rule, eval);
        return key;
    }
    
    protected RuleKey internAddLookAhead(RuleKey key, RuleKey[] match, RuleEval eval) {
        if (gbMultiLookAhead != null) {
            gbMultiLookAhead.addLookAhead(key, match, eval);
        } else if (gbSingleLookAhead != null) {
            if (match.length == 1) {
                gbSingleLookAhead.addLookAhead(key, match[0], eval);
            } else {
                RuleKey proxy = addProxy("$?", key.getSymbol(), match, eval);
                gbSingleLookAhead.addLookAhead(key, proxy, proxyEval());
            }
        } else {
            throw new UnsupportedOperationException(grammarBuilder + 
                    " does not implement Single- or MultiLookAhead");
        }
        return key;
    }
    
    protected RuleKey internAddAntiMatch(RuleKey key, RuleKey[] match, RuleEval eval) {
        if (gbMultiAntiMatch != null) {
            gbMultiAntiMatch.addAntiMatch(key, match, eval);
        } else if (gbSingleAntiMatch != null) {
            if (match.length == 1) {
                gbSingleAntiMatch.addAntiMatch(key, match[0], proxyEval());
            } else {
                RuleKey proxy = addProxy("$!", key.getSymbol(), match, nullEval());
                gbSingleAntiMatch.addAntiMatch(key, proxy, eval);
            }
        } else {
            throw new UnsupportedOperationException(grammarBuilder + 
                    " does not implement Single- or MultiAntiMatch");
        }
        return key;
    }
    
    protected RuleKey internAddRule(RuleKey key, Rule rule, RuleEval eval) {
        if (gbComplexRule != null) {
            gbComplexRule.addRule(key, rule, eval);
        } else {
            decompose(key, rule, eval);
        }
        return key;
    }
    
    protected RuleKey decompose(RuleKey key, Rule rule, RuleEval eval) {
        if (rule instanceof KeyMatchRule) {
            return decompose(key, (KeyMatchRule) rule, eval);
        } else if (rule instanceof ProductionRule) {
            return decompose(key, (ProductionRule) rule, eval);
        } else {
            throw new UnsupportedOperationException(
                    "Cannot decompose " + rule);
        }
    }
    
    protected RuleKey decompose(RuleKey key, KeyMatchRule rule, RuleEval eval) {
        return sanatized(rule.getKey());
    }
    
    protected RuleKey decompose(RuleKey key, ProductionRule rule, RuleEval eval) {
        if (eval == null) eval = rule.getEval();
        RuleKey[] production = decompose(key.getSymbol()+"$", rule);
        return internAddProduction(key, production, eval);
    }
    
    protected RuleKey decompose(RuleKey key, LookAheadRule rule, RuleEval eval) {
        if (eval == null) eval = rule.getEval();
        RuleKey[] production = decompose(key.getSymbol()+"$", rule);
        return internAddLookAhead(key, production, eval);
    }
    
    protected RuleKey decompose(RuleKey key, AntiMatchRule rule, RuleEval eval) {
        if (eval == null) eval = rule.getEval();
        RuleKey[] production = decompose(key.getSymbol()+"$", rule);
        return internAddAntiMatch(key, production, eval);
    }
    
    protected RuleKey[] decompose(String tmpKey, CompositeRule rule) {
        final List<Rule> rules = rule.getRules();
        final RuleKey[] result = new RuleKey[rules.size()];
        for (int i = 0; i < result.length; i++) {
            RuleKey key = genRk(tmpKey, null);
            result[i] = decompose(key, rule, null);
        }
        return result;
    }
    
    public RuleKey addProduction(RuleKey key, RuleKey[] rule, RuleEval eval) {
        return internAddProduction(sanatized(key), sanatized(rule), eval);
    }
    
    public RuleKey addLookAhead(RuleKey key, RuleKey[] match, RuleEval eval) {
        return internAddLookAhead(sanatized(key), sanatized(match), eval);
    }

    public RuleKey addAntiMatch(RuleKey key, RuleKey[] match, RuleEval eval) {
        return internAddAntiMatch(sanatized(key), sanatized(match), eval);
    }

    public ProductionBuilder productionBuilder(RuleKey key) {
        return new ProductionBuilder(this, key);
    }
    
    public ProductionBuilder productionBuilder(String symbol, int priority) {
        return productionBuilder(rk(symbol, priority));
    }
    
}
