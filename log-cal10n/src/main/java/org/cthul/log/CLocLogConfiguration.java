package org.cthul.log;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;
import java.util.Locale;
import org.cthul.strings.format.FormatterConfiguration;
import org.slf4j.Logger;

/**
 *
 * @author Arian Treffer
 */
public class CLocLogConfiguration extends CLogConfigurationBase {
    
    private static final CLocLogConfiguration Default = new CLocLogConfiguration(FormatterConfiguration.getDefault());

    public static CLocLogConfiguration getDefault() {
        return Default;
    }
    
    protected Locale lastLocale = null;
    protected IMessageConveyor messageConveyor = null;
    
    public CLocLogConfiguration(CLogConfigurationBase parent) {
        super(parent);
    }
    
    public CLocLogConfiguration(FormatterConfiguration parent) {
        super(parent);
    }

    public CLocLogConfiguration() {
        super();
    }

    @Override
    public CLocLogConfiguration newSubconfiguration() {
        return new CLocLogConfiguration(this);
    }

    @Override
    public CLocLogConfiguration forLocale(Locale locale) {
        return (CLocLogConfiguration) super.forLocale(locale);
    }
    
    public <E extends Enum<?>> CLocLogger<E> getLogger(Logger logger) {
        return new CLocLogger<>(logger, this);
    }
    
    public <E extends Enum<?>> CLocLogger<E> getLogger(String name) {
        return getLogger(slfLogger(name));
    }
    
    public <E extends Enum<?>> CLocLogger<E> getLogger(Class clazz) {
        return getLogger(slfLogger(clazz));
    }
    
    /**
     * Chooses a logger based on the class name of the caller of this method.
     * Equivalent to {@link #getClassLogger(int) getClassLogger(0)}.
     * @return a logger
     */
    public <E extends Enum<?>> CLocLogger<E> getClassLogger() {
        return getLogger(slfLogger(1));
    }
    
    /**
     * Chooses a logger based on the class name of the caller of this method.
     * {@code i == 0} identifies the caller of this method, for {@code i > 0}, 
     * the stack is walked upwards.
     * @param i
     * @return a logger
     */
    public <E extends Enum<?>> CLocLogger<E> getClassLogger(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Expected value >= 0, got " + i);
        }
        return getLogger(slfLogger(i+1));
    }

    @Override
    public void setLocale(Locale locale) {
        messageConveyor = null;
        super.setLocale(locale);
    }

    public IMessageConveyor getMessageConveyor() {
        if (messageConveyor == null) {
            Locale l = locale();
            if (l == null) l = Locale.getDefault();
            messageConveyor = new MessageConveyor(l);
        }
        return messageConveyor;
    }
    
    public String getMessage(Enum<?> e) {
        return getMessageConveyor().getMessage(e, (Object[]) null);
    }
    
}
