package org.cthul.parser.token;

/**
 *
 * @author Arian Treffer
 */
public interface TokenChannel {

    public static final int Ignore = 4;
    public static final int Meta = 3;
    public static final int Comment = 2;
    public static final int Whitespace = 1;
    public static final int Default = 0;
    public static final int Undefined = -1;
    public static final int Any = -1;

}
