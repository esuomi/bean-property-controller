package reflection.bpc.extraction;

import java.lang.reflect.Method;

import reflection.bpc.IAccessor;
import reflection.bpc.IBeanProperty;
import reflection.bpc.IMutator;
import reflection.bpc.MethodAccessor;
import reflection.bpc.MethodMutator;
import reflection.bpc.NonMatchingAccessorAndMutatorException;

public abstract class AbstractExtractor {
    
    public abstract IBeanProperty<Object> extractProperty(String propertyName, Object object);
    
    protected IAccessor<?> findAccessor(String prefix, String propertyName, Object object) {
        Method m = findMethod(prefix, propertyName, object, 0);
        return ( m != null ) ? new MethodAccessor<Object>(m) : null;
    }
    
    protected IMutator<?> findMutator(String prefix, String propertyName, Object object) {
        Method m = findMethod(prefix, propertyName, object, 1);
        return ( m != null ) ? new MethodMutator<Object>(m) : null;
    }

    protected Method findMethod(String prefix, String propertyName, Object object, int expectedParams) {
        // TODO: For mutators check that only 1 input param is allowed
        String possibleMethodName = prefix.toLowerCase() + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        
        for (Method m : object.getClass().getMethods()) {
            if (m.getName().equals(possibleMethodName) ||
                m.getName().equalsIgnoreCase(possibleMethodName)) {
                
                if (m.getParameterTypes().length != expectedParams) {
                    continue;
                }
                return m;
            }
        }        
        
        return null;
    }
    
    protected void validateProperties(IAccessor<?> accessor, IMutator<?> mutator) {
        if (!accessor.getReturnType().equals(mutator.getType()) && 
                !accessor.getReturnType().isAssignableFrom(mutator.getType())) {
                throw new NonMatchingAccessorAndMutatorException();
            }
        }
}
