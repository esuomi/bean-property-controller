package reflection.bpc.instantiation;

public interface IClassInstantiator {
    
    <C> C instantiate(Class<C> c);

}
