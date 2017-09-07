package tdanford.ideals.numerics;

import java.util.function.BiPredicate;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import tdanford.ideals.Field;
import tdanford.ideals.Polynomial;

public class RootFinder<K, F extends Field<K>> {

  private static Logger LOG = LoggerFactory.getLogger(RootFinder.class);

  private final Polynomial<K, F> poly;
  private final Polynomial<K, F> derivative;

  public RootFinder(final Polynomial<K, F> poly, final Function<Integer, K> intLift) {
    Preconditions.checkArgument(poly != null, "Polynomial cannot be null");
    Preconditions.checkArgument(poly.getPolyRing().variables().length == 1,
      "Can only find roots of single-variable polynomial equations");

    this.poly = poly;
    final String var = poly.getPolyRing().variables()[0];
    this.derivative = poly.partialDerivative(var, intLift);

    LOG.info("Newton-Raphson: POLY={} DERIVATIVE={}", poly, derivative);
  }

  @SuppressWarnings("unchecked")
  public K newtonRaphson(final K start, final BiPredicate<K, K> close, final int iters) {

    K current = start;
    K prev ;
    F field = poly.getPolyRing().coefficientField();
    int i = 0;

    do {
      LOG.info(String.format("Newton-Raphson %d: %s", i, current));

      K fValue = poly.evaluate(current);
      K fPrimeValue = derivative.evaluate(current);
      K ratio = field.divide(fValue, fPrimeValue);

      LOG.info(String.format("VALUE=%s, DERIVATIVE_VALUE=%s, RATIO=%s", fValue, fPrimeValue, ratio));

      prev = current;
      current = field.sum(current, field.negative(ratio));
      i += 1;

    } while (i < iters && !close.test(current, prev));

    if (i >= iters && !close.test(current, prev)) {
      LOG.warn("Newton-Raphson root finder exceeded specified number of iterations %d", iters);
    }

    return current;
  }
}
