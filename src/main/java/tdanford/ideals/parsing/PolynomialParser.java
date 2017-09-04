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

class MonomialVisitor
  extends PolynomialsBaseVisitor<Monomial> {

  private final String[] vars;
  private final Map<String, Integer> indices;

  public MonomialVisitor(final String... vars) {
    this.vars = vars;
    this.indices = IntStream.range(0, vars.length).boxed().collect(toMap(
      i -> vars[i], i -> i
    ));
  }

  @Override
  public Monomial visitSingleVar(final PolynomialsParser.SingleVarContext ctx) {
    final String var = ctx.var().getText();
    return new Monomial(vars.length, indices.get(var), 1);
  }

  @Override
  public Monomial visitExponentiatedVar(final PolynomialsParser.ExponentiatedVarContext ctx) {
    Preconditions.checkState(vars != null, "vars cannot be null");
    Preconditions.checkState(indices != null, "indices cannot be null");

    final String var = ctx.var().getText();

    Preconditions.checkState(indices.containsKey(var), String.format(
      "indices %s doesn't contain var %s", indices.keySet(), var));

    final int exp = Integer.parseInt(ctx.exponent().getText());
    return new Monomial(vars.length, indices.get(var), exp);
  }
}

class PolynomialVisitor<K, F extends Ring<K, K>, PR extends PolynomialRing<K, F>>
  extends PolynomialsBaseVisitor<Polynomial<K, F>> {

  private final PR polyRing;
  private final Function<String, K> constantParser;

  public PolynomialVisitor(final PR polyRing, final Function<String, K> constantParser) {
    this.polyRing = polyRing;
    this.constantParser = constantParser;
  }

  @Override
  public Polynomial<K, F> visitMonomialTerm(final PolynomialsParser.MonomialTermContext ctx) {
    final K coeff = ctx.coefficient() != null ?
      constantParser.apply(ctx.coefficient().getText()) : polyRing.coefficientField().one();

    int numVars = polyRing.variables().length;
    final MonomialVisitor monomialVisitor = new MonomialVisitor(polyRing.variables());

    final Monomial monomial = ctx.exp_var().stream().map(monomialVisitor::visit).reduce(
      new Monomial(numVars),
      Monomial::multipliedBy);

    return new Polynomial<>(polyRing, coeff, monomial);
  }

  @Override
  public Polynomial<K, F> visitConstantTerm(final PolynomialsParser.ConstantTermContext ctx) {
    final K coeff = constantParser.apply(ctx.coefficient().getText());
    //System.out.println(String.format("CONSTANT PARSING: \"%s\" -> %s", ctx.coefficient().getText(), coeff));
    return new Polynomial<>(polyRing, coeff);
  }

  @Override
  public Polynomial<K, F> visitMultiplication(final PolynomialsParser.MultiplicationContext ctx) {
    return ctx.grouped_polynomial().stream().map(this::visit).reduce(
      new Polynomial<>(polyRing, polyRing.coefficientField().one()),
      Polynomial::multipliedBy
    );
  }

  @Override
  public Polynomial<K, F> visitAddition(final PolynomialsParser.AdditionContext ctx) {
    final Polynomial<K, F> restPoly = visit(ctx.term());
    final Polynomial<K, F> termPoly = visit(ctx.polynomial());
    //System.out.println(String.format("ADDITION: %s PLUS %s", termPoly, restPoly));
    return termPoly.addedTo(restPoly);
  }

  @Override
  public Polynomial<K, F> visitSubtraction(final PolynomialsParser.SubtractionContext ctx) {
    final Polynomial<K, F> restPoly = visit(ctx.term());
    final Polynomial<K, F> termPoly = visit(ctx.polynomial());
    //System.out.println(String.format("SUBTRACTION: %s MINUS %s", termPoly, restPoly));

    final F ring = polyRing.coefficientField();
    final K negativeOne = ring.negative(ring.one());
    return termPoly.addedTo(restPoly.scaleBy(negativeOne));
  }

  @Override
  public Polynomial<K, F> visitGrouped_polynomial(final PolynomialsParser.Grouped_polynomialContext ctx) {
    return this.visit(ctx.polynomial());
  }
}
