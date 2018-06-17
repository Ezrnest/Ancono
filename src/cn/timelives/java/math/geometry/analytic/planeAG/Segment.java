package cn.timelives.java.math.geometry.analytic.planeAG;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.numberModels.*;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.AbstractPlaneCurve;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.SubstituableCurve;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.numberModels.api.Simplifiable;
import cn.timelives.java.math.numberModels.api.Simplifier;

import java.util.Objects;
import java.util.function.Function;

/**
 * A segment consists of two points namely {@code A} and {@code B}.
 * @param <T>
 */
public final class Segment<T> extends AbstractPlaneCurve<T> implements Simplifiable<T,Segment<T>>,SubstituableCurve<T> {
    final Line<T> line;
    final Point<T> A,B;
    final PVector<T> v;
    //determines whether the x coordinate of the direct vector is zero
    final boolean xZero;
    Segment(Line<T> line, Point<T> A, Point<T> B,PVector<T> v, MathCalculator<T> mc){
        super(mc);
        this.line = line;
        this.A = A;
        this.B = B;
        this.v = v;
        xZero = mc.isZero(v.x);
    }

    @Override
    public <N> AbstractPlaneCurve<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
        var nline = line.mapTo(mapper,newCalculator);
        var nA = A.mapTo(mapper,newCalculator);
        var nB = B.mapTo(mapper,newCalculator);
        var nv = v.mapTo(mapper,newCalculator);
        return new Segment<>(nline,nA,nB,nv,newCalculator);
    }

    @Override
    public boolean valueEquals(MathObject<T> obj) {
        if(!(obj instanceof Segment)){
            return false;
        }
        Segment<T> seg = (Segment<T>)obj;
        return line.valueEquals(seg.line) && A.valueEquals(seg.A) && B.valueEquals(seg.B);
    }

    @Override
    public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
        if(!(obj instanceof Segment)){
            return false;
        }
        Segment<N> seg = (Segment<N>)obj;
        return line.valueEquals(seg.line,mapper)
                && A.valueEquals(seg.A,mapper)
                && B.valueEquals(seg.B,mapper);
    }

    @Override
    public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
        return "Segment:A"+A.toString(nf)+"-B"+B.toString(nf);
    }

    @Override
    public boolean contains(Point<T> p) {
        if(!line.contains(p)){
            return false;
        }
        if(xZero){
            return MathUtils.oppositeSide(A.y,B.y,p.y,mc);
        }
        return MathUtils.oppositeSide(A.x,B.x,p.x,mc);
    }

    @Override
    public AbstractPlaneCurve<T> transform(PAffineTrans<T> trans) {
        return super.transform(trans);
    }

    /**
     * Gets the line of this segment.
     * @return the line
     */
    public Line<T> getLine() {
        return line;
    }

    /**
     * Gets the endpoint A.
     * @return A
     */
    public Point<T> getA() {
        return A;
    }

    /**
     * Gets the endpoint B.
     * @return B
     */
    public Point<T> getB() {
        return B;
    }


    /**
     * Returns the length of this segment.
     * @return
     */
    public T length(){
        return v.calLength();
    }

    /**
     * Returns the square of the length.
     * @return
     */
    public T lengthSq(){
        return v.calLengthSq();
    }

    @Override
    public Segment<T> simplify() {
        var nline = line.simplify();
        return new Segment<>(nline,A,B,v,mc);
    }

    @Override
    public Segment<T> simplify(Simplifier<T> sim) {
        var nline = line.simplify(sim);
        return new Segment<>(nline,A,B,v,mc);
    }


    /**
     * Returns the square of the distance of the point as the substituting result.
     * @param x
     * @param y
     * @return
     */
    @Override
    public T substitute(T x, T y) {
        return distanceSq(x,y);
    }

    /**
     * Returns the minimum of the distance of a point on this segment and the point {@code p}.
     * @return
     */
    public T distance(Point<T> p){
        return mc.squareRoot(distanceSq(p));
    }
    /**
     * Returns the square of {@code distance(p)}
     * @return
     */
    public T distanceSq(Point<T> p){
        Point<T> protection = line.projection(p);
        if(contains(protection)){
            return line.distanceSq(p);
        }
        T d1 = A.distanceSq(p),
                d2 = B.distanceSq(p);
        return Utils.min(d1,d2,mc);
    }

    public T distanceSq(T x,T y){
        return distanceSq(Point.valueOf(x,y,mc));
    }

    public T distance(T x,T y){
        return distance(Point.valueOf(x,y,mc));
    }


    /**
     * Creates a segment whose endpoints are A and B(the order is considered).
     * @param A
     * @param B
     * @param <T>
     * @return
     */
    public static <T> Segment<T> twoPoints(Point<T> A,Point<T> B){
        if(A.valueEquals(B)){
            throw new IllegalArgumentException("A==B");
        }
        MathCalculator<T> mc = A.getMathCalculator();
        Line<T> line = Line.twoPoint(A,B,mc);
        PVector<T> v = A.directVector(B);
        return new Segment<>(line,A,B,v,mc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Segment<?> segment = (Segment<?>) o;
        return Objects.equals(A, segment.A) &&
                Objects.equals(B, segment.B);
    }

    @Override
    public int hashCode() {
        return Objects.hash(A, B);
    }



//    public static void main(String[] args){
//        MathCalculator<Double> mc = Calculators.getCalculatorDoubleDev();
//        var A = Point.valueOf(0d,0d,mc);
//        var B = Point.valueOf(1d,0d,mc);
//        var P = Point.valueOf(2d,1d,mc);
//        var AB = twoPoints(A,B);
//        print(AB.distanceSq(P));
//    }
}
