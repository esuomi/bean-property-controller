package reflection.bpc;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    
    protected Object instantiate() {
        List<Throwable> exceptions = new ArrayList<Throwable>();
        Object instantiated = null;
        if (policy.compareTo(InstantiationPolicy.NO_ARGS) == 0) {
            try {
                instantiated = c.newInstance();
            } catch (InstantiationException e) {
                exceptions.add(e);
            } catch (IllegalAccessException e) {
                exceptions.add(e);
            }
        } else if (instantiated == null && policy.compareTo(InstantiationPolicy.NICE) == 0) {
            List<Constructor<?>> constructors = new ArrayList<Constructor<?>>();
            for (Constructor<?> constructor : c.getConstructors()) {
                constructors.add(constructor);
            }
            Collections.sort(constructors, ConstructorComparator.PARAMETER_COUNT);
            Constructor<?> constructor = constructors.get(0);
            try {
                instantiated = constructor.newInstance(niceParamsFor(constructor));
            } catch (InstantiationException e) {
                exceptions.add(e);
            } catch (IllegalAccessException e) {
                exceptions.add(e);
            } catch (IllegalArgumentException e) {
                exceptions.add(e);
            } catch (InvocationTargetException e) {
                exceptions.add(e);
            }
        }

        if (instantiated != null) {
            return instantiated;    
        } else {
            throw new BeanInstantiationException("Failed to instantiate given class :: " + exceptions.toString(),
                                                 exceptions);
        }
        
    }
    
    private static Object[] niceParamsFor(Constructor<?> constructor) {
        Object[] niceParameters = new Object[constructor.getParameterTypes().length];
        
        for (int i = 0; i < constructor.getParameterTypes().length; i++) {
            niceParameters[i] = NiceValueProvider.INSTANCE.getNiceValueFor(constructor.getParameterTypes()[i]);
        }
        
        return niceParameters;
    }
}
