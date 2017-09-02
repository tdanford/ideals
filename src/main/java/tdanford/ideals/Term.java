package tdanford.ideals;

public class Term<K, F extends Ring<K, K>> {

  private final Monomial monomial;
  private final K coefficient;
  private final F field;

  public Term(final F field, final Monomial m, final K coeff) {
    this.field = field;
    this.monomial = m;
    this.coefficient = coeff;
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
