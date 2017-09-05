package tdanford.ideals;

import java.util.Objects;
import com.google.common.base.Preconditions;

public class Rational {

  private long numer, denom;

  public Rational(final long num, final long denom) {
    this.numer = num;
    this.denom = denom;

    Preconditions.checkArgument(denom != 0 || num == 0, "Cannot have zero denominator without zero numerator");
    reduce();
  }

  public Rational(final long num) {
    this.numer = num;
    this.denom = 1;
  }

  private static long gcd(final long astart, final long bstart) {
    long a = astart, b = bstart;
    while (b != 0) {
      long t = b;
      b = a % b;
      a = t;
    }
    return a;
  }

  public boolean isZero() { return numer == 0; }

  public Rational product(final Rational r) {
    return new Rational(numer * r.numer, denom * r.denom);
  }

  public Rational sum(final Rational r) {
    return new Rational(numer * r.denom + r.numer * denom, denom * r.denom);
  }

  public Rational negative() {
    return new Rational(-numer, denom);
  }

  public Rational inverse() {
    return new Rational(denom, numer);
  }

  private void reduce() {
    if (denom != 0) {

      if (denom < 0) {
        denom = -denom;
        numer = -numer;
      }

      long gcd = gcd(Math.abs(numer), denom);

      if (gcd == 0) {
        throw new IllegalStateException(String.format("GCD(%d, %d) returned 0", Math.abs(numer), denom));
      }
      numer /= gcd;
      denom /= gcd;
    }
  }

  public String toString() {
    if (denom == 0) {
      return "0";
    } else if (denom == 1) {
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

}

