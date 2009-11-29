package reflection.bpc.testbeans;

public class HiddenBean {

    
    private String completelyHidden;

    private String getCompletelyHidden() {
        return completelyHidden;
    }
    
    private void setCompletelyHidden(String completelyHidden) {
        this.completelyHidden = completelyHidden;
    }
}
