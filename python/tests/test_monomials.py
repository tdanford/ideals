
import pytest 
from ideals.monomials import term, Monomial

def test_bad_term(): 
    with pytest.raises(ValueError) as error:
        term("---")

def test_bad_coeff(): 
    with pytest.raises(ValueError) as error: 
        Monomial(coeff="x")
    
def test_negative_monomial(): 
    assert term("-x") == term("-1x") 

def test_repr(): 
    assert str(term("")) == "0" 
    assert str(term("1")) == "1" 
    assert str(term("-1")) == "-1"
    assert str(term("x^2")) == "x\u00b2"

def test_is_linear_in(): 
    assert term("").is_linear_in("x")
    assert term("").is_linear_in("y")
    assert term("2x").is_linear_in("x")
    assert term("2x").is_linear_in("y")
    assert not term("2x^2").is_linear_in("x")
    assert term("2x^2").is_linear_in("y")
    assert term("2xy").is_linear_in("x")
    assert term("2xy").is_linear_in("y")
    assert term("2xy").is_linear_in("x")
    assert not term("2xy^2").is_linear_in("y")

def test_is_linear(): 
    assert term("").is_linear
    assert term("2").is_linear
    assert term("2x").is_linear
    assert not term("2x^2").is_linear
    assert not term("2xy").is_linear


def test_pow(): 
    assert term("3") ** 2 == term("9") 
    assert term("3x") ** 2 == term("9x^2") 
    assert term("-3x^2") ** 2 == term("9x^4")
    assert term("2y") ** 1 == term("2y")
    assert term("2y") ** 0 == term("1")

def test_raw_exponents(): 
    assert  term("-3x").coeff == -3 
    assert  term("-3x").exponents == { "x": 1 }
    assert  term("-3x^2").coeff == -3 
    assert  term("-3x^2").exponents == { "x": 2 }

def test_float_coeff(): 
    assert str(term("2.0x")) == "2.0x"

def test_eq(): 
    assert term("") == term("0")
    assert term(0) == term("0")
    assert term(1) == term("1")
    assert term(2) != term(3) 
    assert term(2) == term(2.0) 
    assert term("x") == term("x") 
    assert term("xy") == term("yx") 
    assert term("x^2y") != term("yx") 
    assert term("x^2y")[{"x": 2}] == term("4y") 

def test_has_common_exponents(): 
    assert term("x^2y").has_common_exponents(term("x^2y"))
    assert term("2x^2y").has_common_exponents(term("x^2y"))
    assert term("x^2y").has_common_exponents(term("2x^2y"))
    assert not term("x^2y").has_common_exponents(term("xy^2"))

def test_zero(): 
    zero = term("")
    assert Monomial(coeff=0) == zero
    assert zero * term("2x^2") == zero 
    assert term("2x^2") * zero == zero 

def test_divides(): 
    assert term("x^2y").divides(term("x^2y"))
    assert term("2x^2y").divides(term("x^2y"))
    assert not term("x^2y").divides(term("2xy"))
    assert not term("x^2y").divides(term("2xy^2"))
    assert term("x^2y").divides(term("2x^2y^2"))
    assert not term("x^2y").divides(term("xy^2"))

def test_monomial_divides(): 
    assert term("x^2y") / term("x") == term("xy")
    assert term("x^2y") / term("x^2") == term("y")
    assert term("x^2y") / term("y") == term("x^2")
    assert term("2x^2y") / term("y") == term("2x^2")

def test_dict_key(): 
    d = {} 
    d[term("2x^2y")] = 1 
    assert term("2x^2y") in d 
    assert term("x^2y") not in d 
    assert term("2xy") not in d 
    assert term("2x^2") not in d 

def test_substitution(): 
    assert term("1")[{"x": 3}] == term(1)
    assert term("x")[{"x": 3}] == term(3) 
    assert term("2x")[{"x": 3}] == term(6)
    assert term("2xy")[{"x": 3}] == term("6y")
    assert term("2x^2")[{"x": 3}] == term(18)
    assert term("2x^2y")[{"x": 3}] == term("18y")

def test_substitute_monomials(): 
    assert term("1")[{"x": term("2y")}] == term(1)
    assert term("x")[{"x": term("2y")}] == term("2y") 
    assert term("2x")[{"x": term("2y")}] == term("4y")
    assert term("2xy")[{"x": term("2y")}] == term("4y^2")
    assert term("2x^2")[{"x": term("2y")}] == term("8y^2")
    assert term("2x^2y")[{"x": term("2y")}] == term("8y^3")
