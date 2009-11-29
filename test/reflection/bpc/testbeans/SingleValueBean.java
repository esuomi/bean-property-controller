package reflection.bpc.testbeans;

public class SingleValueBean<Type> {
    
    private Type value;
    
    public SingleValueBean() {}
    
    public SingleValueBean(Type value) {
        this.value = value;
    }

    public void setValue(Type value) {
        this.value = value;
    }
    
    public Type getValue() {
        return value;
    }

}
