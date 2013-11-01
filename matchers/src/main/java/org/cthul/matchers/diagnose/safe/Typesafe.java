package org.cthul.matchers.diagnose.safe;

import org.hamcrest.Description;

/**
 * Provides utility methods for type-safe matching.
 */
public class Typesafe {

    public static boolean matches(Object item, Class<?> expectedType) {
        return expectedType.isInstance(item);
    }

    public static boolean matches(Object item, Class<?> expectedType, Description mismatch) {
        if (item == null) {
            mismatch.appendText("was null");
            return false;
        } else if (! expectedType.isInstance(item)) {
            mismatch.appendText("was a ")
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
