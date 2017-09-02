package tdanford.ideals;

public class Reals implements Field<Double> {
  @Override
  public Double reciprocal(final Double value) {
    return 1.0 / value;
  }

  @Override
  public Double[] array(final int length) {
    return new Double[length];
  }

  @Override
  public Double product(final Double p1, final Double p2) {
    return p1 * p2;
  }

  @Override
  public Double sum(final Double a1, final Double a2) {
    return a1 + a2;
  }

  @Override
  public Double negative(final Double value) {
    return -value;
  }

  @Override
  public Double zero() {
    return 0.0;
  }

  @Override
  public Double one() {
    return 1.0;
  }
}
