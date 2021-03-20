package cn.ancono.math.numberTheory.combination;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.numberModels.MathCalculatorAdapter;
import cn.ancono.math.numberTheory.combination.Permutation.Cycle;
import cn.ancono.math.numberTheory.combination.Permutation.Transposition;
import cn.ancono.math.set.FiniteSet;
import cn.ancono.math.set.MathSets;
import cn.ancono.utilities.ArraySup;
import cn.ancono.utilities.CollectionSup;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author liyicheng 2018-03-02 20:26
 */
public final class Permutations {

    /**
     *
     */
    private Permutations() {
    }

    static class ArrPermutation extends AbstractPermutation {
        protected final int[] parr;

        /**
         *
         */
        public ArrPermutation(int[] parr) {
            super(parr.length);
            this.parr = parr;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(int)
         */
        @Override
        public int apply(int x) {
            return parr[x];
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#inverse(int)
         */
        @Override
        public int inverse(int y) {
            for (int i = 0; i < size; i++) {
                if (parr[i] == y) {
                    return i;
                }
            }
            throw new AssertionError();
        }

        private ArrPermutation inverseTemp;

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#inverse()
         */
        @Override
        public Permutation inverse() {
            if (inverseTemp == null) {
                int[] narr = new int[size];
                for (int i = 0; i < size; i++) {
                    narr[parr[i]] = i;
                }
                inverseTemp = new ArrPermutation(narr);
            }

            return inverseTemp;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#getArray()
         */
        @Override
        public int[] getArray() {
            return parr.clone();
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(java.lang.Object[])
         */
        @Override
        public <T> T[] apply(T[] array) {
            if (array.length < size) {
                throw new IllegalArgumentException("array's length!=" + size);
            }
            T[] copy = array.clone();
            for (int i = 0; i < size; i++) {
                array[i] = copy[parr[i]];
            }
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(int[])
         */
        @NotNull
        @Override
        public int[] apply(int[] array) {
            if (array.length < size) {
                throw new IllegalArgumentException("array's length!=" + size);
            }
            int[] copy = array.clone();
            for (int i = 0; i < size; i++) {
                array[i] = copy[parr[i]];
            }
            return array;
        }

        /*
         * @see
         * cn.ancono.math.numberTheory.combination.AbstractPermutation#compose(cn.timelives.
         * java.math.combination.Permutation)
         */
        @NotNull
        @Override
        public Permutation compose(@NotNull Permutation before) {
            return new ArrPermutation(apply(before.getArray()));
        }

        /*
         * @see
         * cn.ancono.math.numberTheory.combination.AbstractPermutation#andThen(cn.timelives.
         * java.math.combination.Permutation)
         */
        @NotNull
        @Override
        public Permutation andThen(@NotNull Permutation after) {
            return new ArrPermutation(after.apply(getArray()));
        }
    }

    static class Swap extends AbstractPermutation implements Transposition {
        protected final int i, j;

        /**
         * Swap {@code i} and {@code j} if {@code i>j}
         *
         * @param size
         */
        Swap(int size, int i, int j) {
            super(size);
            if (i > j) {
                int t = i;
                i = j;
                j = t;
            }
            this.i = i;
            this.j = j;
        }

        /*
         * @see
         * cn.ancono.math.numberTheory.combination.Permutation.ElementaryPermutation#getFirst
         * ()
         */
        @Override
        public int getFirst() {
            return i;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation.ElementaryPermutation#
         * getSecond()
         */
        @Override
        public int getSecond() {
            return j;
        }


        /*
         * @see cn.ancono.math.numberTheory.combination.AbstractPermutation#toString()
         */
        @Override
        public String toString() {
            return "(" + i + "," + j + ")";
        }
    }

    static class Identity extends AbstractPermutation implements Transposition {

        /**
         * @param size
         */
        public Identity(int size) {
            super(size);
        }

        /*
         * @see
         * cn.ancono.math.numberTheory.combination.Permutation.ElementaryPermutation#getFirst
         * ()
         */
        @Override
        public int getFirst() {
            return 0;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation.ElementaryPermutation#
         * getSecond()
         */
        @Override
        public int getSecond() {
            return 0;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(int)
         */
        @Override
        public int apply(int x) {
            return x;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#inverse(int)
         */
        @Override
        public int inverse(int y) {
            return y;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#inverse()
         */
        @Override
        public Transposition inverse() {
            return this;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation.ElementaryPermutation#containsElement(int)
         */
        @Override
        public boolean containsElement(int x) {
            return x == 0;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation.ElementaryPermutation#getElements()
         */
        @Override
        public int[] getElements() {
            return new int[]{0};
        }


        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(boolean[])
         */
        @Override
        public boolean[] apply(boolean[] array) {
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(double[])
         */
        @Override
        public double[] apply(double[] array) {
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(int[])
         */
        @NotNull
        @Override
        public int[] apply(int[] array) {
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(long[])
         */
        @Override
        public long[] apply(long[] array) {
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(java.lang.Object[])
         */
        @Override
        public <T> T[] apply(T[] array) {
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#getArray()
         */
        @Override
        public int[] getArray() {
            return ArraySup.indexArray(size);
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#index()
         */
        @Override
        public long index() {
            return 0;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation.ElementaryPermutation#length()
         */
        @Override
        public int length() {
            return 1;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#rank()
         */
        @Override
        public int rank() {
            return 1;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#inverseCount()
         */
        @Override
        public int inverseCount() {
            return 0;
        }


        /*
         * @see
         * cn.ancono.math.numberTheory.combination.AbstractPermutation#andThen(cn.timelives.
         * java.math.combination.Permutation)
         */
        @NotNull
        @Override
        public Permutation andThen(@NotNull Permutation after) {
            return after;
        }

        /*
         * @see
         * cn.ancono.math.numberTheory.combination.AbstractPermutation#compose(cn.timelives.
         * java.math.combination.Permutation)
         */
        @NotNull
        @Override
        public Permutation compose(@NotNull Permutation before) {
            return before;
        }

    }

    static class RotateAll extends AbstractPermutation {
        private final int shift;

        /**
         * @param size
         */
        public RotateAll(int size, int shift) {
            super(size);
            this.shift = shift;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(int)
         */
        @Override
        public int apply(int x) {
            x += shift;
            if (x < 0) {
                x += size;
            } else if (x >= size) {
                x -= size;
            }
            return x;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#inverse()
         */
        @Override
        public Permutation inverse() {
            return new RotateAll(size, -shift);
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#inverse(int)
         */
        @Override
        public int inverse(int y) {
            y -= shift;
            if (y < 0) {
                y += size;
            } else if (y >= size) {
                y -= size;
            }
            return y;
        }

    }

    static class Rotate extends AbstractPermutation implements Cycle {
        private final int[] elements;

        /**
         * Elements:
         *
         * @param size
         */
        public Rotate(int size, int[] elements) {
            super(size);
            this.elements = elements;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#inverse(int)
         */
        @Override
        public int apply(int y) {
            if (!containsElement(y)) {
                return y;
            }
            if (elements.length == 1) {
                return y;
            }
            int[] earr = this.elements;
            int index = ArraySup.firstIndexOf(y, earr);
            index++;
            if (index >= earr.length) {
                index -= earr.length;
            }
            return earr[index];
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#inverse()
         */
        @Override
        public Rotate inverse() {
            return new Rotate(size, ArraySup.flip(elements, 0, elements.length));
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation.RotationPermutation#getElements()
         */
        @Override
        public int[] getElements() {
            return elements.clone();
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation.RotationPermutation#containsElement(int)
         */
        @Override
        public boolean containsElement(int x) {
            for (int element : elements) {
                if (element == x) {
                    return true;
                }
            }
            return false;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation.RotationPermutation#length()
         */
        @Override
        public int length() {
            return elements.length;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(int)
         */
        @Override
        public int inverse(int x) {
            if (!containsElement(x)) {
                return x;
            }
            if (elements.length == 1) {
                return x;
            }
            int[] earr = this.elements;
            int index = ArraySup.firstIndexOf(x, earr);
            index--;
            if (index < 0) {
                index += earr.length;
            }
            return earr[index];
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(int[])
         */
        @NotNull
        @Override
        public int[] apply(int[] array) {
            if (elements.length == 1) {
                return array;
            }
            int[] earr = this.elements;
            int t = array[earr[0]];
            for (int i = 0; i < earr.length - 1; i++) {
                array[earr[i]] = array[earr[i + 1]];
            }
            array[earr[earr.length - 1]] = t;
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(boolean[])
         */
        @Override
        public boolean[] apply(boolean[] array) {
            if (elements.length == 1) {
                return array;
            }
            int[] earr = this.elements;
            boolean t = array[earr[0]];
            for (int i = 0; i < earr.length - 1; i++) {
                array[earr[i]] = array[earr[i + 1]];
            }
            array[earr[earr.length - 1]] = t;
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(double[])
         */
        @Override
        public double[] apply(double[] array) {
            if (elements.length == 1) {
                return array;
            }
            int[] earr = this.elements;
            double t = array[earr[0]];
            for (int i = 0; i < earr.length - 1; i++) {
                array[earr[i]] = array[earr[i + 1]];
            }
            array[earr[earr.length - 1]] = t;
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(long[])
         */
        @Override
        public long[] apply(long[] array) {
            if (elements.length == 1) {
                return array;
            }
            int[] earr = this.elements;
            long t = array[earr[0]];
            for (int i = 0; i < earr.length - 1; i++) {
                array[earr[i]] = array[earr[i + 1]];
            }
            array[earr[earr.length - 1]] = t;
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.Permutation#apply(java.lang.Object[])
         */
        @Override
        public <T> T[] apply(T[] array) {
            if (elements.length == 1) {
                return array;
            }
            int[] earr = this.elements;
            T t = array[earr[0]];
            for (int i = 0; i < earr.length - 1; i++) {
                array[earr[i]] = array[earr[i + 1]];
            }
            array[earr[earr.length - 1]] = t;
            return array;
        }

        /*
         * @see cn.ancono.math.numberTheory.combination.AbstractPermutation#toString()
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('(');
            for (int i : elements) {
                sb.append(i).append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(')');
            return sb.toString();
        }

    }

    static void sizeCheck(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Invalid size=" + size);
        }
    }

    static void rangeAndDuplicateCheck(int[] array, int ubound) {
        boolean[] marks = new boolean[ubound];
        for (int j : array) {
            if (j < 0 || j >= ubound) {
                throw new IllegalArgumentException("Invalid index=" + j);
            }
            if (marks[j]) {
                throw new IllegalArgumentException("Duplicate index=" + j);
            }
            marks[j] = true;
        }
    }

    /**
     * Gets a permutation of the specific array as the method getArray() in
     * Permutation.
     */
    public static Permutation valueOf(int... array) {
        sizeCheck(array.length);
        rangeAndDuplicateCheck(array, array.length);
        return new ArrPermutation(array);
    }

    /**
     * Returns the permutation of swapping the i-th element and the j-th element.
     *
     * @param size the size(length) of the permutation
     */
    public static Transposition swap(int size, int i, int j) {
        sizeCheck(size);
        if (i < 0 || j < 0 || i >= size || j >= size) {
            throw new IllegalArgumentException("Invalid index i=" + i + ",j=" + j);
        }
        return new Swap(size, i, j);
    }

    public static Permutation identity(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Invalid size=" + size);
        }
        return new Identity(size);
    }

    /**
     * Returns a new permutation that reverse the order of the array, which can be
     * written as {@code (n-1,n-2,...2,1,0)}
     *
     * @param n
     * @return
     */
    public static Permutation flipAll(int n) {
        sizeCheck(n);
        return new AbstractPermutation(n) {
            @Override
            public Permutation inverse() {
                return this;
            }

            @Override
            public int inverse(int y) {
                return size - y - 1;
            }

            @Override
            public int apply(int x) {
                return size - x - 1;
            }
        };
    }

    /**
     * Returns a new permutation that reverse the order of the array, which can be
     * written as {@code (n-shift,n-shift-1,...n-1,0,1,2,...n-shift-n)}. For
     * example, {@code rotate(5,2)=(3,4,0,1,2)}
     *
     * @param n the size of the permutation
     */
    public static Permutation rotateAll(int n, int shift) {
        sizeCheck(n);
        shift = shift % n;
        if (shift == 0) {
            return new Identity(n);
        }
        return new RotateAll(n, shift);
    }

    /**
     * A left-version of {@link #rotateAll(int, int)}
     */
    public static Permutation rotateAllLeft(int n, int shift) {
        return rotateAll(n, n - shift);
    }

    public static Cycle rotate(int size, int... elements) {
        sizeCheck(size);
        rangeAndDuplicateCheck(elements, size);
        return new Rotate(size, elements);
    }

    /**
     * Returns the flip permutation. For example,
     * {@code flipRange(5,2,5) = (0,1,4,3,2)}
     *
     * @param size
     * @param i    inclusive
     * @param j    exclusive
     * @return
     */
    public static Permutation flipRange(int size, int i, int j) {
        sizeCheck(size);
        if (i < 0 || j < 0 || i >= size || j >= size) {
            throw new IllegalArgumentException("Invalid index i=" + i + ",j=" + j);
        }
        int[] arr = ArraySup.indexArray(size);
        ArraySup.flip(arr, i, j);
        return new ArrPermutation(arr);
    }

    /**
     * Parse the Permutation from the given non-negative index and the given size.
     *
     * @param index
     * @return
     */
    public static Permutation fromIndex(long index, int size) {
        if (index < 0) {
            throw new IllegalArgumentException("Negative index=" + index);
        }
        sizeCheck(size);
        if (index >= CombUtils.factorial(size)) {
            throw new IllegalArgumentException("Invalid index=" + index + " for size=" + size);
        }
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            long f = CombUtils.factorial(size - i - 1);
            int t = 0;
            while (index >= f) {
                index -= f;
                t++;
            }
            arr[i] = t;
        }
        for (int i = size - 2; i >= 0; i--) {
            int t = arr[i];
            for (int j = i + 1; j < size; j++) {
                if (arr[j] >= t) {
                    arr[j]++;
                }
            }
        }
        return new ArrPermutation(arr);
    }

    /**
     * Determines whether the two permutation is equal.
     *
     * @param p1 a permutation
     * @param p2 another permutation
     * @return {@true} if they are equal
     */
    public static boolean isEqual(Permutation p1, Permutation p2) {
        if (p1.size() != p2.size()) {
            return false;
        }
        int size = p1.size();
        for (int i = 0; i < size; i++) {
            if (p1.apply(i) != p2.apply(i)) {
                return false;
            }
        }
        return true;
    }

    private static final MathCalculator<Permutation> mc = new MathCalculatorAdapter<Permutation>() {
        /*
         * @see cn.ancono.math.numberTheory.combination.Permutations#isEqual(cn.ancono.math.numberTheory.combination.Permutation, cn.ancono.math.numberTheory.combination.Permutation)
         */
        public boolean isEqual(Permutation p1, Permutation p2) {
            return Permutations.isEqual(p1, p2);
        }
    };

    public static MathCalculator<Permutation> getMathCalculator() {
        return mc;
    }


    /**
     * Returns a set of permutations that contains all the n-size permutations.
     * This method only supports n smaller than 13.
     *
     * @param n
     * @return
     */
    public static FiniteSet<Permutation> universe(int n) {
        if (n <= 0 || n > 12) {
            throw new IllegalArgumentException("Invalid n=" + n);
        }
        Permutation[] list = new Permutation[(int) CombUtils.factorial(n)];
        int i = 0;
        for (int[] arr : Enumer.permutation(n, n)) {
            list[i++] = new ArrPermutation(arr);
        }
        return MathSets.asSet(mc, list);
    }

    public static Iterable<Permutation> universeIterable(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Invalid n=" + n);
        }
        var en = Enumer.permutation(n, n);
        return CollectionSup.mappedIterable(en, Permutations::valueOf);
    }


    public static FiniteSet<Permutation> even(int n) {
        if (n <= 0 || n > 12) {
            throw new IllegalArgumentException("Invalid n=" + n);
        }
        Permutation[] list = new Permutation[(int) CombUtils.factorial(n) / 2];
        int i = 0;
        for (int[] arr : Enumer.permutation(n, n)) {
            Permutation p = new ArrPermutation(arr);
            if (p.isEven()) {
                list[i++] = p;
            }
        }
        return MathSets.asSet(mc, list);
    }

    /**
     * Composes all the permutations in the list, applying the last element in the list first.
     *
     * @param list a list of permutations, not empty
     * @return the result as a permutation
     */
    public static Permutation composeAll(List<? extends Permutation> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException();
        }
        var lit = list.listIterator(list.size());
        Permutation p = lit.previous();
        while (lit.hasPrevious()) {
            p = p.andThen(lit.previous());
        }
        return p;
    }

    /**
     * Composes all the permutations in the list, applying the last element in the list first.
     * If the list is empty, returns the identity permutation.
     *
     * @param list a list of permutations
     * @param size the size of the permutations
     * @return the result as a permutation
     */
    public static Permutation composeAll(List<? extends Permutation> list, int size) {
        if (list.isEmpty()) {
            return Permutations.identity(size);
        }
        return composeAll(list);
    }

//    public static void main(String[] args) {
//        var p1 = valueOf(new int[]{1,2,0});
//        var p2 = valueOf(new int[]{0,1,2});
//        print(p1.compose(p2));
//        print(p2.compose(p1));
//        print(p1.apply(p2.getArray()));
//    }
}
