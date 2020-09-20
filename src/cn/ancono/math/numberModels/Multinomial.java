package cn.ancono.math.numberModels;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathSymbol;
import cn.ancono.math.MathUtils;
import cn.ancono.math.algebra.IPolynomial;
import cn.ancono.math.exceptions.UnsupportedCalculationException;
import cn.ancono.math.numberModels.api.Computable;
import cn.ancono.math.numberModels.api.Simplifier;
import cn.ancono.math.numberModels.structure.Polynomial;
import cn.ancono.math.numberModels.structure.PolynomialSup;
import cn.ancono.math.numberModels.structure.RingFraction;
import cn.ancono.math.numberTheory.combination.Permutation;
import cn.ancono.math.numberTheory.combination.Permutations;
import cn.ancono.utilities.CollectionSup;
import cn.ancono.utilities.ModelPatterns;
import cn.ancono.utilities.structure.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.*;

import static cn.ancono.utilities.Printer.print;

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

    public String toLatexString() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Term t : terms) {
            if (isFirst) {
                sb.append(t.toLatexString(false));
                isFirst = false;
            } else {
                sb.append(t.toLatexString(true));
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
        if (!isMonomial()) {
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
     */
    public Term getFirst() {
        return terms.first();
    }

    /**
     * Gets the last term in this multinomial according to the dictionary order.
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
        return CollectionSup.compareCollectionLexi(terms, o.terms, Comparator.naturalOrder());
    }

    /**
     * Compares only the character part to another multinomial.
     */
    public int compareCharTo(@NotNull Multinomial m) {
        return CollectionSup.compareCollectionLexi(terms, m.terms, Term::compareChar);
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

    /**
     * Extract the greatest common divisor as a term and returns the remaining multinomial.
     */
    public Pair<Term, Multinomial> extractGCD() {
        var gcd = Term.gcd(terms.toArray(new Term[0]));
        return new Pair<>(gcd, this.divide(gcd));
    }


    /**
     * Determines whether there is a multinomial m such that <code>m * this = 1</code>.
     */
    public boolean isInvertible() {
        if (isMonomial()) {
            return !getFirst().isZero();
        }
        return extractGCD().getSecond().containsOnlyAlgebraicChar();
        // must be the form of x(a + bSqr2 + cSqr3 ...)
    }

    /**
     * Determines whether this multinomial is invertible and the inverse is also a multinomial.
     *
     * @return
     */
    public boolean isPolyInvertible() {
        if (isZero()) {
            return false;
        }
        return containsOnlyAlgebraicChar();
    }

    /**
     * Determines whether there is a multinomial m such that <code>m * this = 1</code>.
     */
    public Multinomial reciprocal() {
        if (isMonomial()) {
            return new Multinomial(getFirst().reciprocal());
        }
        /*
        A non-monomial multinomial is only invertible when it contains only algebraic characters, such as 'i = Sqr(-1)'
         */
        var pair = extractGCD();
        var m0 = pair.getSecond();
        if (!m0.containsOnlyAlgebraicChar()) {
            throw new UnsupportedCalculationException("Not invertible: 1/(" + this.toString() + ")");
        }
        return inverseSqr(m0).divide(pair.getFirst());
    }

    /**
     * Computes the conjugations of m(the gcd is already extracted) and then
     */
    private static Multinomial inverseSqr(Multinomial m) {
        /*computes all the conjugations (m=m0,m1,m2,m3 .. ) of m in Q[SqrA,SqrB,...] and the product
        N(m) = m0*m1*... must be in Q. So the result should be 1/m = (m1*m2*....) / N(m)
         */
        var conjs = conj0(m);
        var nume = Multinomial.ONE;
        for (int i = 1; i < conjs.size(); i++) {
            nume = nume.multiply(conjs.get(i));
        }
        var norm = nume.multiply(m); //must be a monomial
        if (!norm.isMonomial()) {
            throw new ArithmeticException("The product of all the conjugations of (" + m + ") is not a monomial: " + norm);
        }
        return nume.divide(norm.getFirst());
    }

//    private static void conj0(List<Term> terms, int level, ArrayList<Term> buf, List<Multinomial> result) {
//        if (level == terms.size()) {
//            result.add(Multinomial.)
//        }
//    }

    private static List<Multinomial> conj0(Multinomial x) {
        var size = x.terms.size();
        List<Pair<Term, Term>> terms = new ArrayList<>(size);
        List<BigInteger> numbers = new ArrayList<>(size);
        boolean containI = false;
        for (var t : x.terms) {
            numbers.add(t.radical);
            if (t.containsChar(Term.I_STR)) {
                containI = true;
            }
            terms.add(new Pair<>(t, t.negate()));
        }
        var base = coprimeBaseReduction(numbers);
        if (containI) {
            base.add(BigInteger.ONE.negate());
        }
        List<BitSet> factors = new ArrayList<>(size);
        for (var t : x.terms) {
            var n = t.radical;
            BitSet bits = new BitSet(base.size());
            for (int i = 0; i < base.size(); i++) {
                var b = base.get(i);
                if (b.signum() < 0) {
                    if (t.containsChar(Term.I_STR)) {
                        bits.set(i);
                    }
                } else if (n.mod(b).equals(BigInteger.ZERO)) {
                    bits.set(i);
                }
            }
            factors.add(bits);
        }
        var allPossibilities = CollectionSup.cartesianProduct(Collections.nCopies(base.size(), Arrays.asList(false, true)));
        var results = new ArrayList<Multinomial>(allPossibilities.size());
        for (List<Boolean> p : allPossibilities) {
            var nterms = getSet();
            for (int i = 0; i < size; i++) {
                var bits = factors.get(i);
                var pair = terms.get(i);
                boolean positive = true;
                for (int j = 0; j < base.size(); j++) {
                    if (bits.get(j) && p.get(j)) {
                        positive = !positive;
                    }
                }
                if (positive) {
                    nterms.add(pair.getFirst());
                } else {
                    nterms.add(pair.getSecond());
                }
            }
            results.add(new Multinomial(nterms));
        }
        return results;
    }

    /**
     * Returns a list of all the conjugations of this multinomial. That is, first find a family of square roots
     * (a_1,a_2,...a_n) of integers such that <code>F = Q[a_1,a_2,...a_n]</code> contains all the radical parts in
     * the multinomial, then let <code>{f_i}</code> be all the isomorphism of <code>F</code> that keeps all the
     * elements
     * <code>Q</code> fixed, then {f_i(this)} is the result. It is guaranteed that the first element of the returned
     * list
     * is equal to <code>this</code>.
     *
     * <p></p>
     * For example, the conjugations of <code>1+Sqr2</code> are  <code>[1+Sqr2, 1-Sqr2]</code>. <br>
     * The conjugations of <code>1+Sqr2+Sqr3+Sqr6</code> are <code>[1+Sqr(2)+Sqr(3)+Sqr(6), 1+Sqr(2)-Sqr(3)-Sqr(6),
     * 1-Sqr(2)+Sqr(3)-Sqr(6), 1-Sqr(2)-Sqr(3)+Sqr(6)]</code>
     */
    public List<Multinomial> conjugations() {
        var pair = this.extractGCD();
        var list = conj0(pair.getSecond());
        for (int i = 0; i < list.size(); i++) {
            var m = list.get(i);
            list.set(i, m.multiply(pair.getFirst()));
        }
        return list;
    }

    /**
     * Returns the norm defined as the product of all the conjugations of <code>this</code>.
     *
     * @see Multinomial#conjugations()
     */
    public Multinomial normConj() {
        var conjs = conjugations();
        var re = Multinomial.ONE;
        for (var m : conjs) {
            re = re.multiply(m);
        }
        return re;
    }

    /**
     * Computes a base of co-prime integers for the given numbers.
     * For example, coprimeBaseReduction(1,2,3) = (2,3); coprimeBaseReduction(2,3,6) = (2,3);
     * coprimeBaseReduction(30,42) = (5,6,7)
     *
     * @param numbers
     * @return
     */
    private static List<BigInteger> coprimeBaseReduction(List<BigInteger> numbers) {
        List<BigInteger> result = new ArrayList<>(numbers.size());
        while (!numbers.isEmpty()) {
            var n = numbers.remove(numbers.size() - 1);
            if (n.equals(BigInteger.ONE)) {
                continue;
            }
            boolean coprime = true;
            for (var it = result.listIterator(); it.hasNext(); ) {
                var t = it.next();
                var gcd = n.gcd(t);
                if (!gcd.equals(BigInteger.ONE)) {
                    //not coprime
                    coprime = false;
                    if (gcd.equals(t)) {
                        // t | n
                        var toAdd = n.divide(gcd);
                        if (!toAdd.equals(BigInteger.ONE)) {
                            numbers.add(toAdd);
                        }
                    } else {
                        it.set(gcd);
                        var n1 = n.divide(gcd);
                        var n2 = t.divide(gcd);
                        if (!n1.equals(BigInteger.ONE)) {
                            numbers.add(n1);
                        }
                        if (!n2.equals(BigInteger.ONE)) {
                            numbers.add(n2);
                        }
                    }
                    break;
                }
            }
            if (coprime) {
                result.add(n);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Tries the divide this multinomial with the given multinomial, throws an UnsupportedCalculationException
     * if the result cannot be represented by a multinomial.
     */
    public Multinomial divide(Multinomial p2) {
        var qr = divideAndRemainder(p2);
        if (qr[1].isZero()) {
            return qr[0];
        } else {
            throw new UnsupportedCalculationException("(" + this + ")/(" + p2 + ")");
        }
//        int num = p2.getNumOfTerms();
//        if (num == 1) {
//            return divide(p2.terms.first());
//        } else {   //    1/(Sqr2-1)
//            /* e.g. 1 / (Sqr2 -1 )
//             *     = 1 * (Sqr2 + 1)  / ((Sqr2+1)(Sqr2 -1))
//             *     = (Sqr2 + 1) / (2-1)
//             *     =Sqr2 +1
//             */
//            boolean moreComplex = false;
//            for (Term term : p2.terms) {
//                if (term.numOfChar() != 0) {
//                    moreComplex = true;
//                    break;
//                }
//            }
//            if (moreComplex) {
//                return new Multinomial(handleComplexDivision(this.terms, p2.terms));
//            } else {
//                Term d = p2.terms.first();
//                Multinomial mul = p2.subtract(d.multiply(BigInteger.valueOf(2)));
//                return this.multiply(mul).divide(p2.multiply(mul));
//            }
//        }
    }


    /**
     * Returns the quotient and the remainder of the multinomial division.<br>
     * <b>Note: This method is only designed for test divisibility and should not be used for GCD!</b>
     */
    public Multinomial[] divideAndRemainder(Multinomial divisor) {
        if (divisor.isMonomial()) {
            return new Multinomial[]{this.divide(divisor.getFirst()), Multinomial.ZERO};
        }
        var pair = divisor.extractGCD();
        var m0 = pair.getSecond();
        if (m0.containsOnlyAlgebraicChar()) {
            var q = this.multiply(inverseSqr(m0)).divide(pair.getFirst());
            return new Multinomial[]{q, Multinomial.ZERO};
        }
        NavigableSet<Term> m = getSet(terms);
        NavigableSet<Term> q = singleTerm(Term.ZERO);
        multinomialDivision(m, divisor.terms, q);
        return new Multinomial[]{new Multinomial(q), new Multinomial(m)};
    }


    /**
     * Determines whether there is non-integer power of the character in the terms.
     */
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
     * Computes a multinomial division, the division will stop when the leading term of the divisor is smaller than
     * the remaining parts. It is required that all the power of the characters are integers. This algorithm assures
     * that if the division will be done if there is no remainder.
     *
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
        while (!divisorChars.isEmpty() && remainChars.containsAll(divisorChars)) {
            Term head = m.first();

            if (head.compareChar(divisorHead) > 0) {
                //can't divide
                break;
            }
            Term q = head.divide(divisorHead);
            if (q.containNegativePower()) {
                break;
            }
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

//    /**
//     * Parameters will not be modified.
//     * Throws an exception if it can't be done.
//     *
//     * @return quotient
//     */
//    private static NavigableSet<Term> handleComplexDivision(NavigableSet<Term> m, NavigableSet<Term> divisor) {
//        m = getSet(m);
//        divisor = getSet(divisor);
//
//        reduceGcd(m, divisor);
//
//        NavigableSet<Term> quotient = singleTerm(Term.ZERO); //the quotient
//        multinomialDivision(m, divisor, quotient);
//        if (m.size() > 1 || !m.first().isZero()) {
//            throw new UnsupportedCalculationException();
//        }
//        return quotient;
//    }


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
     * Determines whether this multinomial cont
     *
     * @return
     */
    public boolean containsNoChar() {
        for (Term x : terms) {
            if (!x.hasNoChar()) {
                return false;
            }
        }
        return true;
    }

    private static Set<String> AlgebraicChars = new HashSet<>();

    static {
        AlgebraicChars.add(Term.I_STR);
    }

    public boolean containsOnlyAlgebraicChar() {
        for (Term x : terms) {
            if (!AlgebraicChars.containsAll(x.getCharacterNameNoCopy())) {
                return false;
            }
        }
        return true;
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

    /**
     * Replaces the occurred terms in the multinomial.
     *
     * @param sub must be a term containing only characters.
     */
    public Multinomial replacePossible(Term sub, Multinomial expr) {
        if (!sub.numberPart().equals(Term.ONE)) {
            throw new IllegalArgumentException();
        }
        NavigableSet<Term> result = getSet();
        for (Term t1 : terms) {
            int power = getMaxPower(t1, sub);
            if (power == 0) {
                mergingAdd(result, t1);
                continue;
            }
            Term f = t1.divide(sub.pow(power));
            Multinomial temp = monomial(f);
            temp = temp.multiply(expr.pow(power));
            mergingAddAll(result, temp.terms);

        }

        return new Multinomial(result);

    }

    private static int getMaxPower(Term t, Term sub) {
        int maxPower = 0;
        for (var en : sub.character.entrySet()) {
            String ch = en.getKey();
            Fraction pow = en.getValue();
            Fraction p0 = t.getCharacterPower(ch);
            maxPower = Math.max(maxPower, p0.abs().divide(pow.abs()).toInt());
            if (maxPower == 0) {
                break;
            }
        }
        return maxPower;
    }

    public Multinomial replaceChar(UnaryOperator<String> replacer) {
        var nterms = getSet();
        for (Term t : terms) {
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
    public boolean isSymmetry() {
        // find all the characters in this multinomial

        var characters = getCharacters().toArray(new String[]{});
        int n = characters.length;
        for (Permutation p : Permutations.universeIterable(n)) {
            if (p.isIdentity()) {
                continue;
            }
            var m = replaceChar(x -> {
                int index = Arrays.binarySearch(characters, x);
                return characters[p.apply(index)];
            });
            if (!equals(m)) {
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
        for (int i = 0; i <= p.getLeadingPower(); i++) {
            Term t = p.getCoefficient(i);
            if (t.isZero()) {
                continue;
            }
            t = t.multiply(Term.characterPower(variableName, Fraction.of(i)));
            mergingAdd(set, t);
        }
        if (set.isEmpty()) {
            return ZERO;
        }
        return new Multinomial(set);
    }

    public static Multinomial fromPolynomialM(IPolynomial<Multinomial> p, String variableName) {
        NavigableSet<Term> set = getSet();
        for (int i = 0; i <= p.getLeadingPower(); i++) {
            Multinomial t = p.getCoefficient(i);
            if (t.isZero()) {
                continue;
            }
            var x = Term.characterPower(variableName, Fraction.of(i));
            var re = multiplyToSet(t.terms, x);
            mergingAddAll(set, re);
        }
        if (set.isEmpty()) {
            return ZERO;
        }
        return new Multinomial(set);
    }

    private static final MultinomialCalculator cal = new MultinomialCalculator();

    private static final Simplifier<Multinomial> sim = new MultinomialCalculator.MSimplifier();

    public static MultinomialCalculator getCalculator() {
        return cal;
    }

    public static Simplifier<Multinomial> getSimplifier() {
        return sim;
    }


    private static MathCalculator<RingFraction<Multinomial>> CAL_RF_M =
            RingFraction.getCalculator(getCalculator(), Multinomial.getSimplifier());

    private static Polynomial<RingFraction<Multinomial>> injectiveMap(Polynomial<Multinomial> p) {
        return p.mapTo(m -> RingFraction.valueOf(m, cal), CAL_RF_M);
    }

    private static void computeCharPowMap(Multinomial m, Map<String, Long> charMap) {
        for (var t : m.terms) {
            for (var en : t.getCharacterNoCopy().entrySet()) {
                var pow = en.getValue();
                if (pow.isInteger()) {
                    continue;
                }
                var deno = pow.getDenominator();
                var ch = en.getKey();
                if (charMap.containsKey(ch)) {
                    var value = charMap.get(ch);
                    var nValue = MathUtils.lcm(deno, value);
                    charMap.put(ch, nValue);
                } else {
                    charMap.put(ch, deno);
                }
            }
        }
    }

    private static Multinomial replaceCharPow(Multinomial m, Map<String, Long> charMap) {
        var terms = m.terms;
        var nTerms = getSet();
        for (var t : terms) {
            var map = t.getCharacter();
            for (var en : charMap.entrySet()) {
                var ch = en.getKey();
                if (!map.containsKey(ch)) {
                    continue;
                }
                var pow = map.get(ch);
                var n = en.getValue();
                pow = pow.multiply(n);
                map.put(ch, pow);
            }
            var nt = t.sameNumber0(map);
            nTerms.add(nt);
        }
        return new Multinomial(nTerms);
    }

    private static Multinomial restoreCharPow(Multinomial m, Map<String, Long> charMap) {
        var terms = m.terms;
        var nTerms = getSet();
        for (var t : terms) {
            var map = t.getCharacter();
            for (var en : charMap.entrySet()) {
                var ch = en.getKey();
                if (!map.containsKey(ch)) {
                    continue;
                }
                var pow = map.get(ch);
                var n = en.getValue();
                pow = pow.divide(n);
                map.put(ch, pow);
            }
            var nt = t.sameNumber0(map);
            nTerms.add(nt);
        }
        return new Multinomial(nTerms);
    }

    private static Pair<Multinomial, Multinomial> charPowToInt(Multinomial m1, Multinomial m2,
                                                               Map<String, Long> charMap) {
        computeCharPowMap(m1, charMap);
        computeCharPowMap(m2, charMap);
        var n1 = replaceCharPow(m1, charMap);
        var n2 = replaceCharPow(m1, charMap);
        return new Pair<>(n1, n2);
    }

    /**
     * Returns the greatest common divisor for two multinomials.
     *
     * @param m1
     * @param m2
     * @return
     */
    public static Multinomial gcd(Multinomial m1, Multinomial m2) {
        //first deal with special cases
        if (m1.isZero()) {
            return m2;
        }
        if (m2.isZero()) {
            return m1;
        }
        if (m1.isPolyInvertible() || m2.isPolyInvertible()) {
            return Multinomial.ONE;
        }
        if (!containsNonInteger(m1.terms) && !containsNonInteger(m2.terms)) {
            return gcd0(m1, m2);
        }
        var map = new HashMap<String, Long>();
        var p = charPowToInt(m1, m2, map);
        var gcd = gcd0(p.getFirst(), p.getSecond());
        return restoreCharPow(gcd, map);
    }

    private static Multinomial gcd0(Multinomial m1, Multinomial m2) {
        /*
        Created by liyicheng at 2020/2/27
        */
        // consider m1 and m2 as polynomial on fraction ring of multinomial
        var ch = m1.getCharacters().first();
        var p1 = Polynomial.fromMultinomial(m1, ch); // Polynomial<Multinomial>
        var p2 = Polynomial.fromMultinomial(m2, ch);

        var gcd = PolynomialSup.subResultantGCD(p1, p2);
        return fromPolynomialM(gcd, ch);
//        //extract the gcd of coefficient first
//        var c1 = p1.getNonZeroCoefficients();
//        var c2 = p2.getNonZeroCoefficients();
//        c1.addAll(c2);
//        var gcdCoe = Multinomial.gcd(c1);
//        p1 = p1.divide(gcdCoe);
//        p2 = p2.divide(gcdCoe);
//
//        //computes the gcd in fraction ring
//        var f1 = injectiveMap(p1);
//        var f2 = injectiveMap(p2);
//        var gcd = f1.gcd(f2); //Polynomial<RingFraction<Multinomial>>
//
//        var coes = gcd.getNonZeroCoefficients();
//        var pair = RingFraction.extractGcd(coes, cal);
//        var result = gcd.mapTo(rf ->
//                        rf.getNume().divide(pair.getFirst()).multiply(pair.getSecond().divide(rf.getDeno())),
//                cal);
//        var m = fromPolynomialM(result, ch);
//        return gcdCoe.multiply(m);
    }


    public static Multinomial gcd(List<Multinomial> ms) {
        var result = Multinomial.ZERO;
        for (var m : ms) {
            result = gcd(result, m);
        }
        return result;
    }


    public static Multinomial lcm(Multinomial m1, Multinomial m2) {
        if (m1.isZero() || m2.isZero()) {
            return Multinomial.ZERO;
        }
        return m1.divide(gcd(m1, m2)).multiply(m2);
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
                gcd = Multinomial.gcd(gcd, po);
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
     * respectively. If the second
     *
     * @param m1 a Multinomial
     * @param m2 another Multinomial
     * @return an array containing the Multinomials after simplification correspondingly
     */
    public static Pair<Multinomial, Multinomial> simplifyFraction(Multinomial m1, Multinomial m2) {
        if (m2.isMonomial()) {
            m1 = m1.divide(m2.getFirst());
            return new Pair<>(m1, Multinomial.ONE);
        }
        var set1 = getSet(m1.terms);
        var set2 = getSet(m2.terms);
        reduceGcd(set1, set2);
        m1 = new Multinomial(set1);
        m2 = new Multinomial(set2);
        var gcd = Multinomial.gcd(m1, m2);
        if (gcd.isMonomial() && gcd.getFirst().hasNoChar()) {
            return adjustSign(m1, m2);
        }
        m1 = m1.divide(gcd);
        m2 = m2.divide(gcd);
        if (m2.isMonomial()) {
            m1 = m1.divide(m2.getFirst());
            return new Pair<>(m1, Multinomial.ONE);
        }

        return adjustSign(m1, m2);
    }

    /**
     * Adjust the sign of the leading terms of the two multinomials to make them of the form:
     * (x,y) or (-x,y).
     */
    private static Pair<Multinomial, Multinomial> adjustSign(Multinomial m1, Multinomial m2) {
        var set1 = getSet(m1.terms);
        var set2 = getSet(m2.terms);
        reduceGcd(set1, set2);
        m1 = new Multinomial(set1);
        m2 = new Multinomial(set2);
        var s2 = m2.getFirst().signum;
        if (s2 < 0) {
            m1 = m1.negate();
            m2 = m2.negate();
        }
        return new Pair<>(m1, m2);

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
     *
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
     *
     * @param characters different characters
     */
    public static Multinomial[] primarySymmetryAll(String... characters) {
        Multinomial[] arr = new Multinomial[characters.length];
        for (int i = 0; i < characters.length; i++) {
            arr[i] = primarySymmetry(i + 1, characters);
        }
        return arr;
    }

    /**
     * Returns the newton multinomial of power <code>p</code> of the given characters.<p></p>
     * For example, <code>newtonMultinomial(2,"a","b","c")</code> returns <code>a^2+b^2+c^2</code>
     *
     * @param p          the power of each characters
     * @param characters different characters.
     * @return a new newton multinomial
     */
    public static Multinomial newtonMultinomial(int p, String... characters) {
        var terms = getSet();
        Fraction power = Fraction.of(p);
        for (String c : characters) {
            terms.add(Term.characterPower(c, power));
        }
        return new Multinomial(terms);
    }

    /**
     * Reduces a symmetry multinomial to a multinomial of primary symmetry multinomial.
     *
     * @see #primarySymmetryReduce(IntFunction)
     * @see #primarySymmetry(int, String...)
     */
    public Multinomial primarySymmetryReduce() {
        return primarySymmetryReduce(x ->
                MathSymbol.GREEK_SIGMA + Integer.toString(x + 1));
    }

    /**
     * Reduces a symmetry multinomial to a multinomial of primary symmetry multinomial whose names are
     * given by <code>symNameList</code>.
     *
     * @param symNameList a provider for primary symmetry multinomials, starting from zero.
     */
    public Multinomial primarySymmetryReduce(IntFunction<String> symNameList) {
        if (!isSymmetry()) {
            throw new IllegalArgumentException("m is not symmetry!");
        }
        for (Term t : terms) {
            for (var en : t.getCharacterNoCopy().entrySet()) {
                Fraction pow = en.getValue();
                if (pow.isNegative() || !pow.isInteger()) {
                    throw new ArithmeticException("Cannot reduce multinomial containing negative or fractional power: " + this);
                }
            }
        }
        var characters = getCharacters().toArray(new String[]{});
        Multinomial[] syms = primarySymmetryAll(characters);
        Multinomial m = this;
        var result = getSet();
        var top = m.getFirst();
        while (!top.isZero()) {
            Term coe = top.numberPart();
            Multinomial g = Multinomial.monomial(coe);
            NavigableMap<String, Fraction> map = new TreeMap<>();
            for (int i = 0; i < characters.length - 1; i++) {
                String c = characters[i];
                Fraction pow = top.getCharacterPower(c).subtract(top.getCharacterPower(characters[i + 1]));
                g = g.multiply(syms[i].pow(pow.toInt()));
                if (!pow.isZero()) {
                    map.put(symNameList.apply(i), pow);
                }
            }
            int last = characters.length - 1;
            String c = characters[last];
            Fraction pow = top.getCharacterPower(c);
            g = g.multiply(syms[last].pow(pow.toInt()));
            if (!pow.isZero()) {
                map.put(symNameList.apply(last), pow);
            }

            m = m.subtract(g);
            mergingAdd(result, coe.sameNumber0(map));
            top = m.getFirst();
        }
        return new Multinomial(result);

    }


    public static void main(String[] args) {
//        var nums = Arrays.asList(
//                BigInteger.valueOf(2),
//                BigInteger.valueOf(3),
//                BigInteger.valueOf(7)
//        );
//        var t = coprimeBaseReduction(new ArrayList<>(nums));
//        print(t);
//        var m = Multinomial.valueOf("1+Sqr2+Sqr3+Sqr6");
//        var conjs = m.conjugations();
//        var re = conjs.stream().reduce(Multinomial::multiply);
//        print(conjs);
//        print(re);
//        print(m.reciprocal());
//        var m1 = Multinomial.ONE;
//        var m2 = Multinomial.valueOf("Sqr2+i");
//        print(m2.conjugations());
//        print(Multinomial.simplifyFraction(m1, m2));
        var m1 = Multinomial.valueOf("a^2dk");
        var m2 = Multinomial.valueOf("a^2*k^2+b^2");
        print(gcd(m1, m2));
    }
//        var m1 = Multinomial.valueOf("xy+x");
//        var m2 = Multinomial.valueOf("y+1");
//        print(m1.compareTo(m2));
//        print(Multinomial.simplifyFraction(m2, m1));
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
//        cn.ancono.utilities.Timer t = new cn.ancono.utilities.Timer(TimeUnit.MILLISECONDS);
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
