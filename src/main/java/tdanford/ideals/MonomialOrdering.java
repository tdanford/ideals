package tdanford.ideals;

import java.util.*;

public interface MonomialOrdering<K, F extends Field<K>> extends Comparator<Monomial<K,F>> {
}

class LexOrdering<K, F extends Field<K>> implements MonomialOrdering<K,F> {

    private String[] variables;
    private Map<String,Integer> varIndices;

    public LexOrdering(String... vars) {
        this.variables = vars;
        varIndices = new TreeMap<String,Integer>();
        for(int i = 0; i < vars.length; i++) { varIndices.put(vars[i], i); }
    }

    @Override
    public int compare(Monomial<K, F> o1, Monomial<K, F> o2) {
        for(int i = 0; i < variables.length; i++) {
            String var = variables[i];
            int e1 = o1.exponent(var), e2 = o2.exponent(var);
            if(e1 > e2) { return -1; }
            if(e1 < e2) { return 1; }
        }
        return 0;
    }
}
