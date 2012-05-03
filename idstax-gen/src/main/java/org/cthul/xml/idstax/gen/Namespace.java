/*
 * 
 */

package org.cthul.xml.idstax.gen;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author derari
 */
public class Namespace implements Comparable<Namespace> {

    public static String asAbbrev(String xmlns) {
        if (xmlns.startsWith("xmlns:")) xmlns = xmlns.substring(6);
        else if (xmlns.equals("xmlns")) xmlns = "";
        return xmlns;
    }

    public static String asXmlns(String abbrev) {
        if (abbrev.isEmpty()) return "xmlns";
        else if (abbrev.startsWith("xmlns:")) return abbrev;
        return "xmlns:"+abbrev;
    }

    public String abbrev;
    public String constant;
    public String uri;

    public SortedSet<ElementName> enames = new TreeSet<ElementName>();

    public Namespace(String abbrev, Set<String> constants, String uri) {
        this.abbrev = asAbbrev(abbrev);
        this.uri = uri;

        String c = JavaNameUtils.UNDERSCORE(asXmlns(abbrev));

        if (constants.add(c)) {
            this.constant = c;
        } else {
            int i = 1;
            while (!constants.add(c + "_$" + i)) i++;
            this.constant = c + "_$" + i;
        }
    }

    public boolean match(String ns) {
        return abbrev.equals(asAbbrev(ns)) || uri.equals(ns);
    }

    public String abbrev() {
        return abbrev;
    }

    public String xmlns() {
        return asXmlns(abbrev);
    }

    @Override
    public int compareTo(Namespace o) {
        return abbrev.compareTo(o.abbrev);
    }

    public boolean hasElements() {
        return ! enames.isEmpty();
    }

    public boolean isDefault() {
        return abbrev.isEmpty();
    }

    public String getUri() {
        return uri;
    }

    public String getConstant() {
        return constant;
    }

    public SortedSet<ElementName> getEnames() {
        return enames;
    }

}
