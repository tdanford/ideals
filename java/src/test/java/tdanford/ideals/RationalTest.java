package tdanford.ideals;

import org.junit.*;
import static org.assertj.core.api.Assertions.assertThat;
import static tdanford.ideals.Rationals.FIELD;

public class RationalTest {

    public static Rational ratio(int numer, int denom) { return new Rational(numer, denom); }

    @Test
    public void testEqualityWithOne() {
      assertThat(new Rational(1, 1)).isEqualTo(Rationals.FIELD.one());
      assertThat(new Rational(2, 2)).isEqualTo(Rationals.FIELD.one());
      assertThat(new Rational(100, 100)).isEqualTo(Rationals.FIELD.one());
    }

    @Test
    public void testAddition() {
        assertThat(FIELD.sum(ratio(1, 5), ratio(2, 5)))
          .isEqualTo(ratio(3, 5));
    }

    @Test
    public void testMultiplication() {
        assertThat(FIELD.product(ratio(2, 3), ratio(2, 5)))
          .isEqualTo(ratio(4, 15));
    }


}
