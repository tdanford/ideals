package tdanford.ideals;

import org.junit.*;
import java.util.regex.*;

public class MonomialTest {

    public static final Pattern exponentiated = Pattern.compile("([a-zA-Z][a-zA-Z0-9_\\-]*^\\d+)+");


    @Test
    public void testDivides() {
        Rationals field = new Rationals();

        //Monomial m1 = new Monomial(field, new Rational(1) );
    }


}
