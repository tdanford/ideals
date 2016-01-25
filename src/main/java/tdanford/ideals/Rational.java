package tdanford.ideals;

public class Rational {

    private int numer, denom;

    public Rational(int num, int denom) {
        this.numer = num;
        this.denom = denom;
        reduce();
    }

    public Rational(int num) {
        this.numer = num;
        this.denom = 1;
    }

    public Rational product(Rational r) {
        return new Rational(numer * r.numer, denom * r.denom);
    }

    public Rational sum(Rational r) {
        return new Rational(numer * r.denom + r.numer * denom, denom * r.denom);
    }

    public Rational negative() { return new Rational(-numer, denom); }

    public Rational inverse() { return new Rational(denom, numer); }

    private void reduce() {
        int gcd = gcd(numer, denom);
        numer /= gcd;
        denom /= gcd;
    }

    public int hashCode() {
        int code = 17;
        code += numer; code *= 37;
        code += denom; code *= 37;
        return code;
    }

    public boolean equals(Object o) {
        if(!(o instanceof Rational)) { return false; }
        Rational r = (Rational)o;
        return r.numer == numer && r.denom == denom;
    }

    private static int gcd(int a, int b) {
        while(b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

}

