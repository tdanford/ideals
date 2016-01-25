package tdanford.ideals;

import org.junit.*;
import java.util.regex.*;
import static org.junit.Assert.*;

public class MonomialTest {

    public static final Pattern exponentiated = Pattern.compile("([a-zA-Z][a-zA-Z0-9_\\-]*^\\d+)+");


    @Test
    public void testDivides() {
        RationalField field = new RationalField();

        //Monomial m1 = new Monomial(field, new Rational(1) );
    }


}
