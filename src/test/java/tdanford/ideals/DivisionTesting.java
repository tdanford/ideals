package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class DivisionTesting extends PolynomialTesting {

  /**
   * Example test of single-variable (k[x]) polynomial division
   */
  @Test
  public void testExampleIVA44() {
    final Polynomial<Rational, Rationals> f = kxPoly("x^3 + 4x^2 + 3x - 7"),
      g = kxPoly("x - 1");

    final DivisorsRemainder<Polynomial<Rational, Rationals>> dr = KX.div(f, g);

    assertThat(dr).isNotNull();

    assertThat(dr.divisors[0]).isEqualTo(kxPoly("x^2 + 5x + 8"));
    assertThat(dr.remainder).isEqualTo(kxPoly("1"));
  }

  /**
   * Example test of multi-variable (k[x, y]) polynomial division
   *
   * (pg. 64 of I, V, & A)
   */
  @Test
  public void testExampleIVA64() {
    assertThat(
      KXY.div(
        kxyPoly("x^2y + xy^2 + y^2"),
        kxyPoly("y^2 - 1"),   // f1
        kxyPoly("xy - 1")     // f2
      )
    ).isEqualTo(
      new DivisorsRemainder<>(
        kxyPoly("2x + 1"),   // remainder
        kxyPoly("x + 1"),    // coefficient of f1
        kxyPoly("x")         // coefficient of f2
      )
    );
  }
}
