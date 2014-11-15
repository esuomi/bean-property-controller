package reflection.bpc.extraction;

import java.lang.reflect.Field;

import reflection.bpc.BeanProperty;
import reflection.bpc.FieldAccessMutateControl;
import reflection.bpc.IBeanProperty;

public class FieldExtractor extends AbstractExtractor {

    @Override
    public IBeanProperty<Object> extractProperty(String propertyName, Object object) {
        Field f = findField(propertyName, object);
        
        if (f != null) {
            FieldAccessMutateControl<?> fieldControl = new FieldAccessMutateControl<Object>(f);
            return new BeanProperty(object, propertyName, fieldControl, fieldControl);    
        }
        return null;
    }
    
    protected Field findField(String propertyName, Object object) {
        return extractField(propertyName, object.getClass().getFields());
    }
    
    protected Field extractField(String propertyName, Field[] fields) {
        for (Field f : fields) {
            if (f.getName().equals(propertyName) ||
                f.getName().equalsIgnoreCase(propertyName)) {
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                return f;
            }
        }
        return null;
    }

}
