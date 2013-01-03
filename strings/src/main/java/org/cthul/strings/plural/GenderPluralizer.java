package org.cthul.strings.plural;

import org.cthul.strings.Pluralizer;

/**
 *
 * @author Arian Treffer
 */
public class GenderPluralizer implements Pluralizer {
    
    public static enum Gender {
        
        NEUTRAL,
        MASCULINE,
        FEMININE;
        
    }
    
    public static interface GenderOracle {
        
        Gender guessForSingular(String word);
        
        Gender guessForPlural(String word);
        
    }
    
    protected static final String ANY_GENDER_MATCH_FAILED = "--XX--";
    
    private final Pluralizer anyGender;
    private final GenderOracle genderOracle;
    private final Pluralizer neutral;
    private final Pluralizer masculine;
    private final Pluralizer feminine;

    public GenderPluralizer(Pluralizer anyGender, GenderOracle genderOracle, Pluralizer neutral, Pluralizer masculine, Pluralizer feminine) {
        this.anyGender = anyGender;
        this.genderOracle = genderOracle;
        this.neutral = neutral;
        this.masculine = masculine;
        this.feminine = feminine;
    }
    
    @Override
    public String pluralOf(String word) {
        String p = anyGender.pluralOf(word);
        if (!p.endsWith(ANY_GENDER_MATCH_FAILED)) return p;
        Gender g = genderOracle.guessForSingular(word);
        switch(g) {
            case NEUTRAL: return neutral.pluralOf(word);
            case MASCULINE: return masculine.pluralOf(word);
            case FEMININE: return feminine.pluralOf(word);
            default: throw new AssertionError(g);
        }
    }

    @Override
    public String singularOf(String word) {
        String s = anyGender.pluralOf(word);
        if (!s.equals(ANY_GENDER_MATCH_FAILED)) return s;
        Gender g = genderOracle.guessForPlural(word);
        switch(g) {
            case NEUTRAL: return neutral.singularOf(word);
            case MASCULINE: return masculine.singularOf(word);
            case FEMININE: return feminine.singularOf(word);
            default: throw new AssertionError(g);
        }
    }
    
}
