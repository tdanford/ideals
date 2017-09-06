package tdanford.ideals.parsing;

import static java.util.stream.Collectors.toMap;
import java.util.Map;
import java.util.stream.IntStream;
import com.google.common.base.Preconditions;
import tdanford.ideals.Monomial;
import tdanford.ideals.antlr.PolynomialsBaseVisitor;
import tdanford.ideals.antlr.PolynomialsParser;

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
    Preconditions.checkState(vars != null);
    Preconditions.checkState(vars.length > 0);
    Preconditions.checkState(indices != null);

    final String var = ctx.var().getText();
    Preconditions.checkState(indices.containsKey(var), String.format("indices %s must contain var %s", indices.keySet(), var));

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
