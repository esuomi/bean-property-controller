package reflection.bpc;

import java.lang.reflect.Field;

public class FieldAccessMutateControl<T> implements IMutator<T>, IAccessor<T> {

    private final Field f;

    public FieldAccessMutateControl(Field f) {
        this.f = f;
    }

    public Class<?> getType() {
        return f.getType();
    }

    public void mutate(Object object, T newValue) {
        try {
            f.set(object, newValue);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public T access(Object object) {
        try {
            return (T) f.get(object);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> getReturnType() {
        return f.getType();
    }
    
    @Override
    public String toString() {
        return "Field Accessor/Mutator";
    }

}
