/*
 * 
 */

package org.cthul.xml.idstax.gen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author derari
 */
public class IDCollector {

    private Set<String> constants = new HashSet<String>();
    private Set<String> methods = new HashSet<String>();
    private Map<String, Namespace> nsByName = new HashMap<String, Namespace>();
    public SortedSet<ElementName> enames = new TreeSet<ElementName>();
    public SortedSet<Namespace> namespaces = new TreeSet<Namespace>();
    private boolean pvAttribute = false;

    private ElementName getEName(String nsName, String name) {
        for (ElementName en: enames) {
            if (en.name.equals(name) && en.ns.match(nsName))
                return en;
        }
        Namespace ns = nsByName.get(nsName);
        ElementName en = new ElementName(ns, name, constants, methods);
        ns.enames.add(en);
        enames.add(en);
        return en;
    }

    public Namespace addNamespace(String abbrev, String uri) {
        Namespace ns = new Namespace(abbrev, constants, uri);
        nsByName.put(ns.abbrev, ns);
        nsByName.put(ns.uri, ns);
        nsByName.put(ns.xmlns(), ns);
        namespaces.add(ns);
        return ns;
    }

    public ElementName addElement(String ns, String name) {
        ElementName en = getEName(ns, name);
        en.isElement = true;
        return en;
    }

    public ElementName addAttribute(String ns, String name) {
        ElementName en = getEName(ns, name);
        en.isAttribute = true;
        if (name.equals("pv")) pvAttribute = true;
        return en;
    }

    public void finish() {
        int i = 0;
        for (ElementName en: enames) {
            en.id = i++;
        }
    }

    public boolean hasPVAttribute() {
        return pvAttribute;
    }

    public SortedSet<Namespace> getNamespaces() {
        return namespaces;
    }

    public SortedSet<ElementName> getEnames() {
        return enames;
    }

}
