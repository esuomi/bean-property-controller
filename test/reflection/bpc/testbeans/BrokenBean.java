package reflection.bpc.testbeans;

public class BrokenBean {
    
    private String valid;
    private String invalid;
    
    public void setValid(String valid) {
        this.valid = valid;
    }
    
    public Object getValid() {
        return valid;
    }
    
    public void setInvalid(Object invalid) {
        this.invalid = invalid.toString();
    }
    
    public String getInvalid() {
        return invalid;
    }

}
