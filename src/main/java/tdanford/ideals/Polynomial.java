package tdanford.ideals;

import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Polynomial<K, F extends Ring<K, K>> {

  private final PolynomialRing<K, F> polyRing;
  private final Map<Monomial, K> terms;
  private final ArrayList<Monomial> sorted;

  public Polynomial(
    final PolynomialRing<K, F> polyRing,
    final Map<Monomial, K> terms
  ) {
    this.polyRing = polyRing;
    final K zero = polyRing.coefficientField().zero();
    this.terms = terms.entrySet().stream()
      .filter(e -> !e.getValue().equals(zero))
      .collect(toMap(
        Map.Entry::getKey, Map.Entry::getValue
      ));

    this.sorted = new ArrayList<>(terms.keySet());
    Collections.sort(this.sorted, polyRing.getOrdering());
  }

  public Term<K, F> leadingTerm() {
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

    for (final Map.Entry<Monomial, K> entry : terms.entrySet()) {
      newTerms.put(entry.getKey(), field.product(entry.getValue(), scalar));
    }

    return new Polynomial<>(polyRing, newTerms);
  }

  public boolean isZero() {
    return terms.isEmpty();
  }
}



