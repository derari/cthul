package org.cthul.resolve;

/**
 *
 * @author Arian Treffer
 */
public class CompositeResolver extends AbstractResolver {

    private final ResourceResolver[] inputs;

    public CompositeResolver(ResourceResolver... inputs) {
        this.inputs = inputs;
    }

    @Override
    public RResult resolve(RRequest request) {
        for (ResourceResolver r: inputs) {
            RResult res = r.resolve(request);
            if (res != null) return res;
        }
        return null;
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
            return getClass().getSimpleName() + "(" + inputs[0] + ")";
        } else {
            return getClass().getSimpleName() + "(" + inputs.length + ")";
        }
    }
    
    

}
