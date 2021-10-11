package org.cthul.monad.util;

import java.util.concurrent.ThreadLocalRandom;

public interface InternalId {

    String getInternalId();

    String getInternalIdIfPresent();

    static String randomInternalId() {
        long randomId = ThreadLocalRandom.current().nextLong();
        return Long.toHexString(randomId);
    }

    static String getInternalId(Object object) {
        if (object instanceof InternalId) {
            return ((InternalId) object).getInternalId();
        }
        return randomInternalId();
    }

    static String getInternalIdIfPresent(Object object) {
        if (object instanceof InternalId) {
            return ((InternalId) object).getInternalIdIfPresent();
        }
        return null;
    }
}
