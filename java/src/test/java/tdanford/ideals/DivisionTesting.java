package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import static tdanford.ideals.RandomData.randomPolynomial;
import static tdanford.ideals.RandomData.randomPolynomialSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.Test;
import com.google.common.base.Stopwatch;

public class DivisionTesting extends PolynomialTesting {

  @Test
  public void randomDivisions() {
    final Random rand = new Random();
    long count = 0L;
    long sum = 0L;
    for (int i = 0; i < 1000; i++) {
      Stopwatch s = Stopwatch.createStarted();
      randomDivision(rand);
      s.stop();
      final long ms = s.elapsed(TimeUnit.MILLISECONDS);
      sum += ms;
      count += 1L;
    }

    System.out.println(String.format("%.02f ms / division", (double)sum / (double)count));
  }

  public void randomDivision(final Random rand) {
    final Polynomial<Rational, Rationals> p1 = randomPolynomial(rand, KXY);
    final PolynomialSet<Rational, Rationals> fs = randomPolynomialSet(rand, KXY, 3);

    final DivisorsRemainder<Polynomial<Rational, Rationals>> divs = KXY.div(p1, fs);

    final Polynomial<Rational, Rationals> p1mult = IntStream.range(0, divs.divisors.length)
      .mapToObj(i -> divs.divisors[i].multipliedBy(fs.get(i)))
      .reduce(divs.remainder, Polynomial::addedTo);

    assertThat(p1mult).isEqualTo(p1);
  }

  /**
   * Example test of single-variable (k[x]) polynomial division
   */
  @Test
  public void testExampleIVA44() {
    final Polynomial<Rational, Rationals> f = kxPoly("x^3 + 4x^2 + 3x - 7"),
      g = kxPoly("x - 1");

    final DivisorsRemainder<Polynomial<Rational, Rationals>> dr = KX.div(f, g);

    assertThat(dr).isNotNull();

    assertThat(dr.divisors[0]).isEqualTo(kxPoly("x^2 + 5x + 8"));
    assertThat(dr.remainder).isEqualTo(kxPoly("1"));
  }

  /**
   * Example test of multi-variable (k[x, y]) polynomial division
   *
   * (pg. 64 of I, V, & A)
   */
  @Test
  public void testExampleIVA64() {
    assertThat(
      KXY.div(
        kxyPoly("x^2y + xy^2 + y^2"),
        kxyPoly("y^2 - 1"),   // f1
        kxyPoly("xy - 1")     // f2
      )
    ).isEqualTo(
      new DivisorsRemainder<>(
        kxyPoly("2x + 1"),   // remainder
        kxyPoly("x + 1"),    // coefficient of f1
        kxyPoly("x")         // coefficient of f2
      )
    );
  }
}
