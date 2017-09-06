package tdanford.ideals.parsing;

import java.util.function.Function;
import tdanford.ideals.Monomial;
import tdanford.ideals.Polynomial;
import tdanford.ideals.PolynomialRing;
import tdanford.ideals.Ring;
import tdanford.ideals.antlr.PolynomialsBaseVisitor;
import tdanford.ideals.antlr.PolynomialsParser;

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
