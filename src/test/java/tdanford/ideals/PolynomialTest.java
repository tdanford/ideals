package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import nl.jqno.equalsverifier.EqualsVerifier;

public class PolynomialTest extends PolynomialTesting {

  public static Term term(final Rational coeff, final Monomial m) {
    return new Term<>(Rationals.FIELD, m, coeff);
  }

  public static Rational ratio(final int numer, final int denom) {
    return new Rational(numer, denom);
  }

  @Test
  public void testPartialDerivative() {
    assertThat(kxyPoly("xy + 2x^2").partialDerivative("x", Rationals::liftInteger))
      .isEqualTo(kxyPoly("y + 4x"));
    assertThat(kxyPoly("xy + 2x^2").partialDerivative("y", Rationals::liftInteger))
      .isEqualTo(kxyPoly("x"));
  }

  @Test
  public void testEquality() {
    EqualsVerifier.forClass(Polynomial.class)
      .withNonnullFields("terms", "sorted")
      .withIgnoredFields("polyRing")
      .verify();

    assertThat(kxyPoly("y^10 - 49/48y^7 + 1/8y^4 - 3/16y"))
      .isEqualTo(kxyPoly("y^10 - 49/48y^7 + 1/8y^4 - 3/16y"));

    Set<Polynomial<Rational, Rationals>> list = new HashSet<>(Collections.singletonList(kxyPoly("y^10 - 49/48y^7 + 1/8y^4 - 3/16y")));
    list.remove(kxyPoly("y^10 - 49/48y^7 + 1/8y^4 - 3/16y"));
    assertThat(list).isEmpty();
  }

  @Test
  public void testMultipliedBy() {
    assertThat(kxPoly("2x^2 - 1").multipliedBy(kxPoly("3x + 3")))
      .isEqualTo(kxPoly("6x^3 + 6x^2 -3x - 3"));
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
