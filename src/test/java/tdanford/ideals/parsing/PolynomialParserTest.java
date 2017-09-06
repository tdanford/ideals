package tdanford.ideals.parsing;

import static org.assertj.core.api.Assertions.assertThat;
import static tdanford.ideals.MonomialOrdering.LEX;
import org.eclipse.collections.impl.factory.Maps;
import org.junit.Test;
import tdanford.ideals.Kx;
import tdanford.ideals.Monomial;
import tdanford.ideals.Polynomial;
import tdanford.ideals.PolynomialRing;
import tdanford.ideals.Rational;
import tdanford.ideals.Rationals;

public class PolynomialParserTest {

  @Test
  public void testMultivariablePolynomials() {
    final PolynomialRing<Rational, Rationals> kxy =
      new PolynomialRing<>(LEX, Rationals.FIELD, "x", "y");

    final Rational one = Rationals.FIELD.one();

    final PolynomialParser<Rational, Rationals, PolynomialRing<Rational, Rationals>> parser =
      new PolynomialParser<>(LEX, Rationals.FIELD, Rationals::parse, kxy.variables());

    assertThat(parser.apply("x^2y + xy^2 + y^2"))
      .isEqualTo(
        new Polynomial<>(kxy, Maps.mutable.of(
          new Monomial(new int[] {2, 1}), one,
          new Monomial(new int[] {1, 2}), one,
          new Monomial(new int[] {0, 2}), one
        ))
      );
  }

  @Test
  public void testParsePolynomials() {
    final Kx<Rational, Rationals> kx = new Kx<>(Rationals.FIELD);
    final Rational one = Rationals.FIELD.one();

    final PolynomialParser<Rational, Rationals, PolynomialRing<Rational, Rationals>> parser =
      new PolynomialParser<>(LEX, Rationals.FIELD, Rationals::parse);

    assertThat(parser.apply("x^2 + 1"))
      .isEqualTo(new Polynomial<>(kx, Maps.mutable.of(
        new Monomial(1, 0, 2), one,
        new Monomial(1), one)));

    assertThat(parser.apply("x^2 - 1"))
      .isEqualTo(new Polynomial<>(kx, Maps.mutable.of(
        new Monomial(1, 0, 2), one,
        new Monomial(1), Rationals.FIELD.negative(one))));
  }
}
