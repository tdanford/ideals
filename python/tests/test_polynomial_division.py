from ideals.polynomials import poly, divide_polys
import pytest

def _test_example_IVA64(): 
    remainder, coeffs = divide_polys(
        poly("x^2y + xy^2 + y^2"), [
            poly("y^2 - 1"), 
            poly("xy - 1")
        ]
    )
    assert remainder == poly("2x + 1")
    assert coeffs == [poly("x + 1"), poly("x")]