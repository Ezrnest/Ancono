package cn.timelives.java.utilities;

import cn.timelives.java.math.MathUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

/**
 * Some useful abstract models or patterns that is used in programming.
 *
 * @author liyicheng
 */
public final class ModelPatterns {

    /**
     *
     */
    private ModelPatterns() {
    }

    /**
     * Operates a binary search. This method is a long version of {@link #binarySearch(int, int, IntUnaryOperator)}
     *
     * @param fromIndex  the lower bound, inclusive
     * @param toIndex    the upper bound, exclusive
     * @param comparator a comparator
     * @return the index of the key or (-(insertion point) - 1).
     * @see #binarySearch(int, int, IntUnaryOperator)
     */
    public static long binarySearchL(long fromIndex, long toIndex, LongToIntFunction comparator) {
        if (fromIndex < 0 || toIndex < 0 || fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
        // the code copied from Arrays.binarySearch
        long low = fromIndex;
        long high = toIndex - 1;

        while (low <= high) {
            long mid = (low + high) >>> 1;
            int cmp = comparator.applyAsInt(mid);
            //noinspection Duplicates
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.

    }

    /**
     * Operates a binary search. For example, a binary search
     * for a sorted array can be done as follow:
     * <pre>
     * 	final int key = ... ;
     * 	int index = binarySearch(0,arr.length, x-> arr[x] < key ? -1 : arr[x] == key ? 0 : -1 );
     *
     * </pre>
     * Note that this method doesn't supports negative values for {@code fromIndex} or {@code toIndex},
     * and {@code fromIndex} should not be bigger than {@code toIndex}
     *
     * @param fromIndex  the lower bound, inclusive
     * @param toIndex    the upper bound, exclusive
     * @param comparator a comparator that determines whether the key is "in front of" the given index
     *                   (return -1), is at the index(return 0) or "behind" it (return 1), which is equal to
     *                   {@code arr[i].compareTo(key)}
     * @return index of the search key,
     * if it is contained in the array within the specified range;
     * otherwise, (-(insertion point) - 1).
     * The insertion point is defined as the point at which the key would be inserted into the "array":
     * the index of the first element in the range greater than the key,
     * or toIndex if all elements in the range are less than the specified key.
     * Note that this guarantees that the return value will be >= 0 if and only if the key is found.
     */
    public static int binarySearch(int fromIndex, int toIndex, IntUnaryOperator comparator) {
        if (fromIndex < 0 || toIndex < 0 || fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
        // the code copied from Arrays.binarySearch
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = comparator.applyAsInt(mid);
            //noinspection Duplicates
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    /**
     * Solve a 'problem' with binary search method. For example, to find a function's zero point,
     * assuming the function is {@code f(x)} and the range to search is {@code [0,1]}, then
     * {@code binarySolve(0d,1d,(a,b)->(a+b)/2,x->signum(f(x)),100)} will try to find the zero point and iterate for 100
     * times.
     *
     * @param low        the initial downer bound
     * @param high       the initial upper bound
     * @param middle     an operator to computes the middle value of low and high
     * @param comparator to determine how the current result deviates from the desired result
     * @param maxTime    the max times to iterate
     */
    public static <T> T binarySolve(T low, T high, BinaryOperator<T> middle, ToIntFunction<T> comparator, int maxTime) {
        T mid = middle.apply(low, high);
        int cl = comparator.applyAsInt(low);
        int ch = comparator.applyAsInt(high);
        if (cl == 0) {
            return low;
        }
        if (ch == 0) {
            return high;
        }
        if (MathUtils.sameSignum(cl, ch)) {
            throw new IllegalArgumentException("Sign numbers are the identity!");
        }

        boolean downerNegative = cl < 0;
        for (int i = 0; i < maxTime; i++) {
            int t = comparator.applyAsInt(mid);
            if (t == 0) {
                return mid;
            }
            if (downerNegative ^ (t < 0)) {
                high = mid;
            } else {
                low = mid;
            }
            mid = middle.apply(low, high);
        }
        return mid;

    }

    /**
     * Solve a 'problem' with binary search method. For example, to find a function's zero point,
     * assuming the function is {@code f(x)} and the range to search is {@code [0,1]}, then
     * {@code binarySolve(0d,1d,(a,b)->(a+b)/2,x->signum(f(x)),100)} will try to find the zero point and iterate for 100
     * times.
     *
     * @param low        the initial downer bound
     * @param high       the initial upper bound
     * @param middle     an operator to computes the middle value of low and high
     * @param comparator to determine how the current result deviates from the desired result
     * @param next       accepts the current lower bound and the higher bound to determine whether to iterate further
     */
    public static <T> T binarySolve(T low, T high, BinaryOperator<T> middle, ToIntFunction<T> comparator,
                                    BiPredicate<T, T> next) {
        T mid = middle.apply(low, high);
        int cl = comparator.applyAsInt(low);
        int ch = comparator.applyAsInt(high);
        if (cl == 0) {
            return low;
        }
        if (ch == 0) {
            return high;
        }
        if (MathUtils.sameSignum(cl, ch)) {
            throw new IllegalArgumentException("Sign numbers are the identity!");
        }

        boolean downerNegative = cl < 0;
        while (next.test(low, high)) {
            int t = comparator.applyAsInt(mid);
            if (t == 0) {
                return mid;
            }
            if (downerNegative ^ (t < 0)) {
                high = mid;
            } else {
                low = mid;
            }
            mid = middle.apply(low, high);
        }
        return mid;

    }

    /**
     * Performs an operation like computing {@code exp(x,p)}.
     *
     * @param p        a non-negative number
     * @param unit     the unit value, such as 1.
     * @param x        the base of the operation
     * @param square   computes the 'square of x' formally
     * @param multiply computes the 'multiplication' formally
     */
    public static <T> T binaryProduce(long p, T unit, T x, Function<T, T> square, BinaryOperator<T> multiply) {
        if (p < 0) {
            throw new IllegalArgumentException("p<0");
        } else if (p == 0) {
            return unit;
        }
        T re = unit;
        while (p > 0) {
            if ((p & 1) != 0) {
                re = multiply.apply(x, re);
            }
            x = square.apply(x);
            p >>= 1;
        }
        return re;
    }

    /**
     * Performs an operation like computing {@code exp(x,p)}.
     *
     * @param p        a non-negative number
     * @param unit     the unit value, such as 1.
     * @param x        the base of the operation
     * @param multiply computes the 'multiplication' formally
     */
    public static <T> T binaryProduce(long p, T unit, T x, BinaryOperator<T> multiply) {
        if (p < 0) {
            throw new IllegalArgumentException("p<0");
        } else if (p == 0) {
            return unit;
        }
        T re = unit;
        while (p > 0) {
            if ((p & 1) != 0) {
                re = multiply.apply(x, re);
            }
            x = multiply.apply(x, x);
            p >>= 1;
        }
        return re;
    }

    /**
     * Performs an operation like computing {@code exp(x,p)}.
     *
     * @param p        a positive number
     * @param x        the base of the operation
     * @param multiply computes the 'multiplication' formally
     */
    public static <T> T binaryProduce(long p, T x, BinaryOperator<T> multiply) {
        if (p <= 0) {
            throw new IllegalArgumentException("p<=0");
        }
        return binaryProduce(p - 1, x, x, multiply);
    }

    private static void checkStartSmallerThanEnd(int startInclusive, int endExclusive) {
        if (startInclusive >= endExclusive) {
            throw new IllegalArgumentException("startInclusive>=endExclusive");
        }
    }

//    private static void checkStartSmallerThanOrEqualToEnd(int startInclusive, int endExclusive) {
//        if (startInclusive > endExclusive) {
//            throw new IllegalArgumentException("startInclusive>endExclusive");
//        }
//    }

    /**
     * Performs a binary reducing operation. It is required that <code>startInclusive < endExclusive</code>.
     * This method will divide the task to halves and compute recursively.
     * For example, assuming there is a list of int named <code>list</code>, then
     * <code>binaryReduce(0,list.size(),list::get,(a,b)->a+b)</code> computes the sum of elements in this list.
     *
     * @param startInclusive an integer, must be smaller than <code>endExclusive</code>
     * @param endExclusive   an integer, must be bigger than <code>startInclusive</code>
     * @param get            a function to get the value to reduce
     * @param operation      a binary operation to reduce two values to one
     */
    public static <T> T binaryReduce(int startInclusive, int endExclusive, IntFunction<T> get, BinaryOperator<T> operation) {
        checkStartSmallerThanEnd(startInclusive, endExclusive);
        return binaryReduce0(startInclusive, endExclusive, get, operation, null);
    }

    /**
     * Performs a binary reducing operation. It is required that <code>startInclusive <= endExclusive</code>.
     * This method will divide the task to halves and compute recursively. If
     * <code>startInclusive >= endExclusive</code>, then <code>identity.get()</code> will be
     * returned.
     * For example, assuming there is a list of int named <code>list</code>, then
     * <code>binaryReduce(0,list.size(),list::get,(a,b)->a+b,()->0)</code> computes the sum of elements in this list.
     *
     * @param startInclusive an integer, must be smaller than or equal to <code>endExclusive</code>
     * @param endExclusive   an integer, must be bigger than or equal to <code>startInclusive</code>
     * @param get            a function to get the value to reduce
     * @param operation      a binary operation to reduce two values to one
     * @param identity       supplies identity element
     */
    public static <T> T binaryReduceWithIdentity(int startInclusive, int endExclusive, IntFunction<T> get,
                                                 BinaryOperator<T> operation, Supplier<T> identity) {
        return binaryReduce0(startInclusive, endExclusive, get, operation, identity);
    }

    private static <T> T binaryReduce0(int startInclusive, int endExclusive, IntFunction<T> get, BinaryOperator<T> operation, Supplier<T> identity) {
        if (startInclusive == endExclusive) {
            return identity.get();
        }
        if (startInclusive == endExclusive - 1) {
            return get.apply(startInclusive);
        }
        if (startInclusive == endExclusive - 2) {
            return operation.apply(get.apply(startInclusive), get.apply(startInclusive + 1));
        }
        int mid = (startInclusive + endExclusive) / 2;
        return operation.apply(binaryReduce0(startInclusive, mid, get, operation, identity), binaryReduce0(mid, endExclusive, get, operation, identity));
    }


    /**
     * Performs reducing operation from the left(start). It is required that <code>startInclusive < endExclusive</code>.
     *
     * @param startInclusive an integer, must be smaller than <code>endExclusive</code>
     * @param endExclusive   an integer, must be bigger than <code>startInclusive</code>
     * @param get            a function to get the value to reduce
     * @param operator       a binary operation to reduce two values to one
     */
    public static <T> T reduceLeft(int startInclusive, int endExclusive, IntFunction<T> get, BinaryOperator<T> operator) {
        checkStartSmallerThanEnd(startInclusive, endExclusive);
        T re = get.apply(startInclusive);
        for (int i = startInclusive + 1; i < endExclusive; i++) {
            re = operator.apply(re, get.apply(i));
        }
        return re;
    }

    /**
     * Performs reducing operation from the left(start) with an identity element provided.
     * Returns the identity if <code>startInclusive >= endExclusive</code>.
     *
     * @param startInclusive an integer
     * @param endExclusive   an integer
     * @param get            a function to get the value to reduce
     * @param operator       a binary operation to reduce two values to one
     */
    public static <T> T reduceLeftWithIdentity(int startInclusive, int endExclusive, IntFunction<T> get,
                                               BinaryOperator<T> operator, T identity) {
        T re = identity;
        for (int i = startInclusive; i < endExclusive; i++) {
            re = operator.apply(re, get.apply(i));
        }
        return re;
    }

    /**
     * Performs reducing operation from the right (end).
     * It is required that <code>startInclusive < endExclusive</code>.
     *
     * @param startInclusive an integer, must be smaller than <code>endExclusive</code>
     * @param endExclusive   an integer, must be bigger than <code>startInclusive</code>
     * @param get            a function to get the value to reduce
     * @param operator       a binary operation to reduce two values to one
     */
    public static <T> T reduceRight(int startInclusive, int endExclusive, IntFunction<T> get, BinaryOperator<T> operator) {
        checkStartSmallerThanEnd(startInclusive, endExclusive);
        T re = get.apply(endExclusive - 1);
        for (int i = endExclusive - 2; i >= startInclusive; i--) {
            re = operator.apply(get.apply(i), re);
        }
        return re;
    }

    /**
     * Performs reducing operation from the right (end) with an identity element provided.
     * Returns the identity if <code>startInclusive >= endExclusive</code>.
     *
     * @param startInclusive an integer
     * @param endExclusive   an integer
     * @param get            a function to get the value to reduce
     * @param operator       a binary operation to reduce two values to one
     */
    public static <T> T reduceRightWithIdentity(int startInclusive, int endExclusive, IntFunction<T> get,
                                                BinaryOperator<T> operator, T identity) {
        T re = identity;
        for (int i = endExclusive - 1; i >= startInclusive; i--) {
            re = operator.apply(get.apply(i), re);
        }
        return re;
    }

    /**
     * Performs folding operation from the left(start).
     * It is required that <code>startInclusive < endExclusive</code>.
     *
     * @param initial   initial value of <code>R</code> to accumulate.
     * @param get       a function to get the value to fold
     * @param operation a binary function to fold
     */
    public static <T, R> R foldLeft(int startInclusive, int endExclusive, R initial, IntFunction<T> get,
                                    BiFunction<R, T, R> operation) {
        R re = initial;
        for (int i = startInclusive; i < endExclusive; i++) {
            re = operation.apply(re, get.apply(i));
        }
        return re;
    }

    /**
     * Performs folding operation from the right(end).
     * It is required that <code>startInclusive < endExclusive</code>.
     *
     * @param initial   initial value of <code>R</code> to accumulate.
     * @param get       a function to get the value to fold
     * @param operation a binary function to fold
     */
    public static <T, R> R foldRight(int startInclusive, int endExclusive, R initial, IntFunction<T> get,
                                     BiFunction<T, R, R> operation) {
        R re = initial;
        for (int i = endExclusive - 1; i >= startInclusive; i--) {
            re = operation.apply(get.apply(i), re);
        }
        return re;
    }

    /**
     * Performs reduction operation on the given range of objects using dynamic programming to
     * minimize the time cost. This method computes the time cost via 'model', which is defined by the user and
     * given by <code>toModel</code>. The user should also provide the function <code>modelOperation</code> to
     * reduce on the model and the function <code>modelTimeCost</code> to tell the time cost of a model.
     *
     * @param startInclusive an integer
     * @param endExclusive   an integer
     * @param get            a function to get the value to reduce
     * @param operation      a binary operation to reduce two values to one, must be associative
     * @param toModel        a function to covert a real object to an abstract model for computing time cost
     * @param modelTimeCost  a function to compute the time cost of a model
     */
    public static <T, R> T reduceDP(int startInclusive, int endExclusive, IntFunction<T> get,
                                    BinaryOperator<T> operation,
                                    Function<T, R> toModel,
                                    BinaryOperator<R> modelOperation,
                                    ToIntBiFunction<R, R> modelTimeCost) {
        //dynamic programming
        int size = endExclusive - startInclusive;
        if (size <= 0) {
            throw new IllegalArgumentException("startInclusive>=endExclusive");
        }
        if (size == 1) {
            return get.apply(startInclusive);
        } else if (size == 2) {
            return operation.apply(get.apply(startInclusive), get.apply(startInclusive + 1));
        }
        @SuppressWarnings("unchecked")
        R[][] models = (R[][]) new Object[size][size];
        for (int i = 0; i < size; i++) {
            models[i][i] = toModel.apply(get.apply(i));
        }
        int[][] partitions = computeTimeCost(size, models, modelOperation, modelTimeCost);
        return recurReduce(partitions, 0, size - 1, get, operation);
    }

    /**
     * Computes the time cost and returns an array contains the best partition.
     */
    private static <R> int[][] computeTimeCost(int size, R[][] models,
                                               BinaryOperator<R> modelOp, ToIntBiFunction<R, R> timeCost) {

        int[][] partitions = new int[size][size];
        int[][] costs = new int[size][size];
        //costs : cost[x][y] = min cost of get(x)get(x+1)...get(y)
        //partitions : partitions[x][y] = split = split point of get(x)get(x+1)...get(y) ->
        // (get(x)get(x+1)...get(x+split))(get(x+split+1)...get(y))

        for (int d = 1; d < size; d++) {
            for (int i = 0; i < size - d; i++) {
                int j = i + d;
                //find the cost
                int minCost = Integer.MAX_VALUE;
                int minSplit = 0;
                R minModel = null;
                for (int r = 0; r < d; r++) {
                    R modelLeft = models[i][i + r];
                    R modelRight = models[i + r + 1][j];
                    int cost = timeCost.applyAsInt(modelLeft, modelRight) + costs[i][i + r] + costs[i + r + 1][j];
                    if (minModel == null || cost < minCost) {
                        R combined = modelOp.apply(modelLeft, modelRight);
                        minCost = cost;
                        minSplit = r;
                        minModel = combined;
                    }
                }
                partitions[i][j] = minSplit;
                models[i][j] = minModel;
                costs[i][j] = minCost;
            }
        }
//        Printer.printMatrix(costs);
//        Printer.printMatrix(partitions);
//        Printer.printMatrix(ArraySup.mapTo2(models, x -> Arrays.toString((int[])x),String.class));
        return partitions;
    }

    private static <T> T recurReduce(int[][] partitions,
                                     int startInclusive,
                                     int endInclusive,
                                     IntFunction<T> get,
                                     BinaryOperator<T> combine) {
        if (startInclusive == endInclusive) {
            return get.apply(startInclusive);
        }
        if (startInclusive == endInclusive - 1) {
            return combine.apply(get.apply(startInclusive), get.apply(endInclusive));
        }
        int split = partitions[startInclusive][endInclusive];
        T left = recurReduce(partitions, startInclusive, startInclusive + split, get, combine);
        T right = recurReduce(partitions, startInclusive + split + 1, endInclusive, get, combine);
        return combine.apply(left, right);
    }

    /**
     * Performs a recursively reducing operation with caching. The results of function <code>reducing</code>
     * will be cached and when the same input appears again, the cached value will be used instead of calling
     * <code>reducing</code> again.
     * <p></p>
     * For example, the following code computes fibonacci number of n:
     * <pre>
     * BiFunction&lt;Integer,Function&lt;Integer,Long>,Long> fib = (x,f) ->{
     *             if(x == 1 || x == 2) {
     *                 return 1L;
     *             }
     *             return f.apply(x-1) + f.apply(x-2);
     * };
     * long result = cachedReduce(n,fib);</pre>
     *
     * @param x        the input of the problem
     * @param reducing a reducing function that may call recursively by its second argument
     * @param <T> the input of the problem
     * @param <R> the result of the problem
     * @return the result of computation
     */
    public static <T, R> R cachedReduce(T x, BiFunction<T, Function<T, R>, R> reducing) {
        Function<T,R> cached = FunctionSup.cachedRecurFunction(reducing);
        return cached.apply(x);
    }








//    public <T,C extends Collection<T>> C recurBuild(int index, T initial, C collection, )

//    public static class TimeCostModel<R>{
//        private final int timeCost;
//        private final R model;
//        public TimeCostModel(int timeCost, R model){
//            this.timeCost = timeCost;
//            this.model = model;
//        }
//
//        public int getTimeCost() {
//            return timeCost;
//        }
//
//        public R getModel() {
//            return model;
//        }
//    }
//		DoubleUnaryOperator f = d -> d*d-d;
//		print(binarySolve(0.5d, 3d, (a,b)->(a+b)/2,x-> {
//			double t = f.applyAsDouble(x);
//			return t < 0 ? -1 : t == 0 ? 0 : 1;
//		}, 10));
//        int[][] matrix = new int[][]{
//                {4,3},
//                {3,5},
//                {5,4},
//                {4,6},
//                {6,7},
//                {7,2},
//                {2,3}
//        };
//        final int size = matrix.length;
//        BinaryOperator<int[]> compose = (x,y)->new int[]{x[0],y[1]};
//        int[] re = reduceDP(0,size,
//                x -> matrix[x],compose,
//                Function.identity(),compose,(x,y)->x[0]*y[0]*y[1] );
//        Printer.print(re);
}
