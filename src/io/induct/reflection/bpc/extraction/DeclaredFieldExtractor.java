package reflection.bpc.extraction;

import java.lang.reflect.Field;

import reflection.bpc.BeanProperty;
import reflection.bpc.FieldAccessMutateControl;
import reflection.bpc.IAccessor;
import reflection.bpc.IBeanProperty;
import reflection.bpc.IMutator;

public class DeclaredFieldExtractor extends FieldExtractor {
    
    public IBeanProperty<Object> extractProperty(String propertyName, Object object) {
        FieldAccessMutateControl<?> declaredFieldControl = null;
        Field f = findField(propertyName, object);
        if (f != null) {
            declaredFieldControl = new FieldAccessMutateControl<Object>(f);    
        }
        if (declaredFieldControl != null) {

            IMutator<?> mutator = declaredFieldControl;
            IAccessor<?> accessor = declaredFieldControl;
            
            validateProperties(accessor, mutator);
            return new BeanProperty(object, propertyName, accessor, mutator);    
        }
        return null;
    }
    @Override
    protected Field findField(String propertyName, Object object) {
        return extractField(propertyName, object.getClass().getDeclaredFields());
    }
    
}
