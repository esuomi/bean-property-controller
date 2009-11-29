package reflection.bpc;

public class NonexistentPropertyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NonexistentPropertyException(String propertyName, Class c) {
        super("Property '"+propertyName+"' doesn't exist for the specified class "+c.getName());
    }

}
