package tdanford.ideals;

public class DivisorRemainder<V> {

  public final V divisor;
  public final V remainder;

  public DivisorRemainder(final V divisor, final V remainder) {
    this.divisor = divisor;
    this.remainder = remainder;
  }
}
