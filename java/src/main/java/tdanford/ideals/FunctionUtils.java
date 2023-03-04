package tdanford.ideals;

import java.util.function.Function;

public abstract class FunctionUtils {

  public static <A, B, C> Function<A, C> compose(final Function<B, C> f2, final Function<A, ?
    extends B> f1) {

    return new Composition<>(f2, f1);
  }

  static class Composition<A, B, C> implements Function<A, C> {

    private Function<A, ? extends B> f1;
    private Function<B, C> f2;

    public Composition(final Function<B, C> f2, final Function<A, ? extends B> f1) {
      this.f1 = f1;
      this.f2 = f2;
    }

    @Override
    public C apply(final A a) {
      return f2.apply(
        f1.apply(a)
      );
    }
  }
}
