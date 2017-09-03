package tdanford.ideals;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolynomialRing<C, F extends Ring<C, C>> implements
  Ring<Polynomial<C, F>, DivisorRemainder<Polynomial<C, F>>> {

  private final MonomialOrdering ordering;
  private final F coefficientField;
  private final String[] vars;

  public PolynomialRing(
    final MonomialOrdering ordering,
    final F coefficientField,
    final String... vars
  ) {
    this.coefficientField = coefficientField;
    this.ordering = ordering;
    this.vars = vars;
  }

  public MonomialOrdering getOrdering() {
    return ordering;
  }

  public F coefficientField() { return coefficientField; }

  public String[] variables() { return vars; }

  @Override
  public Polynomial<C, F>[] array(final int length) {
    return new Polynomial[length];
  }

  @Override
  public Polynomial<C, F> product(final Polynomial<C, F> p1, final Polynomial<C, F> p2) {
    return p1.multipliedBy(p2);
  }

  @Override
  public Polynomial<C, F> sum(final Polynomial<C, F> a1, final Polynomial<C, F> a2) {
    return a1.addedTo(a2);
  }

  @Override
  public Polynomial<C, F> negative(final Polynomial<C, F> value) {
    return value.scaleBy(coefficientField.negative(coefficientField.one()));
  }

  @Override
  public Polynomial<C, F> zero() {
    return new Polynomial<>(this, Collections.emptyMap());
  }

  @Override
  public Polynomial<C, F> one() {
    return new Polynomial<>(
      this,
      Collections.singletonMap(Monomial.zero(vars.length), coefficientField.one())
    );
  }

  @Override
  public boolean divides(final Polynomial<C, F> n, final Polynomial<C, F> d) {
    return true;
  }

  @Override
  public DivisorRemainder<Polynomial<C, F>> divide(final Polynomial<C, F> numer, final Polynomial<C, F> denom) {
    throw new UnsupportedOperationException("can't divide yet");
  }

}
