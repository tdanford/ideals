package tdanford.ideals.parsing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import tdanford.ideals.antlr.PolynomialsBaseVisitor;
import tdanford.ideals.antlr.PolynomialsParser;

class VariablesVisitor extends PolynomialsBaseVisitor<Set<String>> {

  public static Set<String> union(final Set<String> s1, final Set<String> s2) {
    final Set<String> ss = new HashSet<>();
    if (s1 != null) { ss.addAll(s1); }
    if (s2 != null) { ss.addAll(s2); }
    return ss;
  }

  @Override
  public Set<String> visitExponentiatedVar(final PolynomialsParser.ExponentiatedVarContext ctx) {
    return Collections.singleton(ctx.var().getText());
  }

  @Override
  public Set<String> visitSingleVar(final PolynomialsParser.SingleVarContext ctx) {
    return Collections.singleton(ctx.var().getText());
  }

  @Override
  public Set<String> visitMultiplication(final PolynomialsParser.MultiplicationContext ctx) {
    return ctx.grouped_polynomial().stream().map(this::visit).reduce(
      Collections.emptySet(),
      VariablesVisitor::union
    );
  }

  @Override
  public Set<String> visitAddition(final PolynomialsParser.AdditionContext ctx) {
    return union(visit(ctx.term()), visit(ctx.polynomial()));
  }

  @Override
  public Set<String> visitSubtraction(final PolynomialsParser.SubtractionContext ctx) {
    return union(visit(ctx.term()), visit(ctx.polynomial()));
  }

  @Override
  public Set<String> visitGrouped_polynomial(final PolynomialsParser.Grouped_polynomialContext ctx) {
    return this.visit(ctx.polynomial());
  }
}
