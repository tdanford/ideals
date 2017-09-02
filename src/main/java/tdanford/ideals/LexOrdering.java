package tdanford.ideals;

import java.util.Map;
import java.util.TreeMap;

public class LexOrdering implements MonomialOrdering {

  private String[] variables;
  private Map<String, Integer> varIndices;

  public LexOrdering(String... vars) {
    this.variables = vars;
    varIndices = new TreeMap<>();
    for (int i = 0; i < vars.length; i++) {
      varIndices.put(vars[i], i);
    }
  }

  @Override
  public int compare(final Monomial o1, final Monomial o2) {
    for (int i = 0; i < variables.length; i++) {
      final String var = variables[i];
      final int vi = varIndices.get(var);
      int e1 = o1.exponent(vi), e2 = o2.exponent(vi);
      if (e1 > e2) {
        return -1;
      }
      if (e1 < e2) {
        return 1;
      }
    }
    return 0;
  }
}
