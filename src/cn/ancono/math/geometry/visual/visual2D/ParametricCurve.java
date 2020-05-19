package cn.ancono.math.geometry.visual.visual2D;

import cn.ancono.math.geometry.analytic.planeAG.curve.PlaneParametricCurve;

import java.awt.geom.Point2D;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

/*
 * Created at 2018/11/13 11:22
 * @author  liyicheng
 */
public interface ParametricCurve {
    Point2D.Double substitute(double t);

    double downerBound();

    double upperBound();

    static ParametricCurve from(PlaneParametricCurve<Double> p, double downerBound, double upperBound) {
        return new ParametricCurve() {
            @Override
            public Point2D.Double substitute(double t) {
                var point = p.substitute(t);
                return new Point2D.Double(point.x, point.y);
            }

            @Override
            public double downerBound() {
                return downerBound;
            }

            @Override
            public double upperBound() {
                return upperBound;
            }
        };
    }

    static ParametricCurve from(DoubleFunction<Point2D.Double> f, double downer, double upper) {
        return new ParametricCurve() {
            @Override
            public Point2D.Double substitute(double t) {
                return f.apply(t);
            }

            @Override
            public double downerBound() {
                return downer;
            }

            @Override
            public double upperBound() {
                return upper;
            }
        };
    }

    static ParametricCurve fromFunction(DoubleUnaryOperator f, double downer, double upper) {
        return new ParametricCurve() {
            @Override
            public Point2D.Double substitute(double t) {
                return new Point2D.Double(t, f.applyAsDouble(t));
            }

            @Override
            public double downerBound() {
                return downer;
            }

            @Override
            public double upperBound() {
                return upper;
            }
        };
    }
}

