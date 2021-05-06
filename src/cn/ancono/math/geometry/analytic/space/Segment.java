package cn.ancono.math.geometry.analytic.space;

import cn.ancono.math.MathObject;
import cn.ancono.math.MathObjectReal;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.geometry.analytic.space.Line.Relation;
import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Segment is a part of a line
 *
 * @param <T>
 * @author liyicheng
 */
public final class Segment<T> extends SpacePointSet<T> {

    private final Line<T> l;
    //the two endpoint
    private final SPoint<T> p1, p2;

    private SVector<T> v;
    //0,1,2 for x,y,z
    private final int comp;

    Segment(RealCalculator<T> mc, Line<T> l, SPoint<T> p1, SPoint<T> p2) {
        super(mc);
        this.l = l;
        this.p1 = p1;
        this.p2 = p2;
        if (!mc.isEqual(p1.x, p2.x)) {
            comp = 0;
        } else if (!mc.isEqual(p1.y, p2.y)) {
            comp = 1;
        } else {
            comp = 2;
        }
        v = SVector.vector(p1, p2);
    }

    Segment(RealCalculator<T> mc, Line<T> l, SPoint<T> p1, SPoint<T> p2, SVector<T> v, int comp) {
        super(mc);
        this.l = l;
        this.p1 = p1;
        this.p2 = p2;
        this.comp = comp;
        this.v = v;
    }

    Segment(RealCalculator<T> mc, Line<T> l, SPoint<T> p1, SPoint<T> p2, int comp) {
        super(mc);
        this.l = l;
        this.p1 = p1;
        this.p2 = p2;
        this.comp = comp;
        v = SVector.vector(p1, p2);
    }


    @NotNull
    @Override
    public RealCalculator<T> getCalculator() {
        return (RealCalculator<T>) super.getCalculator();
    }

    /**
     * Gets the line of this segment.
     *
     * @return
     */
    public Line<T> getLine() {
        return l;
    }

    /**
     * Gets one of the end points.
     *
     * @return a point
     */
    public SPoint<T> getEndPointA() {
        return p1;
    }

    /**
     * Gets another the end points.
     *
     * @return a point
     */
    public SPoint<T> getEndPointB() {
        return p2;
    }

    /**
     * Returns a vector of <i>AB</i>.
     * The vector's length is equal to that of this segment.
     *
     * @return a vector
     */
    public SVector<T> getDirectVector() {
        return v;
    }

    /**
     * Returns the length of this segment.
     *
     * @return the length of this segment.
     */
    public T getLength() {
        return v.norm();
    }

    /**
     * Returns the square of the length of this segment.
     *
     * @return the square of the length of this segment.
     */
    public T getLengthSq() {
        return v.normSq();
    }

    /**
     * Determines whether the two segment is intersect.<p>
     * <ul><li>If the two segment is on the identity line, then if
     * there is a point on both of them, then the result is true.
     * <li>If the two segment's line is intersect and the intersect point
     * is on both of the segment, then the result is true.
     * <li>Otherwise the result is false.
     * </ul>
     *
     * @param s a segment
     * @return true if intersect
     */
    public boolean isIntersect(Segment<T> s) {
        Line<T> l2 = s.l;
        Relation r = l.relationWith(l2);
        switch (r) {
            case COINCIDE:
                return contains(s.p1) || contains(s.p2) || s.contains(p1) || s.contains(p2);
            case INTERSECT:
                SPoint<T> p = l.intersectPoint(s.l);
                return contains(p) && s.contains(p);
            default:
                return false;
        }
        //need a better algorithm
    }

    /**
     * Determines whether this segment and the line is intersect, on other words, a
     * point that is contained both in this segment and the line exists.
     *
     * @param l2 a line
     * @return true if intersect.
     */
    public boolean isIntersect(Line<T> l2) {
        Relation r = l.relationWith(l2);
        switch (r) {
            case COINCIDE:
                return true;
            case INTERSECT:
                SPoint<T> p = l.intersectPoint(l2);
                return contains(p);
            default:
                return false;
        }
    }

    /**
     * Returns the intersect point of the two segment.Throws an
     * exception if
     * two segments are parallel or on the identity line,
     *
     * @param s another segment
     * @return a point or null if they doesn't intersect
     */
    public SPoint<T> intersectPoint(Segment<T> s) {
        if (l.isParallel(s.l)) {
            throw new IllegalArgumentException("parallel");
        }
        SPoint<T> p = l.intersectPoint(s.l);
        if (contains(p) && s.contains(p)) {
            return p;
        }
        return null;
    }

    /**
     * Returns the intersect point of the two segment.Throws an
     * exception if
     * parallel or on the identity line,
     *
     * @param l2 another line
     * @return a point or null if they doesn't intersect
     */
    public SPoint<T> intersectPoint(Line<T> l2) {
        if (l.isParallel(l2)) {
            throw new IllegalArgumentException("parallel");
        }
        SPoint<T> p = l.intersectPoint(l2);
        if (contains(p)) {
            return p;
        }
        return null;
    }

    /**
     * Returns the middle point of this segment.
     *
     * @return a point
     */
    public SPoint<T> middlePoint() {
        return p1.middle(p2);
    }

    /**
     * Returns the segment AM, where M is the middle point of this.
     *
     * @return a segment
     */
    public Segment<T> middleA() {
        return new Segment<>(getCalculator(), l, p1, middlePoint());
    }

    /**
     * Returns the segment BM, where M is the middle point of this.
     *
     * @return a segment
     */
    public Segment<T> middleB() {
        return new Segment<>(getCalculator(), l, p2, middlePoint());
    }

    /**
     * Returns a segment of AP,
     * where P is a point that AP = kPB,
     * the direction is specific and negative k value is permitted,but it should not be -1.
     *
     * @param k
     * @return a segment
     */
    public Segment<T> propotionSegmentA(T k) {
        return new Segment<>(getCalculator(), l, p1, p1.proportionPoint(p2, k));
    }

    /**
     * Returns a segment of BP,
     * where P is a point that BP = kPA,
     * the direction is specific and negative k value is permitted,but it should not be -1.
     *
     * @param k
     * @return a segment
     */
    public Segment<T> propotionSegmentB(T k) {
        return new Segment<>(getCalculator(), l, p2, p2.proportionPoint(p1, k));
    }

    /**
     * Moves this segment for the given vector.
     *
     * @param s a vector
     * @return a new segment
     */
    public Segment<T> moveToward(SVector<T> s) {
        return new Segment<>(getCalculator(), l.moveToward(s), p1.moveToward(s), p2.moveToward(s), v, comp);
    }

    /**
     * Returns the reverse of this segment, which means the two end points are exchanged.
     *
     * @return a new segment
     */
    public Segment<T> reverse() {
        return new Segment<>(getCalculator(), l, p2, p1, v == null ? null : v.negate(), comp);
    }

    @Override
    public boolean contains(SPoint<T> p) {
        if (l.contains(p)) {
            switch (comp) {
                case 0: {
                    return getCalculator().compare(p1.x, p.x) * getCalculator().compare(p.x, p2.x) >= 0;
                }
                case 1: {
                    return getCalculator().compare(p1.y, p.y) * getCalculator().compare(p.y, p2.y) >= 0;
                }
                case 2: {
                    return getCalculator().compare(p1.z, p.z) * getCalculator().compare(p.z, p2.z) >= 0;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        return false;
    }

    @NotNull
    @Override
    public <N> Segment<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
        return new Segment<>((RealCalculator<N>) newCalculator, l.mapTo(newCalculator, mapper)
                , p1.mapTo(newCalculator, mapper)
                , p2.mapTo(newCalculator, mapper), comp);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Segment) {
            if (this == obj) {
                return true;
            }
            Segment<?> seg = (Segment<?>) obj;
            if (l.equals(seg.l)) {
                return (p1.equals(seg.p1) && p2.equals(seg.p2)) ||
                        (p1.equals(seg.p2) && p2.equals(seg.p1));
            }
        }
        return false;
    }

    private int hash = 0;

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = l.hashCode() * 31 + p1.hashCode() + p2.hashCode();
        }
        return hash;
    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T, EqualPredicate<T>> obj) {
        if (obj instanceof Segment) {
            Segment<T> seg = (Segment<T>) obj;
            return l.valueEquals(seg.l) && ((p1.valueEquals(seg.p1) && p2.valueEquals(seg.p2))
                    || (p1.valueEquals(seg.p1) && p2.valueEquals(seg.p2)));
        }
        return false;
    }


    /**
     * Returns <pre>
     * Segment:(x1,y1,z1)-(x2,y2,z2)
     *
     * </pre>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Segment:").append(p1).append('-').append(p2);
        return sb.toString();
    }

    /**
     * Create a Segment passing through the two points, throws an exception if the two
     * points are the identity.
     * <p>The {@link RealCalculator} will be taken from the first parameter of {@link MathObjectReal}
     *
     * @param p1
     * @param p2
     * @return a new segment
     */
    public static <T> Segment<T> twoPoints(SPoint<T> p1, SPoint<T> p2) {
        if (p1.valueEquals(p2)) {
            throw new IllegalArgumentException("p1=p2");
        }
        RealCalculator<T> mc = (RealCalculator<T>) p1.getCalculator();
        Line<T> l = new Line<>(mc, p1, SVector.vector(p1, p2));
        return new Segment<>(mc, l, p1, p2);
    }

    /**
     * Returns either a Segment or a point decided by whether the two points are equal.
     *
     * @param p1
     * @param p2
     * @return
     */
    public static <T> SpacePointSet<T> segmentOrPoint(SPoint<T> p1, SPoint<T> p2) {
        if (p1.valueEquals(p2)) {
            return p1;
        }
        RealCalculator<T> mc = (RealCalculator<T>) p1.getCalculator();
        Line<T> l = new Line<>(mc, p1, SVector.vector(p1, p2));
        return new Segment<>(mc, l, p1, p2);
    }

    /**
     * Create a Segment with a point and a vector,the vector's length will be the line's.
     * <p>The {@link RealCalculator} will be taken from the first parameter of {@link MathObjectReal}
     *
     * @param p
     * @param v
     * @return a new segment
     */
    public static <T> Segment<T> pointDirect(SPoint<T> p, SVector<T> v) {
        Segment<T> s = twoPoints(p, p.moveToward(v));
        s.v = v;
        return s;
    }


}
