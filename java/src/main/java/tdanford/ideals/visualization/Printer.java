package tdanford.ideals.visualization;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import tdanford.ideals.FunctionUtils;

/**
 * A Printer turns things into Strings
 *
 * This is an interface and set of implementing helper classes, for use in turning
 * matrices and other data objects into well-laid-out string representations.
 *
 * @param <V> The type of the Thing to String
 */
public interface Printer<V> extends Function<V, String> {

  default Printer<V[]> arrayPrinter(final String sep) {
    return new ArrayPrinter<>(this, sep);
  }

  default Printer<V> pad(final int width, final boolean leftAligned) {
    return new FromFunction<>(
      FunctionUtils.compose(new Padding(width, leftAligned), this)
    );
  }

  class Concatenate<C> implements Printer<C> {

    private final String separator;
    private final ImmutableList<Printer<C>> printers;

    public Concatenate(final List<Printer<C>> printers) {
      this("", printers);
    }

    public Concatenate(final String separator, final List<Printer<C>> printers) {
      this.separator = separator;
      this.printers = Lists.immutable.ofAll(printers);
    }

    @Override
    public String apply(final C c) {
      return printers.collect(p -> p.apply(c)).makeString(separator);
    }
  }

  class Constant<C> implements Printer<C> {

    private final String value;

    public Constant(final String value) {
      this.value = value;
    }

    @Override
    public String apply(final C c) {
      return value;
    }
  }

  class ColumnPrinter<C> implements Printer<C> {

    private final Printer<C> valuePrinter;

    public ColumnPrinter(final Iterable<C> columnValues) {
      int maxWidth = 1;
      final Printer<C> unpadded = new Stringify<>();
      for (final C value : columnValues) {
        maxWidth = Math.max(maxWidth, 1 + unpadded.apply(value).length());
      }

      valuePrinter = new FromFunction<>(
        FunctionUtils.compose(new Padding(maxWidth, true), unpadded)
      );
    }

    @Override
    public String apply(final C c) {
      return valuePrinter.apply(c);
    }
  }

  /**
   * Turns a function from C -&gt; String into a Printer&lt;C&gt;
   *
   * @param <C>
   */
  class FromFunction<C> implements Printer<C> {

    private final Function<C, String> func;

    public FromFunction(final Function<C, String> func) {
      this.func = func;
    }

    @Override
    public String apply(final C c) {
      return func.apply(c);
    }
  }

  class Padding implements Printer<String> {

    private final int width;

    private final boolean leftAligned;

    public Padding(final int width) {
      this(width, true);
    }

    public Padding(final int width, final boolean leftAligned) {
      this.width = width;
      this.leftAligned = leftAligned;
    }

    private String pad(final int length) {
      final StringBuilder builder = new StringBuilder();
      for (int i = 0; i < length; i++) {
        builder.append(" ");
      }
      return builder.toString();
    }

    @Override
    public String apply(final String s) {
      if (leftAligned) {
        return s + pad(Math.max(0, width - s.length()));
      } else {
        return pad(Math.max(0, width - s.length())) + s;
      }
    }
  }

  class ArrayPrinter<C> implements Printer<C[]> {

    private final String separator;
    private final Printer<C> valuePrinter;

    public ArrayPrinter(final Printer<C> printer, final String separator) {
      this.valuePrinter = printer;
      this.separator = separator;
    }

    @Override
    public String apply(final C[] cs) {
      return Stream.of(cs).map(valuePrinter).collect(Collectors.joining(separator));
    }
  }
  class Stringify<C> implements Printer<C> {

    @Override
    public String apply(final C c) {
      return String.valueOf(c);
    }
  }
}
