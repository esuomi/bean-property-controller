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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodMutator<T> implements IMutator<T> {

    private final Method m;

    public MethodMutator(Method m) {
        this.m = m;
    }

    public void mutate(Object object, T newValue) {
        try {
            m.invoke(object, newValue);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Class<?> getType() {
        return m.getParameterTypes()[0];
    }
    
    
    @Override
    public String toString() {
        return "Method mutator";
    }

}
