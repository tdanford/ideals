package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class GroebnerBasisTest extends PolynomialTesting {

  @Test
  public void testExampleUAG17() {
    final PolynomialSet<Rational, Rationals> F = kxPolys(
      "x^3y - 2x^2y^2 + x",
      "3x^4 - y"
    );

    final GroebnerBasis<Rational, Rationals, PolynomialRing<Rational, Rationals>> basis =
      new GroebnerBasis<>(KXY, F);

    assertThat(basis.getBasis()).containsExactlyElementsOf(
      kxPolys(
        "-9y + 48y^10 - 49y^7 + 6y^4",
        "252x - 624y^7 + 493y^4 - 3y"
      )
    );
  }
}
