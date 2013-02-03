package org.cthul.strings.format;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import static org.mockito.Mockito.*;

/**
 *
 * @author Arian Treffer
 */
public class FormatStringParserTest {
    
    public FormatStringParserTest() {
    }
    
    @Spy TestFormatStringParser parser;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_appendNewLine() {
        parser.parse("%n");
        verify(parser).appendNewLine();
    }
    
    @Test
    public void test_appendNewLine_2() {
        parser.parse("  %n  ");
        verify(parser).appendNewLine();
    }
    
    @Test
    public void test_appendPercent() {
        parser.parse("%%");
        verify(parser).appendPercent();
    }
    
    @Test
    public void test_appendPercent_2() {
        parser.parse("  %%  ");
        verify(parser).appendPercent();
    }
    
    @Test
    public void test_appendText() {
        parser.parse("asdasd");
        verify(parser).appendText("asdasd", 0, 6);
    }
    
    @Test
    public void test_appendText_2() {
        parser.parse("%%foo%nbar");
        verify(parser, times(2)).appendText(anyString(), anyInt(), anyInt());
        verify(parser).appendText("%%foo%nbar", 2, 5);
        verify(parser).appendText("%%foo%nbar", 7, 10);
    }
    
    @Test
    public void test_customShortFormat() {
        parser.parse("%iX");
        verify(parser).customShortFormat('X', 1, null, -1, -1, "%iX", 3, false);
    }
    
    @Test
    public void test_customShortFormat_2() {
        parser.parse("  %Iy  ");
        verify(parser).customShortFormat('y', 1, null, -1, -1, "  %Iy  ", 5, true);
    }
    
    @Test
    public void test_customLongFormat() {
        parser.parse("%jFoo");
        verify(parser).customLongFormat("Foo", 1, null, -1, -1, "%jFoo", 5, false);
    }
        
    @Test
    public void test_customLongFormat_2() {
        parser.parse("  %Jbar  ");
        verify(parser).customLongFormat("bar", 1, null, -1, -1, "  %Jbar  ", 7, true);
    }
    
    @Test
    public void test_argId() {
        parser.parse("%a %<b %>c %d");
        verify(parser).standardFormat("a", 1, null, -1, -1);
        verify(parser).standardFormat("b", 1, null, -1, -1);
        verify(parser).standardFormat("c", 2, null, -1, -1);
        verify(parser).standardFormat("d", 2, null, -1, -1);
    }
    
    @Test
    public void test_argId_alt() {
        parser.parse("%a %`b %´c %d");
        verify(parser).standardFormat("a", 1, null, -1, -1);
        verify(parser).standardFormat("b", 1, null, -1, -1);
        verify(parser).standardFormat("c", 2, null, -1, -1);
        verify(parser).standardFormat("d", 2, null, -1, -1);
    }
    
    @Test
    public void test_argId_dollar() {
        parser.parse("%$a %`$b %>$c");
        verify(parser).standardFormat("a", 1, null, -1, -1);
        verify(parser).standardFormat("b", 1, null, -1, -1);
        verify(parser).standardFormat("c", 2, null, -1, -1);
    }
    
    @Test
    public void test_argId_explicit() {
        parser.parse("%1$a %.$b %c$c %?named$d");
        verify(parser).standardFormat("a", 1, null, -1, -1);
        verify(parser).standardFormat("b", 1, null, -1, -1);
        verify(parser).getArg('c');
        verify(parser).standardFormat("c", 'c', null, -1, -1);
        verify(parser).getArg("named");
        verify(parser).standardFormat("d", "named", null, -1, -1);
    }
        
    @Test
    public void test_argId_explicit2() {
        parser.parse("%?named$d %<<d %<<$d");
        verify(parser).getArg("named");
        verify(parser, times(3)).standardFormat("d", "named", null, -1, -1);
    }
        
    @Test
    public void test_flags() {
        parser.parse("% =!a %$$b %1 c %1.1$d");
        verify(parser).standardFormat("a", 1, " =!", -1, -1);
        verify(parser).standardFormat("b", 2, "$", -1, -1);
        verify(parser).standardFormat("c", 3, "1 ", -1, -1);
        verify(parser).standardFormat("d", 4, "1.1$", -1, -1);
    }
    
    @Test
    public void test_width_precision() {
        parser.parse("%1a %.2b %1.2c %$1$2d");
        verify(parser).standardFormat("a", 1, null, 1, -1);
        verify(parser).standardFormat("b", 2, null, -1, 2);
        verify(parser).standardFormat("c", 3, null, 1, 2);
        verify(parser).standardFormat("d", 4, "1$", 2, -1);
    }
    
    public static class TestFormatStringParser extends FormatStringParser<RuntimeException> {

        @Override
        protected void appendText(CharSequence csq, int start, int end) throws RuntimeException {
        }

        @Override
        protected void appendPercent() throws RuntimeException {
        }

        @Override
        protected void appendNewLine() throws RuntimeException {
        }

        @Override
        protected int customShortFormat(char formatId, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws RuntimeException {
            return 0;
        }

        @Override
        protected int customLongFormat(String formatId, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws RuntimeException {
            return 0;
        }

        @Override
        protected void standardFormat(String formatId, Object arg, String flags, int width, int precision) throws RuntimeException {
        }

        @Override
        protected Object getArg(int i) {
            return i;
        }

        @Override
        protected Object getArg(char c) {
            return c;
        }

        @Override
        protected Object getArg(String s) {
            return s;
        }
        
    }
    
}
