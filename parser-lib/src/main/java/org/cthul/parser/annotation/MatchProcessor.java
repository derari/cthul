package org.cthul.parser.annotation;

import org.cthul.parser.annotation.scan.DefaultProcessorBase;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.annotation.Key;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.reflect.FieldInputEval;
import org.cthul.parser.reflect.MethodInputEval;

public class MatchProcessor<Token> extends DefaultProcessorBase<Token, MatchResult> {

    @Override
    public void process(AnnotatedElement ae, Object impl, Annotation at, ParserBuilder<? super Token, ? extends MatchResult> pb) {
        Match match = (Match) at;
        Key name = ae.getAnnotation(Key.class);
        for (String s : match.value()) {
            String key = selectName(name, ae, s, false);
            int priority = selectPriority(ae);
            String pattern = selectRule(s);
            InputEval<? extends Token, MatchResult> eval = createMatchEval(impl, ae, pattern);
            pb.addRegexToken(new RuleKey(key, priority), eval, Pattern.compile(pattern));
        }
    }

    protected InputEval<? extends Token, MatchResult> createMatchEval(Object impl, AnnotatedElement ae, String pattern) {
        if (ae instanceof Method) {
            Method m = (Method) ae;
            return new MethodInputEval<>(impl, m, pattern);
        } else if (ae instanceof Field) {
            Field f = (Field) ae;
            return FieldInputEval.create(impl, f);
        }
        throw new IllegalArgumentException("Expected method or field, got " + ae);
    } 
    
}
