package org.cthul.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Arian Treffer
 */
public class NoMatchException extends RuntimeException {
    
    private static final long serialVersionUID = -3380906851858683723L;
    
    private final List<NoMatchException> causes = new ArrayList<>(1);

    /**
     * Creates a new instance of <code>NoMatchException</code> without detail message.
     */
    public NoMatchException() {
    }

    /**
     * Constructs an instance of <code>NoMatchException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoMatchException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>NoMatchException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoMatchException(String msg, Object... args) {
        super(String.format(msg, args));
    }

    public NoMatchException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NoMatchException(String message, List<NoMatchException> causes) {
        super(message);
        if (causes != null) {
            this.causes.addAll(causes);
        }
    }

    public List<NoMatchException> getCauses() {
        return Collections.unmodifiableList(causes);
    }
    
    public static NoMatchException tokenMatchFailed(TokenBuilder tb, Token<?> lastToken, List<NoMatchException> errors) {
        final StringBuilder sb = new StringBuilder();
        buildMsg(sb, tb.getString(), tb.getStart(), "input", lastToken);
        errorReport(sb, errors);
        return new NoMatchException(sb.toString(), errors);
    }
    
    public static NoMatchException productionFailed(TokenStream tokenStream, Token<?> token, List<NoMatchException> errors, List<String> expected) {
        final StringBuilder sb = new StringBuilder();
        Token<?> lastToken = tokenStream.previous();
        buildMsg(sb, tokenStream.getString(), token.getStart(), token.toString(), lastToken);
        errorReport(sb, errors);
        expectedReport(sb, expected);
        return new NoMatchException(sb.toString(), errors);
    }
    
    public static NoMatchException evaluationFailed(List<NoMatchException> errors) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Evaluation of grammar failed");
        errorReport(sb, errors);
        return new NoMatchException(sb.toString(), errors);
    }
    
    private static void buildMsg(final StringBuilder sb, String source, int position, String input, Object lastToken) {
        sb.append("Could not match ")
                .append(input)
                .append(" at ")
                .append(lineAndCol(source, position))
                .append("\n");

        position = Math.min(position, source.length());
        String start = lineStart(source, position);
        String end = lineEnd(source, position);
        if (!start.isEmpty() || !end.isEmpty()) {
            sb.append(start);
            sb.append("==HERE=>");
            sb.append(end);
        }
        if (lastToken != null)
            sb.append("\nPrevious token was ").append(lastToken);
    }

    private static String lineStart(String s, int pos) {
        int start = Math.max(0, pos - 11);
        if (pos <= start) return "";
        s = s.substring(start, pos);
        int i = s.indexOf("\n");
        if (i > -1) s = (i+1 < s.length()) ? s.substring(i+1) : "";
        if (s.length() > 10) s = "..." + s.substring(3);
        return s;
    }

    private static String lineEnd(String s, int pos) {
        int end = Math.min(s.length(), pos + 21);
        if (end <= pos) return "";
        s = s.substring(pos, end);
        int i = s.indexOf("\n");
        if (i > -1) s = s.substring(0, i);
        if (s.length() > 20) s = s.substring(0, 18) + "...";
        return s;
    }

    private static String lineAndCol(String s, int pos) {
        int line = 1;
        int col = pos+1;
        int nl = -1;
        while ((nl = s.indexOf('\n', nl+1)) < pos && nl > -1) {
            col = pos - nl;
            line++;
        }
        return "line " + line + ":" + col;
    }

    private static void errorReport(final StringBuilder sb, List<NoMatchException> errors) {
        if (errors == null) return;
        boolean first = true;
        for (NoMatchException ex: errors) {
            if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
                if (first) {
                    first = false;
                    sb.append("\nGot these exceptions:\n");
                }
                sb.append("> ");
                sb.append(ex.getMessage().replaceAll("\n", "\n  "));
            }
        }
    }
    
    private static void expectedReport(final StringBuilder sb, List<String> expected) {
        if (expected == null) return;
        if (expected.isEmpty()) {
            sb.append("\nExpected <EOI>");
            return;
        }
        sb.append("\nExpected ");
        int len = expected.size();
        for (int i = 0; i < len; i++) {
            if (i > 0) sb.append(i+1 == len ? " or " : ", ");
            sb.append(expected.get(i));
        }
    }

}
