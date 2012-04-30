package org.cthul.parser.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.parser.Context;
import org.cthul.parser.Lexer;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.Token;
import org.cthul.parser.TokenBuilder;
import org.cthul.parser.TokenFactory;
import org.cthul.parser.TokenStream;

/**
 *
 * @author Arian Treffer
 */
public class RegexLexer implements Lexer {
    
    public static TokenFactory<?, ?> DefaultTokenFactory = StringToken.FACTORY;
    
    public static LexerFactory FACTORY = new AbstractLexerFactory() {
        @Override
        public Lexer create(List<? extends Match> rules) {
            return new RegexLexer(rules);
        }
    };
    
    private final List<RegexMatch> rules;

    public RegexLexer(List<? extends Match> rules) {
        this.rules = new ArrayList<RegexMatch>(rules.size());
        for (Match m: rules) {
            this.rules.add(new RegexMatch(m));
        }
    }

    @Override
    public TokenStream scan(String input, Context context) throws NoMatchException {
        final RegexMatcherGroup groupProvider = new RegexMatcherGroup();
        final TokenStreamFactory streamFactory = new TokenStreamFactory(input, DefaultTokenFactory, context, groupProvider);
        final List<NoMatchException> errors = new ArrayList<NoMatchException>();
        while (streamFactory.prepareNext()) {
            final String remaining = streamFactory.getTokenBuilder().getRemainingString();
            streamFactory.pushToken(nextToken(streamFactory, remaining, context, errors, groupProvider));
        }
        return streamFactory.getTokenStream();
    }
    
    private Token<?> nextToken(TokenStreamFactory streamFactory, String remainingInput, 
                    Context context, List<NoMatchException> errors,
                    RegexMatcherGroup groupProvider) throws NoMatchException {
        errors.clear();
        for (RegexMatch m: rules) {
            try {
                final Matcher matcher = m.pattern.matcher(remainingInput);
                if (matcher.find() && matcher.start() == 0) {
                    groupProvider.setMatcher(matcher);
                    streamFactory.setUpNext(matcher.end());
                    Token<?> token = m.invoke(streamFactory.getTokenBuilder(), context);
                    if (token != null) return token;
                }
            } catch (NoMatchException e) {
                errors.add(e);
            }
        }
        throw NoMatchException.tokenMatchFailed(
                streamFactory.getTokenBuilder(), 
                streamFactory.getLastToken(), 
                errors);
    }
    
    private static final class RegexMatch implements Match {
        final Pattern pattern;
        final Match match;
        public RegexMatch(Match match) {
            this.match = match;
            this.pattern = Pattern.compile(match.getPattern());
        }
        @Override
        public String getPattern() {
            return match.getPattern();
        }
        @Override
        public Token<?> invoke(TokenBuilder tokenBuilder, Context context) throws NoMatchException {
            return match.invoke(tokenBuilder, context);
        }
    }
    
    private static final class RegexMatcherGroup implements TokenBuilder.GroupProvider {
        private Matcher matcher;
        public void setMatcher(Matcher matcher) {
            this.matcher = matcher;
        }
        @Override
        public String getGroup(int i) {
            return matcher.group(i);
        }
        @Override
        public int getCount() {
            return matcher.groupCount();
        }
    }
    
}
