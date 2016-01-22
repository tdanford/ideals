package tdanford.ideals;

public interface Ring<T> {

    T[] array(int length);

    T product(T p1, T p2);
    T sum(T a1, T a2);

    T negative(T value);

    T zero();
    T one();
}

