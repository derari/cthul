// generated file
package org.cthul.log;

import org.cthul.log.format.Message;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 *
 * @author Arian Treffer
 */
public class CLogger {
    
    private final Logger logger;
    private final CLogConfiguration conf;

    public CLogger(Logger logger) {
        this(logger, CLogConfiguration.getDefault());
    }
    
    public CLogger(Logger logger, CLogConfiguration conf) {
        CLoggerFix.fix(logger);
        this.logger = logger;
        this.conf = conf;
    }
    
    public boolean trace() {
        return logger.isTraceEnabled();
    }
    
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public boolean trace(Marker marker) {
        return logger.isTraceEnabled(marker);
    }
    
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    public boolean debug() {
        return logger.isDebugEnabled();
    }
    
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean debug(Marker marker) {
        return logger.isDebugEnabled(marker);
    }
    
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    public boolean info() {
        return logger.isInfoEnabled();
    }
    
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean info(Marker marker) {
        return logger.isInfoEnabled(marker);
    }
    
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    public boolean warn() {
        return logger.isWarnEnabled();
    }
    
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public boolean warn(Marker marker) {
        return logger.isWarnEnabled(marker);
    }
    
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    public boolean error() {
        return logger.isErrorEnabled();
    }
    
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public boolean error(Marker marker) {
        return logger.isErrorEnabled(marker);
    }
    
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    public void trace(Object message) {
        if (trace()) {
            logger.trace(new Message(conf, message).toString());
        }
    }

    public void trace(Object message, Object... args) {
        if (trace()) {
            logger.trace(new Message(conf, message, args).toString());
        }
    }

    public void trace(Object message, Object arg0) {
        if (trace()) {
            logger.trace(new Message(conf, message, arg0).toString());
        }
    }

    public void trace(Object message, Object arg0, Object arg1) {
        if (trace()) {
            logger.trace(new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void trace(Throwable throwable, Object message) {
        if (trace()) {
            logger.trace(new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void trace(Throwable throwable, Object message, Object... args) {
        if (trace()) {
            logger.trace(new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void trace(Throwable throwable, Object message, Object arg0) {
        if (trace()) {
            logger.trace(new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void trace(Throwable throwable, Object message, Object arg0, Object arg1) {
        if (trace()) {
            logger.trace(new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public void trace(Marker marker, Object message) {
        if (trace(marker)) {
            logger.trace(marker, new Message(conf, message).toString());
        }
    }

    public void trace(Marker marker, Object message, Object... args) {
        if (trace(marker)) {
            logger.trace(marker, new Message(conf, message, args).toString());
        }
    }

    public void trace(Marker marker, Object message, Object arg0) {
        if (trace(marker)) {
            logger.trace(marker, new Message(conf, message, arg0).toString());
        }
    }

    public void trace(Marker marker, Object message, Object arg0, Object arg1) {
        if (trace(marker)) {
            logger.trace(marker, new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void trace(Marker marker, Throwable throwable, Object message) {
        if (trace(marker)) {
            logger.trace(marker, new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void trace(Marker marker, Throwable throwable, Object message, Object... args) {
        if (trace(marker)) {
            logger.trace(marker, new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void trace(Marker marker, Throwable throwable, Object message, Object arg0) {
        if (trace(marker)) {
            logger.trace(marker, new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void trace(Marker marker, Throwable throwable, Object message, Object arg0, Object arg1) {
        if (trace(marker)) {
            logger.trace(marker, new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public void debug(Object message) {
        if (debug()) {
            logger.debug(new Message(conf, message).toString());
        }
    }

    public void debug(Object message, Object... args) {
        if (debug()) {
            logger.debug(new Message(conf, message, args).toString());
        }
    }

    public void debug(Object message, Object arg0) {
        if (debug()) {
            logger.debug(new Message(conf, message, arg0).toString());
        }
    }

    public void debug(Object message, Object arg0, Object arg1) {
        if (debug()) {
            logger.debug(new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void debug(Throwable throwable, Object message) {
        if (debug()) {
            logger.debug(new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void debug(Throwable throwable, Object message, Object... args) {
        if (debug()) {
            logger.debug(new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void debug(Throwable throwable, Object message, Object arg0) {
        if (debug()) {
            logger.debug(new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void debug(Throwable throwable, Object message, Object arg0, Object arg1) {
        if (debug()) {
            logger.debug(new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public void debug(Marker marker, Object message) {
        if (debug(marker)) {
            logger.debug(marker, new Message(conf, message).toString());
        }
    }

    public void debug(Marker marker, Object message, Object... args) {
        if (debug(marker)) {
            logger.debug(marker, new Message(conf, message, args).toString());
        }
    }

    public void debug(Marker marker, Object message, Object arg0) {
        if (debug(marker)) {
            logger.debug(marker, new Message(conf, message, arg0).toString());
        }
    }

    public void debug(Marker marker, Object message, Object arg0, Object arg1) {
        if (debug(marker)) {
            logger.debug(marker, new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void debug(Marker marker, Throwable throwable, Object message) {
        if (debug(marker)) {
            logger.debug(marker, new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void debug(Marker marker, Throwable throwable, Object message, Object... args) {
        if (debug(marker)) {
            logger.debug(marker, new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void debug(Marker marker, Throwable throwable, Object message, Object arg0) {
        if (debug(marker)) {
            logger.debug(marker, new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void debug(Marker marker, Throwable throwable, Object message, Object arg0, Object arg1) {
        if (debug(marker)) {
            logger.debug(marker, new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public void info(Object message) {
        if (info()) {
            logger.info(new Message(conf, message).toString());
        }
    }

    public void info(Object message, Object... args) {
        if (info()) {
            logger.info(new Message(conf, message, args).toString());
        }
    }

    public void info(Object message, Object arg0) {
        if (info()) {
            logger.info(new Message(conf, message, arg0).toString());
        }
    }

    public void info(Object message, Object arg0, Object arg1) {
        if (info()) {
            logger.info(new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void info(Throwable throwable, Object message) {
        if (info()) {
            logger.info(new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void info(Throwable throwable, Object message, Object... args) {
        if (info()) {
            logger.info(new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void info(Throwable throwable, Object message, Object arg0) {
        if (info()) {
            logger.info(new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void info(Throwable throwable, Object message, Object arg0, Object arg1) {
        if (info()) {
            logger.info(new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public void info(Marker marker, Object message) {
        if (info(marker)) {
            logger.info(marker, new Message(conf, message).toString());
        }
    }

    public void info(Marker marker, Object message, Object... args) {
        if (info(marker)) {
            logger.info(marker, new Message(conf, message, args).toString());
        }
    }

    public void info(Marker marker, Object message, Object arg0) {
        if (info(marker)) {
            logger.info(marker, new Message(conf, message, arg0).toString());
        }
    }

    public void info(Marker marker, Object message, Object arg0, Object arg1) {
        if (info(marker)) {
            logger.info(marker, new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void info(Marker marker, Throwable throwable, Object message) {
        if (info(marker)) {
            logger.info(marker, new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void info(Marker marker, Throwable throwable, Object message, Object... args) {
        if (info(marker)) {
            logger.info(marker, new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void info(Marker marker, Throwable throwable, Object message, Object arg0) {
        if (info(marker)) {
            logger.info(marker, new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void info(Marker marker, Throwable throwable, Object message, Object arg0, Object arg1) {
        if (info(marker)) {
            logger.info(marker, new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public void warn(Object message) {
        if (warn()) {
            logger.warn(new Message(conf, message).toString());
        }
    }

    public void warn(Object message, Object... args) {
        if (warn()) {
            logger.warn(new Message(conf, message, args).toString());
        }
    }

    public void warn(Object message, Object arg0) {
        if (warn()) {
            logger.warn(new Message(conf, message, arg0).toString());
        }
    }

    public void warn(Object message, Object arg0, Object arg1) {
        if (warn()) {
            logger.warn(new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void warn(Throwable throwable, Object message) {
        if (warn()) {
            logger.warn(new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void warn(Throwable throwable, Object message, Object... args) {
        if (warn()) {
            logger.warn(new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void warn(Throwable throwable, Object message, Object arg0) {
        if (warn()) {
            logger.warn(new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void warn(Throwable throwable, Object message, Object arg0, Object arg1) {
        if (warn()) {
            logger.warn(new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public void warn(Marker marker, Object message) {
        if (warn(marker)) {
            logger.warn(marker, new Message(conf, message).toString());
        }
    }

    public void warn(Marker marker, Object message, Object... args) {
        if (warn(marker)) {
            logger.warn(marker, new Message(conf, message, args).toString());
        }
    }

    public void warn(Marker marker, Object message, Object arg0) {
        if (warn(marker)) {
            logger.warn(marker, new Message(conf, message, arg0).toString());
        }
    }

    public void warn(Marker marker, Object message, Object arg0, Object arg1) {
        if (warn(marker)) {
            logger.warn(marker, new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void warn(Marker marker, Throwable throwable, Object message) {
        if (warn(marker)) {
            logger.warn(marker, new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void warn(Marker marker, Throwable throwable, Object message, Object... args) {
        if (warn(marker)) {
            logger.warn(marker, new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void warn(Marker marker, Throwable throwable, Object message, Object arg0) {
        if (warn(marker)) {
            logger.warn(marker, new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void warn(Marker marker, Throwable throwable, Object message, Object arg0, Object arg1) {
        if (warn(marker)) {
            logger.warn(marker, new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public void error(Object message) {
        if (error()) {
            logger.error(new Message(conf, message).toString());
        }
    }

    public void error(Object message, Object... args) {
        if (error()) {
            logger.error(new Message(conf, message, args).toString());
        }
    }

    public void error(Object message, Object arg0) {
        if (error()) {
            logger.error(new Message(conf, message, arg0).toString());
        }
    }

    public void error(Object message, Object arg0, Object arg1) {
        if (error()) {
            logger.error(new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void error(Throwable throwable, Object message) {
        if (error()) {
            logger.error(new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void error(Throwable throwable, Object message, Object... args) {
        if (error()) {
            logger.error(new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void error(Throwable throwable, Object message, Object arg0) {
        if (error()) {
            logger.error(new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void error(Throwable throwable, Object message, Object arg0, Object arg1) {
        if (error()) {
            logger.error(new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public void error(Marker marker, Object message) {
        if (error(marker)) {
            logger.error(marker, new Message(conf, message).toString());
        }
    }

    public void error(Marker marker, Object message, Object... args) {
        if (error(marker)) {
            logger.error(marker, new Message(conf, message, args).toString());
        }
    }

    public void error(Marker marker, Object message, Object arg0) {
        if (error(marker)) {
            logger.error(marker, new Message(conf, message, arg0).toString());
        }
    }

    public void error(Marker marker, Object message, Object arg0, Object arg1) {
        if (error(marker)) {
            logger.error(marker, new Message(conf, message, arg0, arg1).toString());
        }
    }

    public void error(Marker marker, Throwable throwable, Object message) {
        if (error(marker)) {
            logger.error(marker, new Message(conf, throwable, message).toString(), throwable);
        }
    }

    public void error(Marker marker, Throwable throwable, Object message, Object... args) {
        if (error(marker)) {
            logger.error(marker, new Message(conf, throwable, message, args).toString(), throwable);
        }
    }

    public void error(Marker marker, Throwable throwable, Object message, Object arg0) {
        if (error(marker)) {
            logger.error(marker, new Message(conf, throwable, message, arg0).toString(), throwable);
        }
    }

    public void error(Marker marker, Throwable throwable, Object message, Object arg0, Object arg1) {
        if (error(marker)) {
            logger.error(marker, new Message(conf, throwable, message, arg0, arg1).toString(), throwable);
        }
    }

    public boolean isEnabled(CLogLevel level) {
        switch (level) {
            case Trace:
                return isTraceEnabled();
            case Debug:
                return isDebugEnabled();
            case Info:
                return isInfoEnabled();
            case Warn:
                return isWarnEnabled();
            case Error:
                return isErrorEnabled();
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public boolean isEnabled(CLogLevel level, Marker marker) {
        switch (level) {
            case Trace:
                return isTraceEnabled(marker);
            case Debug:
                return isDebugEnabled(marker);
            case Info:
                return isInfoEnabled(marker);
            case Warn:
                return isWarnEnabled(marker);
            case Error:
                return isErrorEnabled(marker);
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Object message) {
        switch (level) {
            case Trace:
                trace(message);
                break;
            case Debug:
                debug(message);
                break;
            case Info:
                info(message);
                break;
            case Warn:
                warn(message);
                break;
            case Error:
                error(message);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Object message, Object... args) {
        switch (level) {
            case Trace:
                trace(message, args);
                break;
            case Debug:
                debug(message, args);
                break;
            case Info:
                info(message, args);
                break;
            case Warn:
                warn(message, args);
                break;
            case Error:
                error(message, args);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Object message, Object arg0) {
        switch (level) {
            case Trace:
                trace(message, arg0);
                break;
            case Debug:
                debug(message, arg0);
                break;
            case Info:
                info(message, arg0);
                break;
            case Warn:
                warn(message, arg0);
                break;
            case Error:
                error(message, arg0);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Object message, Object arg0, Object arg1) {
        switch (level) {
            case Trace:
                trace(message, arg0, arg1);
                break;
            case Debug:
                debug(message, arg0, arg1);
                break;
            case Info:
                info(message, arg0, arg1);
                break;
            case Warn:
                warn(message, arg0, arg1);
                break;
            case Error:
                error(message, arg0, arg1);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Throwable throwable, Object message) {
        switch (level) {
            case Trace:
                trace(throwable, message);
                break;
            case Debug:
                debug(throwable, message);
                break;
            case Info:
                info(throwable, message);
                break;
            case Warn:
                warn(throwable, message);
                break;
            case Error:
                error(throwable, message);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Throwable throwable, Object message, Object... args) {
        switch (level) {
            case Trace:
                trace(throwable, message, args);
                break;
            case Debug:
                debug(throwable, message, args);
                break;
            case Info:
                info(throwable, message, args);
                break;
            case Warn:
                warn(throwable, message, args);
                break;
            case Error:
                error(throwable, message, args);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Throwable throwable, Object message, Object arg0) {
        switch (level) {
            case Trace:
                trace(throwable, message, arg0);
                break;
            case Debug:
                debug(throwable, message, arg0);
                break;
            case Info:
                info(throwable, message, arg0);
                break;
            case Warn:
                warn(throwable, message, arg0);
                break;
            case Error:
                error(throwable, message, arg0);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Throwable throwable, Object message, Object arg0, Object arg1) {
        switch (level) {
            case Trace:
                trace(throwable, message, arg0, arg1);
                break;
            case Debug:
                debug(throwable, message, arg0, arg1);
                break;
            case Info:
                info(throwable, message, arg0, arg1);
                break;
            case Warn:
                warn(throwable, message, arg0, arg1);
                break;
            case Error:
                error(throwable, message, arg0, arg1);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Marker marker, Object message) {
        switch (level) {
            case Trace:
                trace(marker, message);
                break;
            case Debug:
                debug(marker, message);
                break;
            case Info:
                info(marker, message);
                break;
            case Warn:
                warn(marker, message);
                break;
            case Error:
                error(marker, message);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Marker marker, Object message, Object... args) {
        switch (level) {
            case Trace:
                trace(marker, message, args);
                break;
            case Debug:
                debug(marker, message, args);
                break;
            case Info:
                info(marker, message, args);
                break;
            case Warn:
                warn(marker, message, args);
                break;
            case Error:
                error(marker, message, args);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Marker marker, Object message, Object arg0) {
        switch (level) {
            case Trace:
                trace(marker, message, arg0);
                break;
            case Debug:
                debug(marker, message, arg0);
                break;
            case Info:
                info(marker, message, arg0);
                break;
            case Warn:
                warn(marker, message, arg0);
                break;
            case Error:
                error(marker, message, arg0);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Marker marker, Object message, Object arg0, Object arg1) {
        switch (level) {
            case Trace:
                trace(marker, message, arg0, arg1);
                break;
            case Debug:
                debug(marker, message, arg0, arg1);
                break;
            case Info:
                info(marker, message, arg0, arg1);
                break;
            case Warn:
                warn(marker, message, arg0, arg1);
                break;
            case Error:
                error(marker, message, arg0, arg1);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Marker marker, Throwable throwable, Object message) {
        switch (level) {
            case Trace:
                trace(marker, throwable, message);
                break;
            case Debug:
                debug(marker, throwable, message);
                break;
            case Info:
                info(marker, throwable, message);
                break;
            case Warn:
                warn(marker, throwable, message);
                break;
            case Error:
                error(marker, throwable, message);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Marker marker, Throwable throwable, Object message, Object... args) {
        switch (level) {
            case Trace:
                trace(marker, throwable, message, args);
                break;
            case Debug:
                debug(marker, throwable, message, args);
                break;
            case Info:
                info(marker, throwable, message, args);
                break;
            case Warn:
                warn(marker, throwable, message, args);
                break;
            case Error:
                error(marker, throwable, message, args);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Marker marker, Throwable throwable, Object message, Object arg0) {
        switch (level) {
            case Trace:
                trace(marker, throwable, message, arg0);
                break;
            case Debug:
                debug(marker, throwable, message, arg0);
                break;
            case Info:
                info(marker, throwable, message, arg0);
                break;
            case Warn:
                warn(marker, throwable, message, arg0);
                break;
            case Error:
                error(marker, throwable, message, arg0);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

    public void log(CLogLevel level, Marker marker, Throwable throwable, Object message, Object arg0, Object arg1) {
        switch (level) {
            case Trace:
                trace(marker, throwable, message, arg0, arg1);
                break;
            case Debug:
                debug(marker, throwable, message, arg0, arg1);
                break;
            case Info:
                info(marker, throwable, message, arg0, arg1);
                break;
            case Warn:
                warn(marker, throwable, message, arg0, arg1);
                break;
            case Error:
                error(marker, throwable, message, arg0, arg1);
                break;
            default:
                throw new IllegalArgumentException(level.toString());
        }
    }

}
