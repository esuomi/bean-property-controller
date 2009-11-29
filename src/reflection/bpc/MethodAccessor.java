package reflection.bpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodAccessor<T> implements IAccessor<T> {

    private final Method m;

    public MethodAccessor(Method m) {
        this.m = m;
    }

    public T access(Object object) {
        try {
            return (T) m.invoke(object);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> getReturnType() {
        return m.getReturnType();
    }
    
    @Override
    public String toString() {
        return "Method accessor";
    }

}
