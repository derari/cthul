package org.cthul.monad.result;

import org.cthul.monad.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomScope implements Scope {
    
    private final String name;
    private final Logger logger;

    public CustomScope(String name) {
        this.name = name;
        logger = LoggerFactory.getLogger(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
