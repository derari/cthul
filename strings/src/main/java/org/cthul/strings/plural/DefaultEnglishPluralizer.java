package org.cthul.strings.plural;

import java.util.Locale;

/**
 *
 * @author Arian Treffer
 */
public class DefaultEnglishPluralizer extends RegexPluralizer {
    
    /* Rules taken from Ruby on Rails, 
     *   activesupport / lib / active_support / inflections.rb 
     * 
     * which was released under the MIT license:
     *   http://opensource.org/licenses/MIT
     * 
     * Copyright (c) 2012
     * 
     * Permission is hereby granted, free of charge, to any person obtaining a 
     * copy of this software and associated documentation files 
     * (the "Software"), to deal in the Software without restriction, including 
     * without limitation the rights to use, copy, modify, merge, publish, 
     * distribute, sublicense, and/or sell copies of the Software, and to permit
     * persons to whom the Software is furnished to do so, subject to the 
     * following conditions:
     *  
     * The above copyright notice and this permission notice shall be included 
     * in all copies or substantial portions of the Software.
     * 
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
     * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
     * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
     * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
     * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
     * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
     * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
     */

    @Override
    protected Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    protected void initUncountables() {
        uncountable(new String[]{
                "equipment",
                "fish",
                "information",
                "jeans",
                "money",
                "news",
                "police",
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
                "sex",      "sexes",
                "woman",    "women",
                "zombie",   "zombies",
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
                "(o)es$",                       "$1",
                "(shoe)s",                      "$1",
        });
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
                "$",                            "",
        });
    }

}
