package org.cthul.parser.annotation;

import java.lang.annotation.*;
import org.cthul.parser.token.TokenChannel;
import org.cthul.parser.token.TokenStream;

/**
 * Each token belongs to a channel. For parsing the grammar, only the tokens
 * in the {@link TokenChannel#Default default} channel will be used. However, other 
 * tokens also may contain relevant information (like comments). 
 * They can be accessed using the {@link TokenStream}.
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Channel {

    public static final int Ignore = TokenChannel.Ignore;

    public static final int Meta = TokenChannel.Meta;

    public static final int Comment = TokenChannel.Comment;

    public static final int Whitespace = TokenChannel.Whitespace;

    public static final int Default = TokenChannel.Default;

    public static final int Undefined = TokenChannel.Undefined;
    
    public int value();

}
