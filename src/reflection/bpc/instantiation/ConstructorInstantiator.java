package reflection.bpc.instantiation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import reflection.bpc.BeanInstantiationException;
import reflection.bpc.ConstructorComparator;
import reflection.bpc.NiceValueProvider;

public class ConstructorInstantiator implements IClassInstantiator {

    public <C> C instantiate(Class<C> c) {
        List<Constructor<?>> constructors = new ArrayList<Constructor<?>>();
        for (Constructor<?> constructor : c.getConstructors()) {
            constructors.add(constructor);
        }
        Collections.sort(constructors, ConstructorComparator.PARAMETER_COUNT);
        Constructor<?> constructor = constructors.get(0);
        try {
            return (C) constructor.newInstance(niceParamsFor(constructor));
        } catch (InstantiationException e) {
            throw new BeanInstantiationException("Couldn't instantiate class "+c.getName()+" using the "+constructor.getParameterTypes().length+"-argument constructor", e);
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException("Couldn't instantiate class "+c.getName()+" using the "+constructor.getParameterTypes().length+"-argument constructor", e);
        } catch (IllegalArgumentException e) {
            throw new BeanInstantiationException("Couldn't instantiate class "+c.getName()+" using the "+constructor.getParameterTypes().length+"-argument constructor", e);
        } catch (InvocationTargetException e) {
            throw new BeanInstantiationException("Couldn't instantiate class "+c.getName()+" using the "+constructor.getParameterTypes().length+"-argument constructor", e);
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
