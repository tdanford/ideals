package tdanford.ideals;

import org.junit.*;

import static org.junit.Assert.*;

public class MatrixTest {

    public static final RationalField f = new RationalField();

    public static Rational r(int n, int d) { return new Rational(n, d); }

    public static Matrix<Rational, RationalField> matrix(int rows, int cols, Rational... rat) {
        return new Matrix<Rational, RationalField>(rows, cols, f, rat);
    }

    @Test
    public void testMatrixAdd() {
        assertEquals(
                matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)),

                matrix(2, 2, r(0,5), r(1,5), r(2,5), r(3,5))
                        .add(matrix(2, 2, r(1,5), r(1,5), r(1,5), r(1,5))));
    }
}
