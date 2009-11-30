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
package reflection.bpc.testbeans;

public class MultipleConstructorsBean {
    
    private String instantiatedWith;
     
    public MultipleConstructorsBean(String plop, String plap, String plip) {
        setInstantiatedWith("three-arg");
    }
    
    public MultipleConstructorsBean(String plop, String plap) {
        setInstantiatedWith("two-arg");
    }
    
    public MultipleConstructorsBean(String plop) {
        setInstantiatedWith("single-arg");
    }
    
    public void setInstantiatedWith(String instantiatedWith) {
        this.instantiatedWith = instantiatedWith;
    }
    
    public String getInstantiatedWith() {
        return instantiatedWith;
    }

}
