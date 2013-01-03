package org.cthul.strings.format;

/**
 *
 * @author Arian Treffer
 */
public interface PatternAPI extends Appendable {
    
    @Override
    PatternAPI append(char c);
    
    @Override
    PatternAPI append(CharSequence csq);
    
    @Override
    PatternAPI append(CharSequence csq, int start, int end);
    
    PatternAPI addedCapturingGroup();
    
    PatternAPI addedCapturingGroups(int i);
    
    Object putMemento(Object newMemento);
    
    PatternData parse(PatternAPI api, String format);
    
    PatternData parse(PatternAPI api, String format, int start, int end);
    
}
