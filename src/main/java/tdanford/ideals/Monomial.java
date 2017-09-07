package tdanford.ideals;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import com.google.common.base.Preconditions;

public class Monomial {

  private static int[] single(final int len, final int idx, final int exp) {
    final int[] arry = new int[len];
    for (int i = 0; i < arry.length; i++) { arry[i] = i == idx ? exp : 0; }
    return arry;
  }

  private static int[] empty(final int len) {
    final int[] arry = new int[len];
    for (int i = 0; i < arry.length; i++) { arry[i] = 0; }
    return arry;
  }

  private final int[] exponents;

  public Monomial(final int width, final int var, final int exp) {
    this(single(width, var, exp));
  }

  public Monomial(int[] exponents) {
    this.exponents = exponents;
  }

  public Monomial(final int numVars) {
    this(empty(numVars));
  }

  public final int hashCode() {
    return Arrays.hashCode(exponents);
  }

  public final boolean equals(final Object o) {
    if (!(o instanceof Monomial)) { return false; }
    final Monomial m = (Monomial) o;
    return Arrays.equals(exponents, m.exponents);
  }

  public <K, D, F extends Ring<K, D>> K evaluate(final K[] values, final F ring) {
    K result = ring.one();

    for (int i = 0; i < exponents.length; i++) {
      if (exponents[i] > 0) {
        result = ring.product(result, ring.pow(values[i], exponents[i]));
      }
    }

    return result;
  }

  public int width() { return exponents.length; }
  public Integer exponent(final int i) {
    return exponents[i];
  }

  public Monomial lcm(final Monomial m) {
    Preconditions.checkArgument(m != null,
      "Cannot find LCM with null monomial");
    Preconditions.checkArgument(m.exponents.length == exponents.length,
      "Monomials must have same exponent length");

    final int[] lcm = new int[exponents.length];
    for (int i = 0; i < lcm.length; i++) {
      lcm[i] = Math.max(exponents[i], m.exponents[i]);
    }

    return new Monomial(lcm);
  }

  public boolean divides(Monomial m) {
    for (int i = 0; i < exponents.length; i++) {
      if (exponents[i] > m.exponents[i]) {
        return false;
      }
    }

    return true;
  }

  public Monomial dividedBy(final Monomial m) {
    if (!m.divides(this)) {
      throw new IllegalArgumentException();
    }

    int[] newExponents = new int[exponents.length];
    for (int i = 0; i < newExponents.length; i++) {
      newExponents[i] = exponents[i] - m.exponents[i];
    }

    return new Monomial(newExponents);
  }

  public Monomial multipliedBy(final Monomial m) {
    final int[] newExp = new int[exponents.length];
    for (int i = 0; i < newExp.length; i++) {
      newExp[i] = exponents[i] + m.exponents[i];
    }
    return new Monomial(newExp);
  }

  public Monomial changeVars(final String[] oldVars, final String[] newVars) {
    Preconditions.checkArgument(oldVars != null);
    Preconditions.checkArgument(newVars != null);
    Preconditions.checkArgument(oldVars.length == exponents.length);

    final Map<String, Integer> newMap = IntStream.range(0, newVars.length).boxed()
      .collect(toMap(i -> newVars[i], i -> i));

    final int[] newExp = new int[newVars.length];
    for (int i = 0; i < newExp.length; i++) { newExp[i] = 0; }

    for (int i = 0; i < oldVars.length; i++) {
      if (newMap.containsKey(oldVars[i])) {
        newExp[newMap.get(oldVars[i])] = exponents[i];
      }
    }
    return new Monomial(newExp);
  }


  public static Monomial zero(final int len) {
    final int[] arr = new int[len];
    for (int i = 0; i < arr.length; i++) { arr[i] = 0; }
    return new Monomial(arr);
  }

  public boolean isZero() {
    for (int v : exponents) { if (v != 0) { return false; } }
    return true;
  }

  public String renderString(final String[] variables) {
    return IntStream.range(0, variables.length)
      .filter(i -> exponents[i] != 0)
      .mapToObj(i -> exponents[i] > 1 ?
        String.format("%s^%d", variables[i], exponents[i]) :
        variables[i])
      .collect(joining("")).trim();
  }

  public int degree() {
    return IntStream.of(exponents).sum();
  }

  public Monomial oneLess(final int vidx) {
    final int[] array = new int[exponents.length];
    for (int i = 0; i < array.length; i++) {
      array[i] = i == vidx ? exponents[i] - 1 : exponents[i];
    }
    return new Monomial(array);
  }
}
