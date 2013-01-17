package org.cthul.parser.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.annotation.processing.AnnotationReader;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.reflect.MethodInputEval;

public class MatchReader<Token> implements AnnotationReader<Token, MatchResult> {

    @Override
    public void process(AnnotatedElement ae, Object impl, Annotation at, ParserBuilder<? super Token, ? extends MatchResult> pb) {
        Match match = (Match) at;
        Method m = (Method) ae;
        Key name = m.getAnnotation(Key.class);
        for (String s : match.value()) {
            String key = selectName(name, m, s, false);
            int priority = selectPriority(m);
            String pattern = selectRule(s);
            MatchEval<? extends Token, MatchResult> eval = new MethodInputEval<>(impl, m, pattern);
            pb.addRegexToken(new RuleKey(key, priority), eval, Pattern.compile(pattern));
        }
    }
    
    protected String selectName(Key name, Method m, String s, boolean isProduction) {
        int i = s.indexOf(" ::=");
        if (i < 0) {
            if (name != null) {
                String n = name.value();
                return n.isEmpty() ? m.getName() : n;
            } else {
                return isProduction ? m.getName() : null;
            }
        } else {
            return s.substring(0, i).trim();
        }
    }
    
    protected String selectRule(String s) {
        int i = s.indexOf(" ::=");
        if (i < 0) {
            return s.trim();
        } else {
            return s.substring(i + " ::=".length()).trim();
        }
    }

    protected int selectPriority(Method m) {
        Priority priority = m.getAnnotation(Priority.class);
        if (priority == null) {
            return Priority.Default;
        } else {
            return priority.value();
        }
    }
    
}
