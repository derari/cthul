/*
 * 
 */

package org.cthul.xml.idstax.gen;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Arian Treffer
 */
public class SaxIDCollector extends DefaultHandler {

    private IDCollector idCollector;

    /**
     * Collects element IDs from XSD schemas.
     * Currently, only one schema file may be loaded.
     * The target namespace of the schema will be the default namespace
     * for all IDs.
     *
     * @param idCollector
     */
    public SaxIDCollector(IDCollector idCollector) {
        this.idCollector = idCollector;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        int attsLen = atts.getLength();
        for (int i = 0; i < attsLen; i++) {
            String attQName = atts.getQName(i);
            if (attQName.startsWith("xmlns")) {
                int c = attQName.indexOf(':');
                if (c >= 0) {
                    String ns = c < 0 ? "" : attQName.substring(c+1);
                    idCollector.addNamespace(ns, atts.getValue(i));
                }
            } else if (attQName.equals("targetNamespace")) {
                idCollector.addNamespace("", atts.getValue(i));
            }
        }

        if (qName.equals("attribute") || qName.endsWith(":attribute")) {
            String name = atts.getValue(uri, "name");
            if (name != null) idCollector.addAttribute("", name);
        } else if (qName.equals("element") || qName.endsWith(":element")) {
            String name = atts.getValue(uri, "name");
            if (name != null) {
                int c = name.indexOf(':');
                String ns = c < 0 ? "" : name.substring(0, c);
                name = name.substring(c+1);
                idCollector.addElement(ns, name);
            }
        }
    }

}
