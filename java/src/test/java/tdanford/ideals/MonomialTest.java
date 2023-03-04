package tdanford.ideals;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import nl.jqno.equalsverifier.EqualsVerifier;

public class MonomialTest {

  @Test
  public void testEquality() {
    EqualsVerifier.forClass(Monomial.class).verify();
  }

  @Test
  public void testOneLess() {
    assertThat(new Monomial(new int[] {1, 1}).oneLess(0)).isEqualTo(new Monomial(new int[] {0, 1}));
    assertThat(new Monomial(new int[] {1, 1}).oneLess(1)).isEqualTo(new Monomial(new int[] {1, 0}));
  }

  @Test
  public void testToString() {
    assertThat(new Monomial(1).renderString(new String[] {"x"})).isEqualTo("");
    assertThat(new Monomial(1, 0, 1).renderString(new String[] {"x"})).isEqualTo("x");
    assertThat(new Monomial(1, 0, 2).renderString(new String[] {"x"})).isEqualTo("x^2");
  }

}
