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
        sb.append(getShortMessage());
    }
    
    private static final String WS = "                    ";

    protected void msgLocation(StringBuilder sb) {
        Location loc = getLocation();
        if (loc == null) return;
        sb.append("\nAt line ").append(loc);
        if (loc.getColumn() > 20) {
            sb.append("\n...")
                .append(input, loc.getPosition()-17, loc.getPosition());
        } else if (loc.getColumn() > 0) {
            sb.append("\n")
                .append(input, loc.getLineStart(), loc.getPosition());
        }
        int remainder = loc.getLineEnd() - loc.getPosition();
        if (remainder > 0) {
            int indent = Math.min(20, loc.getColumn());
            sb.append("\n").append(WS, 0, indent);
            if (remainder > 20) {
                sb.append(input, loc.getPosition(), loc.getPosition()+17)
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
