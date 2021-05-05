package cn.ancono.math.geometry.analytic.plane;

import cn.ancono.math.MathObject;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Point is one of the most basic elements in the plane.A point has two dimensions,x and y.
 *
 * @param <T> the type of number
 * @author lyc
 */
public final class Point<T> implements MathObject<T> {

    public final T x, y;
    private final RealCalculator<T> mc;

    public Point(RealCalculator<T> mc, T x, T y) {
        this.mc = mc;
        this.x = x;
        this.y = y;
    }

    /**
     * Get the X coordinate of this point.
     *
     * @return x
     */
    public T getX() {
        return x;
    }

    /**
     * Get the Y coordinate of this point.
     *
     * @return y
     */
    public T getY() {
        return y;
    }

    /**
     * Get the vector representing this point.The returned vector will be a column
     * vector and the length of this vector will be 2.
     *
     * @return a vector
     */
    public PVector<T> getVector() {
        return PVector.valueOf(x, y, mc);
    }

    /**
     * Return the square of the distance between this point and the given point.This method
     * is often better than calling {@code distance()} method.
     *
     * @param p another point
     * @return {@literal (x-p.x)^2 + (y-p.y)^2}
     */
    public T distanceSq(Point<T> p) {
        T dx = mc.subtract(x, p.x);
        T dy = mc.subtract(y, p.y);
        return mc.add(mc.multiply(dx, dx), mc.multiply(dy, dy));
    }

    /**
     * Return the distance of {@code this} and the given point {@code p}.The operation
     * {@linkplain RealCalculator#squareRoot(Object)} is required when this method is called.
     * Make sure that the calculator implements the operation.
     *
     * @param p another point
     * @return the distance of {@code this} and {@code p}
     * @see Point#distance(Point)
     */
    public T distance(Point<T> p) {
        return mc.squareRoot(distanceSq(p));
    }

    /**
     * Returns a point M that <i>ThisM</i> = k<i>MP</i>,the direction is specific and negative {@code k} value
     * is permitted,but it should not be {@code -1}.<p>
     * The result will be {@code x = (kp.x+this.x)/(k+1)},
     * {@code y = (kp.y+this.y)/(k+1)}.
     *
     * @param p another point
     * @param k a number except -1.
     * @return the proportion point.
     */
    public Point<T> proportionPoint(Point<T> p, T k) {
        T de = mc.add(k, mc.getOne());
        T xN = mc.add(mc.multiply(k, p.x), x);
        T yN = mc.add(mc.multiply(k, p.y), y);
        xN = mc.divide(xN, de);
        yN = mc.divide(yN, de);
        return new Point<>(mc, xN, yN);
    }

    /**
     * Return the middle point of {@code this} and {@code p}.
     *
     * @param p another point
     * @return middle point
     */
    public Point<T> middle(Point<T> p) {
        T xm = mc.divideLong(mc.add(x, p.x), 2);
        T ym = mc.divideLong(mc.add(y, p.y), 2);
        return new Point<T>(mc, xm, ym);
    }

    /**
     * Translate this point to a new point with the vector. The result will be
     * {@literal (x+v.x,y+v.y)}.
     *
     * @param v a vector
     * @return {@literal (x+v.x,y+v.y)}
     */
    public Point<T> translate(PVector<T> v) {
        return new Point<>(mc, mc.add(x, v.x), mc.add(y, v.y));
    }

    @Override
    public <N> Point<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
        return new Point<>(newCalculator, mapper.apply(x), mapper.apply(y));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Point) {
            Point<?> p = (Point<?>) obj;
            return x.equals(p.x) && y.equals(p.y);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = mc.hashCode();
        h = h * 31 + x.hashCode();
        h = h * 31 + y.hashCode();
        return h;
    }

    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (obj instanceof Point) {
            Point<N> p = (Point<N>) obj;
            return mc.isEqual(x, mapper.apply(p.x)) && mc.isEqual(y, mapper.apply(p.y));
        }
        return false;
    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Point) {
            Point<T> p = (Point<T>) obj;
            return mc.isEqual(x, p.x) && mc.isEqual(y, p.y);
        }
        return false;
    }

    /**
     * Return the String expression of this point.The expression will
     * be like :
     * <pre>
     * "( "+x.toString()+" , "+y.toString()+" )"
     * </pre>
     */
    @Override
    public String toString() {
        return toString(FlexibleNumberFormatter.defaultFormatter());
    }

    /**
     * Return the String expression of this point.The expression will
     * be like :
     * <pre>
     * "( "+x.toString()+" , "+y.toString()+" )"
     * </pre>
     *
     * @param nf
     */
    @Override
    public String toString(FlexibleNumberFormatter<T> nf) {
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        sb.append(nf.format(x)).append(" , ").append(nf.format(y)).append(" )");
        return sb.toString();
    }

    @NotNull
    @Override
    public RealCalculator<T> getCalculator() {
        return mc;
    }

    /**
     * Create a point with the given coordinates.
     *
     * @param x  X coordinate
     * @param y  Y coordinate
     * @param mc a MathCalculator
     * @return a new point
     */
    public static <T> Point<T> valueOf(T x, T y, RealCalculator<T> mc) {
        return new Point<>(mc, x, y);
    }

    /**
     * Returns the point (0,0).
     *
     * @param mc a {@link RealCalculator}
     * @return point (0,0)
     */
    public static <T> Point<T> pointO(RealCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        Point<T> p = (Point<T>) opoints.get(mc);
        if (p == null) {
            p = new Point<>(mc, mc.getZero(), mc.getZero());
            opoints.put(mc, p);
        }
        return p;
    }

    private static final Map<RealCalculator<?>, Point<?>> opoints = new ConcurrentHashMap<>();


    /**
     * Create a vector that is equal to <i>thisP</i>.
     *
     * @param p another point
     * @return a column vector with two dimensions.
     */
    public PVector<T> directVector(Point<T> p) {
        @SuppressWarnings("SuspiciousNameCombination")
        T vx = mc.subtract(p.x, x);
        @SuppressWarnings("SuspiciousNameCombination")
        T vy = mc.subtract(p.y, y);
        return PVector.valueOf(vx, vy, mc);
    }

    public static <T> Point<T> fromVector(PVector<T> v) {
        return new Point<>((RealCalculator<T>) v.getCalculator(), v.x, v.y); //TODO
    }


}
