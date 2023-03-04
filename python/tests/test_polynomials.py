
from ideals.polynomials import poly 

def test_repr(): 
    assert str(poly("1 + x")) == "x+1"
    assert str(poly("x + x^3")) == "x\u00b3+x"

def test_eq(): 
    assert poly("1+x") == poly("1 + x") 
    assert poly("1+x") == poly("x + 1")
    assert poly("1-x") == poly("-x + 1")
    assert poly("x^2 + 2x^2") == poly("3x^2") 
    assert poly("x + x^3") == poly("x^3 + x") 

def test_multiply_poly(): 
    assert poly("1 + x^2") * poly("x") == poly("x + x^3") 

def test_exp(): 
    assert poly("1") ** 2 == poly("1")
    assert poly("x") ** 2 == poly("x^2")
    assert poly("2x^2") ** 2 == poly("4x^4")
    assert poly("x + 1") ** 2 == poly("x^2 + 2x + 1")

def test_substitute_polys(): 
    assert poly("1")[{"x": poly("3y^2")}] == poly("1")
    assert poly("x")[{"x": poly("3y^2")}] == poly("3y^2")
    assert poly("x + y")[{"x": poly("3y^2"), "y": poly("3y^2")}] == poly("6y^2")
    assert poly("x + y")[{"x": poly("3y^2")}] == poly("3y^2 + y")
    assert poly("x^2 + x")[{"x": poly("2y")}] == poly("4y^2 + 2y")

def test_is_linear(): 
    assert poly("1").is_linear 
    assert poly("x").is_linear 
    assert poly("x+y").is_linear 
    assert poly("x+2y").is_linear 
    assert poly("3x+2y").is_linear 
    assert not poly("3x^2+2y").is_linear 
    assert not poly("3x+2y^2").is_linear 
