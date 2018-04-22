package tdanford.ideals;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static tdanford.ideals.MonomialOrdering.LEX;
import static tdanford.ideals.parsing.PolynomialParser.rationalPoly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tdanford.ideals.parsing.PolynomialParser;

/**
 * A wrapper around a (locally installed) Macaulay2 installation
 *
 * @param <K> The type of the underlying coefficient value
 */
public class Macaulay2<K, F extends Field<K>, PR extends PolynomialRing<K, F>> {

  public static final PolynomialRing<Rational, Rationals> KXY =
    new PolynomialRing<>(LEX, Rationals.FIELD, "x", "y");

  public static Polynomial<Rational, Rationals> kxyPoly(final String str) {
    return rationalPoly(str, KXY.variables());
  }

  public static PolynomialSet<Rational, Rationals> kxyPolys(final String... strs) {
    return new PolynomialSet<>(KXY,
      Stream.of(strs).map(Macaulay2::kxyPoly).collect(toList())
    );
  }

  public static void main(final String[] args) {
    final PolynomialSet<Rational, Rationals> F = kxyPolys(
      "x^3y - 2x^2y^2 + x",
      "3x^4 - y"
    );

    final Macaulay2<Rational, Rationals, PolynomialRing<Rational, Rationals>> mac =
      new Macaulay2<>(
        new PolynomialRing<>(LEX, Rationals.FIELD, F.variables())
      );

    mac.calculateGroebnerBasis(F);
  }

  private static final Logger LOG = LoggerFactory.getLogger(Macaulay2.class);

  private PR polyRing;

  public Macaulay2(final PR polyRing) {
    this.polyRing = polyRing;
  }

  public PolynomialSet<K, F> calculateGroebnerBasis(
    final Iterable<Polynomial<K, F>> polys
  ) {

    final MutableList<Polynomial<K, F>> polyList = Lists.mutable.ofAll(polys);

    final String ringLine =
      String.format("QQ[%s, MonomialOrder => Lex]", Stream.of(polyRing.variables()).collect
        (joining(", ")));

    final String polyLine =
      String.format("gens gb ideal(%s)", polyList.stream().map(this::polyString).collect(joining
        (", ")));

    final ProcessBuilder builder = new ProcessBuilder("M2");
    builder.redirectInput(ProcessBuilder.Redirect.PIPE);

    try {
      final Process process = builder.start();

      final OutputStream stdin = process.getOutputStream();

      try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(stdin, "UTF-8"))) {

        pw.println(ringLine);
        pw.println(polyLine);
      }

      process.waitFor();

      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      IOUtils.copy(process.getInputStream(), byteArrayOutputStream);

      final String output = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
      LOG.info(output);

    } catch (IOException e) {
      e.printStackTrace(System.err);

    } catch (InterruptedException e) {
      e.printStackTrace(System.err);
    }

    Iterable<Polynomial<K, F>> basis = Lists.mutable.empty();

    return new PolynomialSet<>(polyRing, basis);
  }

  private String polyString(final Polynomial<K, F> poly) {
    return poly.renderString("*");
  }
}
