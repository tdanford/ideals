package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import static tdanford.ideals.MonomialOrdering.LEX;
import org.junit.Test;

public class GroebnerBasisTest extends PolynomialTesting {

  @Test
  public void testLinearExamples() {
    final String[] vars = new String[] {"x", "y", "p"};

    final PolynomialRing<Rational, Rationals> ring = new PolynomialRing<>(LEX, Rationals.FIELD, vars);

    final PolynomialSet<Rational, Rationals> constraints =
      rationalPolys(vars, "x + y - 2", "2x + p", "2y + p");

    final GroebnerBasis<Rational, Rationals, PolynomialRing<Rational, Rationals>> basis =
      new GroebnerBasis<>(ring, constraints);

    assertThat(basis.getBasis()).containsExactlyInAnyOrder(
      rationalPolys(vars, "p+2", "y-1", "x-1").toArray()
    );
  }

  @Test
  public void testExampleUAG17() {
    final PolynomialSet<Rational, Rationals> F = kxyPolys(
      "x^3y - 2x^2y^2 + x",
      "3x^4 - y"
    );

    final GroebnerBasis<Rational, Rationals, PolynomialRing<Rational, Rationals>> basis =
      new GroebnerBasis<>(KXY, F);

    assertThat(basis.getBasis()).containsExactlyInAnyOrder(
      kxyPolys(
        "-9y + 48y^10 - 49y^7 + 6y^4",
        "252x - 624y^7 + 493y^4 - 3y"
      ).toArray()
    );

    final PolynomialRing<Double, Reals> DXY = new PolynomialRing<>(LEX, Reals.FIELD, "x", "y");

    for (Polynomial<Rational, Rationals> p : basis.getBasis()) {
      System.out.println(p.convert(DXY, Reals::fromRational));
    }
  }
}
