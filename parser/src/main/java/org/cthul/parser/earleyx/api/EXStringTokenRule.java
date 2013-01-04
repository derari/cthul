package org.cthul.parser.earleyx.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.util.Format;

public class EXStringTokenRule<C extends Context<? extends StringInput>> extends EXTokenRule<C> {
    
    private final String[] strings;

    public EXStringTokenRule(String symbol, int priority, String... strings) {
        super(symbol, priority);
        this.strings = strings;
    }

    @Override
    public EXMatch match(C context, int start, int end, EXMatch[] right) {
        StringInput input = context.getInput();
        if (start >= input.getLength()) return null;
        String str = ((StringInput) input).getString();
        for (String s: strings) {
            if (str.startsWith(s, start)) {
                return token(context, start, start + s.length());
            }
        }
        return null;
    }

    @Override
    protected String tokenString() {
        if (strings.length > 0 && strings[0].length() > 28) {
            return "\"" + strings[0].substring(0, 28) + "...\"";
        }
        return Format.join("\"", "\", \"", "\"", 32, "...", (Object[]) strings);
    }
    
}
