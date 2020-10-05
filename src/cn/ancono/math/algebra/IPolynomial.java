/**
 * 2017-10-06
 */
package cn.ancono.math.algebra;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathCalculatorHolder;
import cn.ancono.math.algebra.linear.Vector;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.BiPredicate;


/**
 * A polynomial is an math expression of a variable, usually called {@code x}, with
 * defined operations <i>add</i> and <i>multiply</i>. Generally, a polynomial can be shown as
 * <pre>a<sub>n</sub>*x<sup>n</sup> + ... + a<sub>1</sub>*x + a<sub>0</sub>, (a<sub>n</sub>!=0,n>=0)</pre>
 * The operator <code>x<sup>n</sup></code> represents
 * for multiply {@code x} for {@code n} times. {@code n} is called the power of {@code x} and
 * <code>a<sub>n</sub></code> is called the coefficient.
 *
 * @author liyicheng
 * 2017-10-06 16:51
 */
public interface IPolynomial<T> extends Iterable<T> {
    /**
     * Gets the degree of this polynomial, which is the max power of x.
     * The degree of zero polynomial is <code>-1</code>.
     *
     * @return the degree of polynomial.
     */
    int getDegree();

    /**
     * Returns the power of the leading term. This method is equivalent to
     * <code>max(getDegree(),0)</code>
     *
     * @return
     */
    default int getLeadingPower() {
        return Math.max(getDegree(), 0);
    }

    /**
     * Gets the number of the corresponding index, throws {@link IndexOutOfBoundsException}
     * if {@code n<0}.
     *
     * @param n the power of the variable.
     * @return
     */
    T getCoefficient(int n);

    /**
     * Determines whether this polynomial is a constant (including zero).
     */
    default boolean isConstant() {
        return getDegree() <= 0;
    }

    /**
     * Iterators the coefficient from the lowest one(a0).
     */
    @NotNull
    @Override
    public default Iterator<T> iterator() {
        return new It<>(this);
    }


    /**
     * Returns the leading term of this polynomial.
     */
    default T first() {
        return getCoefficient(getLeadingPower());
    }

    /**
     * Returns the constant term of this polynomial.
     */
    default T constant() {
        return getCoefficient(0);
    }

    /**
     * Determines whether the two polynomial are equal.
     */
    public static <T, S> boolean isEqual(IPolynomial<T> m1, IPolynomial<S> m2, BiPredicate<T, S> equal) {
        if (m1.getDegree() != m2.getDegree()) {
            return false;
        }
        int mp = m1.getDegree();
        for (int i = 0; i <= mp; i++) {
            if (!equal.test(m1.getCoefficient(i), m2.getCoefficient(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines whether the two polynomial are equal, using the
     * equals() method in object.
     */
    public static boolean isEqual(IPolynomial<?> m1, IPolynomial<?> m2) {
        if (m1.getDegree() != m2.getDegree()) {
            return false;
        }
        int mp = m1.getDegree();
        for (int i = 0; i <= mp; i++) {
            if (!m1.getCoefficient(i).equals(m2.getCoefficient(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the basically implemented hash code.
     * <code>int hash = 0;
     * int mp = m.getMaxPower();
     * for(int i=0;i<=mp;i++) {
     * hash = hash *31 + m.getCoefficient(i).hashCode();
     * }
     * return hash;</code>
     *
     * @param m a Polynomial
     */
    public static int hashCodeOf(IPolynomial<?> m) {
        int hash = 0;
        int mp = m.getDegree();
        for (int i = 0; i <= mp; i++) {
            hash = hash * 31 + m.getCoefficient(i).hashCode();
        }
        return hash;
    }

    public static <T> String stringOf(IPolynomial<T> m, MathCalculator<T> mc, FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
        int maxPower = m.getDegree();
        if (maxPower <= 0) {
            return nf.format(m.getCoefficient(0), mc);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = maxPower; i > 0; i--) {
            if (mc.isZero(m.getCoefficient(i)))
                continue;
            T a = m.getCoefficient(i);
            if (mc.isEqual(mc.getOne(), a)) {
                if (i != 1) {
                    sb.append("x^").append(i);
                } else {
                    sb.append("x");
                }
            } else {
                sb.append(nf.format(m.getCoefficient(i), mc));
                if (i != 1) {
                    sb.append("*x^").append(i);
                } else {
                    sb.append("*x");
                }
            }
            sb.append(" + ");

        }
        if (!mc.isZero(m.getCoefficient(0))) {
            sb.append(nf.format(m.getCoefficient(0), mc));
        } else {
            sb.delete(sb.length() - 3, sb.length());

        }
        return sb.toString();
    }

    /**
     * Returns a vector whose n-th element is the coefficient of x^n.
     */
    static <T> Vector<T> coefficientVector(IPolynomial<T> fx, MathCalculator<T> mc) {
        int length = fx.getDegree() + 1;
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Object[length];
        for (int i = 0; i < length; i++) {
            arr[i] = fx.getCoefficient(i);
        }
        return Vector.of(mc, arr);
    }

    /**
     * Returns the last non-zero term in this polynomial,
     */
    static <T, S extends IPolynomial<T> & MathCalculatorHolder<T>> T last(S s) {
        var mc = s.getMathCalculator();
        int p = 0;
        T re;
        do {
            re = s.getCoefficient(p);
        } while (mc.isZero(re));
        return re;
    }

}

class It<T> implements ListIterator<T> {
    private final IPolynomial<T> f;
    private final int max;
    private int n;

    /**
     *
     */
    public It(IPolynomial<T> f) {
        this.f = f;
        this.max = f.getDegree();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return n <= max;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
        try {
            int index = n + 1;
            T t = f.getCoefficient(n);
            n = index;
            return t;
        } catch (IndexOutOfBoundsException ex) {
            throw new NoSuchElementException();
        }
    }

    /*
     * @see java.util.ListIterator#add(java.lang.Object)
     */
    @Override
    public void add(T arg0) {
        throw new UnsupportedOperationException();
    }

    /*
     * @see java.util.ListIterator#hasPrevious()
     */
    @Override
    public boolean hasPrevious() {
        return n > 0;
    }

    /*
     * @see java.util.ListIterator#nextIndex()
     */
    @Override
    public int nextIndex() {
        return n;
    }

    /*
     * @see java.util.ListIterator#previous()
     */
    @Override
    public T previous() {
        try {
            int index = n - 1;
            T t = f.getCoefficient(n);
            n = index;
            return t;
        } catch (IndexOutOfBoundsException ex) {
            throw new NoSuchElementException();
        }
    }

    /*
     * @see java.util.ListIterator#previousIndex()
     */
    @Override
    public int previousIndex() {
        return n - 1;
    }

    /*
     * @see java.util.ListIterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /*
     * @see java.util.ListIterator#set(java.lang.Object)
     */
    @Override
    public void set(T arg0) {
        throw new UnsupportedOperationException();
    }


}
