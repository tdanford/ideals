
from enum import Enum
import re

from .polynomials import Polynomial

equation_comparators = [
    lambda a, b: a == b, 
    lambda a, b: a < b, 
    lambda a, b: a <= b, 
    lambda a, b: a > b, 
    lambda a, b: a >= b, 
]

equation_reprs = [
    '=', '<', '<=', '>', '>='
]


class EquationOperator(Enum): 
    EQUALS = 0
    LT = 1
    LTEQ = 2 
    GT = 3
    GTEQ = 4 

    def comp(self, value1, value2): 
        return equation_comparators[self.value](value1, value2) 
    
    def __repr__(self): 
        return equation_reprs[self.value]


class Equation: 

    left: Polynomial 
    right: Polynomial
    op: EquationOperator

    def __init__(self, left: Polynomial, op: EquationOperator, right: Polynomial): 
        self.left = left 
        self.right = right 
        self.op = op 
    
    def __getitem__(self, value) -> bool: 
        return self.op.comp(self.left[value], self.right[value])
    
    def __repr__(self): 
        return f"{self.left} {self.op} {self.right}"

eq_pattern = re.compile("^(.*)(=|(<=)|(>=)|>)(.*)$")

def equation(expr: str) -> Equation: 
    return None

