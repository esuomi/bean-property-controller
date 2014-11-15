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

public class FieldAccessMutateControl<T> implements IMutator<T>, IAccessor<T> {

    private final Field f;

    public FieldAccessMutateControl(Field f) {
        this.f = f;
    }

    public Class<?> getType() {
        return f.getType();
    }

    public void mutate(Object object, T newValue) {
        try {
            f.set(object, newValue);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public T access(Object object) {
        try {
            return (T) f.get(object);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> getReturnType() {
        return f.getType();
    }
    
    @Override
    public String toString() {
        return "Field Accessor/Mutator";
    }

}
