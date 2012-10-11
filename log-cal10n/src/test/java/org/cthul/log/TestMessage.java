package org.cthul.log;

import ch.qos.cal10n.BaseName;
import java.util.List;

/**
 *
 * @author Arian Treffer
 */
@BaseName("TestMessage")
public enum TestMessage implements NamedParameters {
    
    HELLO_WORLD("name");
    
    private final List<String> names;

    private TestMessage(String... names) {
        this.names = ParamUtils.wrap(names);
    }

    @Override
    public List<String> getNames() {
        return names;
    }
    
}
