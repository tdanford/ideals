package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class LagrangianTest extends PolynomialTesting {

  @Test
  public void testSimpleLagrangianProblem() {
    final PolynomialSet<Rational, Rationals> constraints = kxyPolys("x + y - 2");

    final Polynomial<Rational, Rationals> lagrangian = constraints.lagrangian(kxyPoly("x^2 + y^2"));

    System.out.println(String.format("Lagrangian: %s", lagrangian));

    final PolynomialRing<Rational, Rationals> lagrangianRing = lagrangian.getPolyRing();

    final PolynomialSet<Rational, Rationals> derivatives = lagrangian.allPartials(Rationals::liftInteger);

    System.out.println("Derivatives:");
    for (Polynomial<Rational, Rationals> p : derivatives) {
      System.out.println(String.format("\t%s", p));
    }

    final GroebnerBasis<Rational, Rationals, PolynomialRing<Rational, Rationals>> basis =
      new GroebnerBasis<>(lagrangian.getPolyRing(), derivatives);

    assertThat(basis.getBasis()).containsExactlyInAnyOrder(
      rationalPolys(lagrangianRing.variables(), "p0+2", "y-1", "x-1").toArray()
    );

  }
}
