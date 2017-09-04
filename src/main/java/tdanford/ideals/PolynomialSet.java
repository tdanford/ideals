package tdanford.ideals;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;

public class PolynomialSet<K, F extends Ring<K, K>>
  implements Iterable<Polynomial<K, F>> {

  private final ImmutableList<Polynomial<K, F>> polys;

  public PolynomialSet(final Iterable<Polynomial<K, F>> polyIter) {
    polys = Lists.immutable.ofAll(polyIter);
  }

  public boolean anyMatches(final Predicate<? super Polynomial<K, F>> pred) {
    return polys.anySatisfy(pred);
  }

  public <T> Stream<T> map(final Function<Polynomial<K, F>, T> f) {
    return polys.collect(f).castToList().stream();
  }

  public PolynomialSet<K, F> filter(final Predicate<? super Polynomial<K, F>> pred) {
    return new PolynomialSet<>(polys.collectIf(pred, i->i));
  }

  public Set<Term<K, F>> leadingTerms() {
    return Sets.mutable.ofAll(polys.collect(Polynomial::leadingTerm));
  }

  @Override
  public Iterator<Polynomial<K, F>> iterator() {
    return polys.iterator();
  }
}

