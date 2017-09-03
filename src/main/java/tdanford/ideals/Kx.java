package tdanford.ideals;

import java.util.Collections;
import java.util.stream.StreamSupport;

/**
 * A single-variable polynomial ring over 'x', i.e. k[x] for some field k
 */
public class Kx<K, F extends Field<K>> extends PolynomialRing<K, F> {

  public Kx(final F field) {
    super(MonomialOrdering.LEX, field, "x");
  }

  public Polynomial<K, F> gcd(final Iterable<Polynomial<K, F>> polys) {
    return StreamSupport.stream(polys.spliterator(), false)
      .reduce(this::gcd)
      .orElse(null);
  }

  public Polynomial<K, F> gcd(final Polynomial<K, F> f, final Polynomial<K, F> g) {
    Polynomial<K, F> h = f, s = g;

    while (!s.isZero()) {
      //System.out.println(String.format("h=%s, s=%s", h, s));
      final DivisorRemainder<Polynomial<K, F>> divRem = div(h, s);
      //System.out.println(String.format("\tdivisor=%s, remainder=%s", divRem.divisor, divRem.remainder));
      h = s;
      s = divRem.remainder;
    }

    final K lc = h.leadingCoefficient();

    return h.scaleBy(coefficientField().reciprocal(lc));
  }

  public DivisorRemainder<Polynomial<K, F>> div(final Polynomial<K, F> f, final Polynomial<K, F> g) {
    Polynomial<K, F> q = zero(), r = f;
    while (!r.isZero() && g.leadingTerm().divides(r.leadingTerm())) {
      final Polynomial<K, F> divided = lift(r.leadingTerm().dividedBy(g.leadingTerm()));
      q = sum(q, divided);
      r = subtract(r, product(divided, g));
    }

    return new DivisorRemainder<>(q, r);
  }

  public Polynomial<K, F> subtract(final Polynomial<K, F> p1, final Polynomial<K, F> p2) {
    return sum(p1, negative(p2));
  }

  private Polynomial<K, F> lift(final Term<K, F> m) {
    return new Polynomial<>(this, m);
  }
}
