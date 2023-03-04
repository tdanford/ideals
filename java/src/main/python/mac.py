#!/usr/bin/env python3 

import numbers
import functools
import subprocess

import re

class PolynomialRing: 
    def __init__(self, variables, field='QQ'):
        print('PolynomialRing(variables=%s, field=%s)' % (variables, field))
        self.field = field
        self.variables = variables
    def __repr__(self):
        return '%s[%s]' % (self.field, ','.join(self.variables))

class Monomial: 
    def __init__(self, **kwargs): 
        self.exponents = kwargs
    def variables(self): return list(self.exponents.keys())
    def __repr__(self): 
        return '*'.join(['%s^%s' % (k, self.exponents[k]) for k in self.exponents])
    def is_one(self):
        return len(self.exponents) == 0

class Term:
    def __init__(self, coefficient=1, **kwargs):
        self.coeff = coefficient
        self.monomial = Monomial(**kwargs)
    def variables(self): return self.monomial.variables()
    def __repr__(self):
        if self.monomial.is_one(): return str(self.coeff)
        return '%s*%s' % (self.coeff, self.monomial) if self.coeff != 1.0 else str(self.monomial) 
    def __mul__(self, coeff):
        return Term(coeff * self.coeff, **self.monomial.exponents)
    def __sub__(self, term):
        return self + (-term)
    def __neg__(self):
        return Term(-self.coeff, **self.monomial.exponents)
    def __add__(self, term):
        if isinstance(term, Term): 
            return Polynomial(self, term)
        elif isinstance(term, Polynomial): 
            return term + self
        elif isinstance(term, numbers.Number):
            return self + Term(term)
        else:
            raise ValueError('Unknown term type %s on value %s' % (type(term), term))

class Polynomial: 
    def __init__(self, *terms):
        self.terms = terms
    def variables(self):
        return list(set(functools.reduce(lambda x, y: x + y, [t.variables() for t in self.terms])))
    def ring(self, field='QQ'):
        return PolynomialRing(sorted(self.variables()), field)
    def __add__(self, term):
        if type(term) == Term:
            new_terms = list(self.terms) + [term]
            return Polynomial(*new_terms)
        elif type(term) == Polynomial: 
            new_terms = list(self.terms) + list(term.terms)
            return Polynomial(*new_terms)
        elif isinstance(term, numbers.Number): 
            return self + Term(term)
        else: 
            raise ValueError('Unknown term type %s on value %s' % (type(term), term))
    def __sub__(self, term):
        return self + (-term)
    def __neg__(self):
        return Polynomial(*[t.negate() for t in self.terms])
    def __repr__(self):
        base = ''
        for i in range(len(self.terms)):
            if i == 0: 
                base = str(self.terms[i])
            else:
                if self.terms[i].coeff < 0.0:
                    exps = self.terms[i].monomial.exponents
                    base = '%s - %s' % (base, Term(-self.terms[i].coeff, **exps))
                else:
                    base = '%s + %s' % (base, self.terms[i])
        return base

class Macaulay2:
    def __init__(self, m2='M2'):
        self.m2 = m2
    def groebner_basis(self, *polys, field='QQ'):
        variables = sorted(list(set(functools.reduce(lambda x, y: x + y, [t.variables() for t in polys]))))
        print(variables)
        ring = str(PolynomialRing(variables, field))
        print(ring) 
        ideal = 'gens gb ideal(%s)' % ','.join([str(p) for p in polys])
        print(ideal)

        proc = subprocess.Popen('M2', stdin=subprocess.PIPE, stdout=subprocess.PIPE)
        stdout, stderr = proc.communicate(input=bytes('\n'.join([ring, ideal]), 'utf-8'), timeout=15)
    
        result = stdout.decode('utf-8') 
        lines = result.split('\n')
        o2re = re.compile('o2\\s*=\\s*\\|\\s+([^|]+)\\s+\\|')
        o2 = [x for x in lines if x.startswith('o2')][0]

        m = o2re.match(o2)
        return re.compile('\\s+').split(m.group(1))

p1 = Term(3, z=3) - Term(y=1, w=2)
p2 = Term(y=1, z=1) - Term(x=1, w=1)
p3 = Term(y=3) - Term(x=2, z=1) 
p4 = Term(3, x=1, z=2) - Term(y=2, w=1) 

