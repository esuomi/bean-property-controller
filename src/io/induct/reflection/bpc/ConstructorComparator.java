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

import java.lang.reflect.Constructor;
import java.util.Comparator;

public final class ConstructorComparator {

    public static final Comparator<? super Constructor<?>> PARAMETER_COUNT = new Comparator<Constructor<?>>() {
        public int compare(Constructor<?> left, Constructor<?> right) {
            return left.getParameterTypes().length - right.getParameterTypes().length;
        }                
    };

}
