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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import reflection.bpc.BeanPropertyController.ExtractionDepth;
import reflection.bpc.ClassInstantiator.InstantiationPolicy;
import reflection.bpc.testbeans.ArrayBean;
import reflection.bpc.testbeans.BooleanClass;
import reflection.bpc.testbeans.BrokenBean;
import reflection.bpc.testbeans.HiddenBean;
import reflection.bpc.testbeans.IntegerBean;
import reflection.bpc.testbeans.MultipleConstructorsBean;
import reflection.bpc.testbeans.QuestimationBean;
import reflection.bpc.testbeans.RecursionBean;
import reflection.bpc.testbeans.SerializableBean;
import reflection.bpc.testbeans.SingleValueBean;
import reflection.bpc.testbeans.SomeClass;
import reflection.bpc.testbeans.TraditionalBean;
import reflection.bpc.testbeans.VaryingParametersBean;

public class BeanPropertyControllerTestCase extends TestCase {
    
    private BeanPropertyController bpc;
    private SingleValueBean<String> singleValueBean;
    private IntegerBean intBean;
    private ArrayBean arrayBean;
    private double[] values;
    private SomeClass valueClass;
    private BooleanClass booleanClass;
    private QuestimationBean questimationBean;
    private RecursionBean recursionBean;
    private TraditionalBean traditionalBean;
    private VaryingParametersBean varyingParametersBean;
    
    @Override
    protected void setUp() throws Exception {
        singleValueBean = new SingleValueBean<String>("testingValue");
        intBean = new IntegerBean(3);
        values = new double[3];
        values[0] = 1.2;
        values[1] = 3.4;
        values[2] = 5.6;
        arrayBean = new ArrayBean(values);
        valueClass = new SomeClass("hidden dragon", "crouching tiger");
        booleanClass = new BooleanClass(true);
        questimationBean = new QuestimationBean("onlySetter", "onlyGetter");
        recursionBean = new RecursionBean(singleValueBean);
        traditionalBean = new TraditionalBean();
        varyingParametersBean = new VaryingParametersBean();
    }
    
    public void testCreatesControllerObjectForGivenBean() throws Exception {
        bpc = BeanPropertyController.of(singleValueBean);
        
        assertEquals(singleValueBean, bpc.getObject());
    }
    
    public void testCanAccessNamedProperty() throws Exception {
        bpc = BeanPropertyController.of(singleValueBean);
        Object value = bpc.access("value");
        assertEquals(singleValueBean.getValue(), value);
    }
    
    public void testCanMutateNamedProperty() throws Exception {
        bpc = BeanPropertyController.of(singleValueBean);
        bpc.mutate("value", "newValue");
        assertEquals("newValue", bpc.access("value"));
    }
    
    public void testCanHandlePrimitiveIntegerType() throws Exception {
        bpc = BeanPropertyController.of(intBean);
        assertEquals(3, bpc.access("integer"));
        bpc.mutate("integer", 10);
        assertEquals(10, bpc.access("integer"));
    }
    
    public void testCanHandleArrays() throws Exception {
        bpc = BeanPropertyController.of(arrayBean);
        assertTrue("Expected "+Arrays.toString(values)+", but was "+Arrays.toString((double[]) bpc.access("values")),
                   Arrays.equals(values, (double[]) bpc.access("values")));
        double[] newValues = new double[] { 9.8, 7.6, 5.4};
        bpc.mutate("values", newValues);
        assertTrue("Expected "+Arrays.toString(newValues)+", but was "+Arrays.toString((double[]) bpc.access("values")),
                Arrays.equals(newValues, (double[]) bpc.access("values")));
    }
    
    public void testProvidesConvenienceMethodsForCheckingPropertysAttributes() throws Exception {
        bpc = BeanPropertyController.of(arrayBean);
        assertTrue(bpc.isArray("values"));
    }
    
    public void testAccessesFieldDirectlyIfAppropriateMethodsDontExistAndCorrectConstructorIsUsed() throws Exception {
        bpc = BeanPropertyController.of(valueClass, ExtractionDepth.FIELDS);
        assertEquals("crouching tiger", bpc.access("visible"));
        bpc.mutate("visible", "still visible");
        assertEquals("still visible", bpc.access("visible"));
    }
    
    public void testThrowsDescriptiveExceptionIfNamedPropertyIsntFound() throws Exception {
        bpc = BeanPropertyController.of(valueClass);
        try {
            bpc.access("doughnuts");
        } catch (NonexistentPropertyException e) {
            assertEquals("Property 'doughnuts' doesn't exist for the specified class reflection.bpc.testbeans.SomeClass", e.getMessage());
        }
    }
    
    public void testCanAccessBooleanAccessorCorrectly() throws Exception {
        bpc = BeanPropertyController.of(booleanClass);
        assertEquals(Boolean.TRUE, bpc.access("boo"));
        bpc.mutate("boo", false);
        assertEquals(Boolean.FALSE, bpc.access("boo"));
    }
    
    public void testCanCombineDifferentMutatorsAndAccessorsIfQuestimationIsAllowedToConstructBeanPropertyObject() throws Exception {
        bpc = BeanPropertyController.of(questimationBean, ExtractionDepth.QUESTIMATE);
        assertEquals("onlySetter", bpc.access("onlySetter"));
        assertEquals("onlyGetter", bpc.access("onlyGetter"));
        bpc.mutate("onlySetter", "And that's fine");
        bpc.mutate("onlyGetter", "because this class just doesn't care!");
        assertEquals("And that's fine", bpc.access("onlySetter"));
        assertEquals("because this class just doesn't care!", bpc.access("onlyGetter"));
    }
    
    public void testCanListAllAvailableMethodPropertiesForGivenObject() throws Exception {
        bpc = BeanPropertyController.of(questimationBean, ExtractionDepth.METHODS);
        String[] propertyNames = bpc.getPropertyNames();
        assertEquals(0, propertyNames.length);
        
        bpc = BeanPropertyController.of(traditionalBean);
        propertyNames = bpc.getPropertyNames();
        assertEquals(3, propertyNames.length);
    }
    
    public void testCanListAllAvailableMethodAndFieldPropertiesForGivenObject() throws Exception {
        bpc = BeanPropertyController.of(valueClass, ExtractionDepth.FIELDS);
        String[] propertyNames = bpc.getPropertyNames();
        assertEquals(1, propertyNames.length);
        assertEquals("visible", propertyNames[0]);
    }
    
    public void testCanListAllAvailablePropertiesOfAnyTypeForGivenObject() throws Exception {
        bpc = BeanPropertyController.of(questimationBean, ExtractionDepth.QUESTIMATE);
        String[] propertyNames = removeUnitTestingLibraryProxyProperties(bpc.getPropertyNames());
        
        assertEquals(2, propertyNames.length);
        assertEquals("onlyGetter", propertyNames[0]);
        assertEquals("onlySetter", propertyNames[1]);
    }
    
    public void testCanScanPropertiesFromRecursionBean() throws Exception {
        bpc = BeanPropertyController.of(recursionBean);
        
        Arrays.equals(bpc.getPropertyNames(), new String[] {"bean"});
        Arrays.equals(bpc.getPropertyNames(), new String[] {"bean.value", "bean"});
        
        bpc.mutate("bean.value", "generic hello");
        assertEquals("generic hello", bpc.access("bean.value"));
    }

    private String[] removeUnitTestingLibraryProxyProperties(String[] propertyNames) {
        List<String> actualProperties = new ArrayList<String>();
        for (String property : propertyNames) {
            actualProperties.add(property);
        }
        removeEclEMMAFields(actualProperties);
        return actualProperties.toArray(new String[actualProperties.size()]);
    }
    
    /*
     * Yes, this does happen. Apparently EclEMMA creates proxies to track coverage.
     */
    private void removeEclEMMAFields(List<String> actualProperties) {
        actualProperties.remove("$VRc");
        actualProperties.remove("serialVersionUID");
    }

    public void testIsAbleToExtractPropertiesWithinProperties() throws Exception {
        bpc = BeanPropertyController.of(recursionBean);
        assertEquals("testingValue", bpc.access("bean.value"));
        bpc.mutate("bean.value", "jou");
        assertEquals("jou", bpc.access("bean.value"));
    }
    
    public void testCanBeSetToIgnorePropertiesWithinPropertiesOnConstruct() throws Exception {
        bpc = BeanPropertyController.of(recursionBean, 0);
        try {
            bpc.access("bean.value");
            fail("Should've thrown NonexistentPropertyException");
        } catch (NonexistentPropertyException e) {
            assertEquals("Property 'bean.value' doesn't exist for the specified class reflection.bpc.testbeans.RecursionBean",
                         e.getMessage());
        }
    }
    
    public void testCanDeterminePropertyTypeForSaferManipulation() throws Exception {
        bpc = BeanPropertyController.of(questimationBean, ExtractionDepth.QUESTIMATE);
        assertEquals(String.class, bpc.typeOf("onlySetter"));
    }
    
    public void testSupportsReadOnlyProperties() throws Exception {
        bpc = BeanPropertyController.of(questimationBean);
        assertTrue(bpc.isReadOnly("onlyGetter"));
        assertEquals("onlyGetter", bpc.access("onlyGetter"));
        
        bpc = BeanPropertyController.of(booleanClass);
        assertFalse(bpc.isReadOnly("boo"));
    }
    
    public void testChecksForTheAmountOfParametersInMutators() throws Exception {
        bpc = BeanPropertyController.of(varyingParametersBean);
        bpc.mutate("value", "plap");
        
        assertEquals("plap", bpc.access("value"));
    }
    
    public void testChecksThatAccessorsReturnTypeMatchesWithMutatorsParameter() throws Exception {
        bpc = BeanPropertyController.of(BrokenBean.class, -1);
        bpc.mutate("valid", "this property is valid");
        assertEquals("this property is valid", bpc.access("valid"));
        
        try {
            bpc.access("invalid");
            fail("Should've thrown NonMatchingAccessorAndMutatorException");
        } catch (NonMatchingAccessorAndMutatorException e) {}
    }
    
    public void testCanApplyMultipleMutationsEasily() throws Exception {
        Map<String, Object> newProps = new HashMap<String, Object>();
        newProps.put("name", "John");
        newProps.put("age", 36);
        newProps.put("accountBalance", 3050);
        
        BeanPropertyController c = BeanPropertyController.of(traditionalBean);
        
        c.mutate(newProps);
        
        assertEquals("John", traditionalBean.getName());
        assertEquals(36, traditionalBean.getAge());
        assertEquals(3050d, traditionalBean.getAccountBalance());
        
        c.mutate("name", "Jill").mutate("age", 31).mutate("accountBalance", 32301295);
        
        assertEquals("Jill", traditionalBean.getName());
        assertEquals(31, traditionalBean.getAge());
        assertEquals(32301295d, traditionalBean.getAccountBalance());
    }
    
    public void testAccessesOnlyPublicMethods() throws Exception {
        bpc = BeanPropertyController.of(HiddenBean.class);
        String[] properties = bpc.getPropertyNames();
        
        assertEquals(0, properties.length);
    }
    
    public void testCanTransparentlyCreateAnObjectFromClassAndControlItAsIfItWasANormalObject() throws Exception {
        bpc = BeanPropertyController.of(SingleValueBean.class, ExtractionDepth.METHODS);
        
        assertNull(bpc.access("value"));
        bpc.mutate("value", "newValue");
        assertEquals("newValue", bpc.access("value"));
    }
    
    public void testCanTransparentlyCreateAnObjectWithArbitraryConstructorFromClassAndControlItAsIfItWasANormalObject() throws Exception {
        bpc = BeanPropertyController.of(BooleanClass.class, InstantiationPolicy.NICE);
        assertEquals(false, bpc.access("boo"));
        
        bpc = BeanPropertyController.of(SomeClass.class, ExtractionDepth.FIELDS, InstantiationPolicy.NICE);
        assertEquals("", bpc.access("visible"));
        
        bpc = BeanPropertyController.of(IntegerBean.class, InstantiationPolicy.NICE);
        assertEquals(0, bpc.access("integer"));
    }
    
    public void testUsesTheConstructorWithLeastAmountOfParametersWhenInstantiating() throws Exception {
        bpc = BeanPropertyController.of(MultipleConstructorsBean.class, InstantiationPolicy.NICE);
        assertEquals("single-arg", bpc.access("instantiatedWith"));
    }
    
    public void testThrowsExceptionIfNoArgsConstructorIsFound() throws Exception {
        try {
            bpc = BeanPropertyController.of(MultipleConstructorsBean.class);
            fail("Should've thrown Exception!");
        } catch (BeanInstantiationException e) {
            assertEquals("Failed to instantiate given class :: [java.lang.InstantiationException: reflection.bpc.testbeans.MultipleConstructorsBean]", e.getMessage());
            assertEquals(1, e.getExceptions().size());
        }
    }
    
    public void testChangesActiveBeansProperties() throws Exception {
        assertEquals(null, traditionalBean.getName());
        bpc = BeanPropertyController.of(traditionalBean);
        bpc.mutate("name", "Tsohn");
        assertEquals("Tsohn", traditionalBean.getName());
    }

    public void testIsSerializableIfControlledObjectIsSerializable() throws Exception {
        SerializableBean bean = new SerializableBean();
        bean.setSerial("something else");
        bpc = BeanPropertyController.of(bean);
        
        byte[] objectAsBytes = serialize(bpc);
        BeanPropertyController bpc = deserialize(objectAsBytes);
        
        assertEquals(bean, bpc.getObject());
    }
    
    public void testIsSerializableIfControlledClassIsSerializable() throws Exception {
        bpc = BeanPropertyController.of(SerializableBean.class);
        bpc.mutate("serial", "original");
        SerializableBean original = (SerializableBean) bpc.getObject();
        
        byte[] objectAsBytes = serialize(bpc);
        BeanPropertyController bpc = deserialize(objectAsBytes);
        
        assertEquals(original, bpc.getObject());
    }
    
    private BeanPropertyController deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(is);
        return (BeanPropertyController) in.readObject();
    }

    private byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(o);
        System.out.println("Serialized to "+os.toByteArray().length+" bytes");
        return os.toByteArray();
    }
    // TODO: Test JVM restart support
    
    public void testCanBeUsedAsObjectFactory() throws Exception {
        bpc = BeanPropertyController.of(TraditionalBean.class);
        List<TraditionalBean> listOfBeans = new ArrayList<TraditionalBean>();
        
        for (int i=0; i<10; i++) {
            bpc.mutate("age", i);
            TraditionalBean bean = (TraditionalBean) bpc.getObject();
            listOfBeans.add(bean);
            assertEquals(i, bean.getAge());
            bpc.recycle();
        }
        assertEquals(10, listOfBeans.size());
        
        // ages were set correctly
        for (int i=0; i<listOfBeans.size(); i++) {
            assertEquals(i, listOfBeans.get(i).getAge());
        }
        
        // all items are unique
        List<TraditionalBean> checked = new ArrayList<TraditionalBean>();
        for (TraditionalBean bean : listOfBeans) {
            for (TraditionalBean checkedBean : checked) {
                assertNotSame(bean, checkedBean);
            }
            checked.add(bean);
        }
    }
}
