package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.MathSymbol;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.algebra.IPolynomial;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.api.Computable;
import cn.timelives.java.math.numberModels.api.Simplifier;
import cn.timelives.java.math.numberTheory.combination.Permutation;
import cn.timelives.java.math.numberTheory.combination.Permutations;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.ModelPatterns;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.*;

import static cn.timelives.java.utilities.Printer.print;

/**
 * An improved class for multinomial.
 * A multinomial is composed of several terms.
 *
 * @see Term
 */
public class Multinomial implements Comparable<Multinomial>, Computable, Serializable {
    final NavigableSet<Term> terms;

    static boolean mergingAdd(NavigableSet<Term> base, Term e) {
        Term low = base.floor(e);
        if (low != null && low.canMerge(e)) {
            base.remove(low);

            return mergingAdd(base, low.merge(e));
        }
        Term high = base.higher(e);
        if (high != null && high.canMerge(e)) {
            base.remove(high);
            return mergingAdd(base, high.merge(e));
        }
        return base.add(e);
    }

    /**
     * @param set1 will be modified
     * @param set2 won't be modified
     */
    static void mergingAddAll(NavigableSet<Term> set1, Iterable<Term> set2) {
        for (Term t : set2) {
            mergingAdd(set1, t);
        }
    }

    private static final Comparator<Term> charComp = Term::compareCharAndRadical;

    static NavigableSet<Term> getSet() {
        return new TreeSet<>(charComp);
    }

    static NavigableSet<Term> getSet(NavigableSet<Term> set) {
        NavigableSet<Term> nset = new TreeSet<>(charComp);
        nset.addAll(set);
        return nset;
    }

    /**
     * The result set must not be modified.
     *
     * @param s1
     * @param s2
     * @return
     */
    static NavigableSet<Term> mergingMultiply(NavigableSet<Term> s1, NavigableSet<Term> s2) {
        if (ZERO.terms.equals(s1) || ZERO.terms.equals(s2)) {
            return singleTerm(Term.ZERO);
        }
        NavigableSet<Term> set = getSet();
        for (Term x : s1) {
            for (Term y : s2) {
                mergingAdd(set, x.multiply(y));
            }
        }
        return set;
    }

    static NavigableSet<Term> multiplyToSet(NavigableSet<Term> set, Term t) {
        NavigableSet<Term> nset = getSet();
        for (Term x : set) {
            Term re = x.multiply(t);
            nset.add(re);
        }
        return nset;
    }

    Multinomial(NavigableSet<Term> set) {
        this.terms = set;
    }

    Multinomial(Term t) {
        this.terms = singleTerm(t);
    }

    public static final Multinomial ZERO = new Multinomial(Term.ZERO);

    public static final Multinomial ONE = new Multinomial(Term.ONE);

    public static final Multinomial TWO = new Multinomial(Term.valueOf(2));

    public static final Multinomial NEGATIVE_ONE = new Multinomial(Term.NEGATIVE_ONE);

    public static final Multinomial PI = new Multinomial(Term.PI);
    public static final Multinomial I = new Multinomial(Term.I);
    public static final Multinomial E = new Multinomial(Term.E);

    static NavigableSet<Term> singleTerm(Term t) {
        NavigableSet<Term> ts = getSet();
        ts.add(t);
        return ts;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Term t : terms) {
            // if(!f.equalsIgnoreDecimal(Formula.ZERO))
            sb.append(t.toString());
            if (isFirst && sb.charAt(0) == '+') {
                sb.deleteCharAt(0);
                isFirst = false;
            }
        }
        return sb.toString();
    }

    public int getNumOfTerms() {
        return terms.size();
    }

    public boolean isMonomial() {
        return terms.size() == 1;
    }

    /**
     * Determines whether this multinomial is zero
     */
    public boolean isZero() {
        return isMonomial() && getFirst().isZero();
    }

    public boolean isOne() {
        if(!isMonomial()){
            return false;
        }
        var t = getFirst();
        return t.isInteger() && t.signum == 1 && t.numerator.equals(BigInteger.ONE);
    }

    public NavigableSet<Term> getTerms() {
        return Collections.unmodifiableNavigableSet(terms);
    }

    /**
     * Gets a set of characters that appears in this multinomial.
     */
    public NavigableSet<String> getCharacters() {
        return getCharacters(terms);
    }

    static NavigableSet<String> getCharacters(NavigableSet<Term> ts) {
        NavigableSet<String> set = new TreeSet<>();
        for (Term t : ts) {
            set.addAll(t.getCharacterNameNoCopy());
        }
        return set;
    }

    /**
     * Gets the first term in this multinomial according to the dictionary order.
     *
     */
    public Term getFirst() {
        return terms.first();
    }

    /**
     * Gets the last term in this multinomial according to the dictionary order.
     *
     */
    public Term getLast() {
        return terms.last();
    }


    /**
     * Remove all the terms that matches the predicate.
     */
    public Multinomial removeAll(Predicate<Term> predicate) {
        return retainAll(predicate.negate());
    }

    /**
     * Retains all the terms that matches the predicate.
     */
    public Multinomial retainAll(Predicate<Term> predicate) {
        NavigableSet<Term> set = getSet();
        for (Term t : terms) {
            if (predicate.test(t)) {
                set.add(t);
            }
        }
        if (set.isEmpty()) {
            return ZERO;
        }
        return new Multinomial(set);
    }

    /**
     * Returns a multinomial containing all the terms that contains the given character.
     *
     * @param character a character
     */
    public Multinomial filterChar(String character) {
        return retainAll(x -> x.containsChar(character));
    }

    /**
     * Partitions this multinomial by the given predicate.
     *
     * @param predicate a predicate of terms
     * @return an array containing a multinomial of terms on which the predicate returns true as the
     * first element and a multinomial of remaining terms as the second element
     */
    public Multinomial[] partitionBy(Predicate<Term> predicate) {
        NavigableSet<Term> set1 = getSet();
        NavigableSet<Term> set2 = getSet();
        for (Term t : terms) {
            if (predicate.test(t)) {
                set1.add(t);
            } else {
                set2.add(t);
            }
        }
        Multinomial[] arr = new Multinomial[2];
        if (!set1.isEmpty()) {
            arr[0] = new Multinomial(set1);
        } else {
            arr[0] = ZERO;
        }
        if (!set2.isEmpty()) {
            arr[1] = new Multinomial(set2);
        } else {
            arr[1] = ZERO;
        }
        return arr;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Multinomial)) {
            return false;
        }
        //use the equals() method
        return CollectionSup.collectionEqualSorted(terms, ((Multinomial) obj).terms, Term::equals);
    }

    private int hashCode = 0;

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int hash = 0;
            for (Term f : terms) {
                hash += f.hashCode() * 31;
            }
            hashCode = hash;
        }
        return hashCode;
    }

    @Override
    public int compareTo(@NotNull Multinomial o) {
        if (this == o) {
            return 0;
        }
        return CollectionSup.compareCollection(terms, o.terms, Comparator.naturalOrder());
    }


    /**
     * Compare the value of this and x, assigning one to all the characters.
     *
     * @param x another multinomial
     * @return {@code -1} if this<x, 0 if this = x, 1 if this > x.
     */
    public int compareValueTo(Multinomial x) {
        return Double.compare(computeDouble(Computable.ASSIGN_ONE), x.computeDouble(Computable.ASSIGN_ONE));
    }

    /**
     * Returns the result of {@code this*t}.
     *
     * @return {@code this*t}
     */
    public Multinomial multiply(Term t) {
        if (t.isZero()) {
            return ZERO;
        }
        return new Multinomial(multiplyToSet(terms, t));
    }

    /**
     * Returns the result of {@code this / t}.
     *
     * @return {@code this/t}
     */
    public Multinomial divide(Term divisor) {
        if (divisor.isZero()) {
            throw new ArithmeticException("Divide by zero!");
        }
        NavigableSet<Term> nset = getSet();
        for (Term x : terms) {
            Term re = x.divide(divisor);
            nset.add(re);
        }
        return new Multinomial(nset);
    }

    /**
     * Returns {@code -this}
     *
     * @return {@code -this}
     */
    public Multinomial negate() {
        if (ZERO.equals(this)) {
            return ZERO;
        }
        NavigableSet<Term> nset = getSet();
        for (Term x : terms) {
            nset.add(x.negate());
        }
        return new Multinomial(nset);
    }

    public Multinomial add(Multinomial m) {
        NavigableSet<Term> set = getSet(terms);
        mergingAddAll(set, m.terms);
        return new Multinomial(set);
    }

    public Multinomial subtract(Multinomial m) {
        NavigableSet<Term> set = getSet(terms);
        for (Term x : m.terms) {
            mergingAdd(set, x.negate());
        }
        return new Multinomial(set);
    }

    public Multinomial add(Term t) {
        NavigableSet<Term> nset = getSet(terms);
        mergingAdd(nset, t);
        return new Multinomial(nset);
    }

    public Multinomial subtract(Term t) {
        return add(t.negate());
    }


    public Multinomial multiply(Multinomial m) {
        if (ZERO.equals(this) || ZERO.equals(m)) {
            return ZERO;
        }
        NavigableSet<Term> set = getSet();
        for (Term x : terms) {
            for (Term y : m.terms) {
                mergingAdd(set, x.multiply(y));
            }
        }
        return new Multinomial(set);
    }


    public Multinomial reciprocal() {
        if (ZERO.equals(this)) {
            throw new ArithmeticException("Divide by 0.");
        }
        if (getNumOfTerms() > 1) {
            throw new UnsupportedCalculationException("1/(" + this.toString() + ")");
        }
        return new Multinomial(terms.first().reciprocal());
    }

    /**
     * Tries the divide this multinomial with the given multinomial, throws an UnsupportedCalculationException
     * if the result cannot be represented by a multinomial.
     */
    public Multinomial divide(Multinomial p2) {
        int num = p2.getNumOfTerms();
        if (num == 1) {
            return divide(p2.terms.first());
        } else {   //    1/(Sqr2-1)
            /* e.g. 1 / (Sqr2 -1 )
             *     = 1 * (Sqr2 + 1)  / ((Sqr2+1)(Sqr2 -1))
             *     = (Sqr2 + 1) / (2-1)
             *     =Sqr2 +1
             */
            boolean moreComplex = false;
            for (Term term : p2.terms) {
                if (term.numOfChar() != 0) {
                    moreComplex = true;
                    break;
                }
            }
            if (moreComplex) {
                return new Multinomial(handleComplexDivision(this.terms, p2.terms));
            } else {
                Term d = p2.terms.first();
                Multinomial mul = p2.subtract(d.multiply(BigInteger.valueOf(2)));
                return this.multiply(mul).divide(p2.multiply(mul));
            }
        }
    }


    public Multinomial[] divideAndRemainder(Multinomial divisor) {
        NavigableSet<Term> m = getSet(terms);
        NavigableSet<Term> q = singleTerm(Term.ZERO);
        multinomialDivision(m, divisor.terms, q);
        return new Multinomial[]{new Multinomial(q), new Multinomial(m)};
    }



    private static boolean containsNonInteger(NavigableSet<Term> s) {
        for (Term t : s) {
            for (Fraction p : t.getCharacterNoCopy().values()) {
                if (!p.isInteger()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param m        modified to the remainder
     * @param divisor  remains the identity
     * @param quotient added
     */
    private static void multinomialDivision(NavigableSet<Term> m, NavigableSet<Term> divisor, NavigableSet<Term> quotient) {
        //multinomial division
        boolean nonInteger = containsNonInteger(m);
        //fix bug: 9223372036854775807/2 ~
        if (nonInteger) {
            return;
        }
        nonInteger = containsNonInteger(divisor);
        if (nonInteger) {
            return;
        }

        Set<String> remainChars = getCharacters(m);
        Term divisorHead = divisor.first();
        Set<String> divisorChars = getCharacters(divisor);
//        List<Term> extraRemainders = new ArrayList<>(2);

        //while(true){
        while (remainChars.containsAll(divisorChars)) {
            Term head = m.first();

            if (head.compareChar(divisorHead) > 0) {
                //can't divide
                break;
            }
            Term q = head.divide(divisorHead);
//            if(q.containNegativePower()){
//                extraRemainders.add(head);
//                m.pollFirst();
//                if(m.isEmpty()){
//                    break;
//                }
//                continue;
//            }
            mergingAdd(quotient, q);
            mergingAddAll(m, multiplyToSet(divisor, q.negate()));
            remainChars = getCharacters(m);
        }
//        mergingAddAll(m, extraRemainders);
    }

    private static void reduceGcd(NavigableSet<Term> m1, NavigableSet<Term> m2) {
        int size1 = m1.size(),
                size2 = m2.size();
        List<Term> list = new ArrayList<>(size1 + size2);
        list.addAll(m1);
        list.addAll(m2);
        list = Term.getSimplifier().simplify(list);
        m1.clear();
        m1.addAll(list.subList(0, size1));
        m2.clear();
        m2.addAll(list.subList(size1, list.size()));
    }

    /**
     * Parameters will not be modified.
     * Throws an exception if it can't be done.
     * @return quotient
     */
    private static NavigableSet<Term> handleComplexDivision(NavigableSet<Term> m, NavigableSet<Term> divisor) {
        m = getSet(m);
        divisor = getSet(divisor);

        reduceGcd(m, divisor);

        NavigableSet<Term> quotient = singleTerm(Term.ZERO); //the quotient
        multinomialDivision(m, divisor, quotient);
        if (m.size() > 1 || !m.first().isZero()) {
            throw new UnsupportedCalculationException();
        }
        return quotient;
    }


    public Multinomial pow(int pow) {
        if (pow < 0) {
            if (getNumOfTerms() == 1) {
                Term t = terms.first();
                return new Multinomial(t.pow(pow));
            }
            throw new UnsupportedCalculationException("This Method Cannot Calculate while Power is less than 0");
        }
        if (pow == 0) {
            if (ZERO.equals(this))
                throw new UnsupportedCalculationException("0^0 has not been Defined");
            else
                return ONE;
        }
        NavigableSet<Term> result = ModelPatterns.binaryProduce(pow, terms, Multinomial::mergingMultiply);
        return new Multinomial(result);
    }

    /**
     * Determines whether the multinomial contains the specific character.
     */
    public boolean containsChar(String target) {
        for (Term x : terms) {
            if (x.containsChar(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the count of the terms that have the character of {@code targer}.
     */
    public int containsCharCount(String target) {
        int count = 0;
        for (Term x : terms) {
            if (x.containsChar(target)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Replace the character {@code target} with the given Multinomial. This method
     * will throw an Exception is the power of the character is not a positive integer.
     *
     * @param target the character to replace
     * @param expr   a Multinomial
     * @return the result
     */
    public Multinomial replace(String target, Multinomial expr) {
        NavigableSet<Term> result = getSet();

        for (Term t1 : terms) {
            if (t1.containsChar(target)) {
                Fraction power = t1.getCharacterPower(target);
                //should be an int above zero

                if (power.isInteger()) {
                    long pow = power.toLong();
                    Term f = t1.removeChar(target);
                    Multinomial temp = monomial(f);
                    temp = temp.multiply(expr.pow(Math.toIntExact(pow)));

                    mergingAddAll(result, temp.terms);
                } else {
                    if (ONE.equals(expr)) {
                        mergingAdd(result, t1.removeChar(target));
                    } else if (ZERO.equals(expr)) {
                        // zero
                    } else {
                        throw new UnsupportedCalculationException("Cannot calculate : Decimal exponent");
                    }
                }
            } else {
                mergingAdd(result, t1);
            }
        }

        return new Multinomial(result);
    }

    public Multinomial replaceChar(UnaryOperator<String> replacer){
        var nterms = getSet();
        for(Term t : terms){
            nterms.add(t.replaceChar(replacer));
        }
        return new Multinomial(nterms);
    }

    @Override
    public double computeDouble(ToDoubleFunction<String> valueMap) {
        double re = 0d;
        for (Term t : terms) {
            re += t.computeDouble(valueMap);
        }
        return re;
    }

    @Override
    public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
        T re = mc.getZero();
        for (Term t : terms) {
            re = mc.add(re, t.compute(valueMap, mc));
        }
        return re;
    }

    /**
     * Determines whether this multinomial is a symmetry multinomial.
     */
    public boolean isSymmetry(){
        // find all the characters in this multinomial

        var characters = getCharacters().toArray(new String[]{});
        int n = characters.length;
        for(Permutation p : Permutations.universeIterable(n)){
            if(p.isIdentity()){
                continue;
            }
            var m = replaceChar( x -> {
                int index = Arrays.binarySearch(characters,x);
                return characters[p.apply(index)];
            });
            if(!equals(m)){
                return false;
            }
        }
        return true;
    }


    /**
     * Creates a multinomial from a string expression, which should consist of a series
     * of string representing terms connected by '+' and '-'.
     * <P>Examples:
     * <pre>abc + 2x^2</pre>
     *
     * @param expr a string representing the multinomial
     * @return a new multinomial.
     */
    public static Multinomial valueOf(String expr) {
        expr = expr.trim();
        if (expr.isEmpty()) {
            throw new NumberFormatException("Empty!");
        }
        NavigableSet<Term> terms = getSet();
        char c;
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            c = expr.charAt(i);
            if (c == '+' || c == '-') {

                if (i != 0) {
                    char cr = expr.charAt(i - 1);
                    if (cr != '+' && cr != '-' && cr != '^') {
                        mergingAdd(terms, Term.valueOf(temp.toString()));
                        temp = new StringBuilder();
                    }
                }
            }
            temp.append(c);
        }
        mergingAdd(terms, Term.valueOf(temp.toString()));
        if (terms.isEmpty()) {
            return ZERO;
        }
        return new Multinomial(terms);
    }

    /**
     * Creates a multinomial from a BigInteger.
     *
     * @param val a BigInteger
     * @return a Multinomial
     */
    public static Multinomial valueOf(BigInteger val) {
        return monomial(Term.valueOf(val));
    }

    /**
     * Returns a Multinomial that only contains the given term.
     *
     * @param t a term
     * @return a new Multinomial
     */
    public static Multinomial monomial(Term t) {
        if (t.isZero()) {
            return ZERO;
        }
        return new Multinomial(singleTerm(t));
    }

    public static Multinomial fromTerms(Iterable<Term> ts) {
        NavigableSet<Term> set = getSet();
        mergingAddAll(set, ts);
        if (set.isEmpty()) {
            return ZERO;
        }
        return new Multinomial(set);
    }

    public static Multinomial fromPolynomialT(IPolynomial<Term> p, String variableName) {
        NavigableSet<Term> set = getSet();
        for (int i = 0; i <= p.getDegree(); i++) {
            Term t = p.getCoefficient(i);
            if (t.isZero()) {
                continue;
            }
            t = t.multiply(Term.characterPower(variableName, Fraction.valueOf(i)));
            mergingAdd(set, t);
        }
        if (set.isEmpty()) {
            return ZERO;
        }
        return new Multinomial(set);
    }

    public static Multinomial fromPolynomialM(IPolynomial<Multinomial> p, String variableName) {
        NavigableSet<Term> set = getSet();
        for (int i = 0; i <= p.getDegree(); i++) {
            Multinomial t = p.getCoefficient(i);
            if (t.isZero()) {
                continue;
            }
            var x = Term.characterPower(variableName, Fraction.valueOf(i));
            var re = multiplyToSet(t.terms, x);
            mergingAddAll(set, re);
        }
        if (set.isEmpty()) {
            return ZERO;
        }
        return new Multinomial(set);
    }


    /**
     * Reduces the greatest common divisor of all the Multinomials given.
     *
     * @param numbers a list of Multinomial
     * @return a list containing the result
     */
    public static List<Multinomial> reduceGcd(List<Multinomial> numbers) {
        int len = numbers.size();
        if (len <= 1) {
            return numbers;
        }
        List<Term> list = new ArrayList<>(len * 2);
        int[] indexes = new int[len];
        int i = 0;
        int pos = 0;
        for (Multinomial p : numbers) {
            for (Term f : p.terms) {
                list.add(f);
                i++;
            }
            indexes[pos++] = i;
        }
        List<Term> reF = Term.getSimplifier().simplify(list);
        List<Multinomial> re = new ArrayList<>(len);
        i = 0;
        Iterator<Term> it = reF.iterator();
        Multinomial gcd = null;
        for (int j = 0; j < len; j++) {
            NavigableSet<Term> set = getSet();
            for (; i < indexes[j]; i++) {
                set.add(it.next());
            }
            Multinomial po = new Multinomial(set);
            if (gcd == null) {
                gcd = po;
            } else {
                gcd = cal.gcd(gcd, po);
            }
            re.add(po);
        }
        if (!gcd.isMonomial()) {
            for (int j = 0; j < len; j++) {
                re.set(j, re.get(j).divide(gcd));
            }
        }

        return re;
    }

    /**
     * Simplifies the two multinomial as if they are a fraction, finding the gcd and dividing by it
     * respectively.
     *
     * @param m1 a Multinomial
     * @param m2 another Multinomial
     * @return an array containing the Multinomials after simplification correspondingly
     */
    public static Multinomial[] simplifyFraction(Multinomial m1, Multinomial m2) {
        var set1 = getSet(m1.terms);
        var set2 = getSet(m2.terms);
        reduceGcd(set1, set2);
        m1 = new Multinomial(set1);
        m2 = new Multinomial(set2);
        var gcd = cal.gcd(m1, m2);
        return new Multinomial[]{m1.divide(gcd), m2.divide(gcd)};
    }

    private static final MultinomialCalculator cal = new MultinomialCalculator();

    public static MultinomialCalculator getCalculator() {
        return cal;
    }

    private static final Simplifier<Multinomial> sim = new MultinomialCalculator.MSimplifier();

    public static Simplifier<Multinomial> getSimplifier() {
        return sim;
    }

    /**
     * Adds all the multinomials, providing better performance than adding one by one.
     * Returns ZERO if the given array is empty.
     *
     * @param ms an array of Multinomial
     * @return the result after addition
     */
    public static Multinomial addAll(Multinomial... ms) {
        if (ms.length == 0) {
            return ZERO;
        }
        NavigableSet<Term> result = getSet();
        for (Multinomial m : ms) {
            mergingAddAll(result, m.terms);
        }
        return new Multinomial(result);
    }

    /**
     * Multiplies all the multinomials, providing better performance than multiplying
     * them one by one. Returns ONE if the given array is empty.
     *
     * @param ms an array of Multinomial
     * @return the result after multiplication
     */
    public static Multinomial multiplyAll(Multinomial... ms) {
        if (ms.length == 0) {
            return ONE;
        }
        NavigableSet<Term> result = singleTerm(Term.ONE);
        for (Multinomial m : ms) {
            result = mergingMultiply(result, m.terms);
        }
        return new Multinomial(result);
    }


    /**
     * Returns a term if the {@link Multinomial} can be convert to a term, or null if it cannot.
     *
     * @param p a multinomial
     * @return a term or null/
     */
    public static Term asSingleTerm(Multinomial p) {
        if (p.isMonomial()) {
            return p.getFirst();
        }
        return null;
    }

    /**
     * Returns a big integer if the {@link Multinomial} can be convert to an integer, or null
     */
    public static BigInteger asBigInteger(Multinomial p) {
        Term f = asSingleTerm(p);
        if (f == null) {
            return null;
        }
        BigInteger x = Term.asInteger(f);
        return x;
    }

    /**
     * Creates a Multinomial from a long.
     *
     * @param n a long
     */
    public static Multinomial valueOf(long n) {
        if (n == 0) {
            return ZERO;
        } else if (n == -1) {
            return NEGATIVE_ONE;
        } else if (n == 1) {
            return ONE;
        } else if (n == 2) {
            return TWO;
        }
        return monomial(Term.valueOf(n));
    }

    /**
     * Returns the primary symmetry multinomial of the given characters.
     * <p></p>
     * For example, <code>primarySymmetry(1,"a","b","c")</code> returns <code>a+b+c</code><br>
     * <code>primarySymmetry(2,"a","b","c")</code> returns <code>ab+bc+ca</code> and <br>
     * <code>primarySymmetry(3,"a","b","c")</code> returns <code>abc</code>.
     * @param r          the number of characters in each term, must be not bigger than characters' length.
     * @param characters different characters
     * @return primary symmetry multinomial of the given character
     */
    public static Multinomial primarySymmetry(int r, String... characters) {
        var terms = getSet();
        Collections.addAll(terms, Term.symmetryTerms(r, characters));
        return new Multinomial(terms);
    }

    /**
     * Returns an array of all the primary symmetry multinomial.
     * @param characters different characters
     */
    public static Multinomial[] primarySymmetryAll(String...characters){
        Multinomial[] arr = new Multinomial[characters.length];
        for(int i=0;i<characters.length;i++){
            arr[i] = primarySymmetry(i+1,characters);
        }
        return arr;
    }

    /**
     * Returns the newton multinomial of power <code>p</code> of the given characters.<p></p>
     * For example, <code>newtonMultinomial(2,"a","b","c")</code> returns <code>a^2+b^2+c^2</code>
     * @param p the power of each characters
     * @param characters different characters.
     * @return a new newton multinomial
     */
    public static Multinomial newtonMultinomial(int p, String... characters){
        var terms = getSet();
        Fraction power = Fraction.valueOf(p);
        for(String c : characters){
            terms.add(Term.characterPower(c,power));
        }
        return new Multinomial(terms);
    }

    /**
     * Reduces a symmetry multinomial to a multinomial of primary symmetry multinomial.
     * @see #primarySymmetryReduce(IntFunction)
     * @see #primarySymmetry(int, String...)
     */
    public Multinomial primarySymmetryReduce(){
        return primarySymmetryReduce(x ->
            MathSymbol.GREEK_SIGMA +Integer.toString(x+1));
    }

    /**
     * Reduces a symmetry multinomial to a multinomial of primary symmetry multinomial whose names are
     * given by <code>symNameList</code>.
     * @param symNameList a provider for primary symmetry multinomials, starting from zero.
     */
    public Multinomial primarySymmetryReduce(IntFunction<String> symNameList){
        if(!isSymmetry()){
            throw new IllegalArgumentException("m is not symmetry!");
        }
        for(Term t : terms){
            for(var en : t.getCharacterNoCopy().entrySet()){
                Fraction pow = en.getValue();
                if(pow.isNegative() || !pow.isInteger()){
                    throw new ArithmeticException("Cannot reduce multinomial containing negative or fractional power: "+this);
                }
            }
        }
        var characters = getCharacters().toArray(new String[]{});
        Multinomial[] syms = primarySymmetryAll(characters);
        Multinomial m = this;
        var result = getSet();
        var top = m.getFirst();
        while(!top.isZero()){
            Term coe = top.numberPart();
            Multinomial g = Multinomial.monomial(coe);
            NavigableMap<String,Fraction> map = new TreeMap<>();
            for (int i = 0; i < characters.length-1; i++) {
                String c = characters[i];
                Fraction pow = top.getCharacterPower(c).subtract(top.getCharacterPower(characters[i+1]));
                g = g.multiply(syms[i].pow(pow.toInt()));
                if(!pow.isZero()){
                    map.put(symNameList.apply(i),pow);
                }
            }
            int last = characters.length-1;
            String c = characters[last];
            Fraction pow = top.getCharacterPower(c);
            g = g.multiply(syms[last].pow(pow.toInt()));
            if(!pow.isZero()){
                map.put(symNameList.apply(last),pow);
            }

            m = m.subtract(g);
            mergingAdd(result,coe.sameNumber0(map));
            top = m.getFirst();
        }
        return new Multinomial(result);

    }




//    public static void main(String[] args) {
//        print(Multinomial.valueOf("Sqr18-1"));
//        print(Term.valueOf(1).canMerge(Term.valueOf("Sqr18")));
//    }
//        print(cal.gcd(valueOf("a-b"), valueOf("a+b")));
//        var re = simplifyFraction(Multinomial.monomial(
//                Term.singleChar("x",Fraction.Companion.valueOf("5/2"))),Multinomial.valueOf("-x+1"));
//        print(re);
//        Multinomial m1 = valueOf("x"),
//                m2 = valueOf("x-4y");
//        print(m1.add(m2));
//        print(m1.subtract(m2));
//        print(m1.multiply(m2));
//        print(m1.negate());
//        print(m1.multiply(Multinomial.ONE.negate()));
//        print(m1);
//        print(m2);
//        print(m1.divideAndRemainder(m2));
//        Multinomial m = valueOf("x+y+z");
//        cn.timelives.java.utilities.Timer t = new cn.timelives.java.utilities.Timer(TimeUnit.MILLISECONDS);
//        t.start();
//        m.pow(100);
//        print(t.end());
//
//        PolynomialOld p = PolynomialOld.valueOf("x+y+z");
//        var pc = PolynomialOld.getCalculator();
//        t.start();
//        pc.pow(p,100);
//        print(t.end());
//    }
}
