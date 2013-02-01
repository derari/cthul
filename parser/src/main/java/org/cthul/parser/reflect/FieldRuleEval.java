package org.cthul.parser.reflect;

import java.lang.reflect.Field;
import org.cthul.parser.api.Context;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;

public abstract class FieldRuleEval implements RuleEval {
    
    public static FieldRuleEval create(Object impl, Field field) {
        FieldValue v = FieldValue.create(impl, field);
        if (RuleEval.class.isAssignableFrom(field.getType())) {
            return new Proxy(v);
        } else {
            return new Value(v);
        }
    }
    
    protected final FieldValue field;

    public FieldRuleEval(FieldValue field) {
        this.field = field;
    }
    
    public abstract boolean isSimpleValue();

    public static class Value extends FieldRuleEval {

        public Value(FieldValue field) {
            super(field);
        }

        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            return field.get(context);
        }

        @Override
        public boolean isSimpleValue() {
            return true;
        }
    }
    
    public static class Proxy extends FieldRuleEval {

        public Proxy(FieldValue field) {
            super(field);
        }

        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            RuleEval re = (RuleEval) field.get(context);
            return re.eval(context, match, arg);
        }

        @Override
        public boolean isSimpleValue() {
            return false;
        }
    }
    
}
