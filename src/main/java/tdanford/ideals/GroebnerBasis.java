package tdanford.ideals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public class GroebnerBasis<K, F extends Ring<K, K>, PR extends PolynomialRing<K, F>> {

  private final PR polyRing;
  private final ImmutableList<Polynomial<K, F>> spec;
  private final MutableList<Polynomial<K, F>> basis;

  public GroebnerBasis(final PR polyRing, final Collection<Polynomial<K, F>> polys) {

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

    } while (!toAdjoin.isEmpty());

    basis.clear();
    basis.addAll(building);
  }

}
