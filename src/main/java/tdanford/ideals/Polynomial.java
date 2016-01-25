package tdanford.ideals;

import java.util.*;

public class Polynomial<K, F extends Field<K>> {

    private F field;
    private MonomialOrdering<K,F> ordering;

    private ArrayList<Monomial<K,F>> monomials;

    public Polynomial(F field, MonomialOrdering<K,F> ordering, Collection<Monomial<K,F>> monomials) {
        this.field = field;
        this.ordering = ordering;
        this.monomials = new ArrayList<Monomial<K,F>>(monomials);
    }
}



