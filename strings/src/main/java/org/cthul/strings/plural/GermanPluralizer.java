package org.cthul.strings.plural;

import org.cthul.strings.Pluralizer;

/**
 *
 * @author Arian Treffer
 */
public class GermanPluralizer extends GenderedPluralizer {

    public GermanPluralizer(Pluralizer anyGender, GenderOracle genderOracle, Pluralizer neutral, Pluralizer masculine, Pluralizer feminine) {
        super(anyGender, genderOracle, neutral, masculine, feminine);
    }
    
    public GermanPluralizer() {
        this(new DefaultAnyGenderPluralizer(),
                new DefaultGenderOracle(),
                new DefaultNeutralPluralizer(), 
                new DefaultMasculinePluralizer(),
                new DefaultFemininePluralizer());
    }
    
    protected static class DefaultAnyGenderPluralizer extends RegexPluralizer {

        @Override
        protected void initUncountables() {
            super.initUncountables();
        }

        @Override
        protected void initIrregulars() {
            irregular(new String[]{
                "datum", "daten",
                "ei", "eier",
            });
        }
        
        @Override
        protected void initRules() {
            
            // always feminine
            plural(new String[]{
                "(ei|heit|keit|schaft|ung)$", "$1en",
                "in$", "$1nen",
            });
            singular(new String[]{
                "(ei|heit|keit|schaft|ung)en$", "$1",
                "(in)nen$", "$1",
            });            
        }

        @Override
        protected String noMatch(String word) {
            return ANY_GENDER_MATCH_FAILED;
        }
        
    }
    
    protected static class DefaultGenderOracle implements GenderOracle {

        @Override
        public Gender guessForSingular(String word) {
            return Gender.NEUTRAL;
        }

        @Override
        public Gender guessForPlural(String word) {
            return Gender.NEUTRAL;
        }
        
    }

    
    protected static String[] NeutMascPlurals() {
        return new String[]{
            "(er|en|el|chen|lein)$",    "$1",
        };
    }
    
    protected static String[] NeutMascSingulars() {
        return new String[]{
            "(er|en|el|chen|lein)$",    "$1",
        };
    }
    
    protected static class DefaultNeutralPluralizer extends RegexPluralizer {

        @Override
        protected void initRules() {
            plural("^ge.*", "$0");
            plural(NeutMascPlurals());
            
            singular("^ge.*", "$0");
            singular(NeutMascSingulars());
        }
        
    }
    
    protected static class DefaultMasculinePluralizer extends RegexPluralizer {

        @Override
        protected void initRules() {
            plural(NeutMascPlurals());
            
            singular(NeutMascSingulars());
        }
        
    }
    
    protected static class DefaultFemininePluralizer extends RegexPluralizer {
        
    }
    
}
