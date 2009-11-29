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
