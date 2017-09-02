package tdanford.ideals;

import java.util.Collections;

/**
 * A single-variable polynomial ring over 'x', i.e. k[x] for some field k
 */
public class Kx<K, F extends Field<K>> extends PolynomialRing<K, F> {

  public Kx(final F field) {
    super(MonomialOrdering.LEX, field, "x");
  }

  public Polynomial<K, F> gcd(final Polynomial<K, F> f, final Polynomial<K, F> g) {
    Polynomial<K, F> h = f, s = g;

    while (!s.isZero()) {
      final DivisorRemainder<Polynomial<K, F>> divRem = div(h, s);
      h = s;
      s = divRem.remainder;
    }

    return h;
  }

  public DivisorRemainder<Polynomial<K, F>> div(final Polynomial<K, F> f, final Polynomial<K, F> g) {
    Polynomial<K, F> q = zero(), r = f;
    while (!r.isZero() && g.leadingTerm().divides(r.leadingTerm())) {
      final Polynomial<K, F> divided = lift(r.leadingTerm().dividedBy(g.leadingTerm()));
      q = sum(q, divided);
      r = sum(r, negative(product(divided, g)));
    }

    return new DivisorRemainder<>(q, r);
  }

  private Polynomial<K, F> lift(final Term<K, F> m) {
    return new Polynomial<>(
      this,
      Collections.singletonMap(m.getMonomial(), m.getCoefficient())
    );
  }
}
