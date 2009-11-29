package reflection.bpc;

public interface IMutator<T> {
    void mutate(Object object, T newValue);

    Class<?> getType();
}
