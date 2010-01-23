package reflection.bpc.instantiation;

import reflection.bpc.BeanInstantiationException;

public class DefaultInstantiator implements IClassInstantiator {

    public <C> C instantiate(Class<C> c) {
        try {
            return c.newInstance();
        } catch (InstantiationException e) {
            throw new BeanInstantiationException("Couldn't instantiate given class "+c.getName(), e);
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException("Couldn't instantiate given class "+c.getName(), e);
        }
    }

}
