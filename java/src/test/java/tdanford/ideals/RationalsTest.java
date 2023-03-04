package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import static tdanford.ideals.Rationals.parse;
import org.junit.Test;

public class RationalsTest {

  @Test
  public void testParsing() {
    assertThat(parse("1")).isEqualTo(new Rational(1, 1));
    assertThat(parse("1/2")).isEqualTo(new Rational(1, 2));
    assertThat(parse("-1/2")).isEqualTo(new Rational(-1, 2));
    assertThat(parse("3/6")).isEqualTo(new Rational(1, 2));
  }
}
