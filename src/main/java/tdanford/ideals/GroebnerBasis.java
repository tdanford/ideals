package tdanford.ideals;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import com.google.common.base.Preconditions;

public class GroebnerBasis<K, F extends Ring<K, K>, PR extends PolynomialRing<K, F>> {

  private final PR polyRing;
  private final ImmutableList<Polynomial<K, F>> spec;
  private final MutableList<Polynomial<K, F>> basis;
  private final boolean reducingFinalBasis;

  public GroebnerBasis(final PR polyRing, final Iterable<Polynomial<K, F>> polys) {

    this.polyRing = polyRing;
    spec = Lists.immutable.ofAll(polys);
    basis = Lists.mutable.empty();
    reducingFinalBasis = true;
    calculateBasis();
  }

  public Iterable<Polynomial<K, F>> getBasis() { return basis; }

  private void calculateBasis() {

    final ArrayList<Polynomial<K, F>> building =
      new ArrayList<>(spec.castToList().stream().filter(p -> !p.isZero()).collect(toList()));

    if ((new HashSet<>(building)).size() != building.size()) {
      throw new IllegalArgumentException(String.format("Building set %s contains duplicates", building));
    }

    int start = 0;

    Set<Polynomial<K, F>> toAdjoin = new HashSet<>();
    do {
      toAdjoin.removeAll(building);
      building.addAll(toAdjoin);
      start += toAdjoin.size();
      toAdjoin.clear();

      System.out.println(String.format("%d: Building Set (%d): %s", start, building.size(), building));

      for (int i = start; i < building.size(); i++) {
        for (int j = 0; j < i; j++) {

          final Polynomial<K, F> sPoly = building.get(i).sPolynomial(building.get(j));
          final Polynomial<K, F> sRem = polyRing.div(sPoly, building).remainder;

          if (!sRem.isZero()) {
            System.out.println(String.format("\t%d,%d: %s", i, j, sPoly));
            System.out.println(String.format("\t\t-> %s", sRem));
            toAdjoin.add(sRem);
          }
        }
      }

      System.out.println(String.format("\tTo-adjoin set (%d): %s", toAdjoin.size(), toAdjoin));

    } while (!toAdjoin.isEmpty());

    final PolynomialSet<K, F> buildingSet = new PolynomialSet<>(polyRing, building);

    basis.clear();
    basis.addAllIterable(buildingSet);

    System.out.println(String.format("Intermediate basis: %s", basis));

    final Iterable<Polynomial<K, F>> noDups = new HashSet<>(basis);

    basis.clear();
    basis.addAllIterable(noDups);

    System.out.println(String.format("No dups basis: %s", basis));

    if (reducingFinalBasis) {
      System.out.println("Reducing final set...");

      final List<Term<K, F>> leadingTerms = basis.collect(Polynomial::leadingTerm);
      final List<String> leadingTermStrings = leadingTerms.stream().map(t -> t.renderString(polyRing.variables())).collect(toList());
      System.out.println(String.format("Leading terms: %s", leadingTermStrings));

      /*
      final List<Integer> reducibleSet = findAllReduciblePolynomials(basis);
      System.out.println(String.format("Reducible set: %s", reducibleSet));
      final List<Polynomial<K, F>> reduced = IntStream.range(0, basis.size())
        .filter(i -> !reducibleSet.contains(i))
        .mapToObj(basis::get)
        .collect(toList());

      basis.clear();
      basis.addAllIterable(reduced);
      */

      /*
      int reduce = -1;
      while ((reduce = findReduciblePolynomial(basis)) != -1) {
        System.out.println(String.format("Removing poly %d (= %s) from basis", reduce, basis.get(reduce)));
        basis.remove(reduce);
        System.out.println(String.format("Reducing basis: %s", basis));
      }
      */
      reduceBasis(basis);
    }

    scaleLeadingCoefficientsToOne();

    final Iterable<Polynomial<K, F>> cleared = clearDenominators(basis);
    basis.clear();
    basis.addAllIterable(cleared);

    System.out.println(String.format("Final basis: %s", basis));

  }

  private Iterable<Polynomial<K, F>> clearDenominators(final Collection<Polynomial<K, F>> polys) {
    Preconditions.checkArgument(polys.size() > 0);
    return polys.stream()
      .map(this::clearDenominatorsOnPolynomial)
      .collect(toList());
  }

  private Polynomial<K, F> clearDenominatorsOnPolynomial(final Polynomial<K, F> poly) {
    //System.out.println(String.format("Clearing: %s", poly));
    if (polyRing.coefficientField() == Rationals.FIELD) {
      //System.out.println(String.format("\trational poly: %s", poly));

      @SuppressWarnings("unchecked") final List<Rational> rationalCoeffs = (List<Rational>) poly.getCoefficients();
      final BigInteger lcm = rationalCoeffs.stream()
        .map(Rational::getDenominator).reduce(BigInteger.ONE, Rational::lcm);

      //System.out.println("\tLCM: " + lcm);
      final Polynomial<K, F> up = poly.scaleBy((K) new Rational(lcm, BigInteger.ONE));

      /*
      @SuppressWarnings("unchecked") final List<Rational> upCeoffs = (List<Rational>) up.getCoefficients();
      final BigInteger gcd = rationalCoeffs.stream()
        .map(Rational::getNumerator).reduce(BigInteger.ONE, Rational::gcd);

      return poly.scaleBy((K) new Rational(BigInteger.ONE, gcd));
      */

      return up;

    } else {
      //System.out.println(String.format("\tNon-rational poly: %s", poly));
      return poly;
    }
  }

  private void scaleLeadingCoefficientsToOne() {
    basis.replaceAll(Polynomial::scaleToOne);
  }

  private int findReduciblePolynomial(final MutableList<Polynomial<K, F>> basis) {
    for (int i = 0; i < basis.size(); i++) {
      if (isReduciblePolynomial(basis.get(i), basis)) {
        return i;
      }
    }
    return -1;
  }

  private boolean canReduce(final Polynomial<K, F> targetPoly, final Polynomial<K, F> basisPoly) {
    return !basisPoly.isZero() && targetPoly.anyTermMatches(
      targetTerm -> basisPoly.leadingMonomial().divides(targetTerm.getMonomial())
    );
  }

  private void reduceBasis(final MutableList<Polynomial<K, F>> basis) {
    boolean didReduce;
    do {
      System.out.println(String.format("*** REDUCING %s", basis));
      didReduce = false;
      for (int i = 0; i < basis.size(); i++) {
        System.out.println(String.format("REDUCING POLY %d: %s", i, basis));
        final Polynomial<K, F> target = basis.get(i);
        final List<Polynomial<K, F>> rest = basis.stream().filter(p -> !p.equals(target)).collect(toList());
        final Polynomial<K, F> reduced = reduce(target, rest);
        didReduce = didReduce || !reduced.equals(target);
        basis.set(i, reduced);
      }

      basis.removeIf(Polynomial::isZero);
      System.out.println(String.format("DID_REDUCE: %s", didReduce));
    } while (didReduce);
  }

  private Polynomial<K, F> reduce(final Polynomial<K, F> targetPoly, final List<Polynomial<K, F>> basis) {
    return basis.stream().reduce(targetPoly, this::reduce);
  }

  private Polynomial<K, F> reduce(final Polynomial<K, F> targetPoly, final Polynomial<K, F> basisPoly) {
    System.out.println(String.format("\tReducing %s by %s", targetPoly, basisPoly));
    Polynomial<K, F> reduced = targetPoly;
    while (canReduce(reduced, basisPoly)) {
      final Ring<K, K> coeffRing = polyRing.coefficientField();

      final Term<K, F> basisLT = basisPoly.leadingTerm();
      System.out.println(String.format("\t\tbasisLT: %s", basisLT.renderString(polyRing.variables())));

      final List<Term<K, F>> divisible = reduced.getTerms().stream()
        .filter(tterm -> basisLT.getMonomial().divides(tterm.getMonomial()))
        .collect(toList());

      final Term<K, F> divTerm = divisible.get(0);
      System.out.println(String.format("\t\tdivisible: %s", divisible.stream().map(t -> t.renderString(polyRing.variables())).collect(joining(", "))));

      final Polynomial<K, F> scalar = polyRing.divide(reduced, basisPoly).divisors[0];
      final Polynomial<K, F> reducer = basisPoly.multipliedBy(scalar);

      //final K scalar = coeffRing.divide(divTerm.getCoefficient(), basisLT.getCoefficient());
      //final Polynomial<K, F> reducer = basisPoly.scaleBy(scalar);

      System.out.println(String.format("\t\tscalar %s -> reducer %s", scalar, reducer));
      reduced = polyRing.subtract(reduced, reducer);

      System.out.println(String.format("\t\t%s reduces %s -> %s", reducer, targetPoly, reduced));
    }
    return reduced;
  }

  private boolean isReduciblePolynomial(
    final Polynomial<K, F> poly,
    final MutableList<Polynomial<K, F>> basis
  ) {
    final Set<Term<K, F>> leadingTerms = basis.collectIf(
      p -> !p.equals(poly),
      Polynomial::leadingTerm
    ).toSet();

    boolean match = poly.anyTermMatches(t -> leadingTerms.stream().anyMatch(lt -> lt.divides(t)));

    //final String ltString = leadingTerms.stream().map(t -> t.renderString(polyRing.variables())).collect(joining(", "));
    //System.out.println(String.format("%s: %s \n\tin LTS %s", match, poly, ltString));

    return match;
  }

}

