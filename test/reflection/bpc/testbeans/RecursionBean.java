package reflection.bpc.testbeans;

public class RecursionBean {
    
    private SingleValueBean bean;
    
    public RecursionBean(SingleValueBean bean) {
        this.bean = bean;
    }
    
    public SingleValueBean getBean() {
        return bean;
    }
    
    public void setBean(SingleValueBean bean) {
        this.bean = bean;
    }

}
