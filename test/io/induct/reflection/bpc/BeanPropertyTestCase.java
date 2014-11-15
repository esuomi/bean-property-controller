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

import reflection.bpc.BeanProperty;
import reflection.bpc.MethodAccessor;
import reflection.bpc.MethodMutator;
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
