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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.induct.reflection.bpc.extraction.PropertyExtractor;
import io.induct.reflection.bpc.instantiation.ClassInstantiator;
import io.induct.reflection.bpc.instantiation.ClassInstantiator.InstantiationPolicy;

/**
 * Reflection based class for semi-automatic mutation and access of Java Beans.
 * <p>
 * This class is built on top of three interfaces, {@link IBeanProperty}, {@link IMutator} and
 * {@link IAccessor}.
 * <p>
 * To enable its functionality this class does various tricks on the object or class used to
 * call one of several <code>of(..)</code> methods available and as such breaks out from the
 * Java Bean specification constraints rather quickly if any other than {@link #of(Object)} is used.
 * This breakage is completely intentional though since this class is supposed to be as
 * transparent as possible while still enabling as much of its core functionality as possible.
 * <p>
 * The functionality of this class is based on the idea of lazily loading each internal bean
 * property accessor/mutator to increase initial performance and to decrease memory footprint.
 * This rule is followed in every single API call except {@link #getPropertyNames()} which
 * automatically loads everything at once.
 * 
 * @author Esko
 */
public class BeanPropertyController implements Serializable {
    
    /**
     * Determines how deep into the object this class should look into when extracting properties.
     * 
     * @author Esko
     */
    public static enum ExtractionDepth {
        /**
         * Extract properties only from methods seeking for [is|get]/set combinations.
         * This is the default depth level.
         */
        METHODS,
        /**
         * Extract properties from fields if method extraction fails.
         * No differentiation between private/protected/public fields is done, however this
         * depth won't see declared fields.
         */
        FIELDS,
        /**
         * Try to create any combination of mutators and accessors to create a property control
         * which matches given property name. This is the deepest and will try to test its
         * functionality during construction. 
         */
        QUESTIMATE
    }
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Default stepping depth for property extraction, -1 means infinite depth stepping.
     * <p>
     * What this means is that if steps are 1, only the given class (or its instance) is allowed to be
     * accessed, if the stepping is 2, only the given class (or its instance) and its' children is allowed
     * to be accessed, if 3 then the child's children are allowed and so on and so forth.
     */
    public static final int DEFAULT_STEPS = -1;
    
    private transient Object object;
    private Serializable serializableObject;
    private final transient Map<String, IBeanProperty> properties;
    private final ExtractionDepth extractionDepth;
    private final int steps;
    private transient PropertyExtractor extractor;
    private ClassInstantiator instantiatior; //TODO: Finalize?

    private BeanPropertyController(Object object, ExtractionDepth extractionDepth, int stepping) {
        setObject(object);
        this.extractionDepth = extractionDepth;
        this.steps = stepping;
        properties = new ConcurrentHashMap<String, IBeanProperty>();
        extractor = new PropertyExtractor(extractionDepth);
    }
    
    public static BeanPropertyController of(Object object) {
        return of(object, ExtractionDepth.METHODS);
    }
    public static BeanPropertyController of(Object object, int steps) {
        return of(object, ExtractionDepth.METHODS, steps);
    }
    public static BeanPropertyController of(Object object, ExtractionDepth extractionDepth) {
        return of(object, extractionDepth, DEFAULT_STEPS);
    }
    public static BeanPropertyController of(Object object, ExtractionDepth extractionDepth, int steps) {
        return new BeanPropertyController(object, extractionDepth, steps);
    }    
    public static BeanPropertyController of(Class<?> c) {
        return of(c, ExtractionDepth.METHODS, DEFAULT_STEPS, InstantiationPolicy.NO_ARGS);
    }
    public static BeanPropertyController of(Class<?> c, InstantiationPolicy policy) {
        return of(c, ExtractionDepth.METHODS, DEFAULT_STEPS, policy);
    }
    public static BeanPropertyController of(Class<?> c, int steps) {
        return of(c, ExtractionDepth.METHODS, steps, InstantiationPolicy.NO_ARGS);
    }
    public static BeanPropertyController of(Class<?> c, ExtractionDepth extractionDepth) {
        return of(c, extractionDepth, DEFAULT_STEPS, InstantiationPolicy.NO_ARGS);
    }    
    public static BeanPropertyController of(Class<?> c, ExtractionDepth extractionDepth, int steps) {
        return of(c, extractionDepth, steps, InstantiationPolicy.NO_ARGS);
    }    
    public static BeanPropertyController of(Class<?> c, int steps, InstantiationPolicy policy) {
        return of(c, ExtractionDepth.METHODS, steps, policy);
    }
    public static BeanPropertyController of(Class<?> c, ExtractionDepth extractionDepth, InstantiationPolicy policy) {
        return of(c, extractionDepth, DEFAULT_STEPS, policy);
    }    
    public static BeanPropertyController of(Class<?> c, ExtractionDepth extractionDepth, int steps, InstantiationPolicy policy) {
        ClassInstantiator instantiator = new ClassInstantiator(c, policy);
        BeanPropertyController bpc = new BeanPropertyController(instantiator.instantiate(), extractionDepth, steps);
        bpc.instantiatior = instantiator;
        return bpc;
    }
    
    public Object getObject() {
        return (serializableObject == null) ? object : serializableObject;
    }
    
    public <T>T getObject(T objectClass) {
        return (T) getObject();
    }

    public Object access(String propertyName) {
        return getPropertyByName(propertyName).getValue();
    }
    
    public BeanPropertyController mutate(String propertyName, Object newValue) {
        getPropertyByName(propertyName).setValue(newValue);
        return this;
    }

    public BeanPropertyController mutate(Map<String, Object> newProps) {
        for (Entry<String, Object> newProperty : newProps.entrySet()) {
            mutate(newProperty.getKey(), newProperty.getValue());
        }
        return this;
    }

    /**
     * Convenience method for getting type of property's value. Note that generic methods
     * always return Object as their type because of type erasure. 
     */
    public Class<?> typeOf(String propertyName) {
        return getPropertyByName(propertyName).getType();
    }

    public boolean isArray(String propertyName) {
        return getPropertyByName(propertyName).isArray();
    }
    
    public boolean isReadOnly(String propertyName) {
        return getPropertyByName(propertyName).isReadOnly();
    }

    public String[] getPropertyNames() {
        return getPropertyNames(0);
    }
    
    public String[] getPropertyNames(int steps) {
        return getPropertyNames("", steps, getObject());
    }

    private String[] getPropertyNames(String basePathName, int steps, Object root) {
        Map<Method, Boolean> potentialMutators = new HashMap<Method, Boolean>();
        Map<Method, Boolean> potentialAccessors = new HashMap<Method, Boolean>();
        Set<Field> potentialFields = new HashSet<Field>();
        
        Set<String> propertyNames = new HashSet<String>();
        
        for (Method m : root.getClass().getMethods()) {
            if (isMutatorMethod(m)) {
                potentialMutators.put(m, true);
            }
            if (isAccessorMethod(m)) {
                potentialAccessors.put(m, true);
            }
        }
        
        for (Entry<Method, Boolean> mutator : potentialMutators.entrySet()) {
            String baseName = extractAccessorName(mutator.getKey());
            
            for (Entry<Method, Boolean> accessor : potentialAccessors.entrySet()) {
                if (accessor.getValue()) {
                    if (isAccessorProperty(accessor.getKey(), baseName)) {
                        propertyNames.add(basePathName + baseName);
                        accessor.setValue(false);
                    }    
                }                
            }
        }

        if (extractionDepth.compareTo(ExtractionDepth.FIELDS) >= 0) {
            for (Field f : root.getClass().getFields()) {
                potentialFields.add(f);
            }
        }
        if (extractionDepth.compareTo(ExtractionDepth.QUESTIMATE) >= 0) {
            for (Field f : root.getClass().getDeclaredFields()) {
                if (!potentialFields.contains(f)) {
                    potentialFields.add(f);    
                }                
            }
        }
        for (Field f : potentialFields) {
            propertyNames.add(basePathName + f.getName());
        }
        
        if (steps > 0) {
            for (String prop : propertyNames) {
                extractProperty(prop);
                if (access(prop) != null && !access(prop).getClass().isPrimitive()) {
                    for (String stepProperty : getPropertyNames(prop + ".", --steps, access(prop))) {
                        propertyNames.add(stepProperty);
                    }
                }
            }
        }
        
        return propertyNames.toArray(new String[propertyNames.size()]);
    }

    private String extractAccessorName(Method mutator) {
        String accessorBaseName = mutator.getName().substring(3);
        return accessorBaseName.substring(0, 1).toLowerCase() + accessorBaseName.substring(1);
    }

    private boolean isAccessorMethod(Method m) {
        return (m.getName().startsWith("get") || m.getName().startsWith("is")) && m.getParameterTypes().length == 0;
    }

    private boolean isMutatorMethod(Method m) {
        return m.getName().startsWith("set") && m.getParameterTypes().length == 1;
    }

    private boolean isAccessorProperty(Method method, String baseName) {
        return isExpectedProperty(method, baseName, "get", "is");
    }
    
    private boolean isExpectedProperty(Method method, String propertyBaseName, String... prefixes) {
        String methodName = method.getName();
        
        boolean isExpected = false;
        for (String prefix : prefixes) {
            if (methodName.equalsIgnoreCase(prefix + propertyBaseName)) {
                isExpected = true;
                break;
            }
        }
        return isExpected;
    }

    private IBeanProperty getPropertyByName(String propertyName) {
        if (!properties.containsKey(propertyName)) {
            properties.put(propertyName, extractProperty(propertyName));
        }
        return properties.get(propertyName);
    }

    private IBeanProperty<?> extractProperty(String propertyName) {
        
        String[] propertyPath = (steps < 0) ? propertyName.split("\\.")
                                            : propertyName.split("\\.", steps + 1) ;

        IBeanProperty<?> lastBeanProperty = null;
        Object extracted = getObject();
        
        for (String pathPart : propertyPath) {
            String trimmedPathPart = pathPart.trim();
            if (trimmedPathPart.length() > 0) {
                lastBeanProperty = getExtractor().extractProperty(trimmedPathPart, extracted);
                extracted = lastBeanProperty.getValue();
            }            
        }
        return lastBeanProperty;
    }
    
    private PropertyExtractor getExtractor() {
        // TODO: Figure out if this lazyload is redundant.
        if (extractor == null) {
            extractor = new PropertyExtractor(extractionDepth);
        }
        return extractor;
    }

    public void recycle() {
        Object newObject = instantiatior.instantiate();
        
        resetPropertyObjects(newObject);
        
        setObject(newObject);
    }    

    private void resetPropertyObjects(Object newObject) {
        for (Entry<String, IBeanProperty> beanProperty : properties.entrySet()) {
            beanProperty.getValue().setObject(newObject);
        }
    }

    private void setObject(Object newObject) {
        this.object = (newObject instanceof Serializable) ? null : newObject;
        this.serializableObject = (Serializable) ((newObject instanceof Serializable) ? newObject : null);    
    }
}
