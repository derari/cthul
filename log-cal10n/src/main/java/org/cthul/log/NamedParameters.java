package org.cthul.log;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Arian Treffer
 */
public interface NamedParameters {
    
    List<String> getNames();
    
    static class ParamUtils {
        
        public static List<String> wrap(String[] names) {
            return Arrays.asList(names);
        }
        
    }
    
}
