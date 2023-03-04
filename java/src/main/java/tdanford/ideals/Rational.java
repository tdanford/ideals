package tdanford.ideals;

import java.math.BigInteger;
import java.util.Objects;

import com.google.common.base.Preconditions;

public class Rational {

  private BigInteger numer, denom;

  public Rational(final long num, final long denom) {
    this(BigInteger.valueOf(num), BigInteger.valueOf(denom));
  }

  public Rational(final BigInteger num, final BigInteger denom) {
    this.numer = num;
    this.denom = denom;

    Preconditions.checkArgument(
      !this.denom.equals(BigInteger.ZERO) || this.numer.equals(BigInteger.ZERO),
      String.format("Cannot have zero denominator without zero numerator (found %s / %s)", num, denom));
    reduce();
  }

  public Rational(final long num) {
    this.numer = BigInteger.valueOf(num);
    this.denom = BigInteger.ONE;
  }

  public static BigInteger lcm(final BigInteger v1, final BigInteger v2) {
    final BigInteger gcd12 = gcd(v1, v2);
    return gcd12.multiply(v1.divide(gcd12)).multiply(v2.divide(gcd12));
  }

  public static BigInteger gcd(final BigInteger astart, final BigInteger bstart) {
    BigInteger a = astart, b = bstart.abs();
    while (!b.equals(BigInteger.ZERO)) {
      final BigInteger t = b;
      b = a.mod(b).abs();
      a = t;
    }
    return a;
  }

  public boolean isZero() { return numer.equals(BigInteger.ZERO); }

  public Rational product(final Rational r) {
    return new Rational(numer.multiply(r.numer), denom.multiply(r.denom));
  }

  public Rational sum(final Rational r) {
    return new Rational(
      numer.multiply(r.denom).add(r.numer.multiply(denom)),
      denom.multiply(r.denom));
  }

  public Rational negative() {
    return new Rational(numer.negate(), denom);
  }

  public Rational inverse() {
    return new Rational(denom, numer);
  }

  private void reduce() {
    if (!denom.equals(BigInteger.ZERO)) {

      if (denom.compareTo(BigInteger.ZERO) < 0) {
        denom = denom.negate();
        numer = numer.negate();
      }

      BigInteger gcd = gcd(numer.abs(), denom);

      if (gcd.equals(BigInteger.ZERO)) {
        throw new IllegalStateException(String.format("GCD(%d, %d) returned 0", numer.abs(), denom));
      }
      numer = numer.divide(gcd);
      denom = denom.divide(gcd);
    }
  }

  public String toString() {
    if (denom.equals(BigInteger.ZERO)) {
      return "0";
    } else if (denom.equals(BigInteger.ONE)) {
      return String.valueOf(numer);
    } else {
      return String.format("%d/%d", numer, denom);
    }
  }

  public int hashCode() {
    return Objects.hash(numer, denom);
  }

  public boolean equals(Object o) {
    if (!(o instanceof Rational)) {
      return false;
    }
    Rational r = (Rational) o;
    return Objects.equals(numer, r.numer) && Objects.equals(denom, r.denom);
  }

  public BigInteger getDenominator() {
    return denom;
  }

  public BigInteger getNumerator() {
    return numer;
  }

  private static boolean lessThan(
    final BigInteger n1,
    final BigInteger d1,
    final BigInteger n2,
    final BigInteger d2
  ) {

    if (d1.equals(d2)) {
      return n1.compareTo(n2) < 0;
    } else {
      return lessThan(n1.multiply(d2), d1.multiply(d2), n2.multiply(d1), d2.multiply(d1));
    }
  }

  public double doubleValue() {
    if (denom.equals(BigInteger.ZERO)) { return 0.0; }
    return numer.doubleValue() / denom.doubleValue();
  }

  public boolean lessThan(final Rational other) {
    return lessThan(numer, denom, other.numer, other.denom);
  }

  public Rational abs() {
    return new Rational(numer.abs(), denom);
  }

  public Rational pow(final int k) {
    return new Rational(numer.pow(k), denom.pow(k));
  }
}

