package reflection.bpc.testbeans;

public class QuestimationBean {
    
    private String onlySetter;
    private String onlyGetter;
    
    public QuestimationBean(String onlySetter, String onlyGetter) {
        this.onlySetter = onlySetter;
        this.onlyGetter = onlyGetter;
    }

    public String getOnlyGetter() {
        return onlyGetter;
    }
    
    public void setOnlySetter(String onlySetter) {
        this.onlySetter = onlySetter;
    }

}
