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
  public void testToString() {
    assertThat(new Monomial(1).renderString(new String[] {"x"})).isEqualTo("");
    assertThat(new Monomial(1, 0, 1).renderString(new String[] {"x"})).isEqualTo("x");
    assertThat(new Monomial(1, 0, 2).renderString(new String[] {"x"})).isEqualTo("x^2");
  }

}
