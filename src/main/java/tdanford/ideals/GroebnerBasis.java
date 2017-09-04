package tdanford.ideals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public class GroebnerBasis<K, F extends Ring<K, K>, PR extends PolynomialRing<K, F>> {

  private final PR polyRing;
  private final ImmutableList<Polynomial<K, F>> spec;
  private final MutableList<Polynomial<K, F>> basis;

  public GroebnerBasis(final PR polyRing, final Iterable<Polynomial<K, F>> polys) {

    this.polyRing = polyRing;
    spec = Lists.immutable.ofAll(polys);
    basis = Lists.mutable.empty();
    calculateBasis();
  }

  public Iterable<Polynomial<K, F>> getBasis() { return basis; }

  private void calculateBasis() {

    final ArrayList<Polynomial<K, F>> building = new ArrayList<>(spec.castToList());

    List<Polynomial<K, F>> toAdjoin = new ArrayList<>();
    do {
      building.addAll(toAdjoin);
      toAdjoin.clear();

      System.out.println(String.format("Building Set: %s", building));

      for (int i = 0; i < building.size(); i++) {
        for (int j = 0; j < building.size(); j++) {
          if (i != j) {
            final Polynomial<K, F> sPoly = building.get(i).sPolynomial(building.get(j));
            final Polynomial<K, F> sRem = polyRing.div(sPoly, building).remainder;

            if (!sRem.isZero()) {
              toAdjoin.add(sRem);
            }
          }
        }
      }

      System.out.println(String.format("To-adjoin set: %s", toAdjoin));

    } while (!toAdjoin.isEmpty());

    final PolynomialSet<K, F> buildingSet = new PolynomialSet<>(building);
    final Set<Term<K, F>> lts = buildingSet.leadingTerms();

    basis.clear();
    basis.addAllIterable(buildingSet.filter(new ReducedPolynomialPredicate<>(lts)));

    //scaleLeadingCoefficientsToOne();
  }

  private void scaleLeadingCoefficientsToOne() {
    basis.replaceAll(this::scaleLeadingCoeffToOne);
  }

  private Polynomial<K, F> scaleLeadingCoeffToOne(final Polynomial<K, F> poly) {
    final K leadingCoeff = poly.leadingCoefficient();
    return polyRing.div(poly,
      new Polynomial<>(
        polyRing,
        leadingCoeff,
        new Monomial(polyRing.variables().length)
      )
    ).divisors[0];
  }

}

class ReducedPolynomialPredicate<K, F extends Ring<K, K>> implements Predicate<Polynomial<K, F>> {

  private Iterable<Term<K, F>> terms;

  public ReducedPolynomialPredicate(final Iterable<Term<K, F>> terms) {
    this.terms = terms;
  }

  public boolean isDividedByTerm(final Term<K, F> term) {
    for (Term<K, F> other : terms) {
      if (other.divides(term)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean accept(final Polynomial<K, F> p) {
    return !p.anyTermMatches(this::isDividedByTerm);
  }
}
