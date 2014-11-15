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
package io.induct.reflection.bpc;

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
