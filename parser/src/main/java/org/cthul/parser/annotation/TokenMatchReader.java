package org.cthul.parser.annotation;

import org.cthul.parser.token.lexer.TokenInputEval;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.token.lexer.TokenMethodInputEval;
import org.cthul.parser.token.*;
import org.cthul.parser.token.lexer.TokenStreamInputEval;
import org.cthul.parser.util.InstanceMap;

public class TokenMatchReader<V, T extends Token<? extends V>> extends MatchReader<T> {

    @Override
    public void process(AnnotatedElement ae, Object impl, Annotation at, ParserBuilder<? super T, ? extends MatchResult> pb) {
        TokenMatch match = (TokenMatch) at;
        Method m = (Method) ae;
        Key name = m.getAnnotation(Key.class);
        for (String s : match.value()) {
            String key = selectName(name, m, s, false);
            int priority = selectPriority(m);
            String pattern = selectRule(s);
            int channel = selectChannel(m);
            TokenFactory<V, ? extends T> factory = selectFactory(m);
            
            TokenMethodInputEval<?> mEval = new TokenMethodInputEval<>(impl, m, pattern);
            MatchEval<T, Object> eval = TokenStreamInputEval.wrapValue(key, channel, factory, mEval);
            pb.addRegexToken(new RuleKey(key, priority), eval, Pattern.compile(pattern));
        }
    }

    protected int selectChannel(Method m) {
        Channel channel = m.getAnnotation(Channel.class);
        if (channel == null) {
            return Channel.Undefined;
        } else {
            return channel.value();
        }
    }

    @SuppressWarnings("unchecked")
    protected TokenFactory<V, ? extends T> selectFactory(Method m) {
        TokenClass tokenClass = m.getAnnotation(TokenClass.class);
        if (tokenClass == null) return null;
        Class<?> clazz = tokenClass.value();
        return (TokenFactory) InstanceMap.getFactory(clazz, tokenClass.factoryField());
    }
    
}
