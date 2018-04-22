package tdanford.ideals;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;

public class PolynomialSet<K, F extends Ring<K, K>>
  implements Iterable<Polynomial<K, F>> {

  private final PolynomialRing<K, F> ring;
  private final ImmutableList<Polynomial<K, F>> polys;

  public PolynomialSet(final PolynomialRing<K, F> ring, final Iterable<Polynomial<K, F>> polyIter) {
    this.ring = ring;
    this.polys = Lists.immutable.ofAll(polyIter);
  }

  public boolean anyMatches(final Predicate<? super Polynomial<K, F>> pred) {
    return polys.anySatisfy(pred);
  }

  public <T> Stream<T> map(final Function<Polynomial<K, F>, T> f) {
    return polys.collect(f).castToList().stream();
  }

  public PolynomialSet<K, F> filter(final Predicate<? super Polynomial<K, F>> pred) {
    return new PolynomialSet<>(ring, polys.collectIf(pred, i->i));
  }

  public Set<Term<K, F>> leadingTerms() {
    return Sets.mutable.ofAll(polys.collect(Polynomial::leadingTerm));
  }

  public Polynomial<K, F> lagrangian(final Polynomial<K, F> objective) {
    final String[] newVars = new String[polys.size()];
    for (int i = 0; i < newVars.length; i++) {
      newVars[i] = String.format("p%d", i);
    }

    final PolynomialRing<K, F> newRing = ring.adjoin(newVars);
    final F coeffField = newRing.coefficientField();
    final K one = coeffField.one();

    return IntStream.range(0, polys.size())
      .mapToObj(i -> new Polynomial<>(newRing, new Term<>(coeffField, new Monomial(newRing.variables().length, i, 1), one))
        .multipliedBy(polys.get(i).changeRing(newRing)))
      .reduce(objective.changeRing(newRing), Polynomial::addedTo);
  }

  @Override
  public Iterator<Polynomial<K, F>> iterator() {
    return polys.iterator();
  }

  public Polynomial<K, F> get(final int i) {
    return polys.get(i);
  }

  public Polynomial<K, F>[] toArray() {
    return polys.toArray(new Polynomial[polys.size()]);
  }

  public String[] variables() {
    return ring.variables();
  }
}

