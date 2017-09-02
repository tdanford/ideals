package tdanford.ideals;

import java.util.Comparator;

public enum MonomialOrdering implements Comparator<Monomial> {
  LEX(new LexComparator());
  ;

  private final Comparator<Monomial> comp;

  MonomialOrdering(final Comparator<Monomial> comp) {
    this.comp = comp;
  }

  @Override
  public int compare(final Monomial o1, final Monomial o2) {
    return comp.compare(o1, o2);
  }
}

class LexComparator implements Comparator<Monomial> {

  @Override
  public int compare(final Monomial o1, final Monomial o2) {
    if (o1.width() != o2.width()) { throw new IllegalArgumentException(); }

    for (int i = 0; i < o1.width(); i++) {
      int e1 = o1.exponent(i), e2 = o2.exponent(i);
      if (e1 > e2) {
        return -1;
      }
      if (e1 < e2) {
        return 1;
      }
    }
    return 0;
  }
}

