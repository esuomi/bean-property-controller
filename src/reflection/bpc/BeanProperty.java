package reflection.bpc;

public class BeanProperty<T> implements IBeanProperty<T> {

    private T object;
    private final IAccessor<T> accessor;
    private final IMutator<T> mutator;
    private final String propertyName;
    
    public BeanProperty(T object, String propertyName, IAccessor<T> accessor, IMutator<T> mutator) {
        if (object == null) {
            throw new IllegalArgumentException("Can't construct BeanProperty with null object");
        }
        setObject(object);
        if (propertyName == null) {
            throw new IllegalArgumentException("Can't construct BeanProperty with null propertyName");
        }
        if ("".equals(propertyName)) {
            throw new IllegalArgumentException("Can't construct BeanProperty with empty propertyName");
        }
        this.propertyName = propertyName;
        this.mutator = mutator;
        if (accessor == null) {
            throw new IllegalArgumentException("Can't construct BeanProperty with null accessor");
        }
        this.accessor = accessor;
    }    
    
    public void setObject(T object) {
        this.object = object;
    }
    
    public T getValue() {
        return accessor.access(object);
    }

    public boolean isArray() {
        return accessor.getReturnType().isArray();
    }

    public void setValue(T newValue) {
        mutator.mutate(object, newValue);
    }

    public String getPropertyName() {
        return propertyName;
    }
    
    @Override
    public String toString() {
        return "BeanProperty :: "+object.getClass().getName()+"#"+getPropertyName()+" with "+accessor.toString()+","+mutator.toString()+"]";
    }

    public Class<?> getType() {
        return accessor.getReturnType();
    }

    public boolean isReadOnly() {
        return mutator == null;
    }

}
