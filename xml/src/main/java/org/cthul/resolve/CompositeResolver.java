 package org.cthul.resolve;

import java.util.Collection;

/**
 * Looks up resources from an array of resolvers, returning the first
 * result that was found.
 */
public class CompositeResolver extends ResourceResolverBase {
    
    public static CompositeResolver all(ResourceResolver r0, ResourceResolver... more) {
        return join(r0, more);
    }
    
    public static CompositeResolver join(ResourceResolver r0, ResourceResolver... more) {
        ResourceResolver[] all = new ResourceResolver[more.length+1];
        all[0] = r0;
        System.arraycopy(more, 0, all, 1, more.length);
        return new CompositeResolver(false, all);
    }
    
    private final ResourceResolver[] inputs;

    public CompositeResolver(ResourceResolver... inputs) {
        this(true, inputs);
    }
    
    @SuppressWarnings("SuspiciousToArrayCall")
    public CompositeResolver(Collection<? extends ResourceResolver> inputs) {
        this(false, inputs.toArray(new ResourceResolver[inputs.size()]));
    }
    
    protected CompositeResolver(boolean clone, ResourceResolver... inputs) {
        this.inputs = clone ? inputs.clone() : inputs;
    }

    @Override
    public RResponse resolve(RRequest request) {
        ResponseBuilder response = request.noResultResponse();
        for (ResourceResolver r: inputs) {
            RResponse res = r.resolve(request);
            if (res.hasResult()) {
                return response.withResult(res.getResult());
            }
            response = response.withResponse(res);
        }
        return response;
    }

    /**
     * CompositeFinders are always immutable.
     * @return this instance
     */
    @Override
    public ResourceResolver immutable() {
        return this;
    }

    @Override
    public String toString() {
        if (inputs.length == 1) {
            String cn = getClass().getSimpleName();
            if (cn.equals("CompositeResolver")) cn = "";
            return cn + "[" + inputs[0] + "]";
        } else {
            return getClass().getSimpleName() + "(" + inputs.length + ")";
        }
    }
}
