package tdanford.ideals;

public interface Ring<T, DivResult> {

  T[] array(int length);

  T product(T p1, T p2);

  T pow(T value, int k);

  T sum(T a1, T a2);

  T negative(T value);

  T zero();

  T one();

  boolean divides(T n, T d);

  DivResult divide(T numer, T denom);
}

