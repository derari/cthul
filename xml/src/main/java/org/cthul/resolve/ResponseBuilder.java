package org.cthul.resolve;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.RandomAccess;

/**
 * A builder for responses.
 * Instances may be immutable, always use the latest instance that was
 * returned by the building methods.
 */
public abstract class ResponseBuilder implements RResponse {
    
    /**
     * Returns a response builder with an additional warning message.
     * @param message
     * @return response builder
     */
    public abstract ResponseBuilder withWarning(String message);
    
    /**
     * Returns a response builder with an additional warning.
     * @param t
     * @return response builder
     */
    public abstract ResponseBuilder withWarning(Exception t);
    
    /**
     * Returns a response builder that unifies this builder and the given response.
     * @param r
     * @return response builder
     */
    public abstract ResponseBuilder withResponse(RResponse r);
    
    /**
     * Returns a response that combines the warnings of this builder with the given result.
     * @param result
     * @return response
     */
    public abstract RResponse withResult(RResult result);

    /**
     * Creates an immutable no-result response for the given request.
     * @param request
     * @return response builder
     */
    public static ResponseBuilder noResult(RRequest request) {
        return new NoResult(request);
    }
    
    protected static class NoResult extends ResponseBuilder {
        
        private final RRequest request;

        public NoResult(RRequest request) {
            this.request = request;
        }
        
        @Override
        public ResponseBuilder withWarning(Exception e) {
            return new WarningLog(request).withWarning(e);
        }

        @Override
        public ResponseBuilder withWarning(String message) {
            return new WarningLog(request).withWarning(message);
        }

        @Override
        public ResponseBuilder withResponse(RResponse r) {
            if (r instanceof ResponseBuilder) {
                return (ResponseBuilder) r;
            }
            return new WarningLog(request).withResponse(r);
        }

        @Override
        public RResponse withResult(RResult result) {
            return result;
        }

        @Override
        public RRequest getRequest() {
            return request;
        }

        @Override
        public boolean hasResult() {
            return false;
        }

        @Override
        public RResult getResult() {
            throw asException();
        }

        @Override
        public List<Exception> getWarningsLog() {
            return Collections.emptyList();
        }

        @Override
        public ResolvingException asException() {
            return new ResolvingException(request, "No result");
        }
    }
    
    protected static class WarningLog extends ResponseBuilder {
        
        private final RRequest request;
        private WarningsList warningsList = new WarningsList();
        private RResult result = null;

        public WarningLog(RRequest request) {
            this.request = request;
        }

        @Override
        public RRequest getRequest() {
            return request;
        }

        @Override
        public boolean hasResult() {
            return result != null;
        }

        @Override
        public RResult getResult() {
            if (result != null) return result;
            throw asException();
        }

        @Override
        public List<Exception> getWarningsLog() {
            return warningsList.asReadOnly();
        }

        @Override
        public ResolvingException asException() {
            if (hasResult() && warningsList.isEmpty()) return null;
            return warningsList.asException(request);
        }

        @Override
        public ResponseBuilder withWarning(Exception e) {
            warningsList.add(e);
            return this;
        }

        @Override
        public ResponseBuilder withResponse(RResponse r) {
            if (r.hasResult()) {
                withResult(r.getResult());
            } else if (r instanceof WarningLog) {
                warningsList = merge(warningsList, ((WarningLog) r).warningsList);
            } else {
                warningsList.addAll(r.getWarningsLog());
            }
            return this;
        }

        @Override
        public ResponseBuilder withWarning(String message) {
            warningsList.add(message);
            return this;
        }

        @Override
        public RResponse withResult(RResult result) {
            warningsList = merge(warningsList, result.warnings);
            result.warnings = warningsList;
            this.result = result;
            return result;
        }
    }
    
    static WarningsList merge(WarningsList w1, WarningsList w2) {
        if (w1 == null || (w1.isEmpty() && w1.message == null)) return w2;
        if (w2 == null || (w2.isEmpty() && w2.message == null)) return w1;
        w1.add(w2);
        return w1;
    }
    
    static List<Exception> asReadOnly(WarningsList warningsList) {
        if (warningsList == null) return Collections.emptyList();
        return warningsList.asReadOnly();
    }
    
    static ResolvingException asException(RRequest request, WarningsList warningsList) {
        if (warningsList == null) return null;
        return warningsList.asException(request);
    }
    
    protected static interface Warnings {
        int size();
        void addTo(List<Exception> target);
    }
    
    protected static class WarningsList extends AbstractList<Exception> implements Warnings, RandomAccess {
        
        private int size = 0;
        private final Deque<Warnings> source;
        private final List<Exception> actual;
        private List<Exception> readOnly = null;
        private String message;
        
        public WarningsList() {
            source = new ArrayDeque<>();
            actual = new ArrayList<>();
        }

        public void addMessage(final String message) {
            if (this.message != null) {
                add(new Warnings() {
                    @Override
                    public int size() {
                        return 1;
                    }
                    @Override
                    public void addTo(List<Exception> target) {
                        target.add(new ResolvingException(message));
                    }
                });
            } else {
                this.message = message;
            }
        }
        
        public boolean add(WarningsList warningsList) {
            if (warningsList.message != null) add(warningsList.message);
            return add((Warnings) warningsList);
        }
        
        public boolean add(Warnings warnings) {
            size += warnings.size();
            source.add(warnings);
            return true;
        }

        @Override
        public void addTo(List<Exception> target) {
            if (target instanceof WarningsList) {
                ((WarningsList) target).add(this);
            } else {
                target.addAll(this);
            }
        }

        @Override
        public boolean add(final Exception e) {
            return add(new Warnings() {
                @Override
                public int size() {
                    return 1;
                }
                @Override
                public void addTo(List<Exception> target) {
                    target.add(e);
                }
            });
        }

        public boolean add(final String message) {
            return add(new Warnings() {
                @Override
                public int size() {
                    return 1;
                }
                @Override
                public void addTo(List<Exception> target) {
                    target.add(new ResolvingException(message));
                }
            });
        }

        @Override
        public boolean addAll(final Collection<? extends Exception> c) {
            if (c.isEmpty()) return false;
            return add(new Warnings() {
                @Override
                public int size() {
                    return c.size();
                }
                @Override
                public void addTo(List<Exception> target) {
                    target.addAll(c);
                }
            });
        }

        @Override
        public Exception get(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException(
                        "[" + index + "], size: " + size);
            }
            while (index >= actual.size()) {
                source.removeFirst().addTo(actual);
            }
            return actual.get(index);
        }

        @Override
        public int size() {
            return size;
        }

        public List<Exception> asReadOnly() {
            if (readOnly == null) {
                readOnly = Collections.unmodifiableList(this);
            }
            return readOnly;
        }
        
        public ResolvingException asException(RRequest request) {
            if (isEmpty()) return null;
            ResolvingException e = new ResolvingException(
                    request,
                    message != null ? message :
                    size() == 1 ? get(0).getMessage() :
                    size() + " warnings", get(0));
            for (int i = 1; i < size(); i++)
                e.addSuppressed(get(i));
            return e;
        }
    }
}
