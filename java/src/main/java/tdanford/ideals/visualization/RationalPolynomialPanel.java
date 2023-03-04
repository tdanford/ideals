package tdanford.ideals.visualization;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import tdanford.ideals.Polynomial;
import tdanford.ideals.Rational;
import tdanford.ideals.Rationals;

public class RationalPolynomialPanel extends JPanel {

  private final Viewport port;
  private final PolynomialView<Rational, Rationals> view;

  public RationalPolynomialPanel(final Polynomial<Rational, Rationals> poly) {
    this.port = new Viewport(-10.0, -10.0, 10.0, 10.0);
    this.view = new PolynomialView<>(poly, Rationals::fromDouble, Rational::doubleValue, port);
  }

  public void zoomOut() {
    port.zoomOut();
    repaint();
  }

  public void zoomIn() {
    port.zoomIn();
    repaint();
  }

  protected void paintComponent(final Graphics g) {
    final int w = getWidth(), h = getHeight();
    if (w > 0 && h > 0) {
      g.setColor(Color.white);
      g.fillRect(0, 0, w, h);

      view.paint((Graphics2D) g, 0, 0, w, h);
    }
  }

  public static class RationalPolynomialFrame extends JFrame {

    private final RationalPolynomialPanel panel;

    public RationalPolynomialFrame(final Polynomial<Rational, Rationals> poly) {
      super("Rational Polynomial");

      this.panel = new RationalPolynomialPanel(poly);
      panel.setPreferredSize(new Dimension(400, 400));

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      Container c = (Container) getContentPane();
      c.setLayout(new BorderLayout());

      c.add(panel, BorderLayout.CENTER);

      JPanel buttonPanel = new JPanel(new FlowLayout());
      buttonPanel.add(new JButton(zoomOut()));
      buttonPanel.add(new JButton(zoomIn()));

      c.add(buttonPanel, BorderLayout.SOUTH);

      setVisible(true);
      pack();
    }


    public Action zoomOut() {
      return new AbstractAction("--") {
        @Override
        public void actionPerformed(final ActionEvent e) {
          panel.zoomOut();
        }
      };
    }

    public Action zoomIn() {
      return new AbstractAction("++") {
        @Override
        public void actionPerformed(final ActionEvent e) {
          panel.zoomIn();
        }
      };
    }
  }
}
