package tdanford.ideals;

import java.util.Iterator;
import java.util.stream.StreamSupport;

import com.google.common.base.Preconditions;

public class Matrix<V, F extends Field<V>> {

  private F ops;
  private int rows, cols;
  private V[] flat;
  private boolean rowMajor;

  /**
   * Construct an arbitrary matrix in row-major orientation.
   *
   * @param rows
   * @param cols
   * @param field
   * @param values
   */
  public Matrix(int rows, int cols, F field, V... values) {
    this(rows, cols, field, true, values);
  }

  /**
   * Identity matrix.
   *
   * @param dim
   * @param field
   */
  public Matrix(int dim, F field) {
    this(dim, field, field.one());
  }

  /**
   * Square matrix with arbitrary diagonal (dimension set by the size of the diagonal)
   *
   * @param field
   * @param diagonal
   */
  public Matrix(F field, V... diagonal) {
    this.rows = diagonal.length;
    this.cols = this.rows;
    this.ops = field;
    this.rowMajor = true;
    flat = ops.array(rows * cols);
    for (int r = 0, i = 0; r < this.rows; r++) {
      for (int c = 0; c < this.cols; c++, i++) {
        flat[i] = r == c ? diagonal[r] : ops.zero();
      }
    }
  }

  private class SkipIterator implements Iterator<V> {

    private final int start;
    private final int skip;
    private final int end;
    private int next;

    public SkipIterator(final int start, final int end, final int skip) {
      this.start = start;
      this.skip = skip;
      this.end = end;
      this.next = start;
    }

    @Override
    public boolean hasNext() {
      return next < end;
    }

    @Override
    public V next() {
      final V n = hasNext() ? flat[next] : null;
      next = next + skip;
      return n;
    }
  }

  public V[] getColumn(final int c) {
    return StreamSupport.stream(column(c).spliterator(), false).toArray(ops::array);
  }

  public V[] getRow(final int r) {
    return StreamSupport.stream(row(r).spliterator(), false).toArray(ops::array);
  }

  public Iterable<V> column(final int c) {
    return () -> iterateColumn(c);
  }

  public Iterable<V> row(final int r) {
    return () -> iterateRow(r);
  }

  public Iterator<V> iterateColumn(final int c) {
    return rowMajor ?
      new SkipIterator(c, flat.length, cols) :
      new SkipIterator(c * rows, (c + 1) * rows, 1);
  }

  public Iterator<V> iterateRow(final int r) {
    return rowMajor ?
      new SkipIterator(r * cols, (r + 1) * cols, 1) :
      new SkipIterator(r, flat.length, rows);
  }

  /**
   * Square matrix with constant diagonal.
   *
   * @param dim
   * @param field
   * @param diagonal
   */
  public Matrix(int dim, F field, V diagonal) {
    this.ops = field;
    this.rows = this.cols = dim;
    rowMajor = true;
    flat = ops.array(dim * dim);
    for (int r = 0, i = 0; r < dim; r++) {
      for (int c = 0; c < dim; c++, i++) {
        flat[i] = r == c ? diagonal : ops.zero();
      }
    }
  }

  private Matrix(int rows, int cols, F field, boolean rowMajor, V... values) {
    if (rows <= 0) {
      throw new IllegalArgumentException("Illegal rows " + rows);
    }
    if (cols <= 0) {
      throw new IllegalArgumentException("Illegal rows " + cols);
    }
    if (field == null) {
      throw new IllegalArgumentException("Null field");
    }
    if (values == null || values.length != rows * cols) {
      throw new IllegalArgumentException("Illegal values length");
    }

    this.rows = rows;
    this.cols = cols;
    this.ops = field;
    this.flat = values;
    this.rowMajor = rowMajor;
  }

  public int index(int row, int col) {
    return rowMajor ? row * cols + col : col * rows + row;
  }

  public V get(int row, int col) {
    return flat[index(row, col)];
  }

  public boolean isSquare() {
    return rows == cols;
  }

  public int hashCode() {
    int code = 17;
    code += rows;
    code *= 37;
    code += cols;
    code *= 37;
    code += rowMajor ? 1 : 0;
    code *= 37;
    for (int i = 0; i < flat.length; i++) {
      code += flat[i].hashCode();
      code *= 37;
    }
    return code;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Matrix)) {
      return false;
    }
    Matrix<V, F> m = (Matrix<V, F>) o;
    if (rows != m.rows || cols != m.cols) {
      return false;
    }
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (!get(r, c).equals(m.get(r, c))) {
          return false;
        }
      }
    }

    return true;
  }

  public Matrix<V, F> copy() {
    V[] array = ops.array(rows * cols);
    for (int i = 0; i < array.length; i++) {
      array[i] = flat[i];
    }
    return new Matrix<V, F>(rows, cols, ops, rowMajor, array);
  }

  private Matrix<V, F> multiply(Matrix<V, F> multiplicand) {
    Preconditions.checkArgument(multiplicand != null);
    Preconditions.checkArgument(cols == multiplicand.rows);

    V[] newFlat = ops.array(rows * multiplicand.cols);
    for (int r = 0, i = 0; r < rows; r++) {
      for (int c = 0; c < multiplicand.cols; c++, i++) {
        newFlat[i] = rowColumnInnerProduct(r, multiplicand, c);
      }
    }

    this.cols = multiplicand.cols;
    this.rowMajor = true;
    this.flat = newFlat;

    return this;
  }

  public V rowColumnInnerProduct(int row, Matrix<V, F> other, int col) {
    Preconditions.checkArgument(other != null);
    Preconditions.checkArgument(row >= 0 && row < rows, String.format("Row %d not in [%d, %d)", row, 0, rows));
    Preconditions.checkArgument(col >= 0 && col < cols, String.format("Col %d not in [%d, %d)", col, 0, other.cols));
    Preconditions.checkArgument(cols == other.rows, "Inner row/column dimensions don't match");

    V sum = ops.zero();
    for (int i = 0; i < cols; i++) {
      sum = ops.sum(sum, ops.product(get(row, i), other.get(i, col)));
    }
    return sum;
  }

  public Matrix<V, F> add(Matrix<V, F> addend) {
    Preconditions.checkArgument(addend != null);
    Preconditions.checkArgument(rows == addend.rows && cols == addend.cols);

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        int i = index(r, c), ai = addend.index(r, c);
        flat[i] = ops.sum(flat[i], addend.flat[ai]);
      }
    }

    return this;
  }

  public Matrix<V, F> transpose() {
    rowMajor = !rowMajor;
    return this;
  }

  public Matrix<V, F> subtractScaledRow(int row, V scale, int fromRow) {
    Preconditions.checkArgument(row >= 0 && row < rows);
    Preconditions.checkArgument(scale != null);
    Preconditions.checkArgument(fromRow >= 0 && fromRow < rows);

    for (int c = 0, i = fromRow * cols; c < cols; c++, i++) {
      V addend = ops.negative(ops.product(scale, get(row, c)));
      flat[i] = ops.sum(flat[i], addend);
    }
    return this;
  }

  public Matrix<V, F> subtractScaledRowFromAll(int row, V scale) {
    Preconditions.checkArgument(row >= 0 && row < rows);
    Preconditions.checkArgument(scale != null);

    for (int r = 0; r < rows; r++) {
      if (r != row) {
        subtractScaledRow(row, scale, r);
      }
    }
    subtractScaledRow(row, scale, row);
    return this;
  }
}
