package org.cthul.parser.grammar;

import org.cthul.parser.Context;
import org.cthul.parser.Grammar;
import org.cthul.parser.Lexer;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.TokenStream;
import org.cthul.parser.annotation.AnnotationScanner;

/**
 *
 * @author Arian Treffer
 */
public class Profile {
    
    public static void main(String[] args) throws NoMatchException {
        warmUp();
        measure();
    }
    
    public static void measure() throws NoMatchException {
        doIt();
    }
    
    public static void doIt() throws NoMatchException {
        final String input = "{a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a}";
        Class impl = ListGrammar.class;
        
        AnnotationScanner as = new AnnotationScanner();
        as.add(impl);

        final Lexer lexer = as.createLexer();
        final Grammar g = as.createGrammar();
        final Context context = new Context();
        final TokenStream ts = lexer.scan(input, context);

        long t = -System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            g.parse("count", ts, context);
        }
        t += System.currentTimeMillis();
        System.out.println(impl + " " + t + "ms");
    }
    
    public static void warmUp() throws NoMatchException {
        for (int j = 0; j < 5; j ++) {
            doIt();
        }
    }
    
}
