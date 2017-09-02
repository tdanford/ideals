package tdanford.ideals;

public class Rationals implements Field<Rational> {
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
