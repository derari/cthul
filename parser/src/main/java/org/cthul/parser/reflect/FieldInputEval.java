package org.cthul.parser.reflect;

import java.lang.reflect.Field;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;

public abstract class FieldInputEval<Token, Match> implements InputEval<Token, Match> {
    
    public static <Token, Match> FieldInputEval<Token, Match> create(Object impl, Field field) {
        FieldValue v = FieldValue.create(impl, field);
        if (InputEval.class.isAssignableFrom(field.getType())) {
            return new Proxy<>(v);
        } else {
            return new Value<>(v);
        }
    }
    
    protected final FieldValue field;

    public FieldInputEval(FieldValue field) {
        this.field = field;
    }

    public static class Value<Token, Match> extends FieldInputEval<Token, Match> {

        public Value(FieldValue field) {
            super(field);
        }

        @Override
        public Token eval(Context<? extends StringInput> context, Match match, int start, int end) {
            return (Token) field.get(context);
        }
        
    }
    
    public static class Proxy<Token, Match> extends FieldInputEval<Token, Match> {

        public Proxy(FieldValue field) {
            super(field);
        }

        @Override
        public Token eval(Context<? extends StringInput> context, Match match, int start, int end) {
            InputEval<Token, Match> me = (InputEval) field.get(context);
            return me.eval(context, match, start, end);
        }
        
    }
    
}
