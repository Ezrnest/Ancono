package test.math.planeAg;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.Progression;
import cn.timelives.java.math.ProgressionSup;
import cn.timelives.java.math.equation.SVPEquation.QEquation;
import cn.timelives.java.math.linearAlgebra.LinearEquationSolution;
import cn.timelives.java.math.linearAlgebra.Matrix;
import cn.timelives.java.math.linearAlgebra.MatrixSup;
import cn.timelives.java.math.linearAlgebra.Vector;
import cn.timelives.java.math.numberModels.*;
import cn.timelives.java.math.numberModels.expression.ExprCalculator;
import cn.timelives.java.math.numberModels.expression.Expression;
import cn.timelives.java.math.numberModels.expression.Node;
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies;
import cn.timelives.java.math.planeAG.*;
import cn.timelives.java.math.planeAG.curve.EllipseV;
import cn.timelives.java.math.planeAG.curve.GeneralConicSection;
import cn.timelives.java.math.planeAG.curve.HyperbolaV;
import cn.timelives.java.utilities.ArraySup;
import cn.timelives.java.utilities.EasyConsole;
import cn.timelives.java.utilities.ModelPatterns;
import cn.timelives.java.utilities.Printer;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.print_;
import static org.junit.Assert.*;
import static test.math.TestUtils.isZero;

public class CodePlace {

	// public static void main(String[] args) {
	// CodePlace cp = new CodePlace();
	// cp.m39();
	// }

	public void m1() {
		MathCalculator<Fraction> mc = Fraction.getCalculator();
		// = Line.twoPoint(
		// Fraction.valueOf(3),Fraction.valueOf(4), Fraction.ZERO,Fraction.ZERO,
		// mc);
		// print(l1);
		// information participation
		Line<Fraction> l1;
		Scanner scn = new Scanner(System.in);
		Pattern pt = Pattern.compile("[\\+\\-]?\\d+(\\/\\d+)?");
		Point<Fraction> p1 = new Point<Fraction>(mc, Fraction.valueOf(scn.next(pt)), Fraction.valueOf(scn.next(pt)));
		Point<Fraction> p2 = new Point<Fraction>(mc, Fraction.valueOf(scn.next(pt)), Fraction.valueOf(scn.next(pt)));
		Point<Fraction> p3 = new Point<Fraction>(mc, Fraction.valueOf(scn.next(pt)), Fraction.valueOf(scn.next(pt)));
		scn.close();
		l1 = Line.twoPoint(p1, p2, mc);
		print(l1);
		l1 = Line.twoPoint(p2, p3, mc);
		print(l1);
		l1 = Line.twoPoint(p1, p3, mc);
		print(l1);
	}

	public void m2() {
		MathCalculator<Fraction> mc = Fraction.getCalculator();
		MathCalculator<Long> mct = Calculators.getCalculatorLong();
		Line<Fraction> l1 = Line.generalFormula(1L, -2L, 4L, mct).mapTo(Fraction::valueOf, mc);
		Line<Fraction> l2 = Line.generalFormula(1L, 1L, -2L, mct).mapTo(Fraction::valueOf, mc);
		Point<Fraction> pin = l1.intersectPoint(l2);
		print(pin);
		Line<Fraction> lr = Line.twoPoint(pin, new Point<Long>(mct, 2L, -1L).mapTo(Fraction::valueOf, mc), mc);
		print(lr);
		Line<Fraction> lr2 = Line.generalFormula(3L, -4L, 5L, mct).mapTo(Fraction::valueOf, mc).perpendicular(pin);
		print(lr2);
	}

	MathCalculator<Fraction> mc = Fraction.getCalculator();
	MathCalculator<Long> mct = Calculators.getCalculatorLong();
	MathCalculator<Double> mcd = Calculators.getCalculatorDoubleDev();

	public void m3() {

		Point<Fraction> p = new Point<>(mct, 2l, 3l).mapTo(Fraction::valueOf, mc);
		Line<Fraction> l = Line.generalFormula(2l, -1l, -4l, mct).mapTo(Fraction::valueOf, mc);
		print(l);
		print(l.symmetryPoint(p));
		Line<Fraction> pen = l.perpendicular(p);
		print(pen);
		Point<Fraction> inter = l.intersectPoint(pen);
		print(inter);
		print(Matrix.minusMatrix(inter.getVector().multiplyNumber(2), p.getVector()));
	}

	void m4() {
		Point<Fraction> a = new Point<>(mct, 1l, 4l).mapTo(Fraction::valueOf, mc);
		// Point<Fraction> b = new Point<>(mct,-5l,2l).mapTo(Fraction::valueOf,
		// mc);
		Line<Fraction> l = Line.generalFormula(1l, -2l, 0l, mct).mapTo(Fraction::valueOf, mc);
		Line<Fraction> l2 = Line.generalFormula(1l, 1l, -1l, mct).mapTo(Fraction::valueOf, mc);
		Point<Fraction> a1 = l.symmetryPoint(a);
		Point<Fraction> a2 = l2.symmetryPoint(a);
		print(Line.twoPoint(a1, a2, mc));
	}
//	@Test
	public void m5() {
		Point<Fraction> a = new Point<>(mct, -7l, 1l).mapTo(Fraction::valueOf, mc);
		Point<Fraction> b = new Point<>(mct, -5l, 5l).mapTo(Fraction::valueOf, mc);
		Line<Fraction> l = Line.generalFormula(2l, -1l, -5l, mct).mapTo(Fraction::valueOf, mc);
		b = l.symmetryPoint(b);
		print(b);
		Line<Fraction> l2 = Line.twoPoint(a, b, mc);
		print(l.intersectPoint(l2));
	}

	Point<Fraction> of(int a, int b) {
		return new Point<Fraction>(mc, Fraction.valueOf(a), Fraction.valueOf(b));
	}

	Line<Fraction> of(int a, int b, int c) {
		return Line.generalFormula(Fraction.valueOf(a), Fraction.valueOf(b), Fraction.valueOf(c), mc);
	}

	Line<Fraction> sim(Line<Fraction> l) {
		return LineSup.simplify(l, Fraction.getFractionSimplifier());
	}

	FieldMathObject<Fraction> map(FieldMathObject<Long> obj) {
		return obj.mapTo(Fraction::valueOf, mc);
	}

	/*
	 * values here
	 */
	Point<Fraction> a, b, c, d, e, f, g, m, n, o, p;
	Line<Fraction> l1, l2, l3, l4, l5, l6, l;
	
	public void m6() {
		l1 = Line.parallelX(Fraction.ZERO, mc);
		a = of(5, 2);
		b = l1.symmetryPoint(a);
		l2 = of(1, -1, 0);
		c = l2.symmetryPoint(b);
		print(of(10, 9).distanceSq(c));
	}
//	@Test
	public void m7() {
		l1 = of(1,-2,-2);
		l2 = of(2,-1,-4);
		print(l1.symmetryLine(l2));
		print(l2.symmetryPoint(of(2,0)));
		print(l2.symmetryPoint(of(0,-1)));
	}
	
	public void m8() {
		l1 = of(1,-2,1);
		l2 = of(1,0,-1);
		print(l1.symmetryLine(l2));

	}

	void m9() {
		a = of(1, 3);
		l1 = of(1, -2, 1);
		print(l1.perpendicular(a));
		print(Line.twoPoint(of(-3, -1), a, mc));
		print(Line.twoPoint(of(-3, -1), of(2, 1), mc));
	}

	void m10() {
		l1 = of(3, 4, -7);
		l2 = of(3, 4, 3);
		Fraction tan = Fraction.valueOf(2, 1);
		p = of(2, 3);
		print(l1.rotateAngle(p, tan));
		print(l1.rotateAngle(p, tan.negative()));
	}

	void m11() {
		Progression<Long> pro = ProgressionSup.asFirstElementAndDifferece(1L, 5L, mct);
		pro.limit(10).forEach(l -> print(l));
		print_();
		pro.limit(10).stream().map(l -> Long.toString(l).substring(0, 1)).forEach(Printer::print);

		Long result = pro.limit(100).stream().parallel().map(l -> l - 1)
				///
				.reduce(0L, (sum, a) -> sum + a);

		print(result);
		print(pro.sumOf(0, 100));
	}

	void m12() {
		Triangle<Fraction> tri = Triangle.fromVertex(mct, 0l, 3l, -2l, -1l, 3l, 2l).mapTo(Fraction::valueOf, mc);
		print(tri.centerG());
		print(tri.centerO());
		print(tri.centerH());
		print_();
		l1 = tri.perpendicularBisectorA();
		l2 = tri.perpendicularBisectorB();
		print(l1.intersectPoint(l2));
		Line<Fraction> l1 = tri.altitudeC();
		Line<Fraction> l2 = tri.altitudeB();
		print(l1.intersectPoint(l2));
	}

	void m() {
		Pattern pointP = Pattern
				.compile(" *\\( *(" + Fraction.EXPRESSION_PATTERN + ") *, *(" + Fraction.EXPRESSION_PATTERN + ") *\\)");
		print(pointP);
		EasyConsole con = EasyConsole.getSwingImpl();
		con.open();
		con.setLineFilter(EasyConsole.COMMENT_PATTERN);
		PrintWriter pw = con.getOutput();
		String line;
		while (true) {
			pw.println("Please choose mode:Triangle,");
			line = con.nextLine();
			if (line.equals("^")) {
				break;
			}
			if (line.contains("tri")) {
				while (true) {
					pw.println("Input point A,B,C : (x,y)");
					List<Point<Fraction>> abc = con.nextObjects(pointP,
							mat -> new Point<>(mc, Fraction.valueOf(mat.group(1)), Fraction.valueOf(mat.group(3))), 3);
					Triangle<Fraction> tri = Triangle.fromVertex(mc, abc.get(0), abc.get(1), abc.get(2));
					while (true) {
						pw.println("Center of : G H O");
						line = con.nextLine();
						if (line.equals("^")) {
							break;
						}
						if (line.contains("G")) {
							pw.println(tri.centerG());
						} else if (line.contains("H")) {
							pw.println(tri.centerH());
						} else if (line.contains("O")) {
							pw.println(tri.centerO());
						}
					}
				}
			}
		}
	}

	void m14() {
		FormulaCalculator fc = FormulaCalculator.getCalculator();
		// MathCalculator<Polynomial> mcp = Polynomial
		Polynomial[] pos = new Polynomial[6];
		pos[0] = new Polynomial(fc, Formula.valueOf("d[x1]"));
		pos[1] = new Polynomial(fc, Formula.valueOf("d[x2]"));
		pos[2] = new Polynomial(fc, Formula.valueOf("d[x3]"));
		pos[3] = new Polynomial(fc, Formula.valueOf("d[y1]"));
		pos[4] = new Polynomial(fc, Formula.valueOf("d[y2]"));
		pos[5] = new Polynomial(fc, Formula.valueOf("d[y3]"));
		FracPoly[] fps = new FracPoly[6];
		for (int i = 0; i < 6; i++) {
			fps[i] = FracPoly.valueOf(pos[i]);
		}
		MathCalculator<FracPoly> mcfp = FracPoly.getCalculator();
		Point<FracPoly> pa = new Point<>(mcfp, fps[0], fps[1]);
		Point<FracPoly> pb = new Point<>(mcfp, fps[2], fps[3]);
		Point<FracPoly> pc = new Point<>(mcfp, fps[4], fps[5]);
		Triangle<FracPoly> tri = Triangle.fromVertex(mcfp, pa, pb, pc);
		// print(dealWith(tri.centerG().toString()));
		// print(dealWith(tri.centerH().toString()));
		ps[0] = tri.centerO();
		print(dealWith(ps[0].toString()));
		// print(dealWith(ps[0].distanceSq(pa).toString()));

	}

	FormulaCalculator fc = FormulaCalculator.getCalculator();
	MathCalculator<Polynomial> fcp = Polynomial.getCalculator();
	MathCalculator<FracPoly> mcfp = FracPoly.getCalculator(false);

	final int def_size = 32;
	Polynomial[] pos = new Polynomial[def_size];
	FracPoly[] fps = new FracPoly[def_size];
	Expression[] es = new Expression[def_size];
	@SuppressWarnings("unchecked")
	Point<FracPoly>[] ps = new Point[def_size];

	void compute() {
		for (int i = 0; i < pos.length; i++) {
			if (pos[i] != null)
				fps[i] = FracPoly.valueOf(pos[i]);
		}
	}

	void computeExpression() {
		for (int i = 0; i < pos.length; i++) {
			if (pos[i] != null) {
				es[i] = Expression.fromPolynomial(pos[i]);
			}
		}
	}

	void m15() {

		// Polynomial[] pos = new Polynomial[10];
		pos[0] = new Polynomial(fc, Formula.ZERO);
		pos[1] = new Polynomial(fc, Formula.ONE);
		pos[2] = new Polynomial(fc, Formula.valueOf("0.5"));
		pos[3] = new Polynomial(fc, Formula.valueOf("a"));
		pos[4] = new Polynomial(fc, Formula.valueOf("aa"));
		pos[5] = new Polynomial(fc, Formula.valueOf("k"));
		pos[7] = new Polynomial(fc, "a/3+1/3");
		print(pos[7]);
		pos[8] = new Polynomial(fc, "a^2/3");
		compute();
		fps[6] = mcfp.subtract(FracPoly.ONE, fps[5]);
		Point<FracPoly> A = new Point<>(mcfp, fps[1], fps[1]);
		Point<FracPoly> B = new Point<>(mcfp, fps[0], mcfp.negate(fps[1]));
		Point<FracPoly> D = new Point<>(mcfp, fps[2], fps[0]);
		Point<FracPoly> C = new Point<>(mcfp, fps[3], fps[4]);
		Point<FracPoly> E = A.proportionPoint(C, fps[5]);
		Point<FracPoly> F = B.proportionPoint(C, fps[6]);
		Line<FracPoly> ef = Line.twoPoint(E, F, mcfp);
		Line<FracPoly> cd = Line.twoPoint(C, D, mcfp);
		A = ef.intersectPoint(cd);
		print(A);
		print_();
		FracPoly fpx = A.x;
		print(fcp.subtract(fpx.getNume(), fcp.multiply(fpx.getDeno(), pos[7])));
		fpx = A.y;
		print(fcp.subtract(fpx.getNume(), fcp.multiply(fpx.getDeno(), pos[8])));
		print(pos[7]);
		print(pos[8]);
	}

	private String dealWith(String str) {
		return str.replaceAll("d\\[(\\w+)]", "$1");
	}

	private String dealWith(Object fp) {
		return fp.toString().replaceAll("d\\[(\\w+)]", "$1");
	}

	String dealWith(Expression expr) {
		expr = ec.simplify(expr);
		return expr.toString().replaceAll("d\\[(\\w+)]", "$1");
	}

	void m16() {
		a = of(2, 1);
		b = of(0, -2);
		c = a.middle(b);
		l1 = Line.twoPoint(a, b, mc);
		print(sim(l1.perpendicular(c)));

	}

	void inputPoly(String... data) {
		for (int i = 0; i < data.length; i++) {
			pos[i] = new Polynomial(fc, data[i]);
		}
		compute();
	}

	void inputPoly(String datas) {
		String[] ss = datas.split(",");
		inputPoly(ss);
	}

	void inputExpression(String datas) {
		String[] ss = datas.split(",");
		inputPoly(ss);
		computeExpression();
	}

	void m17() {
		inputPoly("0", "3", "b", "b+8");
		compute();
		ps[0] = new Point<>(mcfp, fps[0], fps[1]);
		ps[1] = new Point<>(mcfp, fps[2], fps[0]);
		ps[2] = new Point<>(mcfp, fps[3], fps[0]);
		Triangle<FracPoly> tri = Triangle.fromVertex(mcfp, ps[0], ps[1], ps[2]);
		print(tri.centerO());
	}

	void m18() {
		inputPoly("0", "2", "2");
		compute();
		Circle<FracPoly> cr = Circle.centerAndRadius(new Point<>(mcfp, fps[0], fps[0]), fps[1], mcfp);
		print(cr);
		Line<FracPoly> l = Line.slopeIntercept(fps[2], fps[0], mcfp);
		print(l);
		print(cr.intersectPoints(l));

	}

	void m19() {
		MathCalculator<Double> cal = Calculators.getCalculatorDouble();
		Circle<Double> cr = Circle.generalFormula(0d, -12d, 27d, cal);
		Point<Double> p = new Point<>(cal, 0d, 0d);
		Line<Double> l = cr.radicalAxis(p);
		print(cr.getDiameter());
		print(cr.getCenter());
		print(l);
		Double d = cr.chordLength(l);
		print(d);
		print(cr.getCentralAngle(d, Math::acos));

	}

	void m20() {
		inputPoly("a,b,3-b,3-a,2,3,1");
		ps[0] = Point.valueOf(fps[0], fps[1], mcfp);
		ps[1] = Point.valueOf(fps[2], fps[3], mcfp);
		Line<FracPoly> l = LineSup.perpendicularMiddleLine(ps[0], ps[1]);
		print(l);
		Circle<FracPoly> cir = Circle.centerAndRadius(Point.valueOf(fps[4], fps[5], mcfp), fps[6], mcfp);
		l = Line.generalFormula(fps[5], fps[5], fps[4], mcfp);
		cir = cir.symmetryCircle(l);
		print(cir.getCenter());
		print(cir.getRadius());
	}

	void m21() {
		print(MathUtils.lcm(5, 5));
		a = of(-1, -3);
		b = of(3, -1);
		Circle<Fraction> c1 = Circle.centerAndRadius(a, Fraction.valueOf(1), mc);
		Circle<Fraction> c2 = Circle.centerAndRadius(b, Fraction.valueOf(3), mc);
		Circle<FracPoly> c1f = c1.mapTo(CodePlace::mapperFF, mcfp);
		Circle<FracPoly> c2f = c2.mapTo(CodePlace::mapperFF, mcfp);
		c1f.outerCommonTangentLine(c2f).forEach(l -> {
			// print(l.mapTo(CodePlace::mapperPF,
			// mc).simplify(Fraction.getFractionSimplifier()));
			print(l.simplify(sim));
		});
	}

	void m22() {
		inputPoly("0,1,-1,a,a^2,k,1-k");
		Point<FracPoly> point_A = Point.valueOf(fps[1], fps[1], mcfp);
		GeneralConicSection<FracPoly> c = GeneralConicSection
				.generalFormula(Arrays.asList(fps[1], fps[0], fps[0], fps[0], fps[2], fps[0]), mcfp);
		print(c);
		Line<FracPoly> line_AB = c.tangentLine(point_A);
		Point<FracPoly> point_B = Point.valueOf(fps[0], line_AB.computeY(fps[0]), mcfp);
		Point<FracPoly> point_C = Point.valueOf(fps[3], fps[4], mcfp);
		Point<FracPoly> point_D = line_AB.intersectPoint(of(0, 1, 0).mapTo(CodePlace::mapperFF, mcfp));
		Point<FracPoly> point_E = point_A.proportionPoint(point_C, fps[5]);
		Point<FracPoly> point_F = point_B.proportionPoint(point_C, fps[6]);
		Line<FracPoly> line_EF = Line.twoPoint(point_E, point_F, mcfp);
		Line<FracPoly> line_CD = Line.twoPoint(point_C, point_D, mcfp);
		Point<FracPoly> point_P = line_EF.intersectPoint(line_CD);

		print(simp(point_P.x));
		print_();
		print(simp(point_P.y));
		// ps[1] =
	}

	private static FracPoly mapperFF(Fraction f) {
		return FracPoly.valueOf(new Polynomial(PolyCalculator.DEFAULT_CALCULATOR.getCal(), f.toString()));
	}

	private static Fraction mapperPF(FracPoly fp) {
		Fraction n = getFraction(fp.getNume());
		Fraction d = getFraction(fp.getDeno());
		return n.divide(d);
	}

	private static Fraction getFraction(Polynomial fp) {
		return Fraction.valueOf(getLongN(fp), getLongD(fp));
	}

	private static long getLongN(Polynomial p) {
		Formula f = p.getFormulaList().get(0);
		return f.getSignum() * f.getNumerator().longValueExact();
	}

	private static long getLongD(Polynomial p) {
		return p.getFormulaList().get(0).getDenominator().longValueExact();
	}

	private static final Simplifier<FracPoly> sim = FracPoly.getSimplifier();

	private static FracPoly simp(FracPoly fp) {

		return sim.simplify(Arrays.asList(fp)).get(0);
	}

	void m23() {
		Fraction f1 = Fraction.valueOf(5l);
		Fraction f2 = Fraction.valueOf(4l);
		EllipseV<Fraction> ell1 = EllipseV.standardEquation(f1, f2, mc);
		print(ell1);
		print(ell1.getCoefficients());
	}

	void m24() {
		inputPoly("12,5,1,1,0");
		EllipseV<FracPoly> ell = EllipseV.standardEquation(fps[0], fps[1], true, mcfp);
		Line<FracPoly> l = Line.generalFormula(fps[2], fps[3], fps[4], mcfp);
		Vector<FracPoly> vec = l.directionVector();
		ell.directTanLine(vec.getNumber(0), vec.getNumber(1))
				.forEach(line -> print(sim(line.mapTo(CodePlace::mapperPF, mc))));
	}

	void m25() {
		inputPoly("1,2,4,Sqr3,82/7");
		EllipseV<FracPoly> ell = EllipseV.generalFormula(fps[0], fps[1], fps[2], mcfp);
		Line<FracPoly> line = Line.slopeIntercept(fps[3], fps[3], mcfp);
		print(ell);
		print(line);
		print(ell.chordLength(line));
		print(ell.intersectPoints(line));
		print(ell.computeY(fps[4]));
	}

	void m26() {
		inputPoly("2,Sqr3,-1/4,1");
		EllipseV<FracPoly> e = EllipseV.standardEquation(fps[0], fps[1], true, mcfp);
		print(e.directTanLine(fps[2], fps[3]));
	}

	void m27() {
		inputPoly("3,2,Pi/3");
		EllipseV<FracPoly> ell = EllipseV.standardEquation(fps[0], fps[1], true, mcfp);
		print(ell);
		print(ell.trianlgeArea(fps[2], fp -> FracPoly.valueOf(new PolyCalculator().tan(fp.getNume()))));
	}

	public static FracPoly tanF(FracPoly fp) {
		return FracPoly.valueOf(new PolyCalculator().tan(fp.getNume()));
	}

	void m28() {
		Line<Fraction> l1 = of(3, -2, 5);
		Line<Fraction> l2 = of(5, 4, -3);
		Line<Fraction> l3 = of(5, 4, -6);
		Point<Fraction> p = l1.intersectPoint(l2);
		Point<Fraction> p2 = Point.valueOf(Fraction.valueOf(1l), Fraction.valueOf(3l), mc);
		print(sim(Line.twoPoint(p, p2, mc)));
		print(l1.symmetryPoint(p2));
		print(l2.intersectPoint(l3));
		print(l2.relationWith(l3));
		// print(l1.mapTo(f -> f.doubleValue(), Calculators.getCalculatorDouble()));
	}

	// void m29(){
	// MathCalculator<Double> mcfp = Calculators.getCalculatorDouble();
	// inputPoly("0,-1,1,2,Sqr3,3,2Sqr2");
	// Point f1 = Point.valueOf(0d, -Math.sqrt(2), mcfp);
	// Point f2 = Point.valueOf(0d, Math.sqrt(2), mcfp);
	//
	// Point[] ps = new Point[3];
	// for(int i=1;i<4;i++){
	// double d = i;
	// Point<Double> p = Point.valueOf(d, Math.sqrt(1D+d*d), mcfp);
	// p = Triangle.fromVertex(mcfp, f1, f2, p).centerI();
	// ps[i-1] = p;
	// }
	// Circle<Double> c = Circle.threePoints(ps[0], ps[1], ps[2], mcfp);
	// for(int i=1;i<100000000;i*=10){
	// double d = i;
	// Point<Double> p = Point.valueOf(d, Math.sqrt(1D+d*d), mcfp);
	// p = Triangle.fromVertex(mcfp, f1, f2, p).centerI();
	// print(p+" : "+c.relation(p));
	// }
	// }
	void m30() {
		MathCalculator<Double> mc = Calculators.getCalculatorDouble();
		HyperbolaV<Double> hy = HyperbolaV.standardEquation(2d, 3d / 2, false, mc);
		print(hy);
		print(hy.getEccentricity());
	}

	void m31() {
		// inputPoly("a,b,k,d,x,y,1+k^2,-x+kb-ky,-2yb+b^2+y^2+x^2,a,b,c,aa,bb,cc");
		// mcfp = FractionalPoly.getCalculator(true);
		// EllipseV<FractionalPoly> ell = EllipseV.create0(fps[9], fps[10], fps[11],
		// fps[12], fps[13], fps[14], true, mcfp);
		// Line<FractionalPoly> l = Line.slopeIntercept(fps[2], fps[3], mcfp);
		// QEquation<FractionalPoly> q = ell.createEquationX(l);
		// FractionalPoly re = mcfp.multiply(fps[6], q.rootsMul());
		// re = mcfp.add(re, mcfp.multiply(fps[7], q.rootsSum()));
		// re = mcfp.add(re, fps[8]);
		// Polynomial p = re.getNume();
		// List<Formula>[] times = new List[5];
		// Formula[] tis = new Formula[4];
		// tis[0] = Formula.valueOf("k");
		// tis[1] = Formula.valueOf("k^2");
		// tis[2] = Formula.valueOf("k^3");
		// tis[3] = Formula.valueOf("k^4");
		// Arrays.setAll(times, i-> new LinkedList<>());
		// FormulaCalculator pc = FormulaCalculator.getCalculator();
		// BigDecimal[] bds = new BigDecimal[4];
		// for(int i=0;i<bds.length;i++){
		// bds[i] = BigDecimal.valueOf(i+1);
		// }
		//
		// p.getFormulaList().forEach(f -> {
		// BigDecimal bd = f.getCharacterPower("k");
		// for(int i=0;i<bds.length;i++){
		// if(bds[i].equals(bd)){
		// times[i+1].add(pc.divide(f, tis[i]));
		// break;
		// }
		// }
		// });
		// for(List<?> li : times){
		// print(li);
		// }
		//
		// print(re);
	}

	void m32() {
		inputPoly("3,1,Pi/6");
		// �������ݣ�������Բ�Ĺ��캯������a,b����������3,1
		EllipseV<FracPoly> ell = EllipseV.standardEquation(fps[0], fps[1], mcfp);
		// ����һ����Բ��fps[0] = 3,fps[1] = 1 , mcfp �Ǽ�����
		Point<FracPoly> f1 = ell.foci().get(0);
		// ell.foci()����������Բ���������㣬get(0)����󽹵�
		Line<FracPoly> line = Line.pointSlope(f1, tanF(fps[2]), mcfp);
		// ������󽹵��ֱ�ߡ�
		print(ell);
		print(ell.chordLength(line));
		// ���
		Line<FracPoly> l2 = ell.chordMPL(line);
		// ��ù����ĺ����е��ֱ��
		Point<FracPoly> m = l2.intersectPoint(line);
		// ��ֱ�߽��㣬�õ����ĵ�����
		print(f1.distance(m));
		// ���
	}

	void m33() {
		// inputPoly("a,b,c,aa,bb,cc,k,d");
		// HyperbolaV<FractionalPoly> hyp = new HyperbolaV<>()
		Progression<Fraction> pf = ProgressionSup.createArithmeticProgression(Fraction.valueOf(1l), Fraction.ONE, mc);
		// pf = pf.limit(100)
		// .computeProgression(mc, f, pro)
		// .stream()
		// .collect(ProgressionSup.getCollector(mc));
		//// print(pf.toArray());
		Progression.computeProgression(mc, (Fraction f) -> f.multiply(f), pf).limit(100).stream().map(f -> f.minus(1))
				.collect(ProgressionSup.getCollector(mc)).forEach(Printer::print);
	}

	void m34() {
		inputPoly("3,1");
		// HyperbolaV<FractionalPoly> hv = HyperbolaV.standardEquation(fps[0],fps[1],
		// true, mcfp);
		// Line<FractionalPoly> l = Line.slopeIntercept(fps[2], fps[3], mcfp);
		// print(hv);
		// print(l);
		MathCalculator<Formula> mc = Formula.getCalculator();
		EllipseV<FracPoly> ell = EllipseV.standardEquationSqrt(Formula.valueOf("3"), Formula.ONE, true, mc)
				.mapTo(CodePlace::mapperFF2, mcfp);
		print(ell);
		Circle<FracPoly> cir = Circle.centerAndRadius(Point.pointO(mc), Formula.valueOf("Sqr6"), mc)
				.mapTo(CodePlace::mapperFF2, mcfp);
		;
		print(cir);
		Point<FracPoly> p = Point.valueOf(Formula.valueOf("cSqr6"), Formula.valueOf("sSqr6"), mc)
				.mapTo(CodePlace::mapperFF2, mcfp);
		;
		print(p);
		Line<FracPoly> line = Line.pointSlope(p, mapperFF2(Formula.valueOf("k")), mcfp);
		print(line);
		QEquation<FracPoly> eq = ell.createEquationX(line);
		print(eq);
		FracPoly delta = eq.delta();
		print(delta);
	}

	static FracPoly mapperFF2(Formula f) {
		return FracPoly.valueOf(new Polynomial(PolyCalculator.DEFAULT_CALCULATOR.getCal(), f));
	}

	void m35() {

		// first: a parabola
		inputPoly("p,d[y1],d[y2],d[y3]");
		GeneralConicSection<FracPoly> parabola = GeneralConicSection.parabola(fps[0], mcfp);
		print(parabola);
		// computes y
		fps[4] = toX(fps[1]);
		fps[5] = toX(fps[2]);
		fps[6] = toX(fps[3]);
		ps[0] = Point.valueOf(fps[4], fps[1], mcfp);
		Line<FracPoly> l1 = parabola.tangentLine(ps[0]);
		Line<FracPoly> l2 = parabola.tangentLine(Point.valueOf(fps[5], fps[2], mcfp));
		Line<FracPoly> l3 = parabola.tangentLine(Point.valueOf(fps[6], fps[3], mcfp));
		Line<FracPoly> l4;
		print(dealWith(l1.toString()));
		ps[0] = l1.intersectPoint(l2);
		ps[1] = l2.intersectPoint(l3);
		ps[2] = l3.intersectPoint(l1);
		ps[3] = Point.valueOf(mcfp.divideLong(fps[0], 2l), mcfp.getZero(), mcfp);
		ps[0] = Simplifier.singleSimplify(sim, ps[0]);
		ps[1] = Simplifier.singleSimplify(sim, ps[1]);
		ps[2] = Simplifier.singleSimplify(sim, ps[2]);
		print(dealWith(ps[0]));
		print(dealWith(ps[1]));
		print(dealWith(ps[2]));
		// if(System.err!=null)
		// return;
		l1 = Line.twoPoint(ps[1], ps[0], mcfp);
		l2 = Line.twoPoint(ps[1], ps[3], mcfp);
		l3 = Line.twoPoint(ps[2], ps[0], mcfp);
		l4 = Line.twoPoint(ps[2], ps[3], mcfp);
		l1 = Simplifier.singleSimplify(sim, l1);
		l1.simplify(sim);
		l2.simplify(sim);
		l3.simplify(sim);
		l4.simplify(sim);
		print("k=" + dealWith(l1.slope()));
		// print(dealWith(l1));
		// print(dealWith(l3));

		fps[0] = l1.intersectTanDirected(l3);
		fps[1] = l2.intersectTanDirected(l4);
		sim.simplify(Arrays.asList(fps[0]));
		print(fps[0]);
		print(fps[1]);
		// print(fps[1]);
		print("SUM = " + dealWith(mcfp.subtract(fps[0], fps[1])));
		print("SUB = " + dealWith(mcfp.add(fps[0], fps[1])));
	}

	FracPoly toX(FracPoly fp) {
		return mcfp.divide(mcfp.multiply(fp, fp), mcfp.multiplyLong(fps[0], 2l));
	}

	private Triangle<FracPoly> GT = null;
	private Triangle<Expression> GTE = null;

	/**
	 * Returns a general triangle: A(d[x1],d[y1])...
	 * 
	 * @return
	 */
	public Triangle<FracPoly> generalTriangle() {
		if (GT == null) {
			inputPoly("d[x1],d[x2],d[x3],d[y1],d[y2],d[y3]");
			GT = Triangle.fromVertex(mcfp, fps[0], fps[3], fps[1], fps[4], fps[2], fps[5]);
		}
		return GT;
	}

	/**
	 * Returns a general triangle: A(d[x1],d[y1])...
	 * 
	 * @return
	 */
	public Triangle<Expression> generalTriangleE() {
		if (GT == null) {
			inputExpression("d[x1],d[x2],d[x3],d[y1],d[y2],d[y3]");
			GTE = Triangle.fromVertex(ec, es[0], es[3], es[1], es[4], es[2], es[5]);
		}
		return GTE;
	}

	public void m36() {
		Triangle<FracPoly> tri = generalTriangle();
		Point<FracPoly> i = Point.valueOf(fps[6], fps[7], mcfp);
		ps[0] = tri.vertexA();
		ps[1] = tri.vertexB();
		ps[2] = tri.vertexC();
		PVector<FracPoly> v1, v2, v3;
		v1 = PVector.vector(ps[0], i).multiplyNumber(Triangle.fromVertex(mcfp, i, ps[1], ps[2]).areaPN());
		v2 = PVector.vector(ps[1], i).multiplyNumber(Triangle.fromVertex(mcfp, i, ps[2], ps[0]).areaPN());
		v3 = PVector.vector(ps[2], i).multiplyNumber(Triangle.fromVertex(mcfp, i, ps[0], ps[1]).areaPN());
		print(dealWith(v1));
		print(dealWith(v2));
		print(dealWith(v3));
		print(PVector.sum(v1, v2, v3));
	}

	public void m36e() {
		inputExpression("d[x1],d[x2],d[x3],d[y1],d[y2],d[y3],x,y");
		Triangle<Expression> tri = generalTriangleE();
		Point<Expression> i = Point.valueOf(es[6], es[7], ec);
		@SuppressWarnings("unchecked")
		Point<Expression>[] ps = new Point[3];
		ps[0] = tri.vertexA();
		ps[1] = tri.vertexB();
		ps[2] = tri.vertexC();
		PVector<Expression> v1, v2, v3;
		v1 = PVector.vector(ps[0], i).multiplyNumber(Triangle.fromVertex(ec, i, ps[1], ps[2]).areaPN());
		v2 = PVector.vector(ps[1], i).multiplyNumber(Triangle.fromVertex(ec, i, ps[2], ps[0]).areaPN());
		v3 = PVector.vector(ps[2], i).multiplyNumber(Triangle.fromVertex(ec, i, ps[0], ps[1]).areaPN());
		print(dealWith(v1));
		print(dealWith(v2));
		print(dealWith(v3));
		print(PVector.sum(v1, v2, v3));
	}

	void m37() {
		Fraction[][] mat = new Fraction[5][6];
		Random rd = new Random();
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 6; j++) {
				mat[i][j] = Fraction.valueOf(rd.nextInt(10) + 1, 1 + rd.nextInt(10));
			}
		}

		for (int i = 0; i < 5; i++) {
			print(mat[i]);
		}
		LinearEquationSolution<Fraction> solution = MatrixSup.solveLinearEquation(mat, Fraction.getCalculator());
		print(solution.getSolutionSituation());
		print(solution.getBase());
		print(solution.getSolution());
		Fraction sum = Fraction.ZERO;
		for (int i = 0; i < 5; i++) {
			sum = sum.add(mat[0][i].multiply(solution.getBase().getNumber(i)));
		}
		print("Sum = " + sum);
	}

	// void m38() {
	// int d = 4;
	// int len = d * (d + 2);
	// pos = new Polynomial[len];
	// fps = new FracPoly[len];
	// for (int i = 0; i <= d + 1; i++) {
	// for (int j = 0; j < d; j++) {
	// pos[d * i + j] = Polynomial.valueOf("x[" + i + "," + (j + 1) + "]");
	// }
	// }
	// compute();
	// @SuppressWarnings("unchecked")
	// Vector<FracPoly>[] vs = new Vector[d + 1];
	// for (int i = 0; i <= d + 1; i++) {
	// FracPoly[] fp = Arrays.copyOfRange(fps, d * i, d * i + d);
	// vs[i] = Vector.createVector(mcfp, fp);
	// }
	// // input part
	//
	// for (int i = 1; i <= d + 1; i++) {
	// FracPoly[][] mat = new FracPoly[d + 1][d + 1];
	// for (int a = 0; a <= d; a++) {
	// for (int b = 0; b < d; b++) {
	// // mat[a][b] =
	// }
	// }
	// Matrix<FracPoly> matrix = Matrix.valueOf(mat, mcfp);
	// }
	// }

	void m39() {
		long[][] mat = new long[][] { { 1, 3, 5, 7, 1 }, { 2, 4, 3, 0, 2 }, { -3, -7, 0, 6, 8 },
				{ -5, -12, 13, -27, -9 } };
		Matrix<Long> mat1 = Matrix.valueOf(mat);
		Matrix<Fraction> mat2 = mat1.mapTo(l -> Fraction.valueOf(l), Fraction.getCalculator());
		LinearEquationSolution<Fraction> solution = MatrixSup.solveLinearEquation(mat2);
		print(solution);
		print(solution.getBase());
	}

	public void m40() {
		// prove a/sinA = b/sinB
		// a^2(sinB)^2 = b^2 (sinA)^2
		Triangle<FracPoly> tri = generalTriangle();
		Point<FracPoly> A = tri.vertexA(), B = tri.vertexB(), C = tri.vertexC();
		FracPoly a2 = C.distanceSq(B), b2 = C.distanceSq(A);
		print(dealWith(a2));
		PVector<FracPoly> AB = PVector.vector(A, B);
		PVector<FracPoly> AC = PVector.vector(A, C);
		PVector<FracPoly> CB = PVector.vector(C, B);
		fps[0] = CB.innerProduct(AB);
		fps[1] = AB.innerProduct(AC);
		fps[0] = mcfp.pow(fps[0], 2);
		fps[1] = mcfp.pow(fps[1], 2);
		FracPoly sinB2 = mcfp.subtract(FracPoly.ONE,
				mcfp.divide(fps[0], mcfp.multiply(CB.calLengthSq(), AB.calLengthSq()))),
				sinA2 = mcfp.subtract(FracPoly.ONE,
						mcfp.divide(fps[1], mcfp.multiply(AB.calLengthSq(), AC.calLengthSq())));
		FracPoly left = mcfp.multiply(a2, sinB2);
		FracPoly right = mcfp.multiply(b2, sinA2);
		FracPoly result = mcfp.subtract(right, left);
		// print(dealWith(right));
		// print(dealWith(result));
		assertTrue(mcfp.isZero(result));

	}

	// // @Test
	// public void m41() {
	// inputExpression("k,-1/k,2");
	// FracPoly k = fps[0];
	// EllipseV<FracPoly> ell = EllipseV.standardEquationSqrt(fps[2], FracPoly.ONE,
	// mcfp);
	// print(ell);
	// Line<FracPoly> AC = Line.slopeIntercept(k, FracPoly.ZERO, mcfp),
	// BD = Line.slopeIntercept(fps[1], FracPoly.ZERO, mcfp);
	//
	// }

//	@Test
	public void m42() {
		Triangle<FracPoly> tri = generalTriangle();
		Point<FracPoly> A = tri.vertexA(), B = tri.vertexB(), C = tri.vertexC(), O = tri.centerO(), H = tri.centerH();
		PVector<FracPoly> v1 = PVector.vector(O, A), v2 = PVector.vector(O, B), v3 = PVector.vector(O, C),
				v = v1.add(v2).add(v3), oh = PVector.vector(O, H);
		// print(dealWith(v));
		// print(dealWith(oh));
		// print(dealWith(oh));
		// String str
		// ="-x1^3*y2^2+2*x1^3*y2y3-x1^3*y3^2-x1^2*x2y1y2+x1^2*x2y1y3+2*x1^2*x2y2^2-3*x1^2*x2y2y3+x1^2*x2y3^2+"
		// +
		// "x1^2*x3y1y2-x1^2*x3y1y3+x1^2*x3y2^2-3*x1^2*x3y2y3+2*x1^2*x3y3^2+2*x1x2^2*y1^2-x1x2^2*y1y2-3*x1x2^2*y1y3+"
		// +
		// "x1x2^2*y2y3+x1x2^2*y3^2-4*x1x2x3y1^2+4*x1x2x3y1y2+4*x1x2x3y1y3-4*x1x2x3y2^2+4*x1x2x3y2y3-4*x1x2x3y3^2+"
		// +
		// "2*x1x3^2*y1^2-3*x1x3^2*y1y2-x1x3^2*y1y3+x1x3^2*y2^2+x1x3^2*y2y3-3*x1y1^2*y2^2+6*x1y1^2*y2y3-3*x1y1^2*y3^2+"
		// +
		// "3*x1y1y2^3-3*x1y1y2^2*y3-3*x1y1y2y3^2+3*x1y1y3^3-3*x1y2^3*y3+6*x1y2^2*y3^2-3*x1y2y3^3-x2^3*y1^2+2*x2^3*y1y3-"
		// +
		// "x2^3*y3^2+x2^2*x3y1^2+x2^2*x3y1y2-3*x2^2*x3y1y3-x2^2*x3y2y3+2*x2^2*x3y3^2+x2x3^2*y1^2-3*x2x3^2*y1y2+x2x3^2*y1y3+"
		// +
		// "2*x2x3^2*y2^2-x2x3^2*y2y3+3*x2y1^3*y2-3*x2y1^3*y3-3*x2y1^2*y2^2-3*x2y1^2*y2y3+6*x2y1^2*y3^2+6*x2y1y2^2*y3-"
		// +
		// "3*x2y1y2y3^2-3*x2y1y3^3-3*x2y2^2*y3^2+3*x2y2y3^3-x3^3*y1^2+2*x3^3*y1y2-x3^3*y2^2-3*x3y1^3*y2+3*x3y1^3*y3+"
		// +
		// "6*x3y1^2*y2^2-3*x3y1^2*y2y3-3*x3y1^2*y3^2-3*x3y1y2^3-3*x3y1y2^2*y3+6*x3y1y2y3^2+3*x3y2^3*y3-3*x3y2^2*y3^2";
		fps[0] = oh.x;
		// MathCalculator<Polynomial> mcp = PolyCalculator.DEFAULT_CALCULATOR;
		// print(dealWith(fps[0].getNume()));
		fps[1] = v.x;
		// MathCalculator<Polynomial> mcp = Polynomial.getCalculator();
		// print(dealWith(mcp.multiply(fps[0].getNume(), fps[1].getDeno())));
		// print_();
		// print(dealWith(mcp.multiply(fps[0].getNume(), fps[1].getDeno())));

		assertEquals("1", dealWith(mcfp.divide(fps[0], fps[1])));
	}

//	@Test
	public void m42e() {
		Triangle<Expression> tri = generalTriangleE();
		Point<Expression> A = tri.vertexA(), B = tri.vertexB(), C = tri.vertexC(), O = tri.centerO(), H = tri.centerH();
		PVector<Expression> v1 = PVector.vector(O, A), v2 = PVector.vector(O, B), v3 = PVector.vector(O, C),
				v = v1.add(v2).add(v3), oh = PVector.vector(O, H);
		// print(dealWith(oh));
		es[0] = oh.x;
		es[1] = v.x;
		es[2] = ec.divide(es[0], es[1]);
		// leave the step last
		SimplificationStrategies.setCalRegularization(ec);
		es[2] = ec.simplify(es[2]);
		// es[2] = ec.simplify(es[2]);
		assertTrue(ec.isEqual(ec.getOne(), es[2]));
	}

	public void m43() {
		Random rd = new Random();
		double[] cords = new double[6];
		for (int i = 0; i < 6; i++) {
			cords[i] = rd.nextDouble() * 16;
		}
		Triangle<Double> tri = Triangle.fromVertex(mcd, cords[0], cords[3], cords[1], cords[4], cords[2], cords[5]);
		Point<Double> A = tri.vertexA(), B = tri.vertexB(), C = tri.vertexC(), O = tri.centerO(), H = tri.centerH();
		PVector<Double> v1 = PVector.vector(O, A), v2 = PVector.vector(O, B), v3 = PVector.vector(O, C),
				v = v1.add(v2).add(v3), oh = PVector.vector(O, H);
		// print(v1.calLength());
		// print(v2.calLength());
		// print(v3.calLength());
		v1 = PVector.vector(H, A);
		v2 = PVector.vector(H, B);
		v3 = PVector.vector(H, C);
		// print(v1.innerProduct(tri.sideA().directionVector()));
		// print(v2.innerProduct(tri.sideB().directionVector()));
		// print(v3.innerProduct(tri.sideC().directionVector()));
		// print(oh);
		// print(v);
		assertTrue("OH = V", oh.valueEquals(v));
	}

	public void m44() {
		MathCalculator<Double> mc = Calculators.getCalculatorDoubleDev(1E-10);
		EllipseV<Double> ell = EllipseV.standardEquationSqrt(9d, 5d, mc);
		double result = ModelPatterns.binarySolve(0.8d, 0.9d, (a, b) -> (a + b) / 2, k -> {
			Line<Double> l = Line.pointSlope(2d, 0d, k, mc);
			List<Point<Double>> ps = ell.intersectPoints(l);
			Point<Double> p0 = ps.get(0);
			Point<Double> p1 = ps.get(1);
			return mc.compare((p0.x - 2) * 3 - (2 - p1.x), 0d);
		}, 50);
		// print(result,16);
		assertEquals("Result should be 7/9", Fraction.valueOf(7, 9), Fraction.bestApproximate(result * result, 100));

		// print(result,16);
		// print(result*result,16);
		// print(Fraction.valueOfDouble(result*result, 1000));
	}

	private static final ExprCalculator ec = new ExprCalculator();

	public void m45() {
		inputExpression("3,x");
		// ParabolaV<Expression> M = ParabolaV.generalFormula(es[0], true, ec);
		Expression x = es[1], y = ec.squareRoot(ec.multiplyLong(x, 6));
		Circle<Expression> C = Circle.centerAndRadius(Point.valueOf(es[0], ec.getZero(), ec), es[0], ec);
		List<Line<Expression>> lines = C.tangentLines(Point.valueOf(x, y, ec));
		Line<Expression> l1 = lines.get(0), l2 = lines.get(1);
		Expression a = l1.computeY(ec.getZero()), b = l2.computeY(ec.getZero());
		Expression s = ec.subtract(a, b);
		s = ec.multiply(x, s);
		s = ec.divideLong(s, 2);
		SimplificationStrategies.setCalRegularization(ec);
		s = ec.simplify(s);
		// print(s);
		Node.Fraction n = (Node.Fraction) s.getRoot();
		MultinomialX<Formula> nume = MultinomialX.fromPolynomial(Node.getPolynomialPart(n.getC1(), null), "x");
		MultinomialX<Formula> deno = MultinomialX.fromPolynomial(Node.getPolynomialPart(n.getC2(), null), "x");
		Formula f12 = Formula.valueOf(12);
		Formula result = fc.divide(nume.compute(f12), deno.compute(f12));
		assertTrue("Result is 72", Formula.valueOf(72).absEquals(result));
		// MultinomialCalculator<Formula> muc =
		// MultinomialX.getCalculator(Formula.getCalculator());
		// print(muc.divideAndReminder(nume,deno));
		// AbstractSVPFunction<Formula> f1 = AbstractSVPFunction.fromMultinomial(nume),
		// f2 = AbstractSVPFunction.fromMultinomial(deno);
		// AbstractSVPFunction<Formula> f =
		// AbstractSVPFunction.subtract(AbstractSVPFunction.multiply(f1,
		// f2.derive()),AbstractSVPFunction.multiply(f1.derive(), f2));
		//
		// print(f.apply(Formula.valueOf(12)));
	}

	// @Test
	public void m46() {
		final double t = 0.5;
		// any value for k is acceptable
		final double k = 1;
		HyperbolaV<Double> L = HyperbolaV.standardEquationSqrt(1d, 3d, true, mcd);
		Line<Double> line = Line.pointSlope(L.foci().get(1), k, mcd);
		List<Point<Double>> AB = L.intersectPoints(line);
		Point<Double> A = AB.get(0), B = AB.get(1);
		Point<Double> C = Point.valueOf(t, A.y, mcd), D = Point.valueOf(t, B.y, mcd);
		Line<Double> AD = Line.twoPoint(A, D), BC = Line.twoPoint(B, C);
		// print(AD);
		Point<Double> p = AD.intersectPoint(BC);
		assertThat("Point is on the x axis", p.y,isZero(mcd));

	}

//	@Test
	public void m46e() {
		inputExpression("1,3,t,k");
		Expression k = es[3], t = es[2];
		HyperbolaV<Expression> L = HyperbolaV.standardEquationSqrt(es[0], es[1], true, ec);
		// create hyperbola x^2 - y^2/3 = 1
		Point<Expression> F = L.foci().get(1);
		Line<Expression> line = Line.pointSlope(F, k);
		// the line that passes through F
		List<Point<Expression>> AB = L.intersectPoints(line);
		// the list of A and B
		Point<Expression> A = AB.get(0), B = AB.get(1);
		// Point A and B
		Point<Expression> C = Point.valueOf(t, A.y, ec), D = Point.valueOf(t, B.y, ec);
		// Point C and D
		Line<Expression> AD = Line.twoPoint(A, D), BC = Line.twoPoint(B, C);
		// two lines AD and BC
		Point<Expression> p = AD.intersectPoint(BC);
		// the intersect point
		// print("x = "+p.x);
		// print("y = "+p.y);
		// show the point p
		p = p.mapTo(x -> ec.substitute(x, "t", Polynomial.valueOf("1/2")), ec);
		// substitute 1/2 for t
		assertThat("Point is on the x axis", p.y,isZero(ec));
	}
//	@Test
	public void m47() {
		double[] xs = ArraySup.ranDoubleArr(4, 1);
		xs[1] = -xs[1];
		xs[2] = -xs[2];
		ComputeExpression ce = ComputeExpression.compile("exp(1-exp($0,2),1/2)");
		Point<Double> A, B, C, D, E, F, K, I, J, P;
		A = Point.valueOf(xs[0], ce.compute( mcd,xs[0]), mcd);
		B = Point.valueOf(xs[1], ce.compute( mcd,xs[1]), mcd);
		C = Point.valueOf(xs[2], -ce.compute( mcd,xs[2]), mcd);
		D = Point.valueOf(xs[3], -ce.compute( mcd,xs[3]), mcd);
		Line<Double> AC = Line.twoPoint(A, C), BD = Line.twoPoint(B, D), AB = Line.twoPoint(A, B);
		P = AC.intersectPoint(BD);
		Circle<Double> ADP = Circle.threePoints(A, D, P), BCP = Circle.threePoints(B, C, P);
		E = ADP.intersectPointAnother(A, AB);
		F = BCP.intersectPointAnother(B, AB);
		I = Triangle.fromVertex(A, D, E).centerI();
		J = Triangle.fromVertex(B, C, F).centerI();
		Line<Double> IJ = Line.twoPoint(I, J);
		K = AC.intersectPoint(IJ);
		Circle<Double> AEK = Circle.threePoints(A, E, K);
		// print(dealWith(AEK));
		double result = AEK.substitute(I);
		if(mcd.isZero(result)) {
			print(result);
		}else {
			print(result);
		}
	}
//	@Test(timeout=1000*10)
	public void m47e() throws IOException {
		inputExpression("d[x1],d[x2],d[x3],d[x4],d[y1],d[y2],d[y3],d[y4]");
		Point<Expression> A,B,C,D,E,F,K,I,J,P;
		A = Point.valueOf(es[0], es[4], ec);
		B = Point.valueOf(es[1], es[5], ec);
		C = Point.valueOf(es[2], es[6], ec);
		D = Point.valueOf(es[3], es[7], ec);
		print("1");
		Line<Expression> AC = Line.twoPoint(A, C),
				BD = Line.twoPoint(B, D),
				AB = Line.twoPoint(A, B);
		P = AC.intersectPoint(BD);
		Circle<Expression> ADP = Circle.threePoints(A, D, P),
				BCP = Circle.threePoints(B, C, P);
		print("2");
		E = ADP.intersectPointAnother(A, AB);
				F = BCP.intersectPointAnother(B, AB);
		I = Triangle.fromVertex(A, D, E).centerI();
		J = Triangle.fromVertex(B, C, F).centerI();
		Line<Expression> IJ = Line.twoPoint(I, J);
		K = AC.intersectPoint(IJ);
		Circle<Expression> AEK = Circle.threePoints(A, E, K);
		print("3");
//		print(dealWith(AEK));
//		Printer.reSet(Files.newOutputStream(
//				new File("E:\\Temp\\result.txt").toPath(),
//				StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE));
//		
//		AEK = AEK.mapTo(ec::simplify, ec);
//		String str = dealWith(AEK.substitute(I));
//		StringBuilder sb = new StringBuilder(str.length());
//		for(int i=0;i<str.length();i+= 100) {
//			if(i + 100 >= str.length()) {
//				sb.append(str.substring(i));
//			}else {
//				sb.append(str.substring(i, i+100));
//			}
//			sb.append(System.lineSeparator());
//		}
//		print(sb.toString());
//		SimplificationStrategies.setCalRegularization(ec);
	}
//	@Test
	public void m48() {
		inputExpression("2t^2,t^2,x");
		Expression a = es[0], b = es[1], x = es[2];
		// input some number
		SimplificationStrategies.setCalRegularization(ec);
		EllipseV<Expression> L = EllipseV.standardEquationSqrt(a, b, true, ec);
		// create ellipse x^2/(2t^2) + y^2/t^2 = 1
		Point<Expression> C, D, M, P;
		C = L.vertices().get(0);
		D = L.vertices().get(1);
		// get vertices C and D
		M = Point.valueOf(D.x, x, ec);
		// get point M
		Line<Expression> CM = Line.twoPoint(C, M);
		P = L.intersectPointAnother(C, CM);
		Expression product = M.getVector().innerProduct(P.getVector());
		assertTrue("OM*OP = 2t^2",ec.isEqual(a, product));
		Line<Expression> DP = Line.twoPoint(D, P), MQ = DP.perpendicular(M);
		// compute point Q
		assertThat(MQ.computeX(ec.getZero()), isZero(ec));
	}
//	@Test
	public void m49() {
		inputExpression("a,b,A,B,C");
		HyperbolaV<Expression> H = HyperbolaV.standardEquation(es[0], es[1], true, ec);
		Line<Expression> l1 = H.asymptote().get(0),
				l2 = H.asymptote().get(1),
				l = Line.generalFormula(es[2], es[3], es[4], ec);
		Point<Expression> A,B,C,D;
		List<Point<Expression>> interPoints = H.intersectPoints(l);
		A = interPoints.get(0);
		B = interPoints.get(1);
		C = l.intersectPoint(l1);
		D = l.intersectPoint(l2);
		Expression d1_2 = A.distanceSq(C),
				d2_2 = B.distanceSq(D);
		Expression diff = ec.subtract(d1_2, d2_2);
		SimplificationStrategies.setCalRegularization(ec);
		diff = ec.simplify(diff);
		assertThat(diff, isZero(ec));
	}
	@Test
	public void m50() {
		inputExpression("-3,-4,7");
		Circle<Fraction> circle = Circle.generalFormula(-4L, -4L, 7L,mct).mapTo(Fraction::valueOf, mc);
		for(Line<Fraction> l:circle.tangentLines(a)) {
			print(l.simplify(Fraction.getFractionSimplifier()));
		}
	}
}
