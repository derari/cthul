package org.cthul.parser.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.cthul.parser.TokenStream;

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

	public static final int Ignore = org.cthul.parser.TokenChannel.Ignore;

	public static final int Meta = org.cthul.parser.TokenChannel.Meta;

	public static final int Comment = org.cthul.parser.TokenChannel.Comment;

	public static final int Whitespace = org.cthul.parser.TokenChannel.Whitespace;

	public static final int Default = org.cthul.parser.TokenChannel.Default;

    public static final int Undefined = org.cthul.parser.TokenChannel.Undefined;
    
	public int value();

}
