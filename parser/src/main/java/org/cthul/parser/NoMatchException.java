package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public class NoMatchException extends RuntimeException {
    
    private static final long serialVersionUID = -3380906851858683723L;
    
//    protected final List<NoMatchException> causes = new ArrayList<>(1);
    protected final String input;
    protected final int position;
    private String fullMessage = null;
    private Location location;

    public NoMatchException(String input, int position, String message, Throwable cause) {
        super(message, cause);
        this.input = input;
        this.position = position;
    }
    
//    public NoMatchException(String input, int position, String message, List<? extends NoMatchException> causes) {
//        this(input, position, message, causes == null || causes.isEmpty() ? null : causes.get(0));
//        this.causes.addAll(causes);
//    }

//    public List<NoMatchException> getCauses() {
//        return Collections.unmodifiableList(causes);
//    }

    public String getInput() {
        return input;
    }

    public int getPosition() {
        return position;
    }

    public Location getLocation() {
        if (location == null) {
            location = loc(input, position);
        }
        return location;
    }

    @Override
    public String getMessage() {
        if (fullMessage == null) {
            fullMessage = generateFullMessage();
        }
        return fullMessage;
    }
    
    public String getShortMessage() {
        return super.getMessage();
    }

    protected String generateFullMessage() {
        StringBuilder sb = new StringBuilder();
        msgHeader(sb);
        msgLocation(sb);
//        msgCauses(sb);
        return sb.toString();
    }

    protected void msgHeader(StringBuilder sb) {
        String msg = getShortMessage();
        if (msg == null) msg = getClass().getSimpleName();
        sb.append(msg);
    }
    
    private static final int MAX_LEN = 40;
    private static final String INDENT = "                                        ";
    private static final String NL = "\n  ";
    static { assert INDENT.length() >= MAX_LEN : "indent"; }

    protected void msgLocation(StringBuilder sb) {
        Location loc = getLocation();
        if (loc == null) return;
        sb.append("\nAt ").append(loc);
        
        int lenBefore = loc.getColumn();
        int lenAfter = loc.getLineEnd() - loc.getPosition();
        int maxBefore, maxAfter;
        if (lenBefore + lenAfter > MAX_LEN) {
            if (lenBefore < MAX_LEN/2) {
                maxBefore = lenBefore;
                maxAfter = MAX_LEN - maxBefore;
            } else if (lenAfter < MAX_LEN/2) {
                maxAfter = lenAfter;
                maxBefore = MAX_LEN - maxAfter;
            } else {
                maxBefore = maxAfter = MAX_LEN/2;
            }
        } else {
            maxBefore = maxAfter = MAX_LEN;
        }
        
        if (lenBefore > maxBefore) {
            lenBefore = maxBefore;
            int startBefore = loc.getPosition()-maxBefore+3;
            sb.append(NL).append("...")
                .append(input, startBefore, loc.getPosition());
        } else if (lenBefore > 0) {
            sb.append(NL)
                .append(input, loc.getLineStart(), loc.getPosition());
        }
        if (lenAfter > 0) {
            if (lenBefore == 0) {
                sb.append(NL);
            } else {
                sb.append("|").append(NL)
                    .append(INDENT, 0, lenBefore)
                    .append("|");
            }
            if (lenAfter > maxAfter) {
                sb.append(input, loc.getPosition(), loc.getPosition()+maxAfter-3)
                    .append("...");
            } else {
                sb.append(input, loc.getPosition(), loc.getLineEnd());
            }
        }
    }
    
    protected String str(Object o) {
        return String.valueOf(o)
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

//    private void msgCauses(StringBuilder sb) {
//        boolean first = true;
//        for (NoMatchException ex: causes) {
//            if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
//                if (first) {
//                    first = false;
//                    sb.append("\nGot these exceptions:\n");
//                }
//                sb.append("> ");
//                sb.append(ex.getMessage().replaceAll("\n", "\n  "));
//            }
//        }
//    }

    protected static Location loc(String s, int pos) {
        int line = 1;
        int lineStart = 0;
        int nl = s.indexOf('\n');
        while (nl < pos && nl > -1) {
            nl++;
            if (s.charAt(nl) == '\r') nl++;
            lineStart = nl;
            nl = s.indexOf('\n', nl);
        }
        if (lineStart > pos) {
            assert s.charAt(pos) == '\r';
            lineStart = pos;
        }
        int col = pos-lineStart;
        int lineEnd = nl < 0 ? s.length() : nl;
        if (lineEnd > 1 && s.charAt(lineEnd-1) == '\r') lineEnd--;
        return new Location(line, col, pos, lineEnd);
    }
    
    public static class Location {
        
        private final int line, col, pos, lineEnd;

        public Location(int line, int col, int pos, int lineEnd) {
            this.line = line;
            this.col = col;
            this.pos = pos;
            this.lineEnd = lineEnd;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return col;
        }

        public int getPosition() {
            return pos;
        }
        
        public int getLineStart() {
            return pos-col;
        }

        public int getLineEnd() {
            return lineEnd;
        }

        @Override
        public String toString() {
            return line + ":" + col;
        }
        
    }
    
}
