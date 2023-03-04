package tdanford.ideals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public interface RandomData {

  static Polynomial<Rational, Rationals> randomPolynomial(
    final Random rand,
    final PolynomialRing<Rational, Rationals> polyRing
  ) {

    final int termCount = rand.nextInt(6) + 1;

    final Map<Monomial, Rational> terms = new HashMap<>();
    final String[] vars = polyRing.variables();

    for (int i = 0; i < termCount; i++) {

      final int[] exp = new int[vars.length];
      for (int j = 0; j < exp.length; j++) {
        exp[j] = rand.nextInt(6);
      }

      final int numer = rand.nextInt(10) + 1;
      final int denom = rand.nextInt(10) + 1;

      terms.put(new Monomial(exp), new Rational(numer, denom));
    }

    return new Polynomial<>(polyRing, terms);
  }

  static PolynomialSet<Rational, Rationals> randomPolynomialSet(
    final Random rand,
    final PolynomialRing<Rational, Rationals> polyRing,
    final int count
  ) {

    final ArrayList<Polynomial<Rational, Rationals>> list = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      list.add(randomPolynomial(rand, polyRing));
    }

    return new PolynomialSet<>(polyRing, list);
  }
}
