package reflection.bpc;

public interface IBeanProperty<T> {

    T getValue();
    void setValue(T newValue);
    void setObject(T newObject);
    String getPropertyName();
    
    Class<?> getType();
    boolean isArray(); 
    boolean isReadOnly();
}
