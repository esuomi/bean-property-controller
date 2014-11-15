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

import io.induct.reflection.bpc.BeanProperty;
import io.induct.reflection.bpc.MethodAccessor;
import io.induct.reflection.bpc.MethodMutator;
import junit.framework.TestCase;

public class BeanPropertyTestCase extends TestCase {
    
    private BeanProperty<String> prop;
    
    @Override
    protected void setUp() throws Exception {
        prop = new BeanProperty<String>("",
                                        "propName",
                                        new MethodAccessor(null),
                                        new MethodMutator(null));
    }
    
    public void testHasCustomToStringMethod() throws Exception {
        assertEquals("BeanProperty :: java.lang.String#propName with Method accessor,Method mutator]",
                     prop.toString());
    }
    
    public void testCantInstantiateWithNullObject() throws Exception {
        try {
            prop = new BeanProperty<String>(null, null, null, null);
            fail("Should've thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            assertEquals("Can't construct BeanProperty with null object", e.getMessage());
        }
    }
    
    public void testCantInstantiateWithNullOrEmptyPropertyName() throws Exception {
        try {
            prop = new BeanProperty<String>("", null, null, null);
            fail("Should've thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            assertEquals("Can't construct BeanProperty with null propertyName", e.getMessage());
        }
        try {
            prop = new BeanProperty<String>("", "", null, null);
            fail("Should've thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            assertEquals("Can't construct BeanProperty with empty propertyName", e.getMessage());
        }
    }
    
    /**
     * Null mutator isn't included in these tests because BPC supports read-only properties.
     */
    public void testCantInstantiateWithNullAccessor() throws Exception {
        try {
            prop = new BeanProperty<String>("", "a", null, null);
            fail("Should've thrown IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            assertEquals("Can't construct BeanProperty with null accessor", e.getMessage());
        }
    }

}
