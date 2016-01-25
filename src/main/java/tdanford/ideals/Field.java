package tdanford.ideals;

/**
 * A Ring with multiplicative inverses.
 * @param <T>
 */
public interface Field<T> extends Ring<T> {

    T reciprocal(T value);
}

