package tdanford.ideals;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;

import com.google.common.base.Preconditions;

public class Polynomial<K, F extends Ring<K, K>> {

  private final PolynomialRing<K, F> polyRing;
  private final K[] terms;
  private final Monomial[] sorted;

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

    final ImmutableMap<Monomial, K> tempMap = Maps.immutable.ofMap(terms.entrySet().stream()
      .filter(e -> !e.getValue().equals(zero))
      .collect(toMap(
        Map.Entry::getKey, Map.Entry::getValue
      )));

    final MutableList<Monomial> toSort = Lists.mutable.ofAll(tempMap.keysView());
    toSort.sort(polyRing.getOrdering());

    this.sorted = toSort.toArray(new Monomial[toSort.size()]);
    this.terms = polyRing.coefficientField().array(this.sorted.length);

    for (int i = 0; i < sorted.length; i++) {
      this.terms[i] = tempMap.get(sorted[i]);
    }

    checkInvariants();
  }

  public <K2, F2 extends Ring<K2, K2>> Polynomial<K2, F2> convert(
    final PolynomialRing<K2, F2> newPolyRing,
    final Function<K, K2> coeffConverter
  ) {
    final Map<Monomial, K2> newTerms = IntStream.range(0, sorted.length)
      .boxed()
      .collect(toMap(i -> sorted[i], i -> coeffConverter.apply(terms[i])));

    return new Polynomial<>(newPolyRing, newTerms);
  }

  public List<Term<K, F>> getTerms() {
    return IntStream.range(0, sorted.length)
      .mapToObj(i -> new Term<>(polyRing.coefficientField(), sorted[i], terms[i]))
      .collect(toList());
  }

  public Map<Monomial, K> getTermMap() {
    return getTermMap(polyRing);
  }

  public Map<Monomial, K> getTermMap(final PolynomialRing<K, F> ring) {
    return IntStream.range(0, sorted.length).boxed()
      .collect(toMap(
        i -> sorted[i].changeVars(polyRing.variables(), ring.variables()),
        i -> terms[i]
      ));
  }

  public String toString() {
    return renderString("");
  }

  public String renderString(final String multiplier) {
    checkInvariants();

    final K one = polyRing.coefficientField().one();
    final String str = IntStream.range(0, sorted.length).mapToObj(
      i -> !terms[i].equals(one) ?
        String.format("%s%s%s", terms[i], multiplier, sorted[i].renderString(polyRing.variables(),
          multiplier)) :
        (sorted[i].isZero()
          ? "1"
          : String.valueOf(sorted[i].renderString(polyRing.variables(), multiplier))))
      .reduce((a, b) -> {
        if (b.startsWith("-")) {
          return String.format("%s - %s", a, b.substring(1));
        } else {
          return String.format("%s + %s", a, b);
        }
        })
      .orElse("0");

    if (str.equals("0")) {
      Preconditions.checkState(sorted.length == 0);
      Preconditions.checkState(isZero());
    }

    return str;
  }

  public final int hashCode() {
    return 37 * (IntStream.range(0, sorted.length)
      .map(i -> 37 * (Objects.hash(sorted[i]) + Objects.hash(terms[i])))
      .sum());
  }

  public final boolean equals(final Object o) {
    if (!(o instanceof Polynomial)) { return false; }
    final Polynomial<K, F> p = (Polynomial<K, F>) o;
    return Arrays.deepEquals(sorted, p.sorted) &&
      Arrays.deepEquals(terms, p.terms);
  }

  private void checkInvariants() {
    Preconditions.checkState(sorted.length == terms.length,
      "Sorted monomial list and term map must have same size");
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
    return sorted[0];
  }

  private boolean monomialContainsOnlyVariables(final Monomial m, final Set<Integer> varIndices) {
    return IntStream.range(0, m.width())
      .filter(i -> !varIndices.contains(i))
      .noneMatch(i -> m.exponent(i) > 0);
  }

  public boolean containsOnlyVariables(final String... vars) {
    final Set<Integer> inds = Stream.of(vars).map(polyRing::varIdx).collect(Collectors.toSet());
    return Stream.of(sorted).allMatch(m -> monomialContainsOnlyVariables(m, inds));
  }

  public Term<K, F> leadingTerm() {
    checkInvariants();
    Preconditions.checkState(sorted.length > 0, "Cannot find leading term of empty polynomial");
    return new Term<>(polyRing.coefficientField(), sorted[0], terms[0]);
  }

  public K evaluate(K... values) {
    F field = polyRing.coefficientField();
    K sum = field.zero();
    for (int i = 0; i < sorted.length; i++) {
      final K term = field.product(terms[i], sorted[i].evaluate(values, field));
      sum = polyRing.coefficientField().sum(sum, term);
    }
    return sum;
  }

  public Polynomial<K, F> addedTo(final Polynomial<K, F> p) {
    final F field = polyRing.coefficientField();
    final Map<Monomial, K> newTerms = new HashMap<>();

    for (int i = 0; i < sorted.length; i++) {
      newTerms.put(sorted[i], terms[i]);
    }

    for (int i = 0; i < p.sorted.length; i++) {
      if (newTerms.containsKey(p.sorted[i])) {
        newTerms.put(p.sorted[i], field.sum(newTerms.get(p.sorted[i]), p.terms[i]));
      } else {
        newTerms.put(p.sorted[i], p.terms[i]);
      }
    }

    return new Polynomial<>(polyRing, newTerms);
  }

  public Polynomial<K, F> multipliedBy(final Polynomial<K, F> p) {
    final F field = polyRing.coefficientField();
    final Map<Monomial, K> newTerms = new HashMap<>();

    for (int i1 = 0; i1 < sorted.length; i1++) {
      final Monomial m1 = sorted[i1];

      for (int i2 = 0; i2 < p.sorted.length; i2++) {
        final Monomial m2 = p.sorted[i2];

        final Monomial m12 = m1.multipliedBy(m2);
        final K coeff = field.product(terms[i1], p.terms[i2]);

        if (newTerms.containsKey(m12)) {
          newTerms.put(m12, field.sum(newTerms.get(m12), coeff));
        } else {
          newTerms.put(m12, coeff);
        }
      }
    }

    return new Polynomial<>(polyRing, newTerms);
  }

  public List<K> getCoefficients() {
    return Stream.of(terms).collect(toList());
  }

  public Polynomial<K, F> partialDerivative(final String var, final Function<Integer, K> exponentLift) {
    final Map<Monomial, K> newTerms = Maps.mutable.empty();
    final Ring<K, K> cRing = polyRing.coefficientField();
    final int vidx = polyRing.indexOf(var);

    for (int i = 0; i < sorted.length; i++) {
      final int exponent = sorted[i].exponent(vidx);

      if (exponent > 0) {
        final K newCoeff = cRing.product(terms[i], exponentLift.apply(exponent));
        final Monomial newMonomial = sorted[i].oneLess(vidx);

        //System.out.println(String.format("%s -> %s", newMonomial.renderString(polyRing.variables()), newCoeff));

        if (newTerms.containsKey(newMonomial)) {
          newTerms.put(newMonomial, cRing.sum(newTerms.get(newMonomial), newCoeff));
        } else {
          newTerms.put(newMonomial, newCoeff);
        }
      }
    }

    return new Polynomial<>(polyRing, newTerms);
  }

  public Polynomial<K, F> scaleToOne() {
    final K leadingCoefficient = leadingCoefficient();
    final F ring = polyRing.coefficientField();
    final K scalar = ring.divide(ring.one(), leadingCoefficient);
    return scaleBy(scalar);
  }

  public Polynomial<K,F> scaleBy(final K scalar) {
    final F field = polyRing.coefficientField();
    final Map<Monomial, K> newTerms = new HashMap<>();

    for (int i = 0; i < terms.length; i++) {
      newTerms.put(sorted[i], field.product(terms[i], scalar));
    }

    return new Polynomial<>(polyRing, newTerms);
  }

  public boolean isZero() {
    return sorted.length == 0;
  }

  public K leadingCoefficient() {
    return terms[0];
  }

  public boolean anyTermMatches(final Predicate<Term<K, F>> pred) {
    return IntStream.range(0, sorted.length).anyMatch(i -> pred.accept(
      new Term<>(polyRing.coefficientField(), sorted[i], terms[i])
    ));
  }

  public List<Monomial> getMonomials() {
    return Stream.of(sorted).collect(toList());
  }

  public Polynomial<K, F> changeRing(final PolynomialRing<K, F> newRing) {
    return new Polynomial<>(newRing, getTermMap(newRing));
  }

  public PolynomialRing<K,F> getPolyRing() {
    return polyRing;
  }

  public PolynomialSet<K, F> allPartials(final Function<Integer, K> liftInteger) {
    return new PolynomialSet<>(polyRing,
      Stream.of(polyRing.variables()).map(v -> this.partialDerivative(v, liftInteger)).collect(toList())
      );
  }
}



