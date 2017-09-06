package tdanford.ideals;

import static java.util.stream.Collectors.toList;
import static tdanford.ideals.MonomialOrdering.LEX;
import static tdanford.ideals.parsing.PolynomialParser.rationalPoly;
import java.util.stream.Stream;

public abstract class PolynomialTesting {

  public static final Kx<Rational, Rationals> KX = new Kx<>(Rationals.FIELD);
  public static final PolynomialRing<Rational, Rationals> KXY =
    new PolynomialRing<>(LEX, Rationals.FIELD, "x", "y");

  public static Polynomial<Rational, Rationals> kxPoly(final String str) {
    return rationalPoly(str, KX.variables());
  }

  public static Polynomial<Rational, Rationals> kxyPoly(final String str) {
    return rationalPoly(str, KXY.variables());
  }

  public static PolynomialSet<Rational, Rationals> kxyPolys(final String... strs) {
    return new PolynomialSet<>(KXY,
      Stream.of(strs).map(PolynomialTesting::kxyPoly).collect(toList())
    );
  }

  public static PolynomialSet<Rational, Rationals> rationalPolys(
    final String[] vars,
    final String... polyStrings
  ) {
    final PolynomialRing<Rational, Rationals> ring = new PolynomialRing<>(LEX, Rationals.FIELD, vars);
    return new PolynomialSet<>(ring,
      Stream.of(polyStrings).map(str -> rationalPoly(str, vars)).collect(toList())
    );
  }
}
