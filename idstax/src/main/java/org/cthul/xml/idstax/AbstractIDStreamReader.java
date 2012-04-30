package org.cthul.xml.idstax;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractIDStreamReader implements IDStreamReader {

    @Override
    public DataType nextTag() {
        DataType t = next();
        while (!t.isTagEvent() && t != DataType._EOF_) t = next();
        return t;
    }

    @Override
    public void require(int eID) {
        if (getEID() != eID) {
            throw new IDStreamException(
                    "Expected eID " + eID + ", got " + getEID());
        }
    }

    @Override
    public void require(DataType... dt) {
        DataType myType = getType();
        for (DataType t: dt)
            if (myType == t) return;
        throw new IDStreamException(
                "Unexpected data type " + myType);
    }

    @Override
    public void require(int eID, DataType dt) {
        require(eID);
        require(dt);
    }

}
