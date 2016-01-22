package tdanford.ideals;

import org.junit.*;
import static org.junit.Assert.*;

public class RationalTest {

    public static final Field<Rational> f = new RationalField();

    public static Rational ratio(int numer, int denom) { return new Rational(numer, denom); }

    @Test
    public void testEquality() {
        assertEquals(new Rational(1, 2), new Rational(1, 2));
        assertEquals(new Rational(2, 4), new Rational(1, 2));
    }

    @Test
    public void testAddition() {
        assertEquals(ratio(3, 5), f.sum(ratio(1, 5), ratio(2, 5)));
    }

    @Test
    public void testMultiplication() {
        assertEquals(ratio(4, 15), f.product(ratio(2, 3), ratio(2, 5)));
    }


}
