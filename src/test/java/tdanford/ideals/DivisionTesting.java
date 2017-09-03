package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class DivisionTesting extends PolynomialTesting {

  @Test
  public void testExampleIVA44() {
    final Polynomial<Rational, Rationals> f = kxPoly("x^3 + 4x^2 + 3x - 7"),
      g = kxPoly("x - 1");

    final DivisorRemainder<Polynomial<Rational, Rationals>> dr = KX.div(f, g);

    assertThat(dr).isNotNull();

    assertThat(dr.divisor).isEqualTo(kxPoly("x^2 + 5x + 8"));
    assertThat(dr.remainder).isEqualTo(kxPoly("1"));
  }
}
