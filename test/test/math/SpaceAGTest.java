package test.math;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.algebra.linear.LinearEquationSolution;
import cn.ancono.math.algebra.linear.Matrix;
import cn.ancono.math.algebra.linear.MatrixSup;
import cn.ancono.math.algebra.linear.Vector;
import cn.ancono.math.geometry.analytic.space.*;
import cn.ancono.math.numberModels.*;
import cn.ancono.math.geometry.analytic.space.SPoint.SPointGenerator;
import cn.ancono.math.geometry.analytic.space.SVector.SVectorGenerator;
import cn.ancono.math.geometry.analytic.space.shape.Cube;
import cn.ancono.math.geometry.analytic.space.shape.Tetrahedron;
import cn.ancono.utilities.ArraySup;

import java.util.Random;

import static cn.ancono.utilities.Printer.print;
@Deprecated
public class SpaceAGTest {
    private static final MathCalculator<Double> mc = Calculators.doubleCal();
//	private Map<String,Point<Double>> cube = new HashMap<>();
//	
//	{
//		cube.put("A", Point.valueOf(0d, 0d, 0d, mc));
//		cube.put("B", Point.valueOf(1d, 0d, 0d, mc));
//		cube.put("C", Point.valueOf(1d, 1d, 0d, mc));
//		cube.put("D", Point.valueOf(0d, 1d, 0d, mc));
//		cube.put("A1", Point.valueOf(0d, 0d, 1d, mc));
//		cube.put("B1", Point.valueOf(1d, 0d, 1d, mc));
//		cube.put("C1", Point.valueOf(1d, 1d, 1d, mc));
//		cube.put("D1", Point.valueOf(0d, 1d, 1d, mc));
//	}
	
	private Cube<Double> cb = Cube.unitCube(mc);
	
	
	public void test() {
        MathCalculator<Double> mc = Calculators.doubleCal();
        SVector<Double> sv1 = SVector.valueOf(1d, 1d, 0d, mc),
                sv2 = SVector.valueOf(0d, 1d, 1d, mc);
        print(sv1);
        print(sv2);
        print(sv1.add(sv2));
        print(sv1.innerProduct(sv2));
        print(sv1.angle(sv2, Math::acos) / Math.PI);
    }
	
	
	public void test2(){
		//xb: 199.5
		SPoint<Double> B1 = cb.getVertex("B1"),
				D = cb.getVertex("D"),
				C = cb.getVertex("C"),
				D1= cb.getVertex("D1");
		Line<Double> B1D = Line.twoPoints(B1, D),
				CD1 = Line.twoPoints(C, D1);
		print(B1D);
		print(CD1);
		print(B1D.angleCos(CD1));
		Plane<Double> p = Plane.threePoints(cb.getVertex("A"), D1, C);
		print(p.angleSin(B1D));
		print(p.distanceSq(D));
	}

//	public void test3(){
//		FormulaCalculator fc = FormulaCalculator.getCalculator();
//		// MathCalculator<PolynomialOld> mcp = PolynomialOld
//		PolynomialOld[] pos = new PolynomialOld[9];
//		pos[0] = new PolynomialOld(fc, Formula.valueOf("d[x1]"));
//		pos[1] = new PolynomialOld(fc, Formula.valueOf("d[x2]"));
//		pos[2] = new PolynomialOld(fc, Formula.valueOf("d[x3]"));
//		pos[3] = new PolynomialOld(fc, Formula.valueOf("d[y1]"));
//		pos[4] = new PolynomialOld(fc, Formula.valueOf("d[y2]"));
//		pos[5] = new PolynomialOld(fc, Formula.valueOf("d[y3]"));
//		pos[6] = new PolynomialOld(fc, Formula.valueOf("d[z1]"));
//		pos[7] = new PolynomialOld(fc, Formula.valueOf("d[z2]"));
//		pos[8] = new PolynomialOld(fc, Formula.valueOf("d[z3]"));
//		FracPoly[] fps = new FracPoly[9];
//		for (int i = 0; i < 9; i++) {
//			fps[i] = FracPoly.valueOf(pos[i]);
//		}
//		MathCalculator<FracPoly> mcfp = FracPoly.getCalculator();
//		SPoint<FracPoly> pa = SPoint.valueOf(fps[0], fps[3], fps[6], mcfp);
//		SPoint<FracPoly> pb = SPoint.valueOf(fps[1], fps[4], fps[7], mcfp);
//		SPoint<FracPoly> pc = SPoint.valueOf(fps[2], fps[5], fps[8], mcfp);
//		SVector<FracPoly> v1 = SVector.vector(pa, pb);
//		SVector<FracPoly> v2 = SVector.vector(pa, pc);
//		FracPoly re1 = v1.outerProduct(v2).calLengthSq();
//
//		SVector<FracPoly> vat = pa.getVector(),
//				vbt = pb.getVector(),
//				vct = pc.getVector();
//		SVector<FracPoly> va = vat.outerProduct(vbt),
//				vb = vbt.outerProduct(vct),
//				vc = vct.outerProduct(vat);
//		FracPoly f = mcfp.addX(va.calLengthSq(),vb.calLengthSq(),vc.calLengthSq());
//		FracPoly f2 = mcfp.multiplyLong(mcfp.addX(va.innerProduct(vb),vb.innerProduct(vc),vc.innerProduct(va)), 2l);
//		FracPoly re2 = mcfp.add(f, f2);
//		print(mcfp.isEqual(re1, re2));
//		dealWith(re1.toString());
//		dealWith(re2.toString());
//		dealWith(va.innerProduct(vb).toString());
//	}
//
//	private void dealWith(String str) {
//		print(str.replaceAll("d\\[(\\w+)]", "$1"));
//	}
//	Pattern pattern = Pattern.compile("d\\[(\\w+)]");
//	private void dealWith(FracPoly fp) {
//		print(pattern.matcher(fp.toString()).replaceAll("$1"));
//	}
//
//	public void test4(){
//		SPoint<Double> A = cb.getVertex("A"),
//				C = cb.getVertex("C"),
//				C1 = cb.getVertex("C1"),
//				B1 = cb.getVertex("B1"),
//				A1 = cb.getVertex("A1");
//		SPoint<Double> M = C1.middle(C);
//		Line<Double> l1 = Line.twoPoints(A, M),
//				l2 = Line.twoPoints(A1, B1);
//		print(l1.angleCos(l2));
//		print(l1.distanceSq(l2));
//	}
//
//	public void triangleTest(){
//		SPointGenerator<Double> g = new SPointGenerator<>(Calculators.getCalculatorDouble());
//		SPoint<Double> p = g.of(1d, 1d, 1d);
//		@SuppressWarnings("unchecked")
//		SPoint<Double>[] ps = new SPoint[]{
//			g.of(0d,1d,0d),g.of(1d, 1d, 0d),g.of(0d,0d,0d)
//		};
//		List<STriangle<Double>> list = STriangle.prismSurfaces(p, ps);
//		list.forEach(c -> print(c));
////		assertEquals(list.get(0).getA(), ps[0]);
//
//	}
//
//	public void proveCenterI(){
//		//failed ...
//		FormulaCalculator fc = FormulaCalculator.getCalculator();
//		// MathCalculator<PolynomialOld> mcp = PolynomialOld
//		PolynomialOld[] pos = new PolynomialOld[12];
//		pos[0] = new PolynomialOld(fc, Formula.valueOf("d[x1]"));
//		pos[1] = new PolynomialOld(fc, Formula.valueOf("d[x2]"));
//		pos[2] = new PolynomialOld(fc, Formula.valueOf("d[x3]"));
//		pos[3] = new PolynomialOld(fc, Formula.valueOf("d[y1]"));
//		pos[4] = new PolynomialOld(fc, Formula.valueOf("d[y2]"));
//		pos[5] = new PolynomialOld(fc, Formula.valueOf("d[y3]"));
//		pos[6] = new PolynomialOld(fc, Formula.valueOf("d[z1]"));
//		pos[7] = new PolynomialOld(fc, Formula.valueOf("d[z2]"));
//		pos[8] = new PolynomialOld(fc, Formula.valueOf("d[z3]"));
//		pos[9] = new PolynomialOld(fc, Formula.valueOf("d[x4]"));
//		pos[10] = new PolynomialOld(fc, Formula.valueOf("d[y4]"));
//		pos[11] = new PolynomialOld(fc, Formula.valueOf("d[z4]"));
//		FracPoly[] fps = new FracPoly[pos.length];
//		for (int i = 0; i < fps.length; i++) {
//			fps[i] = FracPoly.valueOf(pos[i]);
//		}
//		MathCalculator<FracPoly> mcfp = FracPoly.getCalculator();
//		SPoint<FracPoly> pa = SPoint.valueOf(fps[0], fps[3], fps[6], mcfp);
//		SPoint<FracPoly> pb = SPoint.valueOf(fps[1], fps[4], fps[7], mcfp);
//		SPoint<FracPoly> pc = SPoint.valueOf(fps[2], fps[5], fps[8], mcfp);
//		SPoint<FracPoly> pp = SPoint.valueOf(fps[9], fps[10], fps[11], mcfp);
//		Tetrahedron<FracPoly> tetra = Tetrahedron.fourPoints(pp,pa, pb, pc);
//		FracPoly[] ts = new FracPoly[10];
//		@SuppressWarnings("unchecked")
//		SVector<FracPoly>[] vs = (SVector<FracPoly>[]) new SVector<?>[10];
//		ts[0] = tetra.getBottom().areaSq();
//		ts[1] = tetra.getBottom().areaSq();
//		ts[2] = tetra.getBottom().areaSq();
//		ts[3] = tetra.getBottom().areaSq();
//		vs[0] = tetra.getBottom().getEdgeA().getDirectVector();
//		vs[1] = tetra.getBottom().getEdgeB().getDirectVector();
//		print("Area for one triangle");
//		dealWith(ts[0]);
//		print("An outer product");
//		dealWith((vs[3]=vs[0].outerProduct(vs[1])).toString());
//		dealWith(vs[3].calLengthSq());
//	}
	
	
	public void proveCenterI2() {
        SPointGenerator<Double> g = new SPointGenerator<>(Calculators.doubleCal());
        MathCalculator<Double> mc = Calculators.doubleCal();
        Random rd = new Random();
        @SuppressWarnings("unchecked")
        SPoint<Double>[] ps = (SPoint<Double>[]) new SPoint<?>[4];
        for (int i = 0; i < ps.length; i++) {
            ps[i] = g.of(rd.nextDouble(), rd.nextDouble(), rd.nextDouble());
        }
        Tetrahedron<Double> te = Tetrahedron.fourPoints(ps[0], ps[1], ps[2], ps[3]);
        print(te);
        double s = te.surfaceArea();
        double left = te.radiusI();
		double x = te.getBottom().area() * ps[0].getX() + te.getSideF2().area() * ps[1].getX()
				+ te.getSideF3().area() * ps[2].getX() + te.getSideF1().area() * ps[3].getX();
		double y = te.getBottom().area() * ps[0].getY() + te.getSideF2().area() * ps[1].getY()
				+ te.getSideF3().area() * ps[2].getY() + te.getSideF1().area() * ps[3].getY();
		double z = te.getBottom().area() * ps[0].getZ() + te.getSideF2().area() * ps[1].getZ()
				+ te.getSideF3().area() * ps[2].getZ() + te.getSideF1().area() * ps[3].getZ();
		x /= s;
		y /= s;
		z /= s;
		SPoint<Double> center = SPoint.valueOf(x, y, z, mc);
		double right = te.getBottom().getPlane().distance(center);
		print(left,7);
		print(right,7);
//		print(te.radiusI(),7);
	}
	
	
	public void studyHCenter() {
        SPointGenerator<Double> g = new SPointGenerator<>(Calculators.doubleCal());
        MathCalculator<Double> mc = Calculators.doubleCal();
        Random rd = new Random();
        @SuppressWarnings("unchecked")
        SPoint<Double>[] ps = (SPoint<Double>[]) new SPoint<?>[4];
        for (int i = 0; i < ps.length; i++) {
            ps[i] = g.of(rd.nextDouble(), rd.nextDouble(), rd.nextDouble());
        }
        Tetrahedron<Double> te = Tetrahedron.fourPoints(ps[0], ps[1], ps[2], ps[3]);
        Plane<Double> p1, p2, p3;
        print(te);
        p1 = te.getBottom().getPlane().perpendicular(te.getSideF1().getEdgeA().getLine());
		p2 = te.getBottom().getPlane().perpendicular(te.getSideF2().getEdgeA().getLine());
		p3 = te.getBottom().getPlane().perpendicular(te.getSideF3().getEdgeA().getLine());
		print(p1.intersectPoint(p2.intersectLine(p3)));
	}
	
	
	public void studyVertexAngle(){
        MathCalculator<Double> mc = Calculators.doubleDev();
        SVectorGenerator<Double> g = new SVectorGenerator<>(mc);
        Random rd = new Random();
        @SuppressWarnings("unchecked")
        SVector<Double>[] ps = (SVector<Double>[]) new SVector<?>[4];
        for (int i = 0; i < ps.length; i++) {
            ps[i] = g.of(rd.nextDouble(), rd.nextDouble(), rd.nextDouble());
        }
        double theta = rd.nextDouble() * Math.PI;
        print(theta, 14);
        print(SVector.angledVector(ps[0], ps[1], Math.tan(theta)).angle(ps[0], Math::acos));
		Plane<Double> p1 = Plane.vectorPoint(ps[0], ps[1], SPoint.pointO(mc));
		Line<Double> l = Line.pointDirect(SPoint.pointO(mc),ps[0]);
		Plane<Double> p2 = Plane.anglePlane(p1, l, Math.tan(theta));
		print(p1.getNormalVector().angle(p2.getNormalVector(), Math::acos));
//		print(theta,14);
//		print(ps[3].innerProduct(ps[1]));
//		double angle = ps[3].angle(ps[0], Math::acos);
//		print(angle,14);
//		print(mc.isEqual(theta, angle));
//		print(SVector.mixedProduct(ps[3], ps[1], ps[0]));
	}
	
//	@Test
	public void testMatrix() {
        MathCalculator<Double> mc = Calculators.doubleDev();
        final int row = 10;
        for (int n = 0; n < 1000; n++) {
            double[][] mat = new double[row][];
            for (int i = 0; i < mat.length; i++) {
                mat[i] = ArraySup.ranDoubleArrNe(row + 1, 100);
            }
            Matrix<Double> matrix = Matrix.of(mat);
            LinearEquationSolution<Double> so = MatrixSup.solveLinearEquation(matrix);
            Vector<Double> x = so.getSpecialSolution();
            Matrix<Double> cofactor = matrix.subMatrix(0, 0, row - 1, row - 1);
            Vector<Double> v = Vector.column(matrix, row);
            Vector<Double> v1 = Vector.column(Matrix.multiply(cofactor, x), 0);
            v = v.mapTo(d -> d, mc);
            v1 = v1.mapTo(d -> d, mc);
            if (!v1.valueEquals(v)) {
                print("WRONG?---");
                matrix.printMatrix();
                print(v1);
                print(v);
                print("======");
            }
        }
	}
}
