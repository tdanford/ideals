package tdanford.ideals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Rationals implements Field<Rational> {

  FIELD ;

  private static Pattern rationalPattern = Pattern.compile("(-?\\d+)(/(\\d+))?");

  public static Rational parse(final String strValue) {
    final Matcher m = rationalPattern.matcher(strValue);
    if (m.matches()) {
      final Integer numer = Integer.parseInt(m.group(1));

      final String group2 = m.group(2);
      final Integer denom = group2 != null && group2.length() > 0 ? Integer.parseInt(m.group(3)) : 1;
      return new Rational(numer, denom);
    } else {
      throw new IllegalArgumentException(String.format("Can't parse a Rational from \"%s\"", strValue));
    }
  }

  @Override
  public Rational reciprocal(Rational value) {
    return value.inverse();
  }

  @Override
  public Rational[] array(int length) {
    return new Rational[length];
  }

  @Override
  public Rational product(Rational p1, Rational p2) {
    return p1.product(p2);
  }

  @Override
  public Rational sum(Rational a1, Rational a2) {
    return a1.sum(a2);
  }

  @Override
  public Rational negative(Rational value) {
    return value.negative();
  }

  @Override
  public Rational zero() {
    return new Rational(0, 1);
  }

  @Override
  public Rational one() {
    return new Rational(1, 1);
  }
}
