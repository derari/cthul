package org.cthul.xml.idstax;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Arian Treffer
 */
public class AbstractElementIDResolver implements ElementIDResolver {

    private Map<String, SimpleDataName> byName;
    private Map<Integer, SimpleDataName> byId;

    public AbstractElementIDResolver() {
        byName = new HashMap<>();
        byId = new HashMap<>();
    }

    protected synchronized void add(String ns, String name, int id) {
        SimpleDataName dm = new SimpleDataName(name, name, id);
        byName.put(key(ns, name), dm);
        byId.put(id, dm);
    }

    protected NamespaceDataResolver namespace(String ns) {
        return new NamespaceDataResolver(ns);
    }

    @Override
    public int getEID(String ns, String name) {
        SimpleDataName dm = byName.get(key(ns, name));
        if (dm == null) return -1;
        return dm.id;
    }

    @Override
    public ElementID getElementID(String ns, String name) {
        return byName.get(key(ns, name));
    }

    @Override
    public ElementID getElementID(int id) {
        return byId.get(id);
    }

    @Override
    public Iterator<ElementID> iterator() {
        Iterator it = byName.values().iterator();
        return it;
    }

    private static String key(String ns, String name) {
        if (ns == null) return name;
        return "{" + ns + "}" + name;
    }

    public class NamespaceDataResolver {

        private String ns;

        private NamespaceDataResolver(String ns) {
            this.ns = ns;
        }

        public NamespaceDataResolver add(String name, int id) {
            AbstractElementIDResolver.this.add(ns, name, id);
            return this;
        }

        public NamespaceDataResolver namespace(String ns) {
            return new NamespaceDataResolver(ns);
        }
        
    }

    public static class SimpleDataName implements ElementID {

        private String namespace, name;
        private int id;

        public SimpleDataName(String namespace, String name, int id) {
            this.namespace = namespace;
            this.name = name;
            this.id = id;
        }

        @Override
        public String getNamespace() {
            return namespace;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getID() {
            return id;
        }

    }

}
