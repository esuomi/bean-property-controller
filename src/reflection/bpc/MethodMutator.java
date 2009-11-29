package reflection.bpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodMutator<T> implements IMutator<T> {

    private final Method m;

    public MethodMutator(Method m) {
        this.m = m;
    }

    public void mutate(Object object, T newValue) {
        try {
            m.invoke(object, newValue);
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
    }

    public Class<?> getType() {
        return m.getParameterTypes()[0];
    }
    
    
    @Override
    public String toString() {
        return "Method mutator";
    }

}
