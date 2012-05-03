package org.cthul.xml.idstax.gen;

import java.util.Set;

/**
 *
 * @author Arian Treffer
 */
public class ElementName implements Comparable<ElementName> {

    public Namespace ns;
    public String name;
    public String constant;
    public String method;
    public int id = -1;

    public boolean isElement = false;
    public boolean isAttribute = false;

    public ElementName(Namespace ns, String name, Set<String> constants, Set<String> methods) {
        this.ns = ns;
        this.name = name;

        String c = JavaNameUtils.UNDERSCORE(name);
        String m = JavaNameUtils.camelCase(name);

        if (constants.add(c) && methods.add(m)) {
            this.constant = c;
            this.method = m;
        } else {
            int i = 1;
            while (!(constants.add(c + "_$" + i) &&
                     methods.add(m + "_$" + i))) i++;
            this.constant = c + "_$" + i;
            this.method = m + "_$" + i;
        }
    }

    @Override
    public int compareTo(ElementName o) {
        int c = ns.compareTo(o.ns);
        if (c != 0) return c;
        return name.compareTo(o.name);
    }

    public String ns_name() {
        String ns_ = ns.isDefault() ? "" : ns.abbrev+":";
        return ns_ + name;
    }

    public String getTypeString() {
        if (isElement) {
            if (isAttribute) return "element or attribute";
            else return "element";
        } else {
            return "attribute";
        }
    }

    public String getConstant() {
        return constant;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
