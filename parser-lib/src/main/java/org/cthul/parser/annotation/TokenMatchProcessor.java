package org.cthul.parser.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.reflect.FieldInputEval;
import org.cthul.parser.token.Token;
import org.cthul.parser.token.TokenBuilder;
import org.cthul.parser.token.TokenFactory;
import org.cthul.parser.token.lexer.TokenMethodInputEval;
import org.cthul.parser.token.lexer.TokenStreamInputEval;
import org.cthul.parser.util.InstanceMap;

public class TokenMatchProcessor<T extends Token<?>> extends MatchProcessor<T> {

    @Override
    public void process(AnnotatedElement ae, Object impl, Annotation at, ParserBuilder<? super T, ? extends MatchResult> pb) {
        TokenMatch match = (TokenMatch) at;
        Key name = ae.getAnnotation(Key.class);
        for (String s : match.value()) {
            String key = selectName(name, ae, s, false);
            if (key == null) key = "";
            int priority = selectPriority(ae);
            String pattern = selectRule(s);
            int channel = selectChannel(ae);
            TokenFactory<?, ? extends T> factory = selectFactory(ae);
            
            InputEval<?, ? super TokenBuilder<?, ? super T>> mEval = createTokenMatchEval(impl, ae, pattern);
            InputEval<T, Object> eval = TokenStreamInputEval.wrapValue(key, channel, factory, mEval);
            pb.addRegexToken(new RuleKey(key, priority), eval, Pattern.compile(pattern));
        }
    }

    protected int selectChannel(AnnotatedElement ae) {
        Channel channel = ae.getAnnotation(Channel.class);
        if (channel == null) {
            return Channel.Undefined;
        } else {
            return channel.value();
        }
    }

    @SuppressWarnings("unchecked")
    protected TokenFactory<?, ? extends T> selectFactory(AnnotatedElement ae) {
        TokenClass tokenClass = ae.getAnnotation(TokenClass.class);
        if (tokenClass == null) return null;
        Class<?> clazz = tokenClass.value();
        return (TokenFactory) InstanceMap.getFactory(clazz, tokenClass.factoryField());
    }

    protected InputEval<?, ? super TokenBuilder<?, ? super T>> createTokenMatchEval(Object impl, AnnotatedElement ae, String pattern) {
        if (ae instanceof Method) {
            Method m = (Method) ae;
            return new TokenMethodInputEval<>(impl, m, pattern);
        } else if (ae instanceof Field) {
            Field f = (Field) ae;
            return FieldInputEval.create(impl, f);
        }
        throw new IllegalArgumentException("Expected method or field, got " + ae);
    }
    
}
