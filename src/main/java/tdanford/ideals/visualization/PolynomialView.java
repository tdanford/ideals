package tdanford.ideals.visualization;

import static tdanford.ideals.parsing.PolynomialParser.rationalPoly;
import java.awt.*;
import java.util.Objects;
import java.util.function.Function;
import com.google.common.base.Preconditions;
import tdanford.ideals.Polynomial;
import tdanford.ideals.Rational;
import tdanford.ideals.Rationals;
import tdanford.ideals.Ring;

public class PolynomialView<K, F extends Ring<K, K>> {

  public static void main(String[] args) {
    final String polyStr = args[0];
    final Polynomial<Rational, Rationals> poly = rationalPoly(polyStr);

    new RationalPolynomialPanel.RationalPolynomialFrame(poly);
  }

  private final Viewport viewport;
  private final Polynomial<K, F> poly;
  private final Function<Double, K> lift;
  private final Function<K, Double> lower;

  public PolynomialView(
    final Polynomial<K, F> poly,
    final Function<Double, K> lift,
    final Function<K, Double> lower,
    final Viewport view
  ) {
    this.viewport = view;
    this.poly = poly;
    this.lift = lift;
    this.lower = lower;
  }

  private int scaleFrac(final double f, final int w) {
    return (int) Math.floor(f * w);
  }

  public void paint(final Graphics2D g, final int gx1, final int gy1, final int gx2, final int gy2) {
    g.setColor(Color.black);

    final int gw = gx2 - gx1;
    final int gh = gy2 - gy1;

    if (viewport.containsX(0.0)) {
      int yline = gy2 - scaleFrac(viewport.xfrac(0.0), gh);
      g.drawLine(gx1, yline, gx2, yline);
    }

    if (viewport.containsY(0.0)) {
      int xline = gx1 + scaleFrac(viewport.yfrac(0.0), gw);
      g.drawLine(xline, gy1, xline, gy2);
    }

    int px = -1, py = -1;
    int x = px, y = py;
    for (x = gx1; x < gx2; x++) {

      double polyX = viewport.fracToX((double) (x-gx1) / (double)gw);
      double polyY = lower.apply(poly.evaluate(lift.apply(polyX)));
      y = gy2 - scaleFrac(viewport.yfrac(polyY), gh);

      if (x > gx1) {
        g.drawLine(px, py, x, y);
      }

      px = x; py = y;
    }
  }
}

/**
 * Defines a region (in the space of Polynomials, i.e. the x and y we'd find in such a
 * two-variable Polynomial) which is visible and within which values must be scaled
 */
class Viewport {

  private double x1, x2;  // lower, upper bound of x-axis
  private double y1, y2;  // lower, upper bound of y-axis
  private double w, h;

  public Viewport(final double x1, final double y1, final double x2, final double y2) {
    Preconditions.checkArgument(x1 < x2);
    Preconditions.checkArgument(y1 < y2);
    this.x1 = x1;
    this.x2 = x2;
    this.y1 = y1;
    this.y2 = y2;

    this.w = x2 - x1;
    this.h = y2 - y1;
  }

  public void zoomOut() {
    final double xExpand = w/4.0, yExpand = h/4.0;
    x1 -= xExpand;
    x2 += xExpand;
    y1 -= yExpand;
    y2 += yExpand;

    w = x2 - x1;
    h = y2 - y1;
  }

  public void zoomIn() {
    final double xExpand = -w/4.0, yExpand = -h/4.0;
    x1 -= xExpand;
    x2 += xExpand;
    y1 -= yExpand;
    y2 += yExpand;

    w = x2 - x1;
    h = y2 - y1;
  }

  public boolean containsX(final double x) { return x1 <= x && x <= x2; }
  public boolean containsY(final double y) { return y1 <= y && y <= y2; }

  public double xfrac(final double x) { return (x - x1) / w; }
  public double yfrac(final double y) { return (y - y1) / h; }

  public double fracToX(final double f) { return x1 + f * w; }
  public double fracToY(final double f) { return y1 + f * h; }

  public final int hashCode() { return Objects.hash(x1, y1, x2, y2); }

  public final boolean equals(final Object o) {
    if (!(o instanceof Viewport)) { return false; }
    final Viewport v = (Viewport)o;

    return Objects.equals(x1, v.x1) &&
      Objects.equals(y1, v.y1) &&
      Objects.equals(x2, v.x2) &&
      Objects.equals(y2, v.y2);
  }
}
