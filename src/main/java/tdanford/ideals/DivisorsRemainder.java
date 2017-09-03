package tdanford.ideals;

import static java.util.stream.Collectors.joining;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class DivisorsRemainder<V> {

  public final V[] divisors;
  public final V remainder;

  public DivisorsRemainder(final V remainder, final V... divisors) {
    this.divisors= divisors;
    this.remainder = remainder;
  }

  public String toString() {
    return String.format("[%s]:[%s]",
      Stream.of(divisors).map(String::valueOf).collect(joining(", ")),
      remainder.toString()
    );
  }

  public int hashCode() {
    int code = 17;
    code += Objects.hash(remainder); code *= 37;
    code += Arrays.deepHashCode(divisors); code *= 37;
    return code;
  }

  public boolean equals(final Object o) {
    if (!(o instanceof DivisorsRemainder)) { return false; }
    DivisorsRemainder<V> dr = (DivisorsRemainder<V>) o;
    return Objects.equals(remainder, dr.remainder) &&
      Arrays.deepEquals(divisors, dr.divisors);
  }
}
