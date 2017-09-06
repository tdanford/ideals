package tdanford.ideals;

import static java.util.stream.Collectors.toList;
import static tdanford.ideals.MonomialOrdering.LEX;
import java.util.stream.Stream;
import tdanford.ideals.parsing.PolynomialParser;

public abstract class PolynomialTesting {

  public static final Kx<Rational, Rationals> KX = new Kx<>(Rationals.FIELD);
  public static final PolynomialRing<Rational, Rationals> KXY =
    new PolynomialRing<>(LEX, Rationals.FIELD, "x", "y");

  public static Polynomial<Rational, Rationals> kxPoly(final String str) {
    return rationalPoly(KX.variables(), str);
  }

  public static Polynomial<Rational, Rationals> kxyPoly(final String str) {
    return rationalPoly(KXY.variables(), str);
  }

  public static PolynomialSet<Rational, Rationals> kxyPolys(final String... strs) {
    return new PolynomialSet<>(
      Stream.of(strs).map(PolynomialTesting::kxyPoly).collect(toList())
    );
  }

  public static Polynomial<Rational, Rationals> rationalPoly(final String[] vars, final String str) {
    return PolynomialParser.rationalPoly(str, vars);
  }

  public static PolynomialSet<Rational, Rationals> rationalPolys(
    final String[] vars,
    final String... polyStrings
  ) {
    final PolynomialRing<Rational, Rationals> ring = new PolynomialRing<>(LEX, Rationals.FIELD, vars);
    return new PolynomialSet<>(
      Stream.of(polyStrings).map(str -> rationalPoly(vars, str)).collect(toList())
    );
  }
}
