package tdanford.ideals;

import static java.util.stream.Collectors.toList;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    final ArrayList<Polynomial<K, F>> building = new ArrayList<>(spec.castToList());

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

    final PolynomialSet<K, F> buildingSet = new PolynomialSet<>(building);

    basis.clear();
    basis.addAllIterable(buildingSet);

    scaleLeadingCoefficientsToOne();

    System.out.println(String.format("Intermediate basis: %s", basis));
    System.out.println(String.format("No dups basis: %s", new HashSet<>(basis)));

    final Iterable<Polynomial<K, F>> cleared = clearDenominators(new HashSet<>(basis));

    basis.clear();
    basis.addAllIterable(cleared);

    if (reducingFinalBasis) {
      System.out.println("Reducing final set...");

      int reduce = -1;
      final List<Term<K, F>> leadingTerms = basis.collect(Polynomial::leadingTerm);
      final List<String> leadingTermStrings = leadingTerms.stream().map(t -> t.renderString(polyRing.variables())).collect(toList());
      System.out.println(String.format("Leading terms: %s", leadingTermStrings));

      while ((reduce = findReduciblePolynomial(basis)) != -1) {
        basis.remove(reduce);
      }
    }

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
      return poly.scaleBy((K) new Rational(lcm, BigInteger.ONE));

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

