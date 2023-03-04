package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import org.junit.Test;

public class GCDTesting extends PolynomialTesting {

  /**
   * Taken from pg. 42 of IV&A
   */
  @Test
  public void testExampleIVA42() {
    final Kx<Rational, Rationals> kx = new Kx<>(Rationals.FIELD);
    final Polynomial<Rational, Rationals> p1 = kxPoly("x^4 - 1");
    final Polynomial<Rational, Rationals> p2 = kxPoly("x^6 - 1");

    final Polynomial<Rational, Rationals> gcd = kx.gcd(p1, p2);
    assertThat(gcd).isEqualTo(kxPoly("x^2 - 1"));
  }

  @Test
  public void testExampleIVA43() {
    final Kx<Rational, Rationals> kx = new Kx<>(Rationals.FIELD);
    final Polynomial<Rational, Rationals> p1 = kxPoly("x^3 - 3x + 2"),
      p2 = kxPoly("x^4 - 1"),
      p3 = kxPoly("x^6 - 1");

    //System.out.println("p1: " + p1);
    //System.out.println(p1.getTerms().stream().map(t -> t.renderString(kx.variables())).collect(toList()));

    assertThat(kx.gcd(p1, kxPoly("x^2 - 1"))).isEqualTo(kxPoly("x - 1"));

    final Polynomial<Rational, Rationals> gcd = kx.gcd(Arrays.asList(p1, p2, p3));
    //assertThat(gcd).isEqualTo(kxPoly("x - 1"));
  }
}
