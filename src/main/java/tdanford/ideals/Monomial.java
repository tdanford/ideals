package tdanford.ideals;

import java.util.*;

public class Monomial<K, F extends Field<K>> {

    private F field;
    private K coefficient;
    private Map<String,Integer> exponents;

    public Monomial(F field, K coeff, Map<String,Integer> vars) {
        this.field = field;
        this.coefficient = coeff;
        this.exponents = vars;
    }

    public String[] variables() { return exponents.keySet().toArray(new String[exponents.size()]); }

    public boolean hasVariable(String var) { return exponents.containsKey(var); }

    public Integer exponent(String var) { return exponents.containsKey(var) ? exponents.get(var) : 0; }

    public K getCoefficient() { return coefficient; }

    public boolean divides(Monomial<K, F> m) {
        for(String var : exponents.keySet()) {
            if(!m.hasVariable(var)) { return false; }
            Integer thisExp = exponents.get(var);
            Integer thatExp = m.exponent(var);
            if(thatExp < thisExp) { return false; }
        }
        return true;
    }

    public Monomial<K,F> dividedBy(Monomial<K, F> m) {
        if(!m.divides(this)) { throw new IllegalArgumentException(); }

        K newCoeff = field.product(coefficient, field.reciprocal(m.coefficient));
        TreeMap<String,Integer> exps = new TreeMap<String,Integer>();
        for(String var : exponents.keySet()) {
            exps.put(var, exponents.get(var) - m.exponent(var));
        }

        return new Monomial<K,F>(field, newCoeff, exps);
    }


}
