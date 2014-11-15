/*
 * Copyright 2009 Esko Suomi (suomi.esko@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
