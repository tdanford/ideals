package tdanford.ideals;

/**
 *
 * @param <T> The type of the value in this Ring
 * @param <DivResult> The type of the result of division in the Ring (this isn't the same as T since
 *                   the Ring might not be a Field)
 */
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

