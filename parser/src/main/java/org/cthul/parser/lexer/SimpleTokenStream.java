package org.cthul.parser.lexer;

import java.util.ArrayList;
import java.util.List;
import org.cthul.parser.TokenChannel;
import org.cthul.parser.Token;
import org.cthul.parser.TokenStream;

/**
 *
 * @author Arian Treffer
 */
public class SimpleTokenStream implements TokenStream {
    
    private final String input;
    
    private final List<Token<?>> tokens = new ArrayList<>();

    private int current = -1;

    public SimpleTokenStream(String input) {
        this.input = input;
    }
    
    @Override
    public void start() {
        current = -1;
    }
    
    public void end() {
        current = tokens.size();
    }

    @Override
    public <V> Token<V> first() {
        start();
        return next();
    }

    @SuppressWarnings("unchecked")
    private <V> Token<V> fetch(final int i) {
        current += i;
        if (current < 0 || current >= tokens.size()) {
            return null;
        }
        return (Token<V>) tokens.get(current);
    }
    
    private <V> Token<V> fetch(final int i, final int channel) {
        if (channel < 0) return fetch(i);
        Token<V> t = fetch(i);
        while (t != null && t.getChannel() != channel) {
            t = fetch(i);
        }
        return t;
    }

    @Override
    public <V> Token<V> next() {
        return next(TokenChannel.Default);
    }

    @Override
    public <V> Token<V> next(int channel) {
        return fetch(1, channel);
    }

    @Override
    public <V> Token<V> current() {
        return (Token) tokens.get(current);
    }

    @Override
    public <V> Token<V> previous() {
        return previous(TokenChannel.Default);
    }

    @Override
    public <V> Token<V> previous(int channel) {
        return fetch(-1, channel);
    }

    @Override
    public <V> Token<V> last() {
        end();
        return previous();
    }
    
    public void add(Token<?> token) {
        tokens.add(token);
    }

    public int nextFreeId() {
        return tokens.size();
    }

    @Override
    public int getLength() {
        int len = 0;
        for (Token t: tokens) {
            if (t.getChannel() == TokenChannel.Default) len++;
        }
        return len;
    }

    @Override
    public String getString() {
        return input;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(current).append(":");
        int c = 0;
        int i = current;
        while (c < 6 && i < tokens.size()) {
            Token<?> t = tokens.get(i);
            if (t.getChannel() == TokenChannel.Default) {
                sb.append(' ');
                if (c < 5) {
                    t.toString(sb);
                } else {
                    sb.append("...");
                }
                c++;
            }
            i++;
        }
        return sb.toString();
    }
    
}
