package reflection.bpc.instantiation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import reflection.bpc.BeanInstantiationException;

public class ClassInstantiator implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final Class<?> c;
    private final InstantiationPolicy policy;

    public ClassInstantiator(Class<?> c, InstantiationPolicy policy) {
        this.c = c;
        this.policy = policy;        
    }
    
    /**
     * Policy to use when instantiating a bean from Class instance. These policies aren't inclusive
     * and won't cascade to any other policy. 
     */
    public static enum InstantiationPolicy {
        /**
         * Default behaviour, instantiate only if default no arguments constructor is available. 
         */
        NO_ARGS,
        /**
         * Instantiate using the shortest possible constructor with nice values;
         * - "" for Strings
         * - 0 for numbers
         * - false for booleans
         * - zero length arrays for any array property
         * 
         *  TODO: Beans within beans?
         */
        NICE
    }
    
    public Object instantiate() {
        List<Throwable> exceptions = new ArrayList<Throwable>();
        Object instantiated = null;
        if (policy.compareTo(InstantiationPolicy.NO_ARGS) == 0) {
            instantiated = new DefaultInstantiator().instantiate(c);
        } else if (instantiated == null && policy.compareTo(InstantiationPolicy.NICE) == 0) {
            instantiated = new ConstructorInstantiator().instantiate(c);
        }

        if (instantiated != null) {
            return instantiated;    
        } else {
            throw new BeanInstantiationException("Failed to instantiate given class :: " + exceptions.toString(),
                                                 exceptions);
        }
        
    }
}
