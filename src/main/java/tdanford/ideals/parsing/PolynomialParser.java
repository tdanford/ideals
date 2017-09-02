package tdanford.ideals.parsing;

import java.util.function.Function;
import tdanford.ideals.Field;
import tdanford.ideals.Polynomial;
import tdanford.ideals.PolynomialRing;

public class PolynomialParser<K, F extends Field<K>, PR extends PolynomialRing<K, F>>
  implements Function<String, Polynomial<K, F>> {

  private Function<String, K> constantParser;
  private PR polyRing;

  @Override
  public Polynomial<K, F> apply(final String s) {
    return null;
  }
}
