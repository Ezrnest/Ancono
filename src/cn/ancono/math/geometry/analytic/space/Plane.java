package cn.ancono.math.geometry.analytic.space;

import cn.ancono.math.AbstractMathObject;
import cn.ancono.math.MathObject;
import cn.ancono.math.MathObjectReal;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.algebra.abs.calculator.FieldCalculator;
import cn.ancono.math.algebra.abs.calculator.OrderedFieldCal;
import cn.ancono.math.algebra.abs.calculator.RingCalculator;
import cn.ancono.math.algebra.linear.LinearEquationSolution;
import cn.ancono.math.algebra.linear.Matrix;
import cn.ancono.math.algebra.linear.Vector;
import cn.ancono.math.function.MathFunction;
import cn.ancono.math.geometry.analytic.plane.Circle;
import cn.ancono.math.geometry.analytic.plane.Point;
import cn.ancono.math.geometry.analytic.plane.Triangle;
import cn.ancono.math.numberModels.api.NumberFormatter;
import cn.ancono.math.numberModels.api.RealCalculator;
import cn.ancono.math.numberModels.api.Simplifiable;
import cn.ancono.math.numberModels.api.Simplifier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Describes a plane in the space, the
 *
 * @param <T>
 * @author liyicheng
 */
public final class Plane<T> extends SpacePointSet<T> implements Simplifiable<T, Plane<T>> {

    /**
     * The relation between two planes or a plane or a line.
     *
     * @author lyc
     */
    public enum Relation {
        /**
         * Parallel
         */
        PARALLEL,
        /**
         * Intersect
         */
        INTERSECT,
        /**
         * Coincide
         */
        COINCIDE;
    }

    //we use ax+by+cz+d = 0 as the general formula for a plane.
    private final T a, b, c, d;
    //the temp for normal vector because it is often used
    private SVector<T> normalVector = null;


    protected Plane(FieldCalculator<T> mc, T a, T b, T c, T d) {
        super(mc);
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    protected Plane(FieldCalculator<T> mc, SVector<T> nv, T d) {
        super(mc);
        this.a = nv.x;
        this.b = nv.y;
        this.c = nv.z;
        this.d = d;
        normalVector = nv;
    }

    @NotNull
    @Override
    public FieldCalculator<T> getCalculator() {
        return (FieldCalculator<T>) super.getCalculator();
    }

    /**
     * Substitute the point's xyz coordinate into the expression ax+by+cz+d and returns the
     * result.
     *
     * @param p a point
     * @return the result
     */
    public T substitute(SPoint<T> p) {
        var mc = getCalculator();
        T res = mc.multiply(a, p.x);
        res = mc.add(res, mc.multiply(b, p.y));
        res = mc.add(res, mc.multiply(c, p.z));
        return mc.add(res, d);
    }

    T compute(T x, T y, T z) {
        var mc = getCalculator();
        T res = mc.multiply(a, x);
        res = mc.add(res, mc.multiply(b, y));
        return mc.add(res, mc.multiply(c, z));
    }

    /**
     * This method determines whether the point is on this plane. This method is
     * generally equal to {@code mc.isZero(this.substitute(p))}
     */
    @Override
    public boolean contains(SPoint<T> p) {
        var mc = getCalculator();
        return mc.isZero(substitute(p));
    }

    /**
     * Determines whether this plane contains the given vector, or say, the vector
     * is parallel to this plane.
     * <pre>a*vec.x+b*vec.y+c*vec.z = 0</pre>
     *
     * @param vec
     * @return true if the plane contain the vector.
     */
    public boolean contains(SVector<T> vec) {
        var mc = getCalculator();
        //or we can write
        //return mc.isZero(getNormalVector().innerProduct(vec));
        return mc.isZero(compute(vec.getX(), vec.getY(), vec.getZ()));
    }

    /**
     * Determines whether this plane contains the given line.
     *
     * @param l
     * @return true if the plane contain the line.
     */
    public boolean contains(Line<T> l) {
        return contains(l.vec) && contains(l.p0);
    }

    private Matrix<T> createEquation(Plane<T> p) {
        // a1x+b1y+c1z = -d1
        // a2x+b2y+c2z = -d2
        @SuppressWarnings("unchecked")
        T[][] mat = (T[][]) new Object[2][4];
        mat[0][0] = a;
        mat[0][1] = b;
        mat[0][2] = c;
        mat[0][3] = d;
        mat[1][0] = p.a;
        mat[1][1] = p.b;
        mat[1][2] = p.c;
        mat[1][3] = p.d;
        var mc = getCalculator();
        return Matrix.of(mat, mc);
    }

    /**
     * Determines whether the two planes are parallel, in this method, if the
     * two planes are coincide, they will also be considered as parallel.
     *
     * @param p another plane
     * @return true if the two planes are parallel
     */
    public boolean isParallel(Plane<T> p) {
        return getNormalVector().isParallel(p.getNormalVector());
    }


    /**
     * Returns the relation with another plane.
     *
     * @param p
     * @return
     * @see {@link Plane.Relation}
     */
    public Relation relationWith(Plane<T> p) {
        if (isParallel(p)) {
            var mc = getCalculator();
            if (mc.isZero(a)) {
                if (mc.isZero(b)) {
                    return mc.isEqual(mc.multiply(c, p.d), mc.multiply(d, p.c)) ? Relation.COINCIDE :
                            Relation.PARALLEL;
                }
                return mc.isEqual(mc.multiply(b, p.d), mc.multiply(d, p.b)) ? Relation.COINCIDE : Relation.PARALLEL;
            }
            return mc.isEqual(mc.multiply(a, p.d), mc.multiply(d, p.a)) ? Relation.COINCIDE : Relation.PARALLEL;
        }
        return Relation.INTERSECT;
    }

    /**
     * Returns the relation with a line.
     * Returns COINCIDE if the plane contains the line, PARALLEL
     * if the line is parallel to this plane, otherwise returns
     * INTERSECT.
     *
     * @param l a line
     * @return the relation
     */
    public Relation relationWith(Line<T> l) {
        if (contains(l.vec)) {
            if (contains(l.p0)) {
                return Relation.COINCIDE;
            }
            return Relation.PARALLEL;
        }
        return Relation.INTERSECT;
    }

    /**
     * Returns the intersect line of {@code this} and {@code p}, throws an {@link ArithmeticException} if
     * the two planes are coincide, returns {@code null} if they are parallel.
     *
     * @param p a plane
     * @return the intersect line, or {@code null} if they are parallel.
     */
    public Line<T> intersectLine(Plane<T> p) {
        Matrix<T> mat = createEquation(p);
//		mat.printMatrix();
        LinearEquationSolution<T> so = Matrix.solveLinearExpanded(mat);
        if (!so.isInfinite()) {
            return null;
        }
//		so.printSolution();
        var basis = so.getSolutionSpace();
        if (basis.getRank() == 2) {
            throw new ArithmeticException("Coincide");
        }
        Vector<T> base = so.getSpecial();
        var mc = getCalculator();
        return new Line<>(mc,
                new SPoint<>(mc, base.get(0), base.get(1), base.get(2)),
                SVector.fromVector(basis.getVectors().get(0)));
    }

    /**
     * Returns the intersect point of the line, returns null if the line
     * is parallel to this plane, throws an exception if the line is on this
     * plane.
     *
     * @param l
     * @return the point, or null
     */
    public SPoint<T> intersectPoint(Line<T> l) {
        return projection(l.p0, l.vec);
    }

    /**
     * Moves this plane.
     *
     * @param vec
     * @return
     */
    public Plane<T> moveToward(SVector<T> vec) {
        var mc = getCalculator();
        Plane<T> p = new Plane<T>(mc, a, b, c, mc.subtract(d, getNormalVector().innerProduct(vec)));
        p.normalVector = normalVector;
        return p;
    }

    /**
     * Move this plane in the direction of it normal vector by the distance.
     *
     * @param dis
     * @return
     */
    public Plane<T> moveDistance(T dis) {
        var mc = getCalculator();
        Plane<T> p = new Plane<T>(mc, a, b, c, mc.subtract(d, mc.multiply(getNormalVector().norm(), dis)));
        p.normalVector = normalVector;
        return p;
    }

    /**
     * Returns two vectors as an array, both of them are parallel to this plane. The vector actually is
     * <pre>(b,-a,0)
     * (c,0,-a)
     * </pre>
     *
     * @return
     */
    public SVector<T>[] getDirectVectors() {
        @SuppressWarnings("unchecked")
        SVector<T>[] res = new SVector[2];
        var mc = getCalculator();
        T a_ = mc.negate(a);
        res[0] = new SVector<T>(b, a_, mc.getZero(), mc);
        res[1] = new SVector<T>(c, mc.getZero(), a_, mc);
        return res;
    }

    /**
     * Gets the normal vector of the plane.
     * <pre>(a,b,c)</pre>
     *
     * @return normal vector
     */
    public SVector<T> getNormalVector() {
        if (normalVector == null) {
            var mc = getCalculator();
            normalVector = new SVector<>(a, b, c, mc);
        }
        return normalVector;
    }

    /**
     * Returns the cos value of the angle of {@code this} and {@code p2},
     * the returned value will be always in [0,1].
     *
     * @param p2 another plane
     * @return cos value of the angle
     */
    public T angleCos(Plane<T> p2) {
        SVector<T> n1 = getNormalVector(),
                n2 = p2.getNormalVector();
        var mc = (OrderedFieldCal<T>) getCalculator();
        return mc.abs(n1.angleCos(n2));
    }


    /**
     * Returns the angle of {@code this} and {@code p2}, a function
     * of arccos must be given.
     *
     * @param p2     another plane
     * @param arccos arccos function
     * @return the angle
     */
    public <R> R angle(Plane<T> p2, MathFunction<T, R> arccos) {
        return arccos.apply(angleCos(p2));
    }

    /**
     * Returns the sin value of the angle of {@code this} and {@code l}, the value
     * must be in [0,1]
     *
     * @param l a line
     * @return sin value of the angle
     */
    public T angleSin(Line<T> l) {
        //we calculate the cos instead
        var mc = (OrderedFieldCal<T>) getCalculator();
        return mc.abs(getNormalVector().angleCos(l.vec));
    }

    /**
     * Returns the the angle of {@code this} and {@code l}, a function
     * of arcsin must be provided.
     *
     * @param l      a line
     * @param arcsin arcsin function
     * @return the angle
     */
    public <R> R angle(Line<T> l, MathFunction<T, R> arcsin) {
        return arcsin.apply(angleSin(l));
    }

    /**
     * Returns the projection of the point on this plane.
     *
     * @param p a point
     * @return a point
     */
    public SPoint<T> projection(SPoint<T> p) {
        var mc = getCalculator();
        T t = substitute(p);
        if (mc.isZero(t)) {
            //the point is on this plane.
            return p;
        }
        T k = mc.negate(mc.divide(t, getNormalVector().normSq()));
        T px = mc.add(p.x, mc.multiply(a, k));
        T py = mc.add(p.y, mc.multiply(b, k));
        T pz = mc.add(p.z, mc.multiply(c, k));
        return new SPoint<>(mc, px, py, pz);
    }

    /**
     * Returns the square of the distance from the point to the plane.
     *
     * @param p a point
     * @return the square of the distance
     */
    public T distanceSq(SPoint<T> p) {
        var mc = getCalculator();
        T t = substitute(p);
        return mc.divide(mc.multiply(t, t), getNormalVector().normSq());
    }

    /**
     * Returns the distance from the point to the plane.
     *
     * @param p a point
     * @return the distance
     */
    public T distance(SPoint<T> p) {
        var mc = (OrderedFieldCal<T>) getCalculator();
        return mc.abs(distanceDirected(p));
    }

    /**
     * Returns the directed distance.
     *
     * @param p a point
     * @return
     */
    public T distanceDirected(SPoint<T> p) {
        T t = substitute(p);
        var mc = getCalculator();
        return mc.divide(t, getNormalVector().norm());
    }

    /**
     * Returns a plane with an opposite normal vector.
     *
     * @return a new plane
     */
    public Plane<T> reverse() {
        SVector<T> nor = getNormalVector().negate();
        var mc = getCalculator();
        return new Plane<>(mc, nor, mc.negate(d));
    }

    /**
     * Returns the parallel projection of the point and the direct vector vec.<br/>
     * The result of {@code this.projection(p,this.getNormalVector())} and {@code this.projection(p)}
     * will always be the identity.<p>
     *
     * <pre>
     * . <-- a point
     *  \
     *   \   <-- the direct vector
     *    \
     *     \
     * -----.---------- this plane
     *      ^
     *      result
     *
     * </pre>
     * If {@code vec} is parallel to this plane,
     * <ul>
     * <li>Returns the point {@code p}, if the point is on this plane.
     * <li>Returns {@code null}, if the point is outside this plane.
     * </ul>
     *
     * @param p
     * @param vec
     * @return
     * @see #intersectPoint(Line)
     */
    public SPoint<T> projection(SPoint<T> p, SVector<T> vec) {
        T t = getNormalVector().innerProduct(vec);
        return projection0(p, vec, t);
    }

    private SPoint<T> projection0(SPoint<T> p, SVector<T> vec, T t) {
        var mc = getCalculator();
        if (mc.isZero(t)) {
            if (contains(p)) {
                return p;
            }
            return null;
        }
        T k = mc.negate(mc.divide(substitute(p), t));
        T xn = mc.add(p.x, mc.multiply(k, vec.x));
        T yn = mc.add(p.y, mc.multiply(k, vec.y));
        T zn = mc.add(p.z, mc.multiply(k, vec.z));
        return new SPoint<>(mc, xn, yn, zn);
    }

    /**
     * Returns a MathFunction performing the projection operation.
     *
     * @param vec the vector.
     * @return
     * @see #projection(SPoint, SVector)
     */
    public SPointTrans<T> projectionAsFunction(SVector<T> vec) {
        T t = getNormalVector().innerProduct(vec);
        return x -> projection0(x, vec, t);
    }

    /**
     * Returns the projection of the line on this plane.
     *
     * @param l a line
     * @return a line
     */
    public Line<T> projection(Line<T> l) {
        SPoint<T> ip = intersectPoint(l);
        SVector<T> vec = projection(l.vec);
        var mc = getCalculator();
        return new Line<>(mc, ip, vec);
    }

    /**
     * Returns the projection of the vector on this plane.
     * The result will be parallel to this plane and has the smallest angle with
     * {@code v}.
     *
     * @param v a vector
     * @return a vector
     */
    public SVector<T> projection(SVector<T> v) {
        return getNormalVector().perpendicular(v);
    }

    /**
     * Create a plane that passes through the point and is parallel to {@code this}.
     *
     * @param p a point
     * @return a plane
     */
    public Plane<T> parallel(SPoint<T> p) {
        return pointNormalVector(p, getNormalVector());
    }

    /**
     * Determines whether the line is parallel to this plane, in this method, if the
     * the line is on this plane, they will also be considered as parallel.
     *
     * @param l a line
     * @return true if they are parallel
     */
    public boolean isParallel(Line<T> l) {
        return contains(l.vec);
    }


    /**
     * Determines whether the line is perpendicular to this plane.
     *
     * @param l a line
     * @return true if they are perpendicular
     */
    public boolean isPerpendicular(Line<T> l) {
        return l.vec.isParallel(getNormalVector());
    }

    /**
     * Create a plane that passes through the line and is perpendicular to {@code this}.
     *
     * @param l a line which is not perpendicular to this plane.
     * @return a Plane
     */
    public Plane<T> perpendicular(Line<T> l) {
        Plane<T> p = vectorPoint0(getNormalVector(), l.getDirectVector(), l.getOnePoint(), getCalculator());
        if (p == null) {
            throw new IllegalArgumentException("Perpendicular");
        }
        return p;
    }

    /**
     * Create a line that is perpendicular to this plane and passes through the point
     *
     * @param p a point
     * @return a line which is not perpendicular to this plane.
     */
    public Line<T> perpendicular(SPoint<T> p) {
        return Line.pointDirect(p, getNormalVector());
    }

    /**
     * Create a coordinate converter that bridges planeAG and spaceAG, the vector x,y must be on this
     * plane and is perpendicular
     *
     * @param x
     * @param y
     * @param O
     * @param unitLength the unitLength to divide when converting to planeAG, set {@code null} for 1.
     * @return
     */
    public PlaneCoordinateConverter<T> getCoordinateConverter(SVector<T> x, SVector<T> y, SPoint<T> O, T unitLength) {
        if (!contains(x) || !contains(y) || !contains(O)) {
            throw new IllegalArgumentException("Not contains");
        }
        if (unitLength != null) {
            x = x.parallel(unitLength);
            y = y.parallel(unitLength);
        } else {
            x = x.unitVector();
            y = y.unitVector();
        }
        return new PlaneCoordinateConverter<>(getCalculator(), x, y, O, unitLength, this);
    }

    /**
     * Create a coordinate converter that bridges planeAG and spaceAG.
     *
     * @param x
     * @param y
     * @param O
     * @return
     */
    public PlaneCoordinateConverter<T> getCoordinateConverter(SVector<T> x, SVector<T> y, SPoint<T> O) {
        return getCoordinateConverter(x, y, O, null);
    }

    /**
     * A plane coordinate converter is to convert point in the space to the corresponding point on the
     * plane.
     *
     * @param <T>
     * @author liyicheng
     * 2017-09-22 17:24
     */
    public static class PlaneCoordinateConverter<T> extends AbstractMathObject<T, FieldCalculator<T>> {
        final T D, unit;
        final SVector<T> x, y;
        final SPoint<T> O;
        final Plane<T> p;

        PlaneCoordinateConverter(FieldCalculator<T> mc, SVector<T> x, SVector<T> y, SPoint<T> O, T unit, Plane<T> p) {
            super(mc);
            D = mc.subtract(mc.multiply(x.x, y.y), mc.multiply(y.x, x.y));
            this.unit = unit;
            this.x = x;
            this.y = y;
            this.O = O;
            this.p = p;
        }

        /**
         * Gets the plane.
         *
         * @return
         */
        public Plane<T> getPlane() {
            return p;
        }

        /**
         * Convert the length in space to the length in the plane.
         *
         * @return
         */
        public T convertToPlaneLength(T lengthInSpace) {
            var mc = getCalculator();
            return mc.divide(lengthInSpace, unit);
        }

        /**
         * Convert the length in plane to the length in the space.
         *
         * @return
         */
        public T convertToSpaceLength(T lengthInPlane) {
            var mc = getCalculator();
            return mc.multiply(lengthInPlane, unit);
        }

        public Point<T> toPlanePoint(SPoint<T> p) {
            if (!p.contains(p)) {
                throw new IllegalArgumentException("Not contain");
            }
            return toPlanePoint0(p);
        }

        Point<T> toPlanePoint0(SPoint<T> p) {
            var mc = getCalculator();
            T dx = mc.subtract(p.x, O.x);
            T dy = mc.subtract(p.y, O.y);
            T D1 = mc.subtract(mc.multiply(dx, y.y), mc.multiply(y.x, dy));
            T D2 = mc.subtract(mc.multiply(x.x, dy), mc.multiply(dx, x.y));
            T x = mc.divide(D1, D);
            T y = mc.divide(D2, D);
            return new Point<>(mc, x, y);
        }

        public SPoint<T> toSpacePoint(Point<T> p) {
            var mc = getCalculator();
            T px = p.getX();
            T py = p.getY();
            T sx = mc.add(mc.multiply(px, x.x), mc.multiply(py, y.x));
            T sy = mc.add(mc.multiply(px, x.y), mc.multiply(py, y.y));
            T sz = mc.add(mc.multiply(px, x.z), mc.multiply(py, y.z));
            return O.moveToward(sx, sy, sz);
        }

        public Triangle<T> toPlaneTriangle(STriangle<T> tri) {
            if (p.valueEquals(tri.getPlane())) {
                Point<T> a = toPlanePoint0(tri.getA());
                Point<T> b = toPlanePoint0(tri.getB());
                Point<T> c = toPlanePoint0(tri.getC());
                return Triangle.fromVertex(getCalculator(), a, b, c);
            }
            throw new IllegalArgumentException("NOT SAME PLANE");
        }

        public Circle<T> toPlaneCircle(SCircle<T> c) {
            if (p.valueEquals(c.getPlane())) {
                Point<T> p = toPlanePoint0(c.o);
                var mc = getCalculator();
                if (c.r != null) {
                    return Circle.centerAndRadius(p, convertToPlaneLength(c.r), (RealCalculator<T>) mc);
                } else {
                    return Circle.centerAndRadiusSquare(p, mc.divide(c.r2, mc.multiply(unit, unit)), (RealCalculator<T>) mc);
                }
            }
            throw new IllegalArgumentException("NOT SAME PLANE");
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof PlaneCoordinateConverter) {
                PlaneCoordinateConverter<?> pcc = (PlaneCoordinateConverter<?>) obj;
                return D.equals(pcc.D) && unit.equals(pcc.unit) && x.equals(pcc.x) &&
                        y.equals(pcc.y) && O.equals(pcc.O);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = O.hashCode();
            hash = hash * 31 + x.hashCode();
            hash = hash * 31 + y.hashCode();
            hash = hash * 31 + p.hashCode();
            return hash;
        }

        @Override
        public boolean valueEquals(@NotNull MathObject<T, FieldCalculator<T>> obj) {
            if (obj instanceof PlaneCoordinateConverter) {
                PlaneCoordinateConverter<T> pcc = (PlaneCoordinateConverter<T>) obj;
                var mc = getCalculator();
                return mc.isEqual(D, pcc.D) && mc.isEqual(unit, pcc.unit) &&
                        x.valueEquals(pcc.x) && y.valueEquals(pcc.y) && O.valueEquals(pcc.O);
            }
            return false;
        }

//        @Override
//        public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
//            if (obj instanceof PlaneCoordinateConverter) {
//                PlaneCoordinateConverter<N> pcc = (PlaneCoordinateConverter<N>) obj;
//                return mc.isEqual(D, mapper.apply(pcc.D)) && mc.isEqual(unit, mapper.apply(pcc.unit)) &&
//                        x.valueEquals(pcc.x, mapper) && y.valueEquals(pcc.y, mapper) && O.valueEquals(pcc.O, mapper);
//            }
//            return false;
//        }

        @NotNull
        @Override
        public <N> PlaneCoordinateConverter<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            return new PlaneCoordinateConverter<>((FieldCalculator<N>) newCalculator, x.mapTo(newCalculator, mapper),
                    y.mapTo(newCalculator, mapper),
                    O.mapTo(newCalculator, mapper),
                    mapper.apply(unit),
                    p.mapTo(newCalculator, mapper));
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
         */
        @NotNull
        @Override
        public String toString(@NotNull NumberFormatter<T> nf) {
            return "PlaneCoordinateConverter";
        }
    }

    @NotNull
    @Override
    public <N> Plane<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
        return new Plane<N>((FieldCalculator<N>) newCalculator, mapper.apply(a), mapper.apply(b), mapper.apply(c), mapper.apply(d));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Plane) {
            Plane<?> p = (Plane<?>) obj;
            return this == p || (a.equals(p.a) && b.equals(p.b) &&
                    c.equals(p.c) && d.equals(p.d));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = a.hashCode();
        hash = hash * 31 + b.hashCode();
        hash = hash * 31 + c.hashCode();
        hash = hash * 31 + d.hashCode();
        return hash;
    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T, EqualPredicate<T>> obj) {
        if (obj instanceof Line) {
            return this == obj || relationWith((Plane<T>) obj) == Relation.COINCIDE;
        }
        return false;
    }


    /**
     * Returns "Plane: ax+by+cz+d=0", where a,b,c,d are
     * the parameter in this plane and may be omitted if
     * it is zero or one.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Plane: ");
        var mc = getCalculator();
        T z = mc.getZero();
        T o = mc.getOne();
        if (!mc.isEqual(a, z)) {
            if (!mc.isEqual(a, o)) {
                sb.append('(').append(a.toString()).append(')');
            }
            sb.append("x + ");
        }
        if (!mc.isEqual(b, z)) {
            if (!mc.isEqual(b, o)) {
                sb.append('(').append(b.toString()).append(')');
            }
            sb.append("y + ");
        }
        if (!mc.isEqual(c, z)) {
            if (!mc.isEqual(c, o)) {
                sb.append('(').append(c.toString()).append(')');
            }
            sb.append("z + ");
        }
        if (!mc.isEqual(d, z))
            sb.append('(').append(d.toString()).append(")   ");
        sb.delete(sb.length() - 2, sb.length());
        sb.append("= 0");
        return sb.toString();
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.number_types.Simplifiable#simplify()
     */
    @Override
    public Plane<T> simplify() {
        return this;
    }

    /**
     * Simplify the plane, the normal vector of this plane is possible to be changed.
     * The given Simplifier should maintain that
     * positive(or negative) value should be positive(or negative) after simplifying.
     */
    @Override
    public Plane<T> simplify(Simplifier<T> sim) {
        List<T> ts = Arrays.asList(a, b, c, d);
        ts = sim.simplify(ts);
        Plane<T> pl = new Plane<>(getCalculator(), ts.get(0), ts.get(1), ts.get(2), ts.get(3));
        return pl;
    }

    /**
     * Create a plane
     * <pre>ax + by + cz + d = 0</pre>
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @param mc
     * @return a new plane
     */
    public static <T> Plane<T> generalFormula(T a, T b, T c, T d, FieldCalculator<T> mc) {
        if (a == null || b == null || c == null || d == null) {
            throw new NullPointerException();
        }
        //a b c must not be all 0.
        if (mc.isZero(a) && mc.isZero(b) && mc.isZero(c)) {
            throw new IllegalArgumentException("a=b=c=0");
        }
        return new Plane<>(mc, a, b, c, d);
    }

    /**
     * Create a plane that contains {@code p} and has normal vector {@code nv}.
     * <p>The {@link FieldCalculator} will be taken from the first parameter of {@link MathObjectReal}
     *
     * @param p  a point
     * @param nv a vector, not zero
     * @return
     */
    public static <T> Plane<T> pointNormalVector(SPoint<T> p, SVector<T> nv) {
        if (nv.isZero()) {
            throw new IllegalArgumentException("zero vector");
        }
        FieldCalculator<T> mc = (FieldCalculator<T>) p.getCalculator();
        T d = mc.add(mc.multiply(p.x, nv.x), mc.add(mc.multiply(p.y, nv.y), mc.multiply(p.z, nv.z)));
        d = mc.negate(d);
        Plane<T> pl = new Plane<>(mc, nv.x, nv.y, nv.z, d);
        pl.normalVector = nv;
        return pl;
    }

    /**
     * Create a plane through a line and a point outside the line.
     * <p>The {@link FieldCalculator} will be taken from the first parameter of {@link MathObjectReal}
     *
     * @param l a line
     * @param p a point
     * @return a new plane
     */
    public static <T> Plane<T> linePoint(Line<T> l, SPoint<T> p) {
        SVector<T> v2 = SVector.vector(l.p0, p);
        Plane<T> pr = vectorPoint0(l.vec, v2, l.p0, l.getCalculator());
        if (pr == null) {
            throw new IllegalArgumentException("Point on the line");
        }
        return pr;
    }

    /**
     * Create a plane through two lines.
     * <p>The {@link FieldCalculator} will be taken from the first parameter of {@link MathObjectReal}
     *
     * @param l1 a line
     * @param l2 another line
     * @return a new plane
     */
    public static <T> Plane<T> twoLines(Line<T> l1, Line<T> l2) {
        SVector<T> v2;
        if (l1.isParallel(l2)) {
            v2 = SVector.vector(l1.p0, l2.p0);
        } else {
            v2 = l2.vec;
        }
        Plane<T> pr = vectorPoint0(l1.vec, v2, l1.p0, l1.getCalculator());
        if (pr == null) {
            throw new IllegalArgumentException("Coincide");
        }
        return pr;
    }

    /**
     * Create a plane through three points.
     * <p>The {@link FieldCalculator} will be taken from the first parameter of {@link MathObjectReal}
     *
     * @return a new plane
     */
    public static <T> Plane<T> threePoints(SPoint<T> p1, SPoint<T> p2, SPoint<T> p3) {
        SVector<T> v1 = SVector.vector(p1, p2);
        SVector<T> v2 = SVector.vector(p1, p3);
        Plane<T> p = vectorPoint0(v1, v2, p1, (FieldCalculator<T>) p1.getCalculator());
        if (p == null) {
            throw new IllegalArgumentException("Three point one line");
        }
        return p;
    }


    private static <T> Plane<T> vectorPoint0(SVector<T> v1, SVector<T> v2, SPoint<T> p, FieldCalculator<T> mc) {
        //here we solve the equation and get one solution as
        //v1=(x,y,z), v2=(k,q,j)
        //a = jy - qz , b = kz - jx , c = qx - ky
        // or (a,b,c) = v1 �� v2 (outer product)
        SVector<T> abc = v1.outerProduct(v2);
        if (abc.isZero()) {
            return null;
        }
        SVector<T> pv = p.getVector();
        T d = mc.negate(abc.innerProduct(pv));
        Plane<T> pl = new Plane<>(mc, abc.getX(), abc.getY(), abc.getZ(), d);
        pl.normalVector = abc;
        return pl;
    }

    /**
     * Create a plane with two vector and a point. The two vector must not be parallel.
     *
     * @param v1
     * @param v2
     * @param p
     * @param mc
     * @return a new plane
     */
    public static <T> Plane<T> vectorPoint(SVector<T> v1, SVector<T> v2, SPoint<T> p, RingCalculator<T> mc) {
        //here we solve the equation and get one solution as
        //v1=(x,y,z), v2=(k,q,j)
        //a = jy - qz , b = kz - jx , c = qx - ky
        // or (a,b,c) = v1 �� v2 (outer product)
        SVector<T> abc = v1.outerProduct(v2);
        if (abc.isZero()) {
            throw new IllegalArgumentException("v1 // v2");
        }
        SVector<T> pv = p.getVector();
        T d = mc.negate(abc.innerProduct(pv));
        Plane<T> pl = new Plane<>((FieldCalculator<T>) mc, abc.getX(), abc.getY(), abc.getZ(), d); //TODO
        pl.normalVector = abc;
        return pl;
    }

    /**
     * Create a plane with two vector and a point. The two vector must not be parallel.
     * <p>The {@link FieldCalculator} will be taken from the first parameter of {@link MathObjectReal}
     *
     * @param v1
     * @param v2
     * @param p
     * @return a new plane
     */
    public static <T> Plane<T> vectorPoint(SVector<T> v1, SVector<T> v2, SPoint<T> p) {
        return vectorPoint(v1, v2, p, v1.getCalculator());
    }

    /**
     * Returns a plane that crosses the line and has an intersect angle with {@code pl} whose
     * tangent value is {@code tan}, the line must be parallel to this plane.
     *
     * @param pl
     * @param line
     * @return
     */
    public static <T> Plane<T> anglePlane(Plane<T> pl, Line<T> line, T tan) {
        if (!pl.isParallel(line)) {
            throw new IllegalArgumentException("Not parallel");
        }
        SVector<T> n = line.vec;
        SVector<T> pln = SVector.angledVector(pl.getNormalVector(), n, tan);
        return pointNormalVector(line.p0, pln);
    }


//	public static void main(String[] args) {
//		MathCalculator<Fraction> mc = Fraction.Companion.mc;
//		SVector<Fraction> v1 = SVector.valueOf(Fraction.Companion.getONE(), Fraction.Companion.getZERO(), Fraction.Companion.getZERO(), mc);
//		SVector<Fraction> v2 = SVector.valueOf(Fraction.Companion.getZERO(), Fraction.Companion.getONE(), Fraction.Companion.getZERO(), mc);
////				v3 = SVector.vector(3d, 4d, 1d, mc);
//		print(v1.perpendicular(v2));
////		print(v1);
////		print(v2);
//        Plane<Fraction> p1 = generalFormula(Fraction.Companion.getZERO(), Fraction.Companion.getZERO(), Fraction.Companion.getONE().negate(), Fraction.Companion.getONE(), mc);
////		Plane<Fraction> p2 = generalFormula(1d, 0d, 0d, 0d, mcd);
////		Line<Double> l = Line.pointDirect(Point.pointO(mcd),v3);
////		p1 = vectorPoint(v1, v2, SPoint.pointO(mcd), mcd);
//		print(p1);
////		print(p2);
////		print(p1.intersectLine(p2));
////		print(p1.projection(Point.valueOf(1d, 1d, 1d, mcd)));
////		print(p1.projection(v3));
////		print(l);
////		print(p1.projection(l));
//		PlaneCoordinateConverter<Fraction> pcc = p1.getCoordinateConverter(v1, v2,
//				SPoint.valueOf(Fraction.Companion.getZERO(), Fraction.Companion.getZERO(), Fraction.Companion.getONE(), mc));
//		print(pcc.toPlanePoint(SPoint.valueOf(Fraction.Companion.getONE(), Fraction.Companion.getONE(), Fraction.Companion.getONE(), mc)));
//		print(pcc.toSpacePoint(Point.valueOf(Fraction.Companion.valueOf(10), Fraction.Companion.valueOf(20), mc)));
//	}


}
