package org.cthul.parser.earleyx;

import org.cthul.parser.api.MatchEval;
import org.cthul.parser.api.XMatch;
import org.cthul.parser.earleyx.api.EXMatch;
import org.cthul.parser.util.Format;

public class TestMatchEval implements MatchEval {
    
    public static final TestMatchEval INSTANCE = new TestMatchEval();

    @Override
    public Object eval(XMatch match) {
        EXMatch[] args = (EXMatch[]) match.getArgs();
        if (args == null) return match.getSymbol() + "()";
        Object[] values = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            values[i] = args[i].eval();
        }
        return Format.join(match.getSymbol()+"(", ",", ")", values);
    }
    
}
