
from ideals import R, Q, N

def test_ring_representations(): 
    assert str(R) == "R" 
    assert str(Q) == "Q" 
    assert str(Q["x"]) == "Q[x]"
    assert str(Q["x", "y"]) == "Q[x, y]"

def test_ring_hash(): 
    assert isinstance(hash(Q), int) 
    assert isinstance(hash(Q["x"]), int)
    assert isinstance(hash(Q["x", "y"]), int)
    assert hash(R) != hash(N) 
    assert hash(Q["x", "y"]) != hash(Q["y", "x"])

def test_ring_eq(): 
    assert Q == Q 
    assert Q != N

def test_ring_eq_ajoin(): 
    assert Q["x"] == Q["x"]
    assert Q["x"] != N["x"]
    assert Q["x", "y"] == Q["x", "y"]
    assert Q["x", "y"] != Q["y", "x"]
    assert N["x"]["y"] == N["x", "y"]
    assert N["x"]["y"] != N["y", "x"]

def test_nring(): 
    assert N.sum(1, 2) == 3
    assert N.product(4, 7) == 28 
    assert N.pow(2, 5) == 32