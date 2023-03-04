package tdanford.ideals;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static tdanford.ideals.MonomialOrdering.LEX;
import static tdanford.ideals.parsing.PolynomialParser.rationalPoly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
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
        new PolynomialRing<>(LEX, Rationals.FIELD, F.variables()),
        (str, negative) -> negative ? Rationals.parse(str).negative() : Rationals.parse(str)
      );

    mac.calculateGroebnerBasis(F);
  }

  private static final Logger LOG = LoggerFactory.getLogger(Macaulay2.class);

  private PR polyRing;

  private BiFunction<String, Boolean, K> coefficientParser;

  public Macaulay2(final PR polyRing, final BiFunction<String, Boolean, K> parser) {
    this.polyRing = polyRing;
    this.coefficientParser = parser;
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
      //LOG.info(output);

      final Pattern outputPattern = Pattern.compile("o2\\s*=\\s*\\|([^|]*)\\|");
      final Matcher outputMatcher = outputPattern.matcher(output);

      if (outputMatcher.find()) {
        LOG.info(outputMatcher.group(0));

        final String[] polyStrings = outputMatcher.group(1).split("\\s+");
        final PolynomialSet<K, F> polySet = parseMacaulayPolynomials(polyStrings, polyRing
          .variables());

        LOG.info(polySet.toString());

        return polySet;
      }

    } catch (IOException e) {
      e.printStackTrace(System.err);

    } catch (InterruptedException e) {
      e.printStackTrace(System.err);
    }

    Iterable<Polynomial<K, F>> basis = Lists.mutable.empty();

    return new PolynomialSet<>(polyRing, basis);
  }

  private Pattern macaulayTermPattern(final String[] variables) {
    final String varString = String.format("(?:%s)",
      Stream.of(variables).collect(joining("|")));
    final String patternString = String.format("\\s*(\\+|\\-)?\\s*(\\d*)((?:%s\\d*)*)", varString);

    return Pattern.compile(patternString);
  }

  private Monomial parseMacaulayMonomial(final String str, final Pattern p, final String[] vars) {
    final Map<String, Integer> varMap = IntStream.range(0, vars.length)
      .boxed().collect(toMap(i -> vars[i], i -> i));
    final int[] exps = new int[vars.length];

    final Matcher m = p.matcher(str);
    int i = 0;
    while (i < str.length() && m.find(i)) {
      final String var = m.group(1);
      final Integer exp = m.group(2) != null && !m.group(2).isEmpty()
        ? Integer.parseInt(m.group(2)) : 1;

      exps[varMap.get(var)] = exp;

      i = m.end(0);
    }

    return new Monomial(exps);
  }

  public PolynomialSet<K, F> parseMacaulayPolynomials(
    final String[] polys,
    final String[] variables
  ) {
    return new PolynomialSet<>(
      polyRing,
      Stream.of(polys).map(p -> parseMacaulayPolynomial(p, variables)).collect(toList())
    );
  }

  public Polynomial<K, F> parseMacaulayPolynomial(
    final String poly,
    final String[] variables
  ) {
    final Pattern termPattern = macaulayTermPattern(variables);
    final Matcher matcher = termPattern.matcher(poly);

    final String varString = String.format("(?:%s)",
      Stream.of(variables).collect(joining("|")));
    final Pattern expPattern = Pattern.compile(String.format("(%s)(\\d*)", varString));

    int i = 0;

    final MutableMap<Monomial, K> termMap = Maps.mutable.empty();

    while (i < poly.length() && matcher.find(i)) {
      //LOG.info("position {} matching \"{}\"", i, poly.substring(i));
      boolean negative = matcher.group(1) != null && matcher.group(1).equals("-");
      String coeffString = matcher.group(2);

      final K coeff = coeffString.isEmpty() ? polyRing.coefficientField().zero() :
        coefficientParser.apply(coeffString, negative);

      final String vars = matcher.group(3);

      final Monomial m = parseMacaulayMonomial(vars, expPattern, variables);

      termMap.put(m, coeff);

      i = matcher.end(0);
    }

    return new Polynomial<>(polyRing, termMap);
  }

  private String polyString(final Polynomial<K, F> poly) {
    return poly.renderString("*");
  }
}
