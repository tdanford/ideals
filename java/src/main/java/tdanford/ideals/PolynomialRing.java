package tdanford.ideals;

import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.eclipse.collections.impl.factory.Maps;

import com.google.common.base.Preconditions;

public class PolynomialRing<C, F extends Ring<C, C>> implements
  Ring<Polynomial<C, F>, DivisorsRemainder<Polynomial<C, F>>> {

  private final MonomialOrdering ordering;
  private final F coefficientField;
  private final String[] vars;
  private final Map<String, Integer> varIndexMap;

  public PolynomialRing(
    final MonomialOrdering ordering,
    final F coefficientField,
    final String... vars
  ) {
    this.coefficientField = coefficientField;
    this.ordering = ordering;
    this.vars = vars;
    this.varIndexMap = IntStream.range(0, vars.length).boxed().collect(toMap(
      i -> vars[i], i -> i
    ));
  }

  public MonomialOrdering getOrdering() {
    return ordering;
  }

  public F coefficientField() { return coefficientField; }

  public String[] variables() { return vars; }

  public PolynomialRing<C, F> adjoin(final String... newVars) {
    final String[] array = new String[vars.length + newVars.length];
    for (int i = 0; i < array.length; i++) {
      if (i < newVars.length) {
        array[i] = newVars[i];
      } else {
        array[i] = vars[i - newVars.length];
      }
    }
    return new PolynomialRing<>(ordering, coefficientField, array);
  }

  public DivisorsRemainder<Polynomial<C, F>> div(final Polynomial<C, F> f, final Iterable<Polynomial<C, F>> fs) {
    return div(f, StreamSupport.stream(fs.spliterator(), false).toArray(Polynomial[]::new));
  }

  public DivisorsRemainder<Polynomial<C, F>> div(final Polynomial<C, F> f, final Polynomial<C, F> ... fs) {
    final Polynomial<C, F> zero = new Polynomial<>(this, Maps.mutable.empty());
    Polynomial<C, F>[] as = new Polynomial[fs.length];
    for (int i = 0; i < as.length; i++) { as[i] = zero; }

    Polynomial<C, F> r = zero;
    Polynomial<C, F> p = f, prevP = zero;

    while (!p.isZero() && !p.equals(prevP)) {
      prevP = p;
      //System.out.println(String.format("r=%s, p=%s", r, p));
      int i = 0;
      boolean divisionOccurred = false;
      while (i < as.length && !divisionOccurred) {
        Term<C, F> ltp = p.leadingTerm();
        if (fs[i].leadingTerm().divides(ltp)) {
          final Polynomial<C, F> ratio = lift(ltp.dividedBy(fs[i].leadingTerm()));
          as[i] = sum(as[i], ratio);
          p = subtract(p, product(ratio, fs[i]));
          divisionOccurred = true;
        } else {
          i += 1;
        }
      }

      if (!divisionOccurred) {
        r = sum(r, lift(p.leadingTerm()));
        p = subtract(p, lift(p.leadingTerm()));
      }
    }

    return new DivisorsRemainder<>(r, as);
  }

  public Polynomial<C, F> subtract(final Polynomial<C, F> p1, final Polynomial<C, F> p2) {
    return sum(p1, negative(p2));
  }

  private Polynomial<C, F> lift(final Term<C, F> term) { return new Polynomial<>(this, term); }

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
    Preconditions.checkArgument(a1 != null, "Cannot add a null polynomial");
    Preconditions.checkArgument(a2 != null, "Cannot add a null polynomial");
    return a1.addedTo(a2);
  }

  @Override
  public Polynomial<C, F> negative(final Polynomial<C, F> value) {
    return value.scaleBy(coefficientField.negative(coefficientField.one()));
  }

  @Override
  public Polynomial<C, F> pow(final Polynomial<C, F> p, final int k) {
    return IntStream.range(0, k).mapToObj(i -> p).reduce(one(), this::product);
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
  public DivisorsRemainder<Polynomial<C, F>> divide(final Polynomial<C, F> numer, final Polynomial<C, F> denom) {
    return div(numer, denom);
  }

  public int indexOf(final String var) {
    for (int i = 0; i < vars.length; i++) {
      if (vars[i].equals(var)) {
        return i;
      }
    }
    return -1;
  }

  public int varIdx(final String s) {
    return varIndexMap.get(s);
  }
}
