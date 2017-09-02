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

  private static Pattern EXPONENTIATED = Pattern.compile("^([a-z]+)(\\^\\d*)");

  public Monomial parseMonomial(final String str) {
    int start = 0;
    Matcher m = EXPONENTIATED.matcher(str);
    final Map<String, Integer> exps = new TreeMap<>();
    while (m.find(start)) {
      start = m.end();
      final int exponent = m.group(2).length() > 0 ? Integer.parseInt(m.group(2).substring(1)) : 1;
      exps.put(m.group(1), exponent);
    }

    final int[] array = new int[vars.length];
    for (int i = 0; i < array.length; i++) {
      array[i] = exps.containsKey(vars[i]) ? exps.get(vars[i]) : 0;
    }
    return new Monomial(array);
  }

  public MonomialOrdering getOrdering() {
    return ordering;
  }

  public F coefficientField() { return coefficientField; }

  public String[] variables() { return vars; }

  public int variableIndex(final String var) {
    for (int i = 0; i < vars.length; i++) {
      if (vars[i].equals(var)) {
        return i;
      }
    }
    return -1;
  }

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
