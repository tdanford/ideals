package tdanford.ideals;

import java.util.Objects;

public class Rational {

  private int numer, denom;

  public Rational(final int num, final int denom) {
    this.numer = num;
    this.denom = denom;
    reduce();
  }

  public Rational(final int num) {
    this.numer = num;
    this.denom = 1;
  }

  private static int gcd(final int astart, final int bstart) {
    int a = astart, b = bstart;
    while (b != 0) {
      int t = b;
      b = a % b;
      a = t;
    }
    return a;
  }

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
    int gcd = gcd(numer, denom);
    numer /= gcd;
    denom /= gcd;
  }

  public String toString() {
    return denom != 1 ? String.format("%d/%d", numer, denom) : String.valueOf(numer);
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

