package tdanford.ideals.parsing;

import static java.util.stream.Collectors.toMap;
import static tdanford.ideals.MonomialOrdering.LEX;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import com.google.common.base.Preconditions;
import tdanford.ideals.Monomial;
import tdanford.ideals.Polynomial;
import tdanford.ideals.PolynomialRing;
import tdanford.ideals.Rational;
import tdanford.ideals.Rationals;
import tdanford.ideals.Ring;
import tdanford.ideals.antlr.PolynomialsBaseVisitor;
import tdanford.ideals.antlr.PolynomialsLexer;
import tdanford.ideals.antlr.PolynomialsParser;

public class PolynomialParser<K, F extends Ring<K, K>, PR extends PolynomialRing<K, F>>
  implements Function<String, Polynomial<K, F>> {

  public static Polynomial<Rational, Rationals> rationalPoly(final String str, final String... vars) {
    return new PolynomialParser<>(new PolynomialRing<>(LEX, Rationals.FIELD, vars), Rationals::parse)
      .apply(str);
  }

  private final Function<String, K> constantParser;
  private final PR polyRing;

  public PolynomialParser(final PR polyRing, final Function<String, K> constantParser) {
    this.polyRing = polyRing;
    this.constantParser = constantParser;
  }

  @Override
  public Polynomial<K, F> apply(final String input) {
    final PolynomialsLexer lexer = new PolynomialsLexer(new ANTLRInputStream(input));
    final PolynomialsParser parser = new PolynomialsParser(new CommonTokenStream(lexer));

    return parser.polynomial().accept(new PolynomialVisitor<>(polyRing, constantParser));
  }
}

