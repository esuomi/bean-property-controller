/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Esko Suomi
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.induct.reflection.bpc.instantiation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.induct.reflection.bpc.BeanInstantiationException;
import io.induct.reflection.bpc.ConstructorComparator;
import io.induct.reflection.bpc.NiceValueProvider;

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
