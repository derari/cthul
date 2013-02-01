package org.cthul.parser.token;

import java.util.*;
import org.cthul.parser.api.TokenInput;

public class TokenStream<T extends Token<?>> extends TokenInput<T> {

    protected final List<T> all = new ArrayList<>();
    protected final List<T> publicAll = Collections.unmodifiableList(all);
    
    protected static <T extends Token<?>> List<T> filterChannel(Collection<? extends T> tokens) {
        final List<T> result = new ArrayList<>();
        for (T t: tokens) {
            if (t.getChannel() == TokenChannel.Default)
                result.add(t);
        }
        return result;
    }
    
    public TokenStream(String input, T[] tokens) {
        this(input, Arrays.asList(tokens));
    }

    public TokenStream(String input, Collection<? extends T> tokens) {
        super(input, filterChannel(tokens));
    }

    public List<T> getAll() {
        return publicAll;
    }
    
}
