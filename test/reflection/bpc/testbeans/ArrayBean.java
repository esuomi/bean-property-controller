package reflection.bpc.testbeans;

public class ArrayBean {

    private double[] values;

    public ArrayBean(double[] values) {
        this.values = values;
    }
    
    public double[] getValues() {
        return values;
    }
    public void setValues(double[] values) {
        this.values = values;
    }

}
