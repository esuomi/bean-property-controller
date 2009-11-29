package reflection.bpc.testbeans;

public class VaryingParametersBean {
    
    private String value;
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public void setValue(String value, Object other) {
        throw new RuntimeException("Just for the sake of it");
    }

    public String getValue() {
        return value;
    }

    public String getValue(int anything) {
        throw new RuntimeException("Just for the sake of it");
    }

}
