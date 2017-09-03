package tdanford.ideals;

import java.util.Objects;
import com.google.common.base.Preconditions;

public class Term<K, F extends Ring<K, K>> {

  private final Monomial monomial;
  private final K coefficient;
  private final F field;

  public Term(final F field, final Monomial m, final K coeff) {
    Preconditions.checkArgument(field != null, "Field cannot be null");
    Preconditions.checkArgument(m != null, "Monomial cannot be null");
    Preconditions.checkArgument(coeff != null, "Coefficient cannot be null");

    this.field = field;
    this.monomial = m;
    this.coefficient = coeff;
  }

  public String renderString(final String[] variables) {
    return String.format("%s%s", coefficient, monomial.renderString(variables));
  }

  public int hashCode() {
    return Objects.hash(coefficient, monomial);
  }

  public boolean equals(final Object o) {
    if (!(o instanceof Term)) { return false; }
    Term<K, F> t = (Term<K, F>) o;
    return Objects.equals(coefficient, t.coefficient) &&
      Objects.equals(monomial, t.monomial);
  }

  public K getCoefficient() { return coefficient; }

  public Monomial getMonomial() { return monomial; }

  public boolean divides(final Term<K, F> t) {
    return field.divides(coefficient, t.coefficient) && monomial.divides(t.monomial);
  }

  public Term<K, F> dividedBy(final Term<K, F> t) {
    return new Term<>(
      field,
      monomial.dividedBy(t.monomial),
      field.divide(coefficient, t.coefficient)
    );
  }
}
