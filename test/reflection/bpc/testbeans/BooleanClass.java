package reflection.bpc.testbeans;

public class BooleanClass {

    private boolean boo;

    public BooleanClass(boolean b) {
        this.boo = b;
    }
    
    public boolean isBoo() {
        return boo;
    }
    
    public void setBoo(boolean boo) {
        this.boo = boo;
    }

}
