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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.induct.reflection.bpc.BeanInstantiationException;

public class ClassInstantiator implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final Class<?> c;
    private final InstantiationPolicy policy;

    public ClassInstantiator(Class<?> c, InstantiationPolicy policy) {
        this.c = c;
        this.policy = policy;        
    }
    
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
    
    public Object instantiate() {
        List<Throwable> exceptions = new ArrayList<Throwable>();
        Object instantiated = null;
        if (policy.compareTo(InstantiationPolicy.NO_ARGS) == 0) {
            instantiated = new DefaultInstantiator().instantiate(c);
        } else if (instantiated == null && policy.compareTo(InstantiationPolicy.NICE) == 0) {
            instantiated = new ConstructorInstantiator().instantiate(c);
        }

        if (instantiated != null) {
            return instantiated;    
        } else {
            throw new BeanInstantiationException("Failed to instantiate given class :: " + exceptions.toString(),
                                                 exceptions);
        }
        
    }
}
