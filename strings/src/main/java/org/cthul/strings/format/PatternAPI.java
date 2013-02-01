package org.cthul.strings.format;

/**
 * Used by {@link ConversionPattern}s to generate a regular expression.
 * @author Arian Treffer
 */
public interface PatternAPI extends Appendable {
    
    @Override
    PatternAPI append(char c);
    
    @Override
    PatternAPI append(CharSequence csq);
    
    @Override
    PatternAPI append(CharSequence csq, int start, int end);
    
    /**
     * To be called when a capturing group was added.
     */
    PatternAPI addedCapturingGroup();
    
    /**
     * To be called when capturing groups were added.
     * @param i number of capturing groups
     */
    PatternAPI addedCapturingGroups(int i);
    
    /**
     * Stores a value the will be provided for parsing the match.
     * 
     * @param newMemento
     * @return last value
     */
    Object putMemento(Object newMemento);
    
    PatternData parse(PatternAPI api, String format);
    
    PatternData parse(PatternAPI api, String format, int start, int end);
    
}
