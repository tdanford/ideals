package tdanford.ideals;

public interface Field<T> extends Ring<T> {

    T reciprocal(T value);
}

