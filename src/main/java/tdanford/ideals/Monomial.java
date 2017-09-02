package tdanford.ideals;

public class Monomial {

  private int[] exponents;

  public Monomial(int[] exponents) {
    this.exponents = exponents;
  }

  public <K, D, F extends Ring<K, D>> K evaluate(final K[] values, final F ring) {
    K result = ring.zero();

    for (int i = 0; i < exponents.length; i++) {
      if (exponents[i] > 0) {
        result = ring.product(result, exponentiate(ring, values[i], exponents[i]));
      }
    }

    return result;
  }

  private <K, D, F extends Ring<K, D>> K exponentiate(final F ring, K base, int exp) {
    K result = base;
    while (exp > 1) {
      if (exp % 2 == 0) {
        exp /= 2;
        result = ring.product(result, result);
      } else {
        exp -= 1;
        result = ring.product(result, base);
      }
    }
    return result;
  }

  public Integer exponent(final int i) {
    return exponents[i];
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


  public static Monomial zero(final int len) {
    final int[] arr = new int[len];
    for (int i = 0; i < arr.length; i++) { arr[i] = 0; }
    return new Monomial(arr);
  }
}
