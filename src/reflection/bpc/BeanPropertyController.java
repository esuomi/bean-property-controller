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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Default stepping depth for property extraction, -1 means infinite depth stepping.
     * <p>
     * What this means is that if steps are 1, only the given class (or its instance) is allowed to be
     * accessed, if the stepping is 2, only the given class (or its instance) and its' children is allowed
     * to be accessed, if 3 then the child's children are allowed and so on and so forth.
     */
    public static final int DEFAULT_STEPS = -1;

    /**
     * Policy to use when instantiating a bean from Class instance. These policies aren't inclusive
     * and won't cascade to any other policy. 
     */
    public static enum InstantiationPolicy {
        /**
         * Default behaviour, instantiate only if default no arguments constructor is available. 
         */
        NO_ARGS,
        /**
         * Instantiate using the shortest possible constructor with nice values;
         * - "" for Strings
         * - 0 for numbers
         * - false for booleans
         * - zero length arrays for any array property
         * 
         *  TODO: Beans within beans?
         */
        NICE
    }
    
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

    private transient Object object;
    private Serializable serializableObject;
    private final transient Map<String, IBeanProperty> properties;
    private final ExtractionDepth extractionDepth;
    private final int steps;
    private transient PropertyExtractor extractor;
    private InstantiationPolicy instantiationPolicy; //TODO: Finalize?

    private BeanPropertyController(Object object, ExtractionDepth extractionDepth, int stepping) {
        setObject(object);
        this.extractionDepth = extractionDepth;
        this.steps = stepping;
        properties = new ConcurrentHashMap<String, IBeanProperty>();
        extractor = new PropertyExtractor(getObject(), extractionDepth);
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
        BeanPropertyController bpc = new BeanPropertyController(instantiate(c, policy), extractionDepth, steps);
        bpc.instantiationPolicy = policy;
        return bpc;
    }

    private static Object instantiate(Class<?> c, InstantiationPolicy policy) {
        List<Throwable> exceptions = new ArrayList<Throwable>();
        Object instantiated = null;
        if (policy.compareTo(InstantiationPolicy.NO_ARGS) == 0) {
            try {
                instantiated = c.newInstance();
            } catch (InstantiationException e) {
                exceptions.add(e);
            } catch (IllegalAccessException e) {
                exceptions.add(e);
            }
        } else if (instantiated == null && policy.compareTo(InstantiationPolicy.NICE) == 0) {
            List<Constructor<?>> constructors = new ArrayList<Constructor<?>>();
            for (Constructor<?> constructor : c.getConstructors()) {
                constructors.add(constructor);
            }
            Collections.sort(constructors, ConstructorComparator.PARAMETER_COUNT);
            Constructor<?> constructor = constructors.get(0);
            try {
                instantiated = constructor.newInstance(niceParamsFor(constructor));
            } catch (InstantiationException e) {
                exceptions.add(e);
            } catch (IllegalAccessException e) {
                exceptions.add(e);
            } catch (IllegalArgumentException e) {
                exceptions.add(e);
            } catch (InvocationTargetException e) {
                exceptions.add(e);
            }
        }

        if (instantiated != null) {
            return instantiated;    
        } else {
            throw new BeanInstantiationException("Failed to instantiate given class :: " + exceptions.toString(),
                                                 exceptions);
        }
        
    }

    private static Object[] niceParamsFor(Constructor<?> constructor) {
        Object[] niceParameters = new Object[constructor.getParameterTypes().length];
        
        for (int i = 0; i < constructor.getParameterTypes().length; i++) {
            niceParameters[i] = NiceValueProvider.INSTANCE.getNiceValueFor(constructor.getParameterTypes()[i]);
        }
        
        return niceParameters;
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
                lastBeanProperty = getExtractor(extracted).extractProperty(trimmedPathPart, extracted);
                extracted = lastBeanProperty.getValue();
            }            
        }
        return lastBeanProperty;
    }
    
    private PropertyExtractor getExtractor(Object root) {
        // TODO: Figure out if this lazyload is redundant.
        if (extractor == null) {
            extractor = new PropertyExtractor(root, extractionDepth);
        }
        return extractor;
    }

    public void recycle() {
        Object newObject = instantiate(getObject().getClass(), instantiationPolicy);
        
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
