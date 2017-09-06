package tdanford.ideals.parsing;

import static tdanford.ideals.MonomialOrdering.LEX;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import tdanford.ideals.MonomialOrdering;
import tdanford.ideals.Polynomial;
import tdanford.ideals.PolynomialRing;
import tdanford.ideals.Rational;
import tdanford.ideals.Rationals;
import tdanford.ideals.Ring;
import tdanford.ideals.antlr.PolynomialsLexer;
import tdanford.ideals.antlr.PolynomialsParser;

public class PolynomialParser<K, F extends Ring<K, K>, PR extends PolynomialRing<K, F>>
  implements Function<String, Polynomial<K, F>> {

  public static Polynomial<Rational, Rationals> rationalPoly(final String str, final String... vars) {
    return new PolynomialParser<>(LEX, Rationals.FIELD, Rationals::parse, vars)
      .apply(str);
  }

  private final Function<String, K> constantParser;
  private final MonomialOrdering ordering;
  private final F coeffField;
  private final String[] vars;

  public PolynomialParser(final MonomialOrdering ordering,
                          final F coeffField,
                          final Function<String, K> constantParser,
                          final String... vars) {
    this.ordering = ordering;
    this.coeffField = coeffField;
    this.constantParser = constantParser;
    this.vars = vars.length > 0 ? vars : null;
  }

  @Override
  public Polynomial<K, F> apply(final String input) {
    final PolynomialsLexer lexer = new PolynomialsLexer(new ANTLRInputStream(input));
    final PolynomialsParser parser = new PolynomialsParser(new CommonTokenStream(lexer));

    final PolynomialsParser.PolynomialContext ctx = parser.polynomial();

    String[] polyVars = vars;

    if (polyVars == null) {
      final Set<String> vars = ctx.accept(new VariablesVisitor());
      final String[] varArray = vars.toArray(new String[vars.size()]);
      Arrays.sort(varArray);
      polyVars = varArray;
    }

    final PolynomialRing<K, F> ring = new PolynomialRing<>(ordering, coeffField, polyVars);

    return ctx.accept(new PolynomialVisitor<>(ring, constantParser));
  }
}

