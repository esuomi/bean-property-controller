package reflection.bpc.extraction;

import reflection.bpc.BeanProperty;
import reflection.bpc.IAccessor;
import reflection.bpc.IBeanProperty;
import reflection.bpc.IMutator;

public class MethodExtractor extends AbstractExtractor {
    
    public IBeanProperty<Object> extractProperty(String propertyName, Object object) {
        IMutator<?> mutator = null;
        IAccessor<?> accessor = null;
        mutator = findMutator("set", propertyName, object);
        if (mutator != null) {
            if (mutator.getType().equals(Boolean.class) ||
                mutator.getType().equals(boolean.class)) {
                accessor = findAccessor("is", propertyName, object);
            } else {
                accessor = findAccessor("get", propertyName, object);    
            }
            if (accessor != null) {
                validateProperties(accessor, mutator);
                return new BeanProperty(object, propertyName, accessor, mutator);
            }    
        } else {
            accessor = findAccessor("get", propertyName, object);    
            if (accessor != null) {
                return new BeanProperty(object, propertyName, accessor, null);
            }
        }
        return null;
    }
}
