package reflection.bpc;

import java.lang.reflect.Array;

public enum NiceValueProvider {
    
    INSTANCE;
    
    private NiceValueProvider() {}

    public Object getNiceValueFor(Class<?> c) {
        if (c.isArray()) {
            return Array.newInstance(c, 0);
        }
        else if (c.equals(String.class)) {
            return "";
        } else if (Number.class.isAssignableFrom(c) || // TODO: Test this 
                   c.equals(int.class) || 
                   c.equals(double.class) ||
                   c.equals(float.class)) {
            return 0;
        } else if (c.equals(Boolean.class) || c.equals(boolean.class)) {
            return Boolean.FALSE;
        } else {
            System.out.println("Unidentified type: "+c);
        }
        // TODO: More types.
        return null;
    }
}
