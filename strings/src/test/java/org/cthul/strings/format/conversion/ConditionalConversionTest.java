package org.cthul.strings.format.conversion;

import java.util.Locale;
import org.cthul.proc.Proc;
import org.cthul.strings.Formatter;
import org.cthul.strings.format.FormatterAPIStub;
import org.junit.Test;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
public class ConditionalConversionTest {
    
    static final double NaN = Double.NaN;
    static final double Inf = Double.POSITIVE_INFINITY;
    
    private String format(Object value, String arg) {
        return FormatterAPIStub.format(ConditionalConversion.INSTANCE, value, Locale.ENGLISH, null, -1, -1, arg, 0);
    }
    
    private Proc formatCall(Object value, String arg) {
        return FormatterAPIStub.formatCall(ConditionalConversion.INSTANCE, value, Locale.ENGLISH, null, -1, -1, arg, 0);
    }
    
    @Test
    public void test_format() {
        assertThat(Formatter.Format("Total: %if0[no files]1[one file]<15[%<d files][many files] found", 12), 
                   is("Total: 12 files found"));
        assertThat(Formatter.Format("%d %<if=1[%s][%iP]", 12, "file"), 
                   is("12 files"));
    }
    
    // the following tests test only the argument string that is specified
    // *after* the format identifier '%if'
    
    @Test
    public void test_boolean_simple() {
        assertThat(format(true,  "[yes][no]"), is("yes"));
        assertThat(format(false, "[yes][no]"), is("no"));
    }
    
    @Test
    public void test_null_simple() {
        assertThat(format(null, "[yes][no]"), is("no"));
    }
    
    @Test
    public void test_integer_simple() {
        assertThat(format(   0, "[yes][no]"), is("no"));
        assertThat(format(   1, "[yes][no]"), is("yes"));
        assertThat(format(  -1, "[yes][no]"), is("yes"));
    }
    
    @Test
    public void test_float_simple() {
        assertThat(format( 0.0, "[yes][no]"), is("no"));
        assertThat(format( 0.1, "[yes][no]"), is("yes"));
        assertThat(format(-0.1, "[yes][no]"), is("yes"));
        assertThat(format( NaN, "[yes][no]"), is("no"));
    }
    
    @Test
    public void test_implicit_else() {
        assertThat(format(0, "[yes][no]"), is("no"));
        assertThat(format(0, "[yes]'no'"), is(""));
    }
    
    @Test
    public void test_explicit_else() {
        assertThat(format(0, "[yes]:[no]"), is("no"));
        assertThat(format(0, "[yes]:'no'"), is("no"));
    }
    
    @Test
    public void test_explicit_end() {
        assertThat(format(0, "[yes];[no]"), is(""));
    }
    
    @Test
    public void test_null_check() {
        assertThat(format( true,  "?[yes]"), is("yes"));
        assertThat(format(false,  "?[yes]"), is("yes"));
        assertThat(format(    0,  "?[yes]"), is("yes"));
        assertThat(format( null,  "?[yes]"), is(""));
        assertThat(format( null, "!?[yes]"), is("yes"));
    }
    
    @Test
    public void test_smart_check() {
        assertThat(format( true,  "??[yes]"), is("yes"));
        assertThat(format(    1,  "??[yes]"), is("yes"));
        assertThat(format( this,  "??[yes]"), is("yes"));
        assertThat(format(false,  "??[yes]"), is(""));
        assertThat(format(    0,  "??[yes]"), is(""));
        assertThat(format( null,  "??[yes]"), is(""));
    }
    
    @Test
    public void test_integer_match() {
        assertThat(format( 1,  "1[yes]"), is("yes"));
        assertThat(format( 1, "+1[yes]"), is("yes"));
        assertThat(format(-1, "-1[yes]"), is("yes"));
        assertThat(format( 1,  "0[yes]"), is(""));
    }
    
    @Test
    public void test_float_match() {
        assertThat(format( 1.0,        "1[yes]"), is("yes"));
        assertThat(format( 1.0,       "+1[yes]"), is("yes"));
        assertThat(format(-1.0,       "-1[yes]"), is("yes"));
        assertThat(format( 1.0,        "0[yes]"), is(""));
        assertThat(format( 1.0,      "1.0[yes]"), is("yes"));
        assertThat(format( NaN,      "NaN[yes]"), is("yes"));
        assertThat(format( Inf, "Infinity[yes]"), is("yes"));
        assertThat(format(-Inf,"-Infinity[yes]"), is("yes"));
    }
    
    @Test
    public void test_compare() {
        assertThat(format( 1,  "<1[yes]"), is(""));
        assertThat(format( 1,  "=1[yes]"), is("yes"));
        assertThat(format( 1, "<=1[yes]"), is("yes"));
        assertThat(format( 1, "=<1[yes]"), is("yes"));
        assertThat(format( 1, "!<1[yes]"), is("yes"));
        assertThat(format( 1, "!=1[yes]"), is(""));
    }
    
    @Test
    public void test_null_compare() {
        assertThat(formatCall(null,  "<1[yes]"), raises(NullPointerException.class));
        assertThat(formatCall(null, "?<1[yes]"), returns(""));
    }
    
    @Test
    public void test_junction() {
        assertThat(format(-1,  "<0|>0[yes]"), is("yes"));
        assertThat(format( 0,  "<0|>0[yes]"), is(""));
        assertThat(format( 1,  "<0|>0[yes]"), is("yes"));
    }
    
    @Test
    public void test_conjunction() {
        assertThat(format( 0,  ">0&<5[yes]"), is(""));
        assertThat(format( 1,  ">0&<5[yes]"), is("yes"));
        assertThat(format( 6,  ">0&<5[yes]"), is(""));
    }
    
    @Test
    public void test_complex() {
        assertThat(format(1, "<5&(=1|>2)''yes''"), is("yes"));
    }
    
    @Test
    public void test_chain() {
        assertThat(format( 0, "0[zero]1[one]>0[positive]"), is("zero"));
        assertThat(format( 1, "0[zero]1[one]>0[positive]"), is("one"));
        assertThat(format( 2, "0[zero]1[one]>0[positive]"), is("positive"));
        assertThat(format(-1, "0[zero]1[one]>0[positive]"), is(""));
    }
    
    @Test
    public void test_delimiter() {
        assertThat(format(1, "0[No]1{Yes}"),         is("Yes"));
        assertThat(format(1, "0<No>1(Yes)"),         is("Yes"));
        assertThat(format(1, "0'No'1\"Yes\""),       is("Yes"));
        assertThat(format(1, "(0)xxNoxx(1)eeYesee"), is("Yes"));
    }
    
    @Test
    public void test_invalid_formats() {
        // ';' after "zero" or brackets around "one"
        assertThat(formatCall(1, "0(zero)1one"), raises(IllegalArgumentException.class));
        // second closing bracket required
        assertThat(formatCall(1, "0((zero)"), raises(IllegalArgumentException.class));
        // value required
        assertThat(formatCall(1, "(0)"), raises(IllegalArgumentException.class));
    }
    
}
