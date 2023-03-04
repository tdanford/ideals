
from ideals.equations import EquationOperator

def test_eq_operators(): 
    assert EquationOperator.EQUALS.comp(3, 3) 
    assert not EquationOperator.EQUALS.comp(3, 4) 
    assert not EquationOperator.EQUALS.comp(4, 3) 

    assert not EquationOperator.LT.comp(3, 3) 
    assert EquationOperator.LT.comp(3, 4) 
    assert not EquationOperator.LT.comp(4, 3) 

    assert EquationOperator.LTEQ.comp(3, 3) 
    assert EquationOperator.LTEQ.comp(3, 4) 
    assert not EquationOperator.LTEQ.comp(4, 3) 

    assert not EquationOperator.GT.comp(3, 3) 
    assert not EquationOperator.GT.comp(3, 4) 
    assert EquationOperator.GT.comp(4, 3) 

    assert EquationOperator.GTEQ.comp(3, 3) 
    assert not EquationOperator.GTEQ.comp(3, 4) 
    assert EquationOperator.GTEQ.comp(4, 3) 


