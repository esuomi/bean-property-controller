package reflection.bpc;

import java.io.Serializable;

public interface IAccessor<T> extends Serializable {
    T access(Object object);

    Class<?> getReturnType();
}
