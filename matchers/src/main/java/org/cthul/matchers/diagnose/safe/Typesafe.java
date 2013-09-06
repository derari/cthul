package org.cthul.matchers.diagnose.safe;

import org.hamcrest.Description;

/**
 *
 */
public class Typesafe {

    public static boolean matches(Object item, Class<?> expectedType) {
        return expectedType.isInstance(item);
    }

    public static boolean matches(Object item, Class<?> expectedType, Description description) {
        if (item == null) {
            description.appendText("was null");
            return false;
        } else if (! expectedType.isInstance(item)) {
            description.appendText("was a ")
                       .appendText(item.getClass().getName())
                       .appendText(" (")
                       .appendValue(item)
                       .appendText(")");
            return false;
        } else {
            return true;
        }
    }
}
