package tdanford.ideals;

import org.junit.*;

import static org.junit.Assert.*;

public class MatrixTest {

    public static <V> V[] array(final V... vs) { return vs ;}

    public static Rational r(int n, int d) { return new Rational(n, d); }

    public static Matrix<Rational, Rationals> matrix(int rows, int cols, Rational... rat) {
        return new Matrix<>(rows, cols, Rationals.FIELD, rat);
    }

    @Test
    public void testGet() {
      assertEquals(
        matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)).get(0, 1),
        r(2, 5)
      );
    }

    @Test
    public void testIsSquare() {
      assertTrue(
        matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)).isSquare()
      );
      assertTrue(
        matrix(1, 1, r(1,5)).isSquare()
      );
      assertFalse(
        matrix(1, 2, r(1,5), r(2, 4)).isSquare()
      );
      assertFalse(
        matrix(1, 2, r(1,5), r(2, 4)).isSquare()
      );
    }

    @Test
    public void testCol() {
        assertArrayEquals(
          matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)).getColumn(0),
          array(r(1, 5), r(3, 5))
        );
        assertArrayEquals(
          matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)).getColumn(1),
          array(r(2, 5), r(4, 5))
        );
        assertArrayEquals(
          matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)).transpose().getColumn(1),
          array(r(3, 5), r(4, 5))
        );
    }

    @Test
    public void testRow() {
        assertArrayEquals(
          matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)).getRow(0),
          array(r(1, 5), r(2, 5))
        );
        assertArrayEquals(
          matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)).getRow(1),
          array(r(3, 5), r(4, 5))
        );
        assertArrayEquals(
          matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)).transpose().getRow(1),
          array(r(2, 5), r(4, 5))
        );
    }

    @Test
    public void testMatrixAdd() {
        assertEquals(
                matrix(2, 2, r(1,5), r(2,5), r(3,5), r(4,5)),

                matrix(2, 2, r(0,5), r(1,5), r(2,5), r(3,5))
                        .add(matrix(2, 2, r(1,5), r(1,5), r(1,5), r(1,5))));
    }
}
