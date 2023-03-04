
from fractions import Fraction
from numbers import Number, Rational
from typing import Generic, TypeVar, Union

T = TypeVar("T")
DivResult = TypeVar("DivResult")

class Ring(Generic[T, DivResult]):
    def __init__(self, name, adjoined=[]): 
        self.name = name 
        self.adjoined = adjoined
    
    def adjoin(self, var: str) -> 'Ring[T, DivResult]': 
        return Ring(name=self.name, adjoined=self.adjoined + [var])
    
    def __getitem__(self, *vars: str) -> 'Ring[T, DivResult]': 
        return Ring(name=self.name, adjoined=self.adjoined + list(*vars)) 

    def __hash__(self): 
        return hash((self.name, tuple(self.adjoined)))

    def __eq__(self, other): 
        if not isinstance(other, Ring): return False 
        return self.name  == other.name and self.adjoined == other.adjoined
    
    def __repr__(self): 
        if len(self.adjoined) > 0: 
            adj_str = ", ".join(self.adjoined) 
            return f"{self.name}[{adj_str}]"
        else: 
            return self.name 
    
    def product(self, p1: T, p2: T) -> T: pass
    def pow(self, value: T, k: int) -> T: pass
    def sum(self, a1: T, a2: T) -> T: pass
    def negative(self, value: T) -> T: pass
    def zero(self) -> T: pass
    def one(self) -> T: pass
    def divides(self, n: T, d: T) -> bool: return True 
    def divide(self, numer: T, denom: T) -> DivResult: pass 

class NRing(Ring[int, int]): 
    def __init__(self): Ring.__init__(self, "N")
    def product(self, p1: int, p2: int) -> int: return p1 * p2
    def pow(self, value: int, k: int) -> int: return value ** k
    def sum(self, a1: int, a2: int) -> int: return a1 + a2
    def negative(self, value: int) -> int: return -value
    def zero(self) -> int: return 0
    def one(self) -> int: return 1 
    def divides(self, n: int, d: int) -> bool: return n % d == 0
    def divide(self, numer: int, denom: int) -> int: return numer // denom 

class RRing(Ring[float, float]):
    def __init__(self): Ring.__init__(self, "R")
    def product(self, p1: float, p2: float) -> float: return p1 * p2
    def pow(self, value: float, k: int) -> float: return value ** k
    def sum(self, a1: float, a2: float) -> float: return a1 + a2
    def negative(self, value: float) -> float: return -value
    def zero(self) -> float: return 0.0
    def one(self) -> float: return 1.0
    def divide(self, numer: float, denom: float) -> float: return numer / denom 

class QRing(Ring[Fraction, Fraction]): 
    def __init__(self): Ring.__init__(self, "Q")
    def product(self, p1: Fraction, p2: Fraction) -> Fraction: return p1 * p2
    def pow(self, value: Fraction, k: int) -> Fraction: return value ** k
    def sum(self, a1: Fraction, a2: Fraction) -> Fraction: return a1 + a2
    def negative(self, value: Fraction) -> Fraction: return -value
    def zero(self) -> Fraction: return Fraction(0)
    def one(self) -> Fraction: return Fraction(1)
    def divide(self, numer: Fraction, denom: Fraction) -> Fraction: return numer / denom

Q = QRing()
R = RRing()
N = NRing()

class Evaluable: 
    def __pow__(self, power) -> 'Evaluable': 
        pass 

Numeric = Union[Number, Evaluable]