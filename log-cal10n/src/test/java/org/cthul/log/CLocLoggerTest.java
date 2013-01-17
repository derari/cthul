package org.cthul.log;

import java.util.Locale;
import org.junit.Test;

/**
 *
 * @author Arian Treffer
 */
public class CLocLoggerTest {
    
    @Test
    public void testMessage() {
        CLocLogger<Enum<?>> log = CLocLogConfiguration.getDefault().forLocale(Locale.UK).getClassLogger();
        log.error(TestMessage.HELLO_WORLD, "everyone");
    }
}
