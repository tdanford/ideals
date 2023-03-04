package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;

import org.junit.Test;

public class Macaulay2Test extends PolynomialTesting {

  @Test
  public void testPolynomialParsing() {
    final String p1 = "48y10-49y7+6y4-9y";
    final String p2 = "252x-624y7+493y4-3y";

    final BiFunction<String, Boolean, Rational> parser =
      (str, negative) -> negative ? Rationals.parse(str).negative() : Rationals.parse(str);

    final Macaulay2<Rational, Rationals, PolynomialRing<Rational, Rationals>> mac =
      new Macaulay2<>(KXY, parser);

    final Polynomial<Rational, Rationals> parsed1 = mac.parseMacaulayPolynomial(p1, KXY.variables
      ());

    assertThat(parsed1).isEqualTo(kxyPoly("48y^10 - 49y^7 + 6y^4 -9y"));
  }
}
