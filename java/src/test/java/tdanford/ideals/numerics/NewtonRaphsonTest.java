package tdanford.ideals.numerics;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import tdanford.ideals.Polynomial;
import tdanford.ideals.PolynomialTesting;
import tdanford.ideals.Rational;
import tdanford.ideals.Rationals;

public class NewtonRaphsonTest extends PolynomialTesting {

  /**
   * Example taken from
   * http://www3.ul.ie/~mlc/support/CompMaths2/files/NewtonExample.pdf
   */
  @Test
  public void testNewtonRaphsonExample() {

    final Polynomial<Rational, Rationals> poly = kxPoly("x^3 - x - 1");

    final RootFinder<Rational, Rationals> roots = new RootFinder<>(poly, Rationals::liftInteger);

    final Rational eps = new Rational(1, 100000L);

    final Rational root = roots.newtonRaphson(
      new Rational(3, 2),
      (r1, r2) -> r1.sum(r2.negative()).abs().lessThan(eps),
      1000
      );

    assertThat(root.doubleValue()).isEqualTo(1.3247179572447898);
  }
}
