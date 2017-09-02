package tdanford.ideals;

/**
 * A Ring with multiplicative inverses.
 *
 * @param <T>
 */
public interface Field<T> extends Ring<T, T> {

  T reciprocal(T value);

  default boolean divides(final T numer, final T denom) { return true; }

  default T divide(final T numer, final T denom) {
    return product(numer, reciprocal(denom));
  }
}

