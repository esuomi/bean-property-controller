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
package io.induct.reflection.bpc.extraction;

import java.lang.reflect.Method;

import io.induct.reflection.bpc.IAccessor;
import io.induct.reflection.bpc.IBeanProperty;
import io.induct.reflection.bpc.IMutator;
import io.induct.reflection.bpc.MethodAccessor;
import io.induct.reflection.bpc.MethodMutator;
import io.induct.reflection.bpc.NonMatchingAccessorAndMutatorException;

public abstract class AbstractExtractor {
    
    public abstract IBeanProperty<Object> extractProperty(String propertyName, Object object);
    
    protected IAccessor<?> findAccessor(String prefix, String propertyName, Object object) {
        Method m = findMethod(prefix, propertyName, object, 0);
        return ( m != null ) ? new MethodAccessor<Object>(m) : null;
    }
    
    protected IMutator<?> findMutator(String prefix, String propertyName, Object object) {
        Method m = findMethod(prefix, propertyName, object, 1);
        return ( m != null ) ? new MethodMutator<Object>(m) : null;
    }

    protected Method findMethod(String prefix, String propertyName, Object object, int expectedParams) {
        // TODO: For mutators check that only 1 input param is allowed
        String possibleMethodName = prefix.toLowerCase() + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        
        for (Method m : object.getClass().getMethods()) {
            if (m.getName().equals(possibleMethodName) ||
                m.getName().equalsIgnoreCase(possibleMethodName)) {
                
                if (m.getParameterTypes().length != expectedParams) {
                    continue;
                }
                return m;
            }
        }        
        
        return null;
    }
    
    protected void validateProperties(IAccessor<?> accessor, IMutator<?> mutator) {
        if (!accessor.getReturnType().equals(mutator.getType()) && 
                !accessor.getReturnType().isAssignableFrom(mutator.getType())) {
                throw new NonMatchingAccessorAndMutatorException();
            }
        }
}
