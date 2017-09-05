package tdanford.ideals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
  private final boolean reducingFinalBasis;

  public GroebnerBasis(final PR polyRing, final Iterable<Polynomial<K, F>> polys) {

    this.polyRing = polyRing;
    spec = Lists.immutable.ofAll(polys);
    basis = Lists.mutable.empty();
    reducingFinalBasis = false;
    calculateBasis();
  }

  public Iterable<Polynomial<K, F>> getBasis() { return basis; }

  private void calculateBasis() {

    final ArrayList<Polynomial<K, F>> building = new ArrayList<>(spec.castToList());
    int start = 0;

    Set<Polynomial<K, F>> toAdjoin = new HashSet<>();
    do {
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
    final Set<Term<K, F>> lts = buildingSet.leadingTerms();

    basis.clear();
    if (reducingFinalBasis) {
      System.out.println("Reducing final set...");
      basis.addAllIterable(buildingSet.filter(new ReducedPolynomialPredicate<>(lts)));
    } else {
      basis.addAllIterable(buildingSet);
    }
    scaleLeadingCoefficientsToOne();

    System.out.println(String.format("Final basis: %s", basis));

  }

  private void scaleLeadingCoefficientsToOne() {
    basis.replaceAll(Polynomial::scaleToOne);
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
