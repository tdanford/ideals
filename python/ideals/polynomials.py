from typing import Dict, List, Tuple
from functools import reduce 
import numbers
import itertools
import re

from .monomials import term, Monomial, group_monomials
from .rings import Evaluable, Numeric

class Polynomial(Evaluable): 

    def __init__(self, terms: List[Monomial]): 
        grouped = group_monomials(*terms) 
        self.terms = sorted(   # we're assuming, throughout, that we're in an abelian ring
            [ reduce(lambda t1, t2: t1 + t2, grouped[k]) for k in grouped ], 
            key=lambda t: t.order, 
            reverse=True 
        )
    
    def is_zero(self): 
        return len(self.terms) == 0
    
    @property 
    def is_linear(self): 
        for t in self.terms: 
            if not t.is_linear: 
                return False
        return True 
    
    def leading_term(self): 
        return self.terms[0] 
    
    def __repr__(self): 
        first = [str(self.terms[0])]
        rest = list(itertools.chain(*[[t.term_join_symbol, t.without_sign] for t in self.terms[1:]]))
        return "".join(first + rest) 

    def __pow__(self, exp): 
        if exp < 0: raise ValueError(exp) 
        if exp == 0: return poly("1") 
        p = self 
        while exp > 1: 
            exp -= 1 
            p = p * self 
        return p
    
    def __xor__(self, exp): 
        return self.__pow__(exp) 
    
    def __add__(self, other): 
        if isinstance(other, Monomial): 
            return Polynomial(terms=self.terms + [other])
        if not isinstance(other, Polynomial): 
            raise ValueError(other) 
        return Polynomial(terms=self.terms + other.terms)
    
    def __sub__(self, other) -> Evaluable: 
        return self.__add__(-other)
    
    def __neg__(self): 
        return Polynomial(terms=[-t for t in self.terms])
    
    def __mul__(self, other): 
        if isinstance(other, numbers.Number): 
            return Polynomial(terms=[t * other for t in self.terms])
        if isinstance(other, Monomial): 
            return Polynomial(terms=[other * t for t in self.terms])
        if not isinstance(other, Polynomial): 
            raise ValueError(other) 
        
        terms = list(itertools.chain(*[(other * t).terms for t in self.terms]))
        return Polynomial(terms=terms)
    
    def __truediv__(self, other): 
        if isinstance(other, Monomial) or isinstance(other, numbers.Number): 
            return Polynomial(terms=[t / other for t in self.terms])
        return divide_polys(other, [self])

    def __rmul__(self, other): 
        return self.__mul__(other) 
    
    def __eq__(self, other): 
        if not isinstance(other, Polynomial): return False 
        return self.terms == other.terms 
    
    def __hash__(self): 
        return hash(tuple(self.terms))
    
    def __getitem__(self, subst: Dict[str, Numeric]) -> Numeric: 
        subs = [t[subst] for t in self.terms] 
        terms = list(itertools.chain(*[as_monomials(t) for t in subs]))
        return Polynomial(terms=terms)

def divide_polys(f: Polynomial, fs: List[Polynomial]) -> Tuple[Polynomial, List[Polynomial]]: 
    zero = Polynomial(terms=[]) 
    _as = [ Polynomial(terms=[]) for i in range(len(fs)) ]
    r = zero 
    p = f 
    prevP = zero 

    while not p.is_zero() and p != prevP: 
        prevP = p 
        i = 0 
        divisionOccurred = False 
        ltp = p.leading_term() 
        while i < len(_as) and not divisionOccurred: 
            if fs[i].leading_term().divides(ltp): 
                ratio = ltp / fs[i].leading_term()
                _as[i] = _as[i] + ratio 
                p = p - (ratio * fs[i]) 
                divisionOccurred = True 
            else: 
                i += 1
        if divisionOccurred: 
            r = r + ltp 
            p = p - ltp

        return r, _as

def as_monomials(value) -> List[Monomial]: 
    if isinstance(value, Monomial): 
        return [value] 
    elif isinstance(value, Polynomial): 
        return value.terms 
    else: 
        return [term(value) ]

minus_regexp = re.compile("(-\\s*[^\\s])")

def rewrite_minuses(poly_str: str) -> str: 
    pos = 0
    new_middle = "+ -"
    m = minus_regexp.search(poly_str, pos=pos)
    while m is not None: 
        (start, end) = m.span() 
        left = poly_str[0:start]
        right = poly_str[end-1:] 
        poly_str = left + new_middle + right 
        pos = start + 4 # 4 == len(new_middle) + 1
        m = minus_regexp.search(poly_str, pos=pos)
    return poly_str 


def poly(poly_str: str) -> Polynomial: 
    terms = [term(t) for t in rewrite_minuses(poly_str).split("+")]
    return Polynomial(terms=terms)