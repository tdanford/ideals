package tdanford.ideals;

import org.junit.Test;
import nl.jqno.equalsverifier.EqualsVerifier;

public class TermTest {

  @Test
  public void testEquality() {
    EqualsVerifier.forClass(Term.class)
      .withIgnoredFields("field")
      .verify();
  }
}
