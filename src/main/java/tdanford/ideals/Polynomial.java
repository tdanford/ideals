package tdanford.ideals;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import com.google.common.base.Preconditions;

public class Polynomial<K, F extends Ring<K, K>> {

  private final PolynomialRing<K, F> polyRing;
  private final ImmutableMap<Monomial, K> terms;
  private final ImmutableList<Monomial> sorted;

  public Polynomial(
    final PolynomialRing<K, F> polyRing,
    final K constant
  ) {
    this(polyRing, constant, new Monomial(polyRing.variables().length));
    checkInvariants();
  }

  public Polynomial(
    final PolynomialRing<K, F> polyRing,
    final Term<K, F> term
  ) {
    this(polyRing, term.getCoefficient(), term.getMonomial());
    checkInvariants();
  }

  public Polynomial(
    final PolynomialRing<K, F> polyRing,
    final K coefficient,
    final Monomial monomial
  ) {
    this(polyRing, Collections.singletonMap(monomial, coefficient));
    checkInvariants();
  }

  public Polynomial(
    final PolynomialRing<K, F> polyRing,
    final Map<Monomial, K> terms
  ) {
    this.polyRing = polyRing;
    final K zero = polyRing.coefficientField().zero();
    this.terms = Maps.immutable.ofMap(terms.entrySet().stream()
      .filter(e -> !e.getValue().equals(zero))
      .collect(toMap(
        Map.Entry::getKey, Map.Entry::getValue
      )));

    final MutableList<Monomial> toSort = Lists.mutable.ofAll(this.terms.keysView());
    toSort.sort(polyRing.getOrdering());

    this.sorted = toSort.toImmutable();

    checkInvariants();
  }

  public List<Term<K, F>> getTerms() {
    return sorted.castToList().stream()
      .map(m -> new Term<>(polyRing.coefficientField(), m, terms.get(m)))
      .collect(toList());
  }

  public String toString() {
    final K one = polyRing.coefficientField().one();
    //if (sorted.isEmpty()) { return "0"; }
    return String.format("%s", sorted.collect(
      m -> !terms.get(m).equals(one) ?
        String.format("%s%s", terms.get(m), m.renderString(polyRing.variables())) :
        (m.isZero() ? "1" : String.valueOf(m.renderString(polyRing.variables()))))
      .toList().stream()
      .reduce((a, b) -> {
        if (b.startsWith("-")) {
          return String.format("%s - %s", a, b.substring(1));
        } else {
          return String.format("%s + %s", a, b);
        }
        })
      .orElse("0")
    );
  }

  public int hashCode() {
    return 37 * (
      17 + (int) terms.collectInt(
        e -> 37 * (Objects.hash(e) + Objects.hash(terms.get(e)))
      ).toList().sum()
    );
  }

  public boolean equals(final Object o) {
    if (!(o instanceof Polynomial)) { return false; }
    final Polynomial<K, F> p = (Polynomial<K, F>) o;
    if (sorted.size() != p.sorted.size()) { return false; }
    for (Monomial m : sorted) {
      if (!p.terms.containsKey(m) || !p.terms.get(m).equals(terms.get(m))) {
        return false;
      }
    }
    return true;
  }

  private void checkInvariants() {
    Preconditions.checkState(sorted.size() == terms.size(),
      "Sorted monomial list and term map must have same size");
    Preconditions.checkState(sorted.allSatisfy(terms::containsKey),
      "Term map must contain all monomials in sorted list");
  }

  public Polynomial<K, F> sPolynomial(final Polynomial<K, F> g) {
    final Polynomial<K, F> f = this;
    final Term<K, F> fLT = f.leadingTerm();
    final Term<K, F> gLT = g.leadingTerm();

    final Monomial fgLCM = f.leadingMonomial().lcm(g.leadingMonomial());
    final K one = polyRing.coefficientField().one();
    final Term<K, F> xGamma = new Term<>(polyRing.coefficientField(), fgLCM, one);

    return polyRing.subtract(
      polyRing.product(
        new Polynomial<>(polyRing, xGamma.dividedBy(fLT)),
        f
      ),
      polyRing.product(
        new Polynomial<>(polyRing, xGamma.dividedBy(gLT)),
        g
      )
    );
  }

  public Monomial leadingMonomial() {
    return sorted.get(0);
  }

  public Term<K, F> leadingTerm() {
    checkInvariants();
    Preconditions.checkState(!sorted.isEmpty(), "Cannot find leading term of empty polynomial");
    Preconditions.checkState(terms.containsKey(sorted.get(0)), "Term map must contain leading monomial");
    return new Term<>(polyRing.coefficientField(), sorted.get(0), terms.get(sorted.get(0)));
  }

  public K evaluate(K[] values) {
    K sum = polyRing.coefficientField().zero();
    for (Monomial m : sorted) {
      sum = polyRing.coefficientField().sum(sum, m.evaluate(values, polyRing.coefficientField()));
    }
    return sum;
  }

  public Polynomial<K, F> addedTo(final Polynomial<K, F> p) {
    final F field = polyRing.coefficientField();
    final Map<Monomial, K> newTerms = new HashMap<>();

    for (final Monomial m : sorted) {

      if (p.terms.containsKey(m)) {
        newTerms.put(m, field.sum(terms.get(m), p.terms.get(m)));
      } else {
        newTerms.put(m, terms.get(m));
      }
    }

    for (final Monomial m : p.sorted) {
      if (!terms.containsKey(m)) {
        newTerms.put(m, p.terms.get(m));
      }
    }

    return new Polynomial<>(polyRing, newTerms);
  }

  public Polynomial<K, F> multipliedBy(final Polynomial<K, F> p) {
    final F field = polyRing.coefficientField();
    final Map<Monomial, K> newTerms = new HashMap<>();

    for (final Monomial m1 : sorted) {
      for (final Monomial m2 : p.sorted) {

        final Monomial m12 = m1.multipliedBy(m2);
        final K coeff = field.product(terms.get(m1), p.terms.get(m2));

        if (newTerms.containsKey(m12)) {
          newTerms.put(m12, field.sum(newTerms.get(m12), coeff));
        } else {
          newTerms.put(m12, coeff);
        }
      }
    }

    return new Polynomial<>(polyRing, newTerms);
  }

  public Polynomial<K,F> scaleBy(final K scalar) {
    final F field = polyRing.coefficientField();
    final Map<Monomial, K> newTerms = new HashMap<>();

    for (final Map.Entry<Monomial, K> entry : terms.toMap().entrySet()) {
      newTerms.put(entry.getKey(), field.product(entry.getValue(), scalar));
    }

    return new Polynomial<>(polyRing, newTerms);
  }

  public boolean isZero() {
    return terms.isEmpty();
  }

  public K leadingCoefficient() {
    return terms.get(sorted.get(0));
  }
}



