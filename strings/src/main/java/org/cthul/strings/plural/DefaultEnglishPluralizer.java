package org.cthul.strings.plural;

/**
 *
 * @author Arian Treffer
 */
public class DefaultEnglishPluralizer extends RegexPluralizer {

    @Override
    protected void initUncountables() {
        uncountable(new String[]{
                "equipment",
                "fish",
                "information",
                "money",
                "news",
                "rice",
                "series",
                "sheep",
                "species",
        });
    }

    @Override
    protected void initIrregulars() {
        irregular(new String[]{
                "child",    "children",
                "man",      "men",
                "person",   "people",
                "woman",    "women",
        });

        plural(new String[]{
                "(quiz)$",                      "$1zes",
                "^(ox)$",                       "$1en",
                "([m|l])ouse$",                 "$1ice",
                "(matr|vert|ind)(?:ix|ex)$",    "$1ices",
                "(?:([^f])fe|([lr])f)$",        "$1ves",
                "sis$",                         "ses",
                "([ti])um$",                    "$1a",
                "(buffal|tomat)o$",             "$1oes",
                "(alias|status|bus)$",          "$1es",
                "(octop|vir)us$",               "$1i",
                "(cris|ax|test)is$",            "$1es",
        });

        singular(new String[]{
                "(quiz)zes$",                   "$1",
                "^(ox)en$",                     "$1",
                "([m|l])ice$",                  "$1ouse",
                "(vert|ind)ices$",              "$1ex",
                "(matr)ices$",                  "$1ix",
                "(movie)s$",                    "$1",
                "([^f])ves$",                   "$1fe",
                "(analy|ba|diagno|paranthe|progno|synop|the)ses$",
                        "$1sis",
                "([ti])a$",                     "$1um",
                "(alias|status|bus)es$",        "$1",
                "(octop|vir)i$",                "$1us",
                "(cris|ax|test)es$",            "$1is",
        });

        /*
            singular(/(o)es$/i, '\1')
            singular(/(shoe)s$/i, '\1')
         */
    }

    @Override
    protected void initRules() {
        plural(new String[]{
                "(x|ch|ss|sh)$",                "$1es",
                "([^aeiouy]|qu)y$",             "$1ies",
                "(s)$",                         "$1",
                "$",                            "s",
        });

        singular(new String[]{
                "(x|ch|ss|sh)es$",              "$1",
                "([^aeiouy]|qu)ies$",           "$1y",
                "s$",                           "",
        });
    }

}
