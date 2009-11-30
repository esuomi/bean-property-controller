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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import reflection.bpc.BeanPropertyController.ExtractionDepth;

public class PropertyExtractor {
    
    private final ExtractionDepth extractionDepth;
    private final Object object;
    
    public PropertyExtractor(Object object, ExtractionDepth extractionDepth) {
        this.object = object;
        this.extractionDepth = extractionDepth;
    }
    
    protected IBeanProperty<?> extractProperty(String propertyName, Object object) {
        IMutator<?> mutator = null;
        IAccessor<?> accessor = null;
        FieldAccessMutateControl<?> fieldControl = null;
        
        if (extractionDepth.compareTo(ExtractionDepth.METHODS) == 0) {
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
        }
        if (extractionDepth.compareTo(ExtractionDepth.FIELDS) >= 0) {
            Field f = findField(propertyName);
            
            if (f != null) {
                fieldControl = new FieldAccessMutateControl<Object>(f);
                return new BeanProperty(object, propertyName, fieldControl, fieldControl);    
            }
        }

        if (extractionDepth.compareTo(ExtractionDepth.QUESTIMATE) >= 0) {
            FieldAccessMutateControl<?> declaredFieldControl = null;
            Field f = findDeclaredField(propertyName);
            if (f != null) {
                declaredFieldControl = new FieldAccessMutateControl<Object>(f);    
            }
            if (declaredFieldControl != null) {
                fieldControl = (fieldControl != null) ? fieldControl : declaredFieldControl;
                mutator = (mutator != null) ? mutator : fieldControl;
                accessor = (accessor != null) ? accessor : fieldControl;
                // TODO Create some sort of internal access test to ensure the property is
                //      real and not just a coincidence.
                validateProperties(accessor, mutator);
                return new BeanProperty(object, propertyName, accessor, mutator);    
            }                
        }
        
        throw new NonexistentPropertyException(propertyName, object.getClass());
    }
    private void validateProperties(IAccessor<?> accessor, IMutator<?> mutator) {
        if (!accessor.getReturnType().equals(mutator.getType()) && 
            !accessor.getReturnType().isAssignableFrom(mutator.getType())) {
            throw new NonMatchingAccessorAndMutatorException();
        }
    }

    private IAccessor<?> findAccessor(String prefix, String propertyName, Object object) {
        Method m = findMethod(prefix, propertyName, object, 0);
        return ( m != null ) ? new MethodAccessor<Object>(m) : null;
    }
    
    private IMutator<?> findMutator(String prefix, String propertyName, Object object) {
        Method m = findMethod(prefix, propertyName, object, 1);
        return ( m != null ) ? new MethodMutator<Object>(m) : null;
    }

    private Method findMethod(String prefix, String propertyName, Object object, int expectedParams) {
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

    private Field findDeclaredField(String propertyName) {
        return extractField(propertyName, object.getClass().getDeclaredFields());
    }

    private Field findField(String propertyName) {
        return extractField(propertyName, object.getClass().getFields());
    }
    
    private Field extractField(String propertyName, Field[] fields) {
        for (Field f : fields) {
            if (f.getName().equals(propertyName) ||
                f.getName().equalsIgnoreCase(propertyName)) {
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                return f;
            }
        }
        return null;
    }
    
    
}
