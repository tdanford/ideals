package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import static tdanford.ideals.MonomialOrdering.LEX;
import org.junit.Test;

public class LagrangianTest extends PolynomialTesting {

  @Test
  public void testSimpleLagrangianProblem() {
    final PolynomialSet<Rational, Rationals> constraints = kxyPolys("x + y - 2");

    final Polynomial<Rational, Rationals> lagrangian = constraints.lagrangian(kxyPoly("x^2 + y^2"));

    System.out.println(String.format("Lagrangian: %s", lagrangian));

    final PolynomialRing<Rational, Rationals> lagrangianRing = lagrangian.getPolyRing();

    final PolynomialSet<Rational, Rationals> derivatives = lagrangian.allPartials(Rationals::liftInteger);

    final GroebnerBasis<Rational, Rationals, PolynomialRing<Rational, Rationals>> basis =
      new GroebnerBasis<>(lagrangian.getPolyRing(), derivatives);

    assertThat(basis.getBasis()).containsExactlyInAnyOrder(
      rationalPolys(lagrangianRing.variables(), "p0+2", "y-1", "x-1").toArray()
    );

  }

  @Test
  public void testMultipleConstraintsLagrangian() {

    final PolynomialRing<Rational, Rationals> XYZ = new PolynomialRing<>(LEX, Rationals.FIELD, "x", "y", "z");
    final PolynomialSet<Rational, Rationals> constraints =
      rationalPolys(XYZ.variables(),"x + y - 2", "x + z - 2");

    final Polynomial<Rational, Rationals> lagrangian = constraints.lagrangian(rationalPoly(XYZ.variables(), "x^2 + y^2 + z^2"));

    System.out.println(String.format("Lagrangian: %s", lagrangian));

    final PolynomialRing<Rational, Rationals> lagrangianRing = lagrangian.getPolyRing();

    final PolynomialSet<Rational, Rationals> derivatives = lagrangian.allPartials(Rationals::liftInteger);

    final GroebnerBasis<Rational, Rationals, PolynomialRing<Rational, Rationals>> basis =
      new GroebnerBasis<>(lagrangian.getPolyRing(), derivatives);

    assertThat(basis.getBasis()).containsExactlyInAnyOrder(
      rationalPolys(lagrangianRing.variables(), "3p1 + 4", "3y - 2", "3x - 4", "3p0 + 4", "3z - 2").toArray()
    );

  }
}
