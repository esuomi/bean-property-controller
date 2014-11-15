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

import io.induct.reflection.bpc.BeanProperty;
import io.induct.reflection.bpc.IAccessor;
import io.induct.reflection.bpc.IBeanProperty;
import io.induct.reflection.bpc.IMutator;

public class MethodExtractor extends AbstractExtractor {
    
    public IBeanProperty<Object> extractProperty(String propertyName, Object object) {
        IMutator<?> mutator = null;
        IAccessor<?> accessor = null;
        mutator = findMutator("set", propertyName, object);
        if (mutator != null) {
            if (mutator.getType().equals(Boolean.class) ||
                mutator.getType().equals(boolean.class)) {
                accessor = findAccessor("is", propertyName, object);
            } else {
                accessor = findAccessor("get", propertyName, object);    
            }
            if (accessor != null) {
                validateProperties(accessor, mutator);
                return new BeanProperty(object, propertyName, accessor, mutator);
            }    
        } else {
            accessor = findAccessor("get", propertyName, object);    
            if (accessor != null) {
                return new BeanProperty(object, propertyName, accessor, null);
            }
        }
        return null;
    }
}
