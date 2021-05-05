package cn.ancono.math.geometry;

import cn.ancono.math.AbstractMathObject;
import cn.ancono.math.MathObject;
import cn.ancono.math.geometry.analytic.plane.*;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.ComputeExpression;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.math.numberModels.api.RealCalculator;
import cn.ancono.utilities.ArraySup;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * A geometric triangle
 */
public class GTriangle<T> extends AbstractMathObject<T> {
    /**
     * sides
     */
    final T a, b, c;
    /**
     * angles
     */
    private T A, B, C;
    /**
     * Radius of outer center
     */
    private T outerR;

    private T area;

    /**
     * @param mc
     * @param a  edge a
     * @param b  edge b
     * @param c  edge c
     * @param A  angle A
     * @param B  angle B
     * @param C  angle C
     */
    GTriangle(RealCalculator<T> mc, T a, T b, T c, T A, T B, T C) {
        super(mc);
        this.a = a;
        this.b = b;
        this.c = c;
        this.A = A;
        this.B = B;
        this.C = C;
    }

    /**
     * @param mc
     * @param a  edge a
     * @param b  edge b
     * @param c  edge c
     */
    GTriangle(RealCalculator<T> mc, T a, T b, T c) {
        this(mc, a, b, c, null, null, null);
    }

    public T sideA() {
        return a;
    }

    public T sideB() {
        return b;
    }

    public T sideC() {
        return c;
    }

    public T angleA() {
        if (A == null) {
            A = computeAngle(a, b, c, B, C);
        }
        return A;
    }

    public T angleB() {
        if (B == null) {
            B = computeAngle(b, a, c, A, C);
        }
        return B;
    }

    public T angleC() {
        if (C == null) {
            C = computeAngle(c, a, b, A, B);
        }
        return C;
    }

    public T cosA() {
        if (A != null) {
            return getMc().cos(A);
        }
        return oppositeAngleCos(a, b, c, getMc());
    }

    public T cosB() {
        if (B != null) {
            return getMc().cos(B);
        }
        return oppositeAngleCos(b, a, c, getMc());
    }

    public T cosC() {
        if (C != null) {
            return getMc().cos(C);
        }
        return oppositeAngleCos(c, a, b, getMc());
    }

    /**
     * Rotates the triangle, returns a shape-equal triangle whose names of sides are different.
     * For example, a triangle:a=3,b=4,c=5 after rotate(true) is a triangle: a=5,b=3,c=4 and
     * after rotate(false) is a triangle: a=4,b=5,c=3.
     *
     * @param aToB
     * @return
     */
    public GTriangle<T> rotate(boolean aToB) {
        T na, nb, nc, nA, nB, nC;
        if (aToB) {
            na = c;
            nb = a;
            nc = b;
            nA = C;
            nB = A;
            nC = B;
        } else {
            na = b;
            nb = c;
            nc = a;
            nA = B;
            nB = C;
            nC = A;
        }
        GTriangle<T> ntri = new GTriangle<>(getMc(), na, nb, nc, nA, nB, nC);
        ntri.outerR = outerR;
        ntri.area = area;
        return ntri;
    }

    /**
     * Transform this triangle to a plane triangle, the coordinate of three vertexes are:
     * A(0,0) B(c,0) C(b*cosA,b*sinA)
     *
     * @return
     */
    public Triangle<T> toPlaneTriangle() {
        Point<T> A, B, C;
        A = Point.pointO(getMc());
        B = Point.valueOf(c, getMc().getZero(), getMc());
        T cosA = cosA();
        T sinA = getMc().squareRoot(getMc().subtract(getMc().getOne(), Calculators.square(cosA, getMc())));
        C = Point.valueOf(getMc().multiply(b, cosA), getMc().multiply(b, sinA), getMc());
        return Triangle.fromVertex(A, B, C);
    }

    /**
     * Transforms this triangle to a plane triangle. The vertex A of the returned triangle will
     * be the given pointA and the side AB will be in the identity direction of directAB.
     *
     * @param pointA
     * @param directAB
     * @return
     */
    public Triangle<T> toPlaneTriangle(Point<T> pointA, PVector<T> directAB) {
        Triangle<T> tri = toPlaneTriangle();
        return tri.transform(
                PAffineTrans.valueOf(
                        TransMatrix.rotateXAxisTo(directAB),
                        pointA.getVector()));
    }

    /**
     * Returns the circumference of this triangle: a+b+c.
     *
     * @return
     */
    public T circumference() {
        return getMc().add(a, getMc().add(b, c));
    }

    /**
     * Returns the area of this triangle.
     *
     * @return
     */
    public T area() {
        if (area == null) {
            if (A == null && B == null && C == null) {
                var mc = getMc();
                T p = mc.divideLong(circumference(), 2);
                T square = mc.product(Arrays.asList(
                        p, mc.subtract(p, a),
                        mc.subtract(p, b),
                        mc.subtract(p, c)));
                area = mc.squareRoot(square);
            } else {
                T r1, r2, angle;
                if (A != null) {
                    angle = A;
                    r1 = b;
                    r2 = c;
                } else if (B != null) {
                    angle = B;
                    r1 = a;
                    r2 = c;
                } else {
                    angle = C;
                    r1 = a;
                    r2 = b;
                }
                area = getMc().divideLong(getMc().multiply(getMc().multiply(r1, r2), getMc().sin(angle)), 2);
            }
        }
        return area;
    }

    /**
     * Computes the angle, either by using law of cosines or simply minus the angle.
     *
     * @param coside
     * @param sideA
     * @param sideB
     * @param angleA
     * @param angleB
     * @return
     */
    private T computeAngle(T coside, T sideA, T sideB, T angleA, T angleB) {
//        if(angleA == null || angleB == null){
        return getMc().arccos(oppositeAngleCos(coside, sideA, sideB, getMc()));
//        }else{
//            return mc.subtract(mc.constantValue(MathCalculator.STR_PI),mc.add(angleA,angleB));
//        }
    }


    @NotNull
    @Override
    public <N> MathObject<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
        GTriangle<N> tri = new GTriangle<>(newCalculator,
                mapper.apply(a),
                mapper.apply(b),
                mapper.apply(c));
        if (A != null) {
            tri.A = mapper.apply(A);
        }
        if (B != null) {
            tri.B = mapper.apply(B);
        }
        if (C != null) {
            tri.C = mapper.apply(C);
        }
        if (outerR != null) {
            tri.outerR = mapper.apply(outerR);
        }
        if (area != null) {
            tri.area = mapper.apply(area);
        }
        return tri;
    }

    /**
     * Determines whether the two triangles are equal in shape, regardless of the order of
     * their sides.
     *
     * @param tri
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean shapeEquals(GTriangle<T> tri) {
        return ArraySup.arrayEqualNoOrder((T[]) new Object[]{a, b, c}, (T[]) new Object[]{tri.a, tri.b, tri.c}, getMc()::isEqual);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GTriangle)) return false;
        if (!super.equals(o)) return false;

        GTriangle<?> gTriangle = (GTriangle<?>) o;

        if (!a.equals(gTriangle.a)) return false;
        if (!b.equals(gTriangle.b)) return false;
        if (!c.equals(gTriangle.c)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + a.hashCode();
        result = 31 * result + b.hashCode();
        result = 31 * result + c.hashCode();
        return result;
    }

    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (!(obj instanceof GTriangle)) {
            return false;
        }
        GTriangle<N> tri = (GTriangle<N>) obj;
        return getMc().isEqual(a, mapper.apply(tri.a)) &&
                getMc().isEqual(b, mapper.apply(tri.b)) &&
                getMc().isEqual(c, mapper.apply(tri.c));
    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (!(obj instanceof GTriangle)) {
            return false;
        }
        GTriangle<T> tri = (GTriangle<T>) obj;
        return getMc().isEqual(a, tri.a) &&
                getMc().isEqual(b, tri.b) &&
                getMc().isEqual(c, tri.c);
    }

    @Override
    public String toString(@NotNull FlexibleNumberFormatter<T> nf) {
        StringBuilder sb = new StringBuilder("Triangle:a=");
        sb.append(nf.format(a));
        sb.append(",b=").append(nf.format(b));
        sb.append(",c=").append(nf.format(c));
        return sb.toString();
    }

    /**
     * Check whether three sides of given lengths can compose a triangle.
     *
     * @param a
     * @param b
     * @param c
     * @param mc
     * @param <T>
     * @return
     */
    public static <T> boolean validSides(T a, T b, T c, RealCalculator<T> mc) {
        if (mc.compare(mc.add(a, b), c) <= 0) {
            return false;
        }
        if (mc.compare(mc.add(a, c), b) <= 0) {
            return false;
        }
        if (mc.compare(mc.add(b, c), a) <= 0) {
            return false;
        }
        return true;
    }

    /**
     * @param a
     * @param b
     * @param c
     * @param mc
     * @param <T>
     * @return
     */
    public static <T> GTriangle<T> sss(T a, T b, T c, RealCalculator<T> mc) {
        //firstly check the validation of sides a,b,c
        if (mc.isComparable()) {
            if (mc.compare(mc.add(a, b), c) <= 0) {
                throw new IllegalArgumentException("a+b<=c");
            }
            if (mc.compare(mc.add(a, c), b) <= 0) {
                throw new IllegalArgumentException("a+c<=b");
            }
            if (mc.compare(mc.add(b, c), a) <= 0) {
                throw new IllegalArgumentException("b+c<=a");
            }
        }
        return new GTriangle<>(mc, a, b, c);
    }

    /**
     * Creates a new triangle with an angle A and its side b and c.
     *
     * @param angleA
     * @param sideB
     * @param sideC
     * @param mc
     * @param <T>
     * @return
     */
    public static <T> GTriangle<T> sas(T angleA, T sideB, T sideC, RealCalculator<T> mc) {
        if (mc.isComparable()) {
            if (!Calculators.isPositive(sideB, mc)) {
                throw new IllegalArgumentException("b<=0");
            }
            if (!Calculators.isPositive(sideC, mc)) {
                throw new IllegalArgumentException("c<=0");
            }
            if (!Calculators.between(angleA, mc.getZero(), mc.constantValue(RealCalculator.STR_PI), mc)) {
                throw new IllegalArgumentException("A<=0 or A>=pi");
            }
        }
        T a = oppositeSide(sideB, sideC, angleA, mc);
        return new GTriangle<>(mc, a, sideB, sideC, angleA, null, null);
    }

    public static <T> T oppositeSide(T sideA, T sideB, T angleC, RealCalculator<T> mc) {
        return oppositeSideCos(sideA, sideB, mc.cos(angleC), mc);
    }

    static final ComputeExpression LAW_OF_COSINE = ComputeExpression.compile("$0^2+$1^2-2$0$1$2");

    public static <T> T oppositeSideCos(T sideA, T sideB, T cosC, RealCalculator<T> mc) {
        return mc.squareRoot(LAW_OF_COSINE.compute(mc, sideA, sideB, cosC));
    }

    /**
     * Computes the cosine value of angle A without checking the input a,b and c.
     *
     * @param a
     * @param b
     * @param c
     * @param mc
     * @param <T>
     * @return
     */
    public static <T> T oppositeAngleCos(T a, T b, T c, RealCalculator<T> mc) {
        //a²=b²+c²-2bcCos(A)
        T nume = mc.subtract(mc.add(Calculators.square(b, mc), Calculators.square(c, mc)), Calculators.square(a, mc));
        T deno = mc.multiplyLong(mc.multiply(b, c), 2l);
        T cos = mc.divide(nume, deno);
        return cos;
    }

    /**
     * Creates a triangle with a side and two angles on it.
     *
     * @param angleB
     * @param sideA
     * @param angleC
     * @param mc
     * @param <T>
     * @return
     */
    public static <T> GTriangle<T> asa(T angleB, T sideA, T angleC, RealCalculator<T> mc) {
        T angleA = mc.subtract(mc.constantValue(RealCalculator.STR_PI), mc.add(angleB, angleC));
        if (mc.isComparable()) {
            if (!Calculators.isPositive(sideA, mc)) {
                throw new IllegalArgumentException("a<=0");
            }
            if (!Calculators.isPositive(angleB, mc)) {
                throw new IllegalArgumentException("B<=0");
            }
            if (!Calculators.isPositive(angleC, mc)) {
                throw new IllegalArgumentException("C<=0");
            }
            if (!Calculators.isPositive(angleA, mc)) {
                throw new IllegalArgumentException("A<=0");
            }
        }
        T r_2 = mc.divide(sideA, mc.sin(angleA));
        T sideB = mc.multiply(r_2, mc.sin(angleB));
        T sideC = mc.multiply(r_2, mc.sin(angleC));
        T outerR = mc.divideLong(r_2, 2);
        GTriangle<T> tri = new GTriangle<>(mc, sideA, sideB, sideC, angleA, angleB, angleC);
        tri.outerR = outerR;
        return tri;
    }

    /**
     * Creates the triangle(s) with side A, side B and angle C. The list will contain 0,1 or 2 elements.
     *
     * @param sideA
     * @param sideB
     * @param angleA
     * @param mc
     * @param <T>
     * @return
     */
    public static <T> List<GTriangle<T>> ssaList(T sideA, T sideB, T angleA, RealCalculator<T> mc) {
        T h = mc.multiply(sideB, mc.sin(angleA));
        if (!mc.isComparable()) {
            return ssaTwo(sideA, sideB, angleA, h, mc);
        }
        checkValidSSA(sideA, sideB, angleA, mc);
        int comp = mc.compare(sideA, h);
        if (comp < 0) {
            return Collections.emptyList();
        }
        if (comp == 0) {
            var tri = ssaOneH(sideA, sideB, angleA, mc);
            return Collections.singletonList(tri);
        }
        comp = mc.compare(sideA, sideB);
        if (comp < 0) {
            // a<b => two triangles
            return ssaTwo(sideA, sideB, angleA, h, mc);
        }
        return Collections.singletonList(ssaOne(sideA, sideB, angleA, h, mc));
    }

    private static <T> List<GTriangle<T>> ssaTwo(T sideA, T sideB, T angleA, T h, RealCalculator<T> mc) {
        T bcos = mc.multiply(sideB, mc.cos(angleA));
        T acos = mc.squareRoot(mc.subtract(Calculators.square(sideA, mc), Calculators.square(h, mc)));
        T sideC1 = mc.add(bcos, acos);
        T sideC2 = mc.subtract(bcos, acos);
        var tri1 = new GTriangle<>(mc, sideA, sideB, sideC1);
        var tri2 = new GTriangle<>(mc, sideA, sideB, sideC2);
        tri1.A = angleA;
        tri2.A = angleA;
        var list = new ArrayList<GTriangle<T>>(2);
        list.add(tri1);
        list.add(tri2);
        return list;
    }

    private static <T> GTriangle<T> ssaOneH(T sideA, T sideB, T angleA, RealCalculator<T> mc) {
        T angleB = mc.divideLong(Calculators.pi(mc), 2);
        T sideC = mc.multiply(sideB, mc.cos(angleA));
        var tri = new GTriangle<>(mc, sideA, sideB, sideC);
        tri.B = angleB;
        tri.A = angleA;
        return tri;
    }

    private static <T> GTriangle<T> ssaOne(T sideA, T sideB, T angleA, T h, RealCalculator<T> mc) {
        T bcos = mc.multiply(sideB, mc.cos(angleA));
        T acos = mc.squareRoot(mc.subtract(Calculators.square(sideA, mc), Calculators.square(h, mc)));
        T sideC = mc.add(bcos, acos);
        var tri = new GTriangle<>(mc, sideA, sideB, sideC);
        tri.A = angleA;
        return tri;
    }


    private static <T> void checkValidSSA(T sideA, T sideB, T angleA, RealCalculator<T> mc) {
        if (!Calculators.isPositive(sideA, mc)) {
            throw new IllegalArgumentException("a<=0");
        }
        if (!Calculators.isPositive(sideB, mc)) {
            throw new IllegalArgumentException("b<=0");
        }
        if (!Calculators.between(angleA, mc.getZero(), mc.constantValue(RealCalculator.STR_PI), mc)) {
            throw new IllegalArgumentException("A<=0 or A>=pi");
        }
    }

    /**
     * Creates the triangle with side A, side B and angle C. If there are no or more than one triangles that
     * satisfy the parameters, an exception will be thrown. If the calculator doesn't supports compare, an exception
     * will be thrown.
     *
     * @param sideA
     * @param sideB
     * @param angleA
     * @param mc
     * @param <T>
     * @return
     */
    public static <T> GTriangle<T> ssa(T sideA, T sideB, T angleA, RealCalculator<T> mc) {
        if (!mc.isComparable()) {
            throw new IllegalArgumentException();
        }
        checkValidSSA(sideA, sideB, angleA, mc);
        T h = mc.multiply(sideB, mc.sin(angleA));
        int comp = mc.compare(sideA, h);
        if (comp < 0) {
            throw new IllegalArgumentException("No triangle.");
        }
        if (comp == 0) {
            return ssaOneH(sideA, sideB, angleA, mc);
        }
        comp = mc.compare(sideA, sideB);
        if (comp < 0) {
            // a<b => two triangles
            throw new IllegalArgumentException("Two triangles.");
        }
        return ssaOne(sideA, sideB, angleA, h, mc);
    }


}
