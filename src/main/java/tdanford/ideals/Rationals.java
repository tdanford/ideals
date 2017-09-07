package tdanford.ideals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Rationals implements Field<Rational> {

  FIELD ;

  private static Logger LOG = LoggerFactory.getLogger(Rationals.class);
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

  public static Rational liftInteger(final Integer v) {
    return new Rational((long) v, 1L);
  }

  public boolean lessThan(final Rational left, final Rational right) {
    return left.lessThan(right);
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

  @Override
  public Rational pow(final Rational r, final int k) { return r.pow(k); }

  public static Rational fromDouble(final Double v) {
    final int places = 6;
    final double numer = Math.pow(10.0, (double) places);
    final long integralPart = v.longValue();
    final double fractionalPart = v - (double) integralPart;
    final long fractionalDenom = fractionalPart != 0.0 ? (long) (numer / fractionalPart) : 1L;

    LOG.info("fromDouble(integralPart={}, fractionalPart={}, numer={}, fractionalDenom={})",
      integralPart, fractionalPart, numer, fractionalDenom);

    return new Rational(integralPart, 1L).sum(new Rational((long) numer, fractionalDenom));
  }
}
