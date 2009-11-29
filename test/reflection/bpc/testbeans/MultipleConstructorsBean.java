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
