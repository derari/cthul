package org.cthul.xml.idstax;

/**
 *
 * @author Arian Treffer
 */
public enum DataType {

    _START_         (true),
    _CHARACTERS_    (false),
    _END_           (true),

    _EOF_           (false);

    private final boolean tagEvent;

    private DataType(boolean tagEvent) {
        this.tagEvent = tagEvent;
    }
    
    public boolean isTagEvent() {
        return tagEvent;
    }

}
