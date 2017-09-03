package tdanford.ideals;

import static tdanford.ideals.parsing.PolynomialParser.rationalPoly;

public abstract class PolynomialTesting {

  public static final Kx<Rational, Rationals> KX = new Kx<>(Rationals.FIELD);

  public static Polynomial<Rational, Rationals> kxPoly(final String str) {
    return rationalPoly(str, KX.variables());
  }

}
