package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class PolynomialTest extends PolynomialTesting {

  public static Term term(final Rational coeff, final Monomial m) {
    return new Term<>(Rationals.FIELD, m, coeff);
  }

  public static Rational ratio(final int numer, final int denom) {
    return new Rational(numer, denom);
  }

  @Test
  public void testLeadingTerm() {
    assertThat(kxPoly("2x^2 + x + 1").leadingTerm())
      .isEqualTo(term(ratio(2, 1), new Monomial(1, 0, 2)));
  }

  @Test
  public void testSPolynomialIVA81() {
    final Polynomial<Rational, Rationals> f = kxyPoly("x^3y^2 - x^2y^3 + x"),
      g = kxyPoly("3x^4y + y^2");

    assertThat(f.sPolynomial(g)).isEqualTo(
      kxyPoly("-1 x^3y^3 + x^2 -1/3 y^3")
    );
  }
}
