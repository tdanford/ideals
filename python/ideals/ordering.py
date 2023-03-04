
from enum import Enum

from .monomials import Monomial

def lex_comparator(o1: Monomial, o2: Monomial) -> int: 
    if len(o1) != len(o2): raise ValueError()
    for i in range(len(o1)): 
        e1 = o1.exponents[i] 
        e2 = o2.exponents[i] 
        if e1 > e2: return -1 
        if e1 < e2: return 1
    return 0

def grevlex_comparator(o1: Monomial, o2: Monomial) -> int: 
    return 0

class MonomialOrdering(Enum): 
    LEX = lex_comparator
    GREVLEX = grevlex_comparator

    def compare(self, o1: Monomial, o2: Monomial) -> int: 
        return self.value(o1, o2)