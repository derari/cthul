package org.cthul.parser.token.lexer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import org.cthul.parser.api.Value;
import org.cthul.parser.reflect.AbstractMethodInputEval;
import org.cthul.parser.reflect.MethodExecutor.ArgMap;
import org.cthul.parser.reflect.MethodExecutor.ArgMapFactory;
import org.cthul.parser.reflect.MethodExecutor.DirectIndexArgMap;
import org.cthul.parser.reflect.MethodInputEval;
import org.cthul.parser.token.TokenBuilder;
import org.cthul.parser.util.InstanceMap;

/**
 * 
 * @param <Token> 
 */
public class TokenMethodInputEval<Token> extends AbstractMethodInputEval<Token, TokenBuilder<?,?>> {
    
    public TokenMethodInputEval(Object impl, Method m, String pattern) {
        super(impl, m, MAPPER, pattern);
    }
    
    protected static final TokenEvalArgMapFactory MAPPER = new TokenEvalArgMapFactory();
    
    protected static class TokenEvalArgMapFactory implements ArgMapFactory<TokenBuilder<?,?>, String> {

        @Override
        public ArgMap<? super TokenBuilder<?,?>, ? super String> create(Class<?> paramType, Annotation[] annotations, String hint, boolean isVarArgs) {
            if (Value.class.isAssignableFrom(paramType)) {
                return TOKEN_ARG;
            }
            if (TokenBuilder.class.isAssignableFrom(paramType)) {
                return TOKEN_BUILDER_ARG;
            }
            return otherParameterType(paramType, annotations, hint, isVarArgs);
        }
        
        protected ArgMap<? super TokenBuilder<?,?>, ? super String> otherParameterType(Class<?> paramType, Annotation[] annotations, String hint, boolean isVarArgs) {
            ArgMap<? super MatchResult, ? super String> mrArg = MethodInputEval.MAPPER.create(paramType, annotations, hint, isVarArgs);
            if (mrArg == MethodInputEval.MATCHRESULT_ARG) return TB_MATCHRESULT_ARG;
            if (mrArg == MethodInputEval.GROUP_ARG) return TB_GROUP_ARG;
            return (ArgMap) mrArg;
        }
    }
    
    protected static final ArgMap<TokenBuilder<?,?>, Object> TOKEN_BUILDER_ARG = new DirectIndexArgMap<TokenBuilder<?,?>>() {
        @Override
        public Object map(int i, InstanceMap ctx, TokenBuilder<?,?> tb, Object arg2) {
            return tb;
        }
    };
    
    protected static final ArgMap<TokenBuilder<?,?>, Object> TOKEN_ARG = new DirectIndexArgMap<TokenBuilder<?,?>>() {
        @Override
        public Object map(int i, InstanceMap ctx, TokenBuilder<?,?> tb, Object arg2) {
            return tb.newToken();
        }
    };
    
    protected static final ArgMap<TokenBuilder<?,?>, ? super String> TB_MATCHRESULT_ARG = tbArg(MethodInputEval.MATCHRESULT_ARG);
    
    protected static final ArgMap<TokenBuilder<?,?>, ? super String> TB_GROUP_ARG = tbArg(MethodInputEval.GROUP_ARG);
    
    protected static <T> ArgMap<TokenBuilder<?,?>, ? super String> tbArg(final ArgMap<? super MatchResult, ? super String> mrArg) {
        if (mrArg instanceof DirectIndexArgMap) {
            return new DirectIndexArgMap<TokenBuilder<?,?>>(){
                @Override
                public Object map(int i, InstanceMap ctx, TokenBuilder<?,?> arg, Object arg2) {
                    return mrArg.map(i, ctx, arg.getMatchResult(), arg2);
                }
            };
        } else {
            return new ArgMap<TokenBuilder<?, ?>, String>() {
                @Override
                public Object map(int i, InstanceMap ctx, TokenBuilder<?,?> arg, Object arg2) {
                    return mrArg.map(i, ctx, arg.getMatchResult(), arg2);
                }
                @Override
                public int mapIndex(int i, Annotation[] annotations, String hint) {
                    return mrArg.mapIndex(i, annotations, hint);
                }
            };
        }
    }
    
}
