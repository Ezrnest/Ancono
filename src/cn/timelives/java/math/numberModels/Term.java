package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.javaImpl.JGroupCalculator;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.addableSet.MathAdder;
import cn.timelives.java.math.numberModels.api.Computable;
import cn.timelives.java.math.numberModels.api.Simplifier;
import cn.timelives.java.math.numberTheory.combination.CombUtils;
import cn.timelives.java.math.property.Mergeable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.timelives.java.utilities.Printer.print;

/**
 * Term is an improved class for representing a single term in a polynomial. It is designed to compute and output in
 * full precision.
 * A term can be represented as
 * <pre>p/q*sqrt(r)*<i>(character and power)</i></pre>, where p,q,r are integers.
 * The power of a character in a term must be a rational number whose numerator and denominator can be
 * presented as long.
 * <h3></h3>
 */
public final class Term implements Mergeable<Term>, Comparable<Term>, Computable, Serializable {
    /*
     * The signum of this term, signum == 0 <=> this == 0
     */
    final int signum;
    /**
     * numerator, denominator, and radical
     * all non-negative
     */
    final BigInteger numerator, denominator, // non-zero
            radical;
    /**
     * characters and powers, sorted by default or specific order.
     */
    final NavigableMap<String, Fraction> character;

    private static final NavigableMap<String, Fraction> empty = Collections.emptyNavigableMap();

    private transient int hashCode;


    //constants:
    /**
     * Describes the character constant value Pi
     *
     * @see MathCalculator#STR_PI
     */
    public static final String PI_STR = MathCalculator.STR_PI;
    /**
     * Describes the character constant value e
     *
     * @see MathCalculator#STR_E
     */
    public static final String E_STR = MathCalculator.STR_E;
    /**
     * Describes the character constant value i
     *
     * @see MathCalculator#STR_I
     */
    public static final String I_STR = MathCalculator.STR_I;

    /**
     * Describes the operation of square root in a term.
     */
    public static String SQR_STR = "Sqr";

    public static String PI_SHOW = "π";
    public static String SQR_SHOW = "Sqr";// √

    /**
     * The Term constant 0
     */
    public static final Term ZERO = new Term(0, BigInteger.ZERO);
    /**
     * The Term constant 1
     */
    public static final Term ONE = new Term(1, BigInteger.ONE);
    /**
     * The Term constant -1
     */
    public static final Term NEGATIVE_ONE = new Term(-1, BigInteger.ONE);

    /**
     * The Term constant 10
     */
    public static final Term TEN = new Term(1, BigInteger.TEN);

    /**
     * The Term constant 2
     */
    public static final Term TWO = new Term(1, BigInteger.TWO);

    /**
     * The Term constant Pi
     */
    public static final Term PI = singleChar(PI_STR);

    /**
     * The Term constant e
     */
    public static final Term E = singleChar(E_STR);
    /**
     * The Term constant i
     */
    public static final Term I = singleChar(I_STR);


    Term(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical, NavigableMap<String, Fraction> character) {
        this.signum = signum;
        this.numerator = numerator;
        this.denominator = denominator;
        this.radical = radical;
        this.character = character;
    }

    Term(int signum, BigInteger numerator) {
        this(signum, numerator, BigInteger.ONE, BigInteger.ONE, empty);
    }

    Term(int signum, BigInteger numerator, BigInteger denominator) {
        this(signum, numerator, denominator, BigInteger.ONE, empty);
    }

    Term(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical) {
        this(signum, numerator, denominator, radical, empty);
    }

    Term(NavigableMap<String, Fraction> character) {
        this(1, BigInteger.ONE, BigInteger.ONE, BigInteger.ONE, character);
    }

    Term(int signum, BigInteger[] ndr, NavigableMap<String, Fraction> character) {
        this(signum, ndr[0], ndr[1], ndr[2], character);
    }

    /**
     * Gets the numerator of this term.
     *
     * @return the numerator
     */
    public BigInteger numerator() {
        return numerator;
    }

    /**
     * Gets the denominator of this term.
     */
    public BigInteger denominator() {
        return denominator;
    }

    /**
     * Gets the radical part of this term.
     */
    public BigInteger radical() {
        return radical;
    }

    /**
     * Returns the sign number of this term.
     */
    public int signum() {
        return signum;
    }

    /**
     * Returns signum > 0
     */
    public boolean isPositive() {
        return signum > 0;
    }

    /**
     * Determines whether this term is zero.
     */
    public boolean isZero() {
        return signum == 0;
    }

    /**
     * Returns signum < 0
     */
    public boolean isNegative() {
        return signum < 0;
    }

    /**
     * Determines whether this term contains no character.
     */
    public boolean hasNoChar() {
        return character.isEmpty();
    }

    /**
     * Determines whether this term is rational.
     */
    public boolean isRational() {
        return signum == 0 || radical.equals(BigInteger.ONE);
    }

    /**
     * Determines whether this term is an integer without any character.
     */
    public boolean isInteger() {
        if (signum == 0) {
            return true;
        }
        return character.isEmpty() &&
                denominator.equals(BigInteger.ONE) &&
                radical.equals(BigInteger.ONE);
    }

    /**
     * Determines whether this term is a fraction without any character.
     */
    public boolean isFraction() {
        if (signum == 0) {
            return true;
        }
        return character.isEmpty() && radical.equals(BigInteger.ONE);
    }

    /**
     * Converts the numerator and denominator of this term to a Fraction.
     *
     * @return a Fraction
     */
    public Fraction toFraction() {
        if (signum == 0) {
            return Fraction.ZERO;
        }
        return Fraction.valueOf(signum, numerator.longValueExact(), denominator.longValueExact());
    }

    /**
     * Determines whether this term have coefficient of one.
     */
    public boolean isCoefficientOne() {
        return signum == 0 || (numerator.equals(BigInteger.ONE) &&
                denominator.equals(BigInteger.ONE) &&
                radical.equals(BigInteger.ONE));
    }


    /**
     * Returns this term's character as an unmodifiable map.
     */
    public NavigableMap<String, Fraction> getCharacter() {
        return Collections.unmodifiableNavigableMap(character);
    }

    /**
     * Return this.character, this method is specialized for package classes.
     */
    final NavigableMap<String, Fraction> getCharacterNoCopy() {
        return this.character;
    }

    /**
     * Gets the power of the character, if the character is not contained
     * in this formula, then zero will be returned.
     */
    public Fraction getCharacterPower(String cha) {
        Fraction pow = character.get(cha);
        if (pow == null) {
            return Fraction.ZERO;
        }
        return pow;
    }

    /**
     * Gets a set of character's name in this term.
     */
    public Set<String> getCharacterName() {
        return getCharacter().keySet();
    }


    Set<String> getCharacterNameNoCopy() {
        return character.keySet();
    }

    /**
     * Returns the string representing the characters in this term.
     */
    public int numOfChar() {
        return character.size();
    }

    /**
     * Determines whether this term and another one have the identity
     * characters and corresponding powers.
     *
     * @param t another term
     */
    public boolean haveSameChar(Term t) {
        return this.character.equals(t.character);
    }

    /**
     * Gets the sum of all the powers of this term.
     *
     * @return a Fraction
     */
    public Fraction getPowerTotal() {
        Fraction sum = Fraction.ZERO;
        for (Fraction p : character.values()) {
            sum = sum.add(p);
        }
        return sum;
    }

    /**
     * Determines whether this term contains the character.
     *
     * @param ch the name of the character
     */
    public boolean containsChar(String ch) {
        return character.containsKey(ch);
    }

    /**
     * Determines whether the term contains negative power such as a^-1.
     */
    public boolean containNegativePower() {
        for (Fraction p : character.values()) {
            if (p.isNegative()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether the powers of characters in this
     * term is bigger (or equal) than that in another correspondingly.
     * The powers of characters that do not appear in a term are
     * regarded as zero, unless the term is zero.
     * Notice that all term contains zero.
     * For example, a^2*b contains a, and 1 contains
     */
    public boolean containsAllChar(Term another) {
        if (another.isZero()) {
            return true;
        }
        if (isZero()) {
            return false;
        }
        for (Map.Entry<String, Fraction> en : another.character.entrySet()) {
            String ch = en.getKey();
            Fraction p1 = getCharacterPower(ch);
            Fraction p2 = en.getValue();
            if (p1.compareTo(p2) < 0) {
                return false;
            }
        }
        for (Map.Entry<String, Fraction> en : character.entrySet()) {
            String ch = en.getKey();
            Fraction p1 = en.getValue();
            if (p1.isNegative()) {
                Fraction p2 = another.getCharacterPower(ch);
                if (p1.compareTo(p2) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets the string representation of characters of this term.
     */
    public String getCharStr() {
        return appendCharStr(new StringBuilder()).toString();
    }


    /**
     * Appends the string representing the characters in this term to the
     * StringBuilder and return it.
     */
    public StringBuilder appendCharStr(StringBuilder sb) {
        for (Map.Entry<String, Fraction> t : character.entrySet()) {
            appendChar(t.getKey(), t.getValue(), sb);
        }
        if (sb.charAt(sb.length() - 1) == '*') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb;
    }

    private static void appendChar(String ch, Fraction pow, StringBuilder sb) {
        sb.append(ch);
        if (pow.equals(Fraction.ONE)) {
            return;
        }
        sb.append('^').append(pow.toStringWithBracket());
        sb.append('*');
    }

    /**
     * Returns a String representation of this term.
     */
    @Override
    public String toString() {
        if (signum == 0) {
            return "+0";
        }

        boolean nOne = numerator.equals(BigInteger.ONE);
        boolean dOne = denominator.equals(BigInteger.ONE);
        boolean rOne = radical.equals(BigInteger.ONE);
        boolean noChar = character.isEmpty();

        StringBuilder result = new StringBuilder();

        boolean putNu = (!nOne) || (rOne && (!dOne || noChar));
        if (signum >= 0) {
            result.append("+");
        } else {
            result.append("-");
        }
        if (putNu) {
            result.append(numerator);
        }
        if (!rOne) {
            result.append(SQR_SHOW);
            result.append("(");
            result.append(radical);
            result.append(')');
        }
        if (!dOne) {
            result.append("/");
            result.append(denominator);
        }
        if (!noChar) {
            if (!dOne) {
                result.append('*');
            }
            appendCharStr(result);
        }
        return result.toString();
    }


    private String mapCharToLatex(String ch) {
        if (ch.equals(PI_STR)) {
            return "\\pi";
        }
        return ch;
    }

    private void appendCharStrLatex(StringBuilder sb) {
        for (Map.Entry<String, Fraction> t : character.entrySet()) {
            var ch = t.getKey();
            ch = mapCharToLatex(ch);
            var pow = t.getValue();
            if (pow.equals(Fraction.ONE)) {
                sb.append(ch);
            } else {
                sb.append('{')
                        .append(ch)
                        .append("}^{")
                        .append(pow.toLatexString()).append('}');
            }
        }
    }

    public String toLatexString() {
        return toLatexString(true);
    }

    public String toLatexString(boolean withAddSign) {
        StringBuilder sb = new StringBuilder();
        if (withAddSign) {
            if (signum >= 0) {
                sb.append('+');
            } else {
                sb.append('-');
            }
        } else {
            if (signum < 0) {
                sb.append('-');
            }
        }
        boolean nOne = numerator.equals(BigInteger.ONE);
        boolean dOne = denominator.equals(BigInteger.ONE);
        boolean rOne = radical.equals(BigInteger.ONE);
        boolean noChar = character.isEmpty();
        boolean putNu = (!nOne) || (rOne && (!dOne || noChar));
        if (!dOne) {
            sb.append("\\frac{");
        }
        if (putNu) {
            sb.append(numerator);
        }
        if (!rOne) {
            sb.append("\\sqrt{");
            sb.append(radical);
            sb.append("}");
        }
        if (!dOne) {
            sb.append("}{");
            sb.append(denominator);
            sb.append("}");
        }
        appendCharStrLatex(sb);
        return sb.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Term)) {
            return false;
        }
        Term another = (Term) obj;
        if (this.signum != another.signum) {
            return false;
        }
        if (this.signum == 0) {
            //both 0
            return true;
        }

        if (!haveSameChar(another)) {
            return false;
        }

        if (!numerator.equals(another.numerator)) {
            return false;
        }
        if (!denominator.equals(another.denominator)) {
            return false;
        }
        return radical.equals(another.radical);
    }

    /**
     * Determines whether this term is equal to another term regardless of
     * their signum.
     *
     * @param t another term
     */
    public boolean absEquals(Term t) {
        if (this == t) {
            return true;
        }
        if (signum == 0 && t.signum == 0) {
            return true;
        }
        if (!haveSameChar(t)) {
            return false;
        }
        if (!numerator.equals(t.numerator)) {
            return false;
        }
        if (!denominator.equals(t.denominator)) {
            return false;
        }
        if (!radical.equals(t.radical)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0 && signum != 0) {
            int hashCode = character.hashCode();
            hashCode = hashCode * 31 + numerator.hashCode();
            hashCode = hashCode * 31 + denominator.hashCode();
            hashCode = hashCode * 31 + radical.hashCode();
            this.hashCode = hashCode * signum;
        }
        return hashCode;
    }


    /**
     * Compares the two term using the dictionary order.
     */
    @Override
    public int compareTo(@NotNull Term another) {
        if (this == another) {
            return 0;
        }
        //compare character first
        int comp = compareChar(another);
        if (comp != 0) {
            return comp;
        }
        return compareNumber(another);
    }

    /**
     * Compares the character according to the dictionary order.
     * x^2 < y^2 < x < y < -1 = 1 < x^-1 < y^-1 < 0
     * Notice that
     * zero is considered to be the biggest.
     */
    public int compareChar(Term another) {
        if (this.isZero()) {
            if (another.isZero()) {
                return 0;
            }
            return 1;
        }
        if (another.isZero()) {
            return -1;
        }
        if (numOfChar() == 0 && another.numOfChar() == 0) {
            return 0;
        }

        var it1 = character.entrySet().iterator();
        var it2 = another.character.entrySet().iterator();
        while (true) {
            boolean hs1 = it1.hasNext(),
                    hs2 = it2.hasNext();
            if (hs1) {
                if (!hs2) {
//                    return -it1.next().getValue().getSignum();
                    return -1;
                }
            } else {
                if (hs2) {
                    return 1;
//                    return it2.next().getValue().getSignum();
                } else {
                    return 0;
                }
            }
            var en1 = it1.next();
            var en2 = it2.next();
            String ch1 = en1.getKey();
            String ch2 = en2.getKey();
            int comp = ch1.compareTo(ch2);
            if (comp < 0) {
//                return -en1.getValue().getSignum();
                return -1;
            } else if (comp > 0) {
//                return en2.getValue().getSignum();
                return 1;
            }
            comp = en2.getValue().compareTo(en1.getValue());
            if (comp != 0) {
                return comp;
            }
        }
    }

    /**
     * Compares the character and the radical part, this method will return 0 if the
     * character part and the number part of <code>this</code> and <code>another</code> are
     * the same. Notice that the term 0 will be treated as special case and only zero is equal to zero.
     * <p>
     * x^2 < y^2 < x < y < -1 = 1 < Sqr2 < y^-1 < 0
     *
     * @param another a term
     */
    public int compareCharAndRadical(Term another) {
        int comp = compareChar(another);
        if (comp != 0) {
            return comp;
        }
        return radical.compareTo(another.radical);
    }

    /**
     * Compares the number part of this term with another's.
     */
    public int compareNumber(Term another) {
        int comp = signum - another.signum;
        if (comp != 0) {
            return comp;
        }
        if (signum == 0) {
            //both zero
            return 0;
        }
        BigInteger n1 = numerator, d1 = denominator, r1 = radical,
                n2 = another.numerator, d2 = another.denominator, r2 = another.radical;

        if (r1.equals(BigInteger.ONE) && r2.equals(BigInteger.ONE)) {

            return signum * n1.multiply(d2).compareTo(n2.multiply(d1));
        }
        BigInteger val1 = n1.pow(2).multiply(d2.pow(2)).multiply(r1);
        BigInteger val2 = n2.pow(2).multiply(d1.pow(2)).multiply(r2);

        return signum * val1.compareTo(val2);
    }

    /**
     * Returns a term that doesn't have the given character and all of the other
     * number remains the identity.
     *
     * @param ch the character
     * @return a new formula
     */
    public Term removeChar(String ch) {
        NavigableMap<String, Fraction> nc = new TreeMap<>(character);
        nc.remove(ch);
        return sameNumber0(nc);
    }

    /**
     * Returns a term that only contains the characters and its coefficient is one.
     */
    public Term removeCoefficient() {
        return new Term(character);
    }

    /**
     * Returns a term that only contains the coefficient of the characters.
     */
    public Term removeAllChar() {
        return new Term(signum, numerator, denominator, radical);
    }


    /**
     * @return -this
     */
    public Term negate() {
        if (signum == 0) {
            return ZERO;
        }
        return new Term(-signum, numerator, denominator, radical, character);
    }

    /**
     * Returns the reciprocal of this term.
     *
     * @return 1/this
     */
    public Term reciprocal() {
        if (signum == 0) {
            throw new ArithmeticException("No reciprocal for 0");
        }
        BigInteger[] ndr = new BigInteger[3];
        ndr[0] = this.denominator;
        ndr[1] = this.numerator.multiply(this.radical);
        ndr[2] = this.radical;

        simplifyND(ndr);

        // deal with all chracter
        NavigableMap<String, Fraction> character = new TreeMap<>();
        for (Map.Entry<String, Fraction> e : this.character.entrySet()) {
            character.put(e.getKey(), e.getValue().negate());
        }
        int signum = this.signum;
        // deal with i
        if (character.containsKey(I_STR)) {
            // the times of i should always be in [0,2) so the result of 1/i^a will be
            // -i^(2-a)
            signum = -signum;
            Fraction times = this.character.get(I_STR);
            character.put(I_STR, Fraction.valueOf(2).minus(times));
        }

        return new Term(signum, ndr, character);
    }

    /**
     * Returns this*t.
     *
     * @param t a term
     * @return
     */
    public Term multiply(Term t) {
        int signum = this.signum * t.signum;
        if (signum == 0) {
            return Term.ZERO;
        }
        BigInteger[] nd1 = new BigInteger[3],
                nd2 = new BigInteger[2];

        nd1[0] = numerator;
        nd1[1] = t.denominator;
        nd2[0] = t.numerator;
        nd2[1] = denominator;

        simplifiedMultiply(nd1, nd2);

        nd1[2] = radical.multiply(t.radical);

        if ((!radical.equals(BigInteger.ONE)) && (!t.radical.equals(BigInteger.ONE))) {
            BigInteger[] rad = sortRad(nd1[2]);
            nd1[2] = rad[0];

            nd2[0] = rad[1];//numerator to multiply from radical
            nd2[1] = nd1[1];//denominator
            simplifyND(nd2);

            nd1[0] = nd1[0].multiply(nd2[0]);
            nd1[1] = nd2[1];
        }
        NavigableMap<String, Fraction> ch = new TreeMap<>(getCharacterNoCopy());
        for (Map.Entry<String, Fraction> e : t.getCharacterNoCopy().entrySet()) {
            addChar(ch, e.getKey(), e.getValue());
        }

        signum = simplifyCharacter(signum, ch);
        return new Term(signum, nd1, ch);
    }

    public Term multiply(BigInteger n) {
        int signum = this.signum * n.signum();
        if (signum == 0) {
            return Term.ZERO;
        }
        n = n.abs();
        BigInteger[] nd = {n, denominator};
        simplifyND(nd);
        BigInteger nume = nd[0].multiply(numerator);
        return new Term(signum, nume, nd[1], radical, character);
    }


    public Term divide(Term t) {
        if (t.isZero()) {
            throw new ArithmeticException("Divide by zero.");
        }
        if (signum == 0) {
            return ZERO;
        }
        int signum = this.signum * t.signum;
        BigInteger[] nd1 = new BigInteger[3],
                nd2 = new BigInteger[2];
        nd1[0] = numerator;
        nd1[1] = t.numerator;
        nd2[0] = t.denominator;
        nd2[1] = denominator;
        simplifiedMultiply(nd1, nd2);

        nd1[2] = radical.multiply(t.radical);

        if (!t.radical.equals(BigInteger.ONE)) {
            BigInteger[] rad = sortRad(nd1[2]);

            nd1[2] = rad[0];

            nd2[0] = rad[1];
            nd2[1] = nd1[1];

            nd1[1] = t.radical;
            simplifiedMultiply(nd1, nd2);
        }

        NavigableMap<String, Fraction> ch = new TreeMap<>(getCharacterNoCopy());
        for (Map.Entry<String, Fraction> e : t.getCharacterNoCopy().entrySet()) {
            addChar(ch, e.getKey(), e.getValue().negate());
        }
        signum = simplifyCharacter(signum, ch);
        return new Term(signum, nd1, ch);
    }

    public Term divide(BigInteger n) {
        if (n.signum() == 0) {
            throw new ArithmeticException("Divide by 0.");
        }
        if (this.signum == 0) {
            return Term.ZERO;
        }
        int signum = this.signum * n.signum();
        n = n.abs();
        BigInteger[] nd = {numerator, n};
        simplifyND(nd);
        BigInteger deno = nd[1].multiply(denominator);
        return new Term(signum, nd[0], deno, radical, character);
    }

    /**
     * Determines whether this term and another term can be merged.
     * It is required that the radical and characters are the identity unless
     *
     * @param t
     * @return
     */
    public boolean canMerge(Term t) {
        if (t.isZero() || this.isZero()) {
            return true;
        }
        if (!this.radical.equals(t.radical)) {
            return false;
        }
        return haveSameChar(t);
    }

    @Override
    public Term merge(Term x) {
        return add(x);
    }

    /**
     * Returns this - t. If !canMerge(t), throws an exception
     *
     * @param t
     * @return
     */
    public Term add(Term t) {
        if (!canMerge(t)) {
            throw new ArithmeticException("Can't merge: " + this + "+" + t);
        }
        if (this.isZero()) {
            return t;
        }
        if (t.isZero()) {
            return this;
        }
        BigInteger gcd = denominator.gcd(t.denominator);
        BigInteger m2 = denominator.divide(gcd),
                m1 = t.denominator.divide(gcd);
        BigInteger den = m1.multiply(denominator);
        BigInteger num1 = this.numerator.multiply(m1);
        if (signum < 0) {
            num1 = num1.negate();
        }
        BigInteger num2 = t.numerator.multiply(m2);
        if (t.signum < 0) {
            num2 = num2.negate();
        }
        BigInteger num = num1.add(num2);
        if (num.equals(BigInteger.ZERO)) {
            return ZERO;
        }
        int signum;
        if (num.compareTo(BigInteger.ZERO) < 0) {
            num = num.negate();
            signum = -1;
        } else {
            signum = 1;
        }
        BigInteger[] nd = {num, den};
        simplifyND(nd);
        return new Term(signum, nd[0], nd[1], radical, character);
    }

    /**
     * Returns this^pow
     *
     * @param pow
     * @return
     */
    public Term pow(int pow) {
        if (pow == 0) {
            if (this.isZero()) {
                throw new ArithmeticException("0^0");
            }
            return ONE;
        }
        if (this.isZero()) {
            return ZERO;
        }
        if (pow < 0) {
            return this.reciprocal().pow(-pow);
        }
        int p_2 = pow / 2;
        boolean even = pow % 2 == 0;
        int sign = even ? 1 : signum;
        BigInteger nume = numerator.pow(pow);
        BigInteger deno = denominator.pow(pow);
        BigInteger rad;
        if (even) {
            rad = BigInteger.ONE;
        } else {
            rad = radical;
        }
        BigInteger[] nd = {radical.pow(p_2), deno};
        simplifyND(nd);
        nume = nume.multiply(nd[0]);
        deno = nd[1];
        var map = new TreeMap<String, Fraction>();
        for (Map.Entry<String, Fraction> en : character.entrySet()) {
            map.put(en.getKey(), en.getValue().multiply(pow));
        }
        return new Term(sign, nume, deno, rad, map);
    }

    /**
     * Computes the value of this term using the math calculator mc according to the value assigned for the
     * characters. Characters that the value map doesn't contain will be considered as one.
     *
     * @param valueMap a mapping function to get the value of characters.
     * @param valueOf  converting a BigInteger to the type T
     */
    public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc, Function<BigInteger, T> valueOf) {
        if (isZero()) {
            return mc.getZero();
        }
        T re = valueOf.apply(numerator);
        if (!denominator.equals(BigInteger.ONE)) {
            re = mc.divide(re, valueOf.apply(denominator));
        }
        if (!radical.equals(BigInteger.ONE)) {
            re = mc.multiply(re, mc.squareRoot(valueOf.apply(radical)));
        }
        for (var en : character.entrySet()) {
            String ch = en.getKey();
            Fraction f = en.getValue();
            T exp = CalculatorUtils.valueOfFraction(f, mc);
            T val = valueMap.apply(ch);
            if (val == null) {
                val = mc.getOne();
            }
            re = mc.multiply(re, mc.exp(val, exp));
        }
        if (signum < 0) {
            re = mc.negate(re);
        }
        return re;
    }

    /**
     * Computes the value of this term using the math calculator mc according to the value assigned for the
     * characters. Characters that the value map doesn't contain will be considered as one.
     *
     * @param valueMap a mapping function to get the value of characters.
     */
    public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
        return compute(valueMap, mc, CalculatorUtils.parserBigInteger(mc));
    }

    /**
     * Computes the double value of the term.
     *
     * @param values a mapping function to assign characters values as double.
     * @return
     */
    public double computeDouble(ToDoubleFunction<String> values) {
        if (isZero()) {
            return 0d;
        }
        double re = numerator.doubleValue();
        if (!denominator.equals(BigInteger.ONE)) {
            re = re / denominator.doubleValue();
        }
        if (!radical.equals(BigInteger.ONE)) {
            re = re * Math.sqrt(radical.doubleValue());
        }
        for (var en : character.entrySet()) {
            String ch = en.getKey();
            Fraction f = en.getValue();
            double exp = f.doubleValue();
            double val = values.applyAsDouble(ch);
            re = re * Math.pow(val, exp);
        }
        if (signum < 0) {
            re = -re;
        }
        return re;
    }

    private static void simplifiedMultiply(BigInteger[] nd1, BigInteger[] nd2) {
        simplifyND(nd1);
        simplifyND(nd2);
        nd1[0] = nd1[0].multiply(nd2[0]);
        nd1[1] = nd1[1].multiply(nd2[1]);
    }

    /**
     * Returns the square root of this term, if the result can't be represented as a term, throws an
     * {@link cn.timelives.java.math.exceptions.UnsupportedCalculationException}
     */
    public Term squareRoot() {
        if (this.signum == 0) {
            return ZERO;
        }
        if (!radical.equals(BigInteger.ONE)) {
            throw new UnsupportedCalculationException();
        }
        NavigableMap<String, Fraction> nchars = new TreeMap<>();
        for (Map.Entry<String, Fraction> en : character.entrySet()) {
            nchars.put(en.getKey(), en.getValue().divide(2));
        }
        if (signum < 0) {
            addChar(nchars, I_STR, Fraction.ONE);
        }

        BigInteger nRad = numerator.multiply(denominator);
        return sortNdrAndCreate(1, BigInteger.ONE, denominator, nRad, nchars);
    }

    /**
     * Returns a term who has the identity number part as this but has no character.
     *
     * @return
     */
    public Term numberPart() {
        if (hasNoChar()) {
            return this;
        }
        return new Term(signum, numerator, denominator, radical, empty);
    }

    /**
     * Returns a term which has the identity character as this but has the number part of 1.
     */
    public Term characterPart() {
        return ONE.sameNumber0(character);
    }

    /**
     * Returns a term who has the identity number part as this and has the given character part.
     */
    public Term sameNumber(Map<String, Fraction> charcters) {
        var map = new TreeMap<String, Fraction>();
        for (var en : charcters.entrySet()) {
            if (!en.getValue().isZero()) {
                map.put(en.getKey(), en.getValue());
            }
        }
        return sameNumber0(map);
    }

    public Term sameChar(Fraction fraction) {
        return new Term(fraction.getSignum(), BigInteger.valueOf(fraction.getNumerator()), BigInteger.valueOf(fraction.getDenominator()), BigInteger.ONE, this.character);
    }

    public Term sameChar(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical) {
        return newInstance(signum, numerator, denominator, radical, character);
    }

    Term sameNumber0(NavigableMap<String, Fraction> map) {
        if (signum == 0) {
            return ZERO;
        }
        return new Term(signum, numerator, denominator, radical, map);
    }

    public Term replaceChar(String ch, String after) {
        if (!containsChar(ch)) {
            return this;
        }
        var nmap = new TreeMap<>(character);
        Fraction pow = nmap.remove(ch);
        addChar(nmap, after, pow);
        return sameNumber0(nmap);
    }

    /**
     * Replace the given character <code>ch</code> in this term to <code>ch^pow</code>. <P></P>
     * For example, apply <code>replaceCharByPow("x",3)</code> to term <code>x^2</code> will result in
     * <code>x^6</code>.
     */
    public Term replaceCharByPow(String ch, Fraction p) {
        if (!containsChar(ch)) {
            return this;
        }
        if (p.isZero()) {
            return removeChar(ch);
        }
        var nmap = new TreeMap<>(character);
        Fraction pow = nmap.get(ch);
        nmap.put(ch, pow.multiply(p));
        return sameNumber0(nmap);
    }

    /**
     * Replace all the characters in this term by the given replacer.
     */
    public Term replaceChar(UnaryOperator<String> replacer) {
        if (character.isEmpty()) {
            return this;
        }
        var nmap = new TreeMap<String, Fraction>();
        for (var en : character.entrySet()) {
            addChar(nmap, replacer.apply(en.getKey()), en.getValue());
        }
        return sameNumber0(nmap);
    }


    private static Pattern numberPattern = Pattern.compile("[+-]?[\\d.]+");
    private static Pattern charPattern = Pattern.compile("(" + PI_STR + ")|([a-zA-Z](\\[\\w+\\])?)");


    /**
     * Creates a term with the expression given.
     * <h3>Expression</h3>
     * An expression consists of numbers, operations('*','/','Sqr') and single characters(except Pi).
     * The multiplication operation can be omitted between number and character(such as 2a). Everything
     * after a division operation will be considered as denominator until there is a multiplication operation.
     * Number directly following a character is not valid.
     * <h3>Numbers(rational part)</h3>
     * Numbers, both integers and decimal numbers, are acceptable. The will be converted to a
     * fraction with full precision.
     * <h3>Radical</h3>
     * A term supports radical part which can be represented as the square root of an integer. To input
     * a radical part, use Sqr(x) (bracket is optional). For example, Sqr3 represents the square root
     * of 3.
     * <h3>Character</h3>
     * The power of a character can only be an integer in the string representation.
     *
     * @param str a string representing the term
     */
    public static Term valueOf(String str) {
        str = str.trim();
        if (str.isEmpty()) {
            throw new NumberFormatException("Empty");
        }
        if (str.equals("1"))
            return ONE;
        else if (str.equals("0")) {
            return ZERO;
        }

        BigInteger[] ndr = new BigInteger[3];
        ndr[0] = BigInteger.ONE;
        ndr[1] = BigInteger.ONE;
        ndr[2] = BigInteger.ONE;
        int signum = 1;
        NavigableMap<String, Fraction> character = new TreeMap<>();
        int pos = 0;
        char c;
        while (true) {
            c = str.charAt(pos);
            if (c == '-') {
                signum = -signum;
            } else if (c != '+' && c != ' ') {
                break;
            }
            pos++;
        }
        int state = 0;
        /*
         * state: 0 : top no-sqr 1 : top sqr 2 : down no-sqr 3 : down sqr
         *
         */
        boolean hasChar = false;
        boolean hasOpe = false;
        Matcher nm = numberPattern.matcher(str);//
        Matcher cm = charPattern.matcher(str);//
        BigInteger[] uad;
        int end = str.length();
        while (pos < end) {
            // System.out.println("Start "+pos);
            nm.region(pos, end);
            cm.region(pos, end);
            c = str.charAt(pos);
            if (nm.lookingAt()) {//
                if (hasChar) {
                    throw new NumberFormatException("Number after char");
                }
                BigDecimal tempNum = new BigDecimal(nm.group());
                uad = toFraction(tempNum);
                switch (state) {
                    case 0: {
                        ndr[0] = ndr[0].multiply(uad[0]);
                        ndr[1] = ndr[1].multiply(uad[1]);
                        break;
                    }
                    case 1: {
                        ndr[2] = ndr[2].multiply(uad[0].multiply(uad[1]));
                        ndr[1] = ndr[1].multiply(uad[1]);
                        break;
                    }
                    case 2: {
                        ndr[0] = ndr[0].multiply(uad[1]);
                        ndr[1] = ndr[1].multiply(uad[0]);
                        break;
                    }
                    case 3: {
                        ndr[2] = ndr[2].multiply(uad[0].multiply(uad[1]));
                        ndr[1] = ndr[1].multiply(uad[0]).multiply(uad[1]);
                        break;
                    }
                }
                hasOpe = false;
                pos = nm.end();
                continue;
            }
            if (c == '*' || c == '/' || c == 'S') {
                if (c == '*') {
                    if (hasOpe) {
                        throw new NumberFormatException("Multiple operation *");
                    }
                    state = 0;
                    hasOpe = true;
                } else if (c == '/') {
                    if (hasOpe) {
                        throw new NumberFormatException("Multiple operation /");
                    }
                    state = 2;
                    hasOpe = true;
                } else if (c == SQR_STR.charAt(0) && pos + 2 < str.length() && str.charAt(pos + 1) == SQR_STR.charAt(1)
                        && str.charAt(pos + 2) == SQR_STR.charAt(2)) {
                    if (state == 0) {
                        state = 1;
                    } else if (state == 2) {
                        state = 3;
                    }
                    pos = pos + 2;
                    hasOpe = true;
                }
                if (hasOpe) {
                    hasChar = false;
                }
                pos++;
                continue;
            }
            if (cm.lookingAt()) {
                Fraction time;
                int tpos = cm.end();
                if (tpos < str.length() && str.charAt(tpos) == '^') {
                    nm.region(tpos + 1, end);
                    if (nm.lookingAt()) {
                        time = Fraction.valueOf(nm.group());
                        tpos = nm.end();
                    } else {
                        throw new NumberFormatException("Illegal Expression using ^");
                    }
                } else {
                    time = Fraction.ONE;
                }
                switch (state) {
                    case 1: {
                        time = time.divide(2);
                        break;
                    }
                    case 2: {
                        time = time.negate();
                        break;
                    }
                    case 3: {
                        time = time.divide(2).negate();
                    }
                }
                // System.out.println(character+"---"+cm.group()+"---"+time);
                addChar(character, cm.group(), time);
                hasOpe = false;
                hasChar = true;
                pos = tpos;
                continue;
            } else {
                throw new NumberFormatException("Unsupported Character in  " + str);
            }

        }
        // read part is over
        return newInstanceP(signum, ndr, character);
    }

    static BigInteger[] toFraction(BigDecimal n) {
        BigInteger[] uad = new BigInteger[2];
        uad[0] = n.movePointRight(n.scale()).toBigIntegerExact();
        uad[1] = BigDecimal.ONE.movePointRight(n.scale()).toBigIntegerExact();
        BigInteger temp = uad[0].gcd(uad[1]);
        uad[0] = uad[0].divide(temp);
        uad[1] = uad[1].divide(temp);
        return uad;

    }

    private static void addChar(NavigableMap<String, Fraction> character, String cha, Fraction t) {
        character.merge(cha, t, (ori, toMerge) -> {
            Fraction re = ori.add(toMerge);
            if (re.isZero()) {
                return null;
            } else {
                return re;
            }
        });
//        character.compute(cha,(ch,p)->{
//            if(p == null){
//                return t;
//            }
//            Fraction re = p.add(t);
//            if(re.isZero()){
//                return null;
//            }else{
//                return re;
//            }
//        });
//        if (character.containsKey(cha)) {
//            t = t.add(character.get(cha));
//        }
//        if (t.equals(Fraction.ZERO)) {
//            character.remove(cha);
//        } else {
//            character.put(cha, t);
//        }
    }


    static Term newInstanceP(int signum, BigInteger[] ndr, NavigableMap<String, Fraction> character) {
        return newInstanceP(signum, ndr[0], ndr[1], ndr[2], character);
    }

    /**
     * @param signum      raw
     * @param numerator   raw
     * @param denominator raw
     * @param radical     raw
     * @param character   must be a copy, may be modified in the method later
     * @return
     */
    static Term newInstanceP(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical,
                             NavigableMap<String, Fraction> character) {
        if (signum == 0) {// first check
            return ZERO;
        }
        signum = signum * denominator.compareTo(BigInteger.ZERO);
        if (signum == 0) {
            throw new ArithmeticException("Denominator is ZERO: ");
        }
        signum = signum * numerator.compareTo(BigInteger.ZERO);
        if (signum == 0 || radical.equals(BigInteger.ZERO)) {
            // double check to save storage
            return ZERO;
        }

        // make sure the radical is positive and add i if necessary
        if (radical.compareTo(BigInteger.ZERO) < 0) {
            radical = radical.negate();// 将根号内的-1提出
            addChar(character, "i", Fraction.ONE);
        }

        // make sure that denominator and numerator are both positive
        denominator = denominator.abs();
        numerator = numerator.abs();

        // deal with i
        signum = simplifyCharacter(signum, character);
        if (character.isEmpty()) {
            character = empty;
        }
        return sortNdrAndCreate(signum, numerator, denominator, radical, character);
    }

    /**
     * Regulates ndr
     */
    static Term newInstanceNdr(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical) {
        if (signum == 0) {
            return ZERO;
        }
        signum = signum * denominator.compareTo(BigInteger.ZERO);
        if (signum == 0) {
            throw new ArithmeticException("Denominator is ZERO: ");
        }
        signum = signum * numerator.compareTo(BigInteger.ZERO);
        if (signum == 0 || radical.equals(BigInteger.ZERO)) {
            return ZERO;
        }
        if (radical.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Radical < 0");
        }
        denominator = denominator.abs();
        numerator = numerator.abs();
        return sortNdrAndCreate(signum, numerator, denominator, radical, empty);
    }

    /**
     * @param signum      the signum
     * @param numerator   >0
     * @param denominator >0
     * @param radical     >0
     */
    static Term sortNdrAndCreate(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical,
                                 NavigableMap<String, Fraction> character) {
        // sort radical
        BigInteger[] i = new BigInteger[3];
        i[0] = radical;
        i[1] = BigInteger.ONE;
        i[2] = BigInteger.valueOf(2L);
        sortRad(i);
        radical = i[0];
        numerator = numerator.multiply(i[1]);
        // divide part
        BigInteger temp = numerator.gcd(denominator);
        numerator = numerator.divide(temp);
        denominator = denominator.divide(temp);

        // Printer.print("R="+radical);
        return new Term(signum, numerator, denominator, radical, character);
    }

    static int simplifyCharacter(int signum, NavigableMap<String, Fraction> character) {
        if (character.containsKey(I_STR)) {
            Fraction[] rd = character.get(I_STR).divideAndRemainder(Fraction.valueOf(2));
            if (Fraction.ZERO.equals(rd[1])) {
                character.remove(I_STR);
            } else {

                character.put(I_STR, rd[1]);
            }
            return (Fraction.ZERO.equals(rd[0].remainder(Fraction.valueOf(2)))) ? (signum) : (-signum);
        } else {
            return signum;
        }
    }


    private static void simplifyND(BigInteger[] ndr) {
        BigInteger temp = ndr[0].gcd(ndr[1]);
        ndr[0] = ndr[0].divide(temp);
        ndr[1] = ndr[1].divide(temp);
    }

    /**
     * rad[0] radical
     * rad[1] numerator outside
     * rad[2] current step
     *
     * @param rad
     * @return
     */
    static void sortRad(BigInteger[] rad) {
        if (rad[0].compareTo(BigInteger.valueOf(4)) < 0) {
            return;
        }
        BigInteger temp;
        while (true) {
            temp = rad[2].pow(2);
            if (temp.compareTo(rad[0]) > 0) {
                break;
            }
            if (rad[0].mod(temp).compareTo(BigInteger.ZERO) == 0) {
                rad[0] = rad[0].divide(temp);
                rad[1] = rad[1].multiply(rad[2]);
                continue;
            }
            rad[2] = rad[2].add(BigInteger.ONE);
            // temp = i.pow(2);
        }
    }

    static BigInteger[] sortRad(BigInteger radical) {
        BigInteger[] rad = new BigInteger[3];
        rad[0] = radical;
        rad[1] = BigInteger.ONE;
        rad[2] = BigInteger.valueOf(2L);
        sortRad(rad);
        return rad;
    }

    public static Term singleChar(String character) {
        if (character.isEmpty()) {
            throw new IllegalArgumentException();
        }
        TreeMap<String, Fraction> map = new TreeMap<>();
        map.put(character, Fraction.ONE);
        return new Term(map);
    }

    private static void checkCharacters(String... character) {
        for (String c : character) {
            if (c.isEmpty()) {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Returns a term as the multiplication of all the given characters.
     *
     * @param character a
     * @return
     */
    public static Term multiplyOf(String... character) {
        checkCharacters(character);
        TreeMap<String, Fraction> map = new TreeMap<>();
        for (String c : character) {
            NumberModelUtils.accumulateMapAdd(map, c, Fraction.ONE);
        }
        return new Term(map);
    }

    public static Term characters(NavigableMap<String, Fraction> characters) {
        TreeMap<String, Fraction> map = new TreeMap<>(characters);
        return new Term(map);
    }

    /**
     * Returns an array of terms containing <code>r</code> of the given characters.
     *
     * @param r          the number of characters in each term
     * @param characters all the
     * @return
     * @see Multinomial#primarySymmetry(int, String...)
     */
    public static Term[] symmetryTerms(int r, String... characters) {
        checkCharacters(characters);
        int n = characters.length;
        if (r <= 0 || r > n) {
            throw new IllegalArgumentException("r <=0 || r > n");
        }
        if (r == n) {
            return new Term[]{multiplyOf(characters)};
        }
        int size = Math.toIntExact(CombUtils.combination(n, r));
        Term[] arr = new Term[size];
        fillSymmetryTerms(r, characters, 0, 0, new TreeMap<>(), arr, 0);
        return arr;
    }


    private static int fillSymmetryTerms(int r, String[] characters, int index, int nextSelect, TreeMap<String, Fraction> map,
                                         Term[] arr, int arrPos) {
        if (index == r) {
            //finished
            arr[arrPos] = new Term(map);
            return arrPos + 1;
        }
        int bound = characters.length - r + index;
        //leave the last one non-copy
        for (int i = nextSelect; i < bound; i++) {
            @SuppressWarnings("unchecked")
            TreeMap<String, Fraction> nmap = (TreeMap<String, Fraction>) map.clone();
            nmap.put(characters[i], Fraction.ONE);
            arrPos = fillSymmetryTerms(r, characters, index + 1, i + 1, nmap, arr, arrPos);
        }
        map.put(characters[bound], Fraction.ONE);
        arrPos = fillSymmetryTerms(r, characters, index + 1, bound + 1, map, arr, arrPos);
        return arrPos;
    }


    public static Term gcd(Term... ts) {
        if (ts.length <= 1) {
            return ts[0];
        }
        BigInteger[] ndr = new BigInteger[3];
        NavigableMap<String, Fraction> map = gcd0(ts, ndr);
        return newInstanceP(1, ndr, map);
    }

    /**
     * Returns the gcd of the formulas.
     *
     * @param fs
     * @return
     */
    static NavigableMap<String, Fraction> gcd0(Term[] fs, BigInteger[] ndr) {
        // find GCD for numerator and radical,
        // find LCM for denominator
        BigInteger gcd_n = null;
        BigInteger gcd_r = null;
        BigInteger lcm_d = null;
        /*
         * The power to minus
         */
        NavigableMap<String, Fraction> commonCharP = null;
        boolean first = true;
        // fill the map with the first one's characters
        for (Term f : fs) {
            if (first) {
                gcd_n = f.numerator();
                lcm_d = f.denominator();
                gcd_r = f.radical();
                commonCharP = new TreeMap<>(f.getCharacterNoCopy());
                first = false;
            } else {
                gcd_n = gcd_n.gcd(f.numerator());
                lcm_d = lcm_d.multiply(f.denominator().divide(lcm_d.gcd(f.denominator())));
                gcd_r = gcd_r.gcd(f.radical());
                NavigableMap<String, Fraction> commonCharPN = new TreeMap<>();
                NavigableMap<String, Fraction> fchars = f.getCharacterNoCopy();
                for (Map.Entry<String, Fraction> en : commonCharP.entrySet()) {
                    Fraction fv = fchars.get(en.getKey());
                    if (fv != null) {
                        Fraction p = en.getValue();
                        if (p.compareTo(fv) > 0) {
                            p = fv;
                        }
                        commonCharPN.put(en.getKey(), p);
                    } else {
                        // does not contain this char
                        if (en.getValue().compareTo(Fraction.ZERO) < 0) {
                            commonCharPN.put(en.getKey(), en.getValue());
                        }
                    }
                }
                fchars.forEach((k, v) -> {
                    if (v.compareTo(Fraction.ZERO) < 0) {
                        commonCharPN.putIfAbsent(k, v);
                    }
                });
                commonCharP = commonCharPN;
            }
        }
        gcd_n = gcd_n.equals(BigInteger.ZERO) ? BigInteger.ONE : gcd_n;
        gcd_r = gcd_r.equals(BigInteger.ZERO) ? BigInteger.ONE : gcd_r;
        ndr[0] = gcd_n;
        ndr[1] = lcm_d;
        ndr[2] = gcd_r;
        return commonCharP;
    }

    public static Term gcdAndDivide(Term... ts) {
        if (ts.length <= 1) {
            return ts[0];
        }
        BigInteger[] ndr = new BigInteger[3];
        NavigableMap<String, Fraction> commonCharP = gcd0(ts, ndr);
        BigInteger gcd_n = ndr[0],
                gcd_r = ndr[2],
                lcm_d = ndr[1];

        for (int i = 0; i < ts.length; i++) {
            Term f = ts[i];
            BigInteger nume = f.numerator().divide(gcd_n).multiply(lcm_d.divide(f.denominator()));
            BigInteger deno = BigInteger.ONE;
            BigInteger rad = f.radical().divide(gcd_r);
            NavigableMap<String, Fraction> cha = new TreeMap<>();
            Map<String, Fraction> fchars = f.getCharacterNoCopy();
            for (Map.Entry<String, Fraction> en : commonCharP.entrySet()) {
                Fraction bd = fchars.get(en.getKey());
                if (bd == null) {
                    cha.put(en.getKey(), en.getValue().negate());
                } else {
                    bd = bd.minus(en.getValue());
                    cha.put(en.getKey(), bd);
                }
            }
            fchars.forEach(cha::putIfAbsent);
            cha.entrySet().removeIf(en -> Fraction.ZERO.equals(en.getValue()));

            ts[i] = Term.newInstanceP(f.signum(), nume, deno, rad, cha);
        }

        return Term.newInstanceP(1, ndr, commonCharP);

    }

    /**
     * Returns an integer if the formula actually represents an integer, or
     * {@code null}.
     *
     * @param f
     * @return
     */
    public static BigInteger asInteger(Term f) {
        if (f.signum == 0) {
            return BigInteger.ZERO;
        }
        if (f.character.isEmpty()) {
            if (f.radical.equals(BigInteger.ONE) && f.denominator.equals(BigInteger.ONE)) {
                return f.signum < 0 ? f.numerator.negate() : f.numerator;
            }
        }
        return null;
    }

    public static Term characterPower(String character, Fraction times) {
        if (character.isEmpty() || times == null) {
            throw new IllegalArgumentException();
        }
        if (Fraction.ZERO.equals(times)) {
            return ONE;
        }
        NavigableMap<String, Fraction> map = new TreeMap<>();
        map.put(character, times);
        return new Term(1, BigInteger.ONE, BigInteger.ONE, BigInteger.ONE, map);
    }

    public static Term valueOf(long l) {
        return valueOf(BigInteger.valueOf(l));
    }

    public static Term valueOf(BigInteger val) {
        int signum = val.compareTo(BigInteger.ZERO);
        if (signum == 0) {
            return ZERO;
        }
        if (val.equals(BigInteger.ONE)) {
            return ONE;
        }
        if (signum < 0) {
            val = val.negate();
        }
        return new Term(signum, val, BigInteger.ONE, BigInteger.ONE);

    }

    public static Term valueOf(Fraction f) {
        return new Term(f.getSignum(), BigInteger.valueOf(f.getNumerator())
                , BigInteger.valueOf(f.getDenominator()), BigInteger.ONE);
    }

//    public static Term singleChar(String ch){
//        NavigableMap<String,Fraction> map = new TreeMap<>();
//        map.put(ch, Fraction.ONE);
//        return ONE.sameNumber0(map);
//    }

    public static Term valueOfRecip(long l) {
        if (l == 0) {
            throw new ArithmeticException();
        }
        return new Term(l > 0 ? 1 : -1, BigInteger.ONE, BigInteger.valueOf(Math.abs(l)));
    }

    public static Term asFraction(long n, long d, long r) {
        return newInstanceNdr(1, BigInteger.valueOf(n), BigInteger.valueOf(d), BigInteger.valueOf(r));
    }

    public static Term asFraction(BigInteger n, BigInteger d, BigInteger r) {
        return newInstanceNdr(1, n, d, r);
    }


    public static Term newInstance(int signum, BigInteger numerator, BigInteger denominator, BigInteger radical,
                                   Map<String, Fraction> character) {
        return newInstanceP(signum, numerator, denominator, radical, new TreeMap<>(character));
    }


    static class TermCalculator implements JGroupCalculator<Term>, MathAdder<Term> {
        @NotNull
        @Override
        public Term inverse(@NotNull Term x) {
            return x.reciprocal();
        }

        @NotNull
        @Override
        public Term getIdentity() {
            return Term.ONE;
        }

        @NotNull
        @Override
        public Term apply(@NotNull Term x, @NotNull Term y) {
            return x.multiply(y);
        }

        @Override
        public boolean isEqual(@NotNull Term x, @NotNull Term y) {
            return x.equals(y);
        }

        @Override
        public boolean canAdd(Term e1, Term e2) {
            return e1.canMerge(e2);
        }

        @Override
        public Term addEle(Term e1, Term e2) {
            return e1.add(e2);
        }

        @NotNull
        @Override
        public Term gpow(@NotNull Term x, long n) {
            if (n > Integer.MAX_VALUE || n < Integer.MIN_VALUE) {
                if (x.isZero()) {
                    return ZERO;
                } else if (x.equals(ONE)) {
                    return ONE;
                } else if (x.equals(NEGATIVE_ONE)) {
                    return n % 2 == 0 ? ONE : NEGATIVE_ONE;
                }
                throw new ArithmeticException("Overflow for n=" + n);
            }
            return x.pow((int) n);
        }

        @Override
        public boolean isCommutative() {
            return true;
        }

    }

    private static final TermCalculator TERM_CALCULATOR = new TermCalculator();


    static class TermSimplifier implements Simplifier<Term> {

        @Override
        public List<Term> simplify(List<Term> numbers) {
            Term[] fs = numbers.toArray(new Term[]{});
            Term.gcdAndDivide(fs);
            return Arrays.asList(fs);
        }

        @Override
        public Term simplify(Term x) {
            return x;
        }
    }

    private static final TermSimplifier SIMPLIFIER = new TermSimplifier();

    public static GroupCalculator<Term> getCalculator() {
        return TERM_CALCULATOR;
    }

    public static Simplifier<Term> getSimplifier() {
        return SIMPLIFIER;
    }

    /**
     * Only applicable for {-1,0,1}
     *
     * @param x
     * @return
     */
    public static Term nroot(Term x, long n) {

        if (x.equals(ZERO)) {
            return ZERO;
        }
        if (x.equals(ONE)) {
            return ONE;
        }
        if (x.equals(NEGATIVE_ONE)) {
            if (n % 2 == 1) {
                return NEGATIVE_ONE;
            }
            if (n % 4 == 2)
                return I;
        }
        throw new UnsupportedCalculationException();
    }

    public static void main(String[] args) {

    }
//        var t1 = valueOf("x^2");
//        var t2 = valueOf("y^2");
//        var t3 = valueOf("x");
//        var t4 = valueOf("y");
//        var t5 = valueOf("y^-1");
//        var t6 = valueOf("x^-1");
//        var list = Arrays.asList(t1, t2, t3, t4, t5, t6);
//        Collections.sort(list);
//        print(list);

//       var t1 = Term.valueOf("6a");
//       var t2 = Term.valueOf("-3");
//       var t3 = Term.valueOf("9a");
//       var t4 = Term.valueOf("-6");
//       print(Term.getSimplifier().simplify(Arrays.asList(t1,t2,t3,t4)));


//        print(ONE.equals(ONE.negate()));
//        print(ONE.compareTo(ONE.negate()));
//        print(Term.valueOf("x^-1"));
//        int times = 100000;
//		Term tt = Term.valueOf("3aasdadxxcqSqr3/2");
//		print(tt);
//		Term tsum = Term.ZERO;
//        Timer t = new Timer(TimeUnit.MILLISECONDS);
//        t.start();
//        for(int i=0;i<times;i++){
//            tsum = tsum.add(tt);
////            tt = Term.valueOf(10);
//        }
//        print(t.end());
//        print(tsum);
//
//        Formula tf = Formula.valueOf("3aasdadxxcqSqr3/2");
//        Formula fsum = Formula.ZERO;
//        FormulaCalculator fc = FormulaCalculator.getCalculator();
//        t.start();
//        for(int i=0;i<times;i++){
//            fsum = fc.add(fsum,tf);
////            tf = Formula.valueOf(10);
//        }
//        print(t.end());
//        print(fsum);
//    }
}
