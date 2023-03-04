import re 
import numbers
from typing import Dict, Union, List
from functools import reduce  
from collections import defaultdict 

from .rings import Evaluable, Numeric

exp_unicode = ['\u00b2', '\u00b3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079']
def power_repr(var: str, exp: int = 1): 
    if exp == 0: 
        return var 
    elif exp < 0: 
        return f"{var}^({exp})"
    elif exp <= 1: 
        return var 
    elif exp <= 9: 
        return f"{var}{exp_unicode[exp-2]}"
    else: 
        return f"{var}^{exp}"

class Monomial(Evaluable): 

    coeff: numbers.Number 
    exponents: Dict[str, int] 
    order: int 
    vars: List[str]

    def __init__(self, coeff: numbers.Number=1, exponents: dict[str, int]={}): 
        self.coeff = coeff 
        self.exponents = { k: exponents[k] for k in exponents if exponents[k] != 0 } if coeff != 0 else {}
        self.order = sum([self.exponents[var] for var in self.exponents])
        self.vars = sorted([k for k in self.exponents]) 
        #print(f"{self.coeff}: {self.exponents}")
    
    def __eq__(self, other): 
        if not isinstance(other, Monomial): return False 
        return self.coeff == other.coeff and self.exponents == other.exponents 
    
    @property 
    def is_negative(self): 
        return self.coeff < 0
    
    @property 
    def term_join_symbol(self): 
        return '-' if self.is_negative else '+'
    
    @property 
    def without_sign(self): 
        return str(-self) if self.is_negative else str(self) 
    
    @property 
    def num_vars(self): 
        return len(self.exponents) 

    def is_linear_in(self, var): 
        if var not in self.exponents: return True 
        return self.exponents[var] in (0, 1) 
    
    @property 
    def is_linear(self): 
        return self.num_vars == 0 or (
            self.num_vars == 1 and self.is_linear_in(next(iter(self.exponents)))
        )
    
    def divides(self, other): 
        return other.exponents_larger_than(self) 
    
    def has_common_exponents(self, other) -> bool: 
        return isinstance(other, Monomial) and self.exponents == other.exponents
    
    def as_monomial(self): 
        return Monomial(coeff=1, exponents=self.exponents)

    def exponents_larger_than(self, other) -> bool: 
        for e in other.exponents: 
            if not e in self.exponents: return False 
            if self.exponents[e] < other.exponents[e]: return False 
        return True 
    
    def __hash__(self): 
        return hash((self.coeff, *self.exponents.items()))

    def scale(self, coeff): 
        return Monomial(coeff =  self.coeff * coeff, expoennts=self.exponents)
    
    def __xor__(self, value):
        return self.__pow__(value) 
    
    def __pow__(self, value): 
        new_exponents = {k: (self.exponents[k] * value) for k in self.exponents}
        return Monomial(coeff = self.coeff ** value, exponents = new_exponents)
    
    def __add__(self, other): 
        if not isinstance(other, Monomial): 
            return other + self 
        if not self.has_common_exponents(other): 
            raise ValueError(f"Cannot add two terms with different monomials")
        return Monomial(coeff=self.coeff + other.coeff, exponents=self.exponents) 
    
    def __sub__(self, other): 
        return other + (-other)
    
    def __neg__(self): 
        return Monomial(coeff = -self.coeff, exponents=self.exponents) 
    
    def __mul__(self, other): 
        if isinstance(other, numbers.Number): 
            return Monomial(coeff=self.coeff * other, exponents=self.exponents)
        if not isinstance(other, Monomial): 
            return other * self 
        new_exponents = { **self.exponents } 
        for e in other.exponents: 
            new_exponents[e] = other.exponents[e] + new_exponents.get(e, 0)
        return Monomial(coeff = self.coeff * other.coeff, exponents=new_exponents)
    
    def __rmul__(self, other): 
        return self.__mul__(other) 
    
    def __truediv__(self, other): 
        if isinstance(other, numbers.Number): 
            return Monomial(coeff=self.coeff / other, exponents=self.exponents)
        if not isinstance(other, Monomial): 
            return other / self 
        if not self.exponents_larger_than(other): raise ValueError(other) 
        new_exponents = { k: self.exponents[k] - other.exponents.get(k, 0) for k in self.exponents }
        return Monomial(coeff = self.coeff / other.coeff, exponents = new_exponents)
    
    def __getitem__(self, subst: Dict[str, Numeric]) -> Numeric: 
        """Peforms substitution on a term
        """
        exp_values = [subst[k] ** self.exponents[k] for k in self.exponents if k in subst]
        remaining_monomial = Monomial(coeff=1, exponents={ k: self.exponents[k] for k in self.exponents if k not in subst })
        new_monomial = reduce(lambda x, y: x * y, exp_values, remaining_monomial) 
        return new_monomial * self.coeff 
    
    def __repr__(self): 
        if len(self.exponents) == 0: 
            return str(self.coeff) 
        else: 
            coeff_str = str(self.coeff) if self.coeff != 1 else "" 
            exps = [power_repr(var, self.exponents[var]) for var in self.exponents]
            exps_str = "".join(exps)
            return f"{coeff_str}{exps_str}"

term_regexp = re.compile("^(-?\\d*\\.?\\d*)?((?:[a-zA-Z](?:\\^\\d+)?)*)$")
exp_regexp = re.compile("([a-zA-Z])(\\^\\d+)?")

def term(term_str: Union[str, numbers.Number]) -> Monomial: 
    if isinstance(term_str, numbers.Number): 
        return Monomial(coeff=term_str)
    term_str = term_str.strip() 
    if len(term_str) == 0: 
        return Monomial(coeff=0)
    term_matcher = term_regexp.match(term_str) 
    if term_matcher is None: 
        raise ValueError(term_str) 
    coeff: numbers.Number = 0.0
    (coeff_str, vars_str) = term_matcher.groups() 
    if coeff_str is None or coeff_str == "": 
        coeff = 1 
    elif coeff_str == "-": 
        coeff = -1
    elif coeff_str.find(".") != -1: 
        coeff = float(coeff_str)
    else: 
        coeff = int(coeff_str) 
    exp_strs = exp_regexp.findall(vars_str) 
    exps = { v: int(e[1:]) if len(e) > 0 else 1 for (v, e) in exp_strs }
    return Monomial(coeff=coeff, exponents=exps) 

def group_monomials(*terms: Monomial) -> dict[Monomial, List[Monomial]]: 
    d = defaultdict(list) 
    for t in terms: 
        d[t.as_monomial()].append(t) 
    return { **d } 

    