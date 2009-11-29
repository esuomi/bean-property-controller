package reflection.bpc;

import java.lang.reflect.Constructor;
import java.util.Comparator;

public final class ConstructorComparator {

    public static final Comparator<? super Constructor<?>> PARAMETER_COUNT = new Comparator<Constructor<?>>() {
        public int compare(Constructor<?> left, Constructor<?> right) {
            return left.getParameterTypes().length - right.getParameterTypes().length;
        }                
    };

}
