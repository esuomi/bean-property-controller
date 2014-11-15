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

import java.lang.reflect.Array;

public enum NiceValueProvider {
    
    INSTANCE;
    
    private NiceValueProvider() {}

    public Object getNiceValueFor(Class<?> c) {
        if (c.isArray()) {
            return Array.newInstance(c, 0);
        }
        else if (c.equals(String.class)) {
            return "";
        } else if (Number.class.isAssignableFrom(c) || // TODO: Test this 
                   c.equals(int.class) || 
                   c.equals(double.class) ||
                   c.equals(float.class)) {
            return 0;
        } else if (c.equals(Boolean.class) || c.equals(boolean.class)) {
            return Boolean.FALSE;
        } else {
            System.out.println("Unidentified type: "+c);
        }
        // TODO: More types.
        return null;
    }
}
