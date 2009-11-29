package reflection.bpc;

import java.util.ArrayList;
import java.util.List;

public class BeanInstantiationException extends RuntimeException {

    private final List<Throwable> exceptions;

    public BeanInstantiationException(String message, List<Throwable> exceptions) {
        super(message);
        this.exceptions = exceptions;
    }
    
    public BeanInstantiationException(String message, Throwable t) {
        super(message, t);
        this.exceptions = new ArrayList<Throwable>();
        exceptions.add(t);
    }

    public List<Throwable> getExceptions() {
        return exceptions;
    }

}
