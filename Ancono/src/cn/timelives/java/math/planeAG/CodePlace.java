package cn.timelives.java.math.planeAG;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.print_;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;


import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.LinearEquationSolution;
import cn.timelives.java.math.MathFunctions;
import cn.timelives.java.math.Progression;
import cn.timelives.java.math.ProgressionSup;
import cn.timelives.java.math.SVPEquation.QEquation;
import cn.timelives.java.math.linearAlgebra.Matrix;
import cn.timelives.java.math.linearAlgebra.MatrixSup;
import cn.timelives.java.math.linearAlgebra.Vector;
import cn.timelives.java.math.numberModels.Formula;
import cn.timelives.java.math.numberModels.FormulaCalculator;
import cn.timelives.java.math.numberModels.Fraction;
import cn.timelives.java.math.numberModels.FractionalPoly;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.MathCalculatorAdapter;
import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.math.numberModels.Simplifier;
import cn.timelives.java.math.planeAG.curve.EllipseV;
import cn.timelives.java.math.planeAG.curve.GeneralConicSection;
import cn.timelives.java.math.planeAG.curve.HyperbolaV;
import cn.timelives.java.utilities.EasyConsole;
import cn.timelives.java.utilities.Printer;

 class CodePlace {

//	public static void main(String[] args) {
//		CodePlace cp = new CodePlace();
//		cp.m39();
//	}

	public void m1() {
		MathCalculator<Fraction> mc = Fraction.getCalculator();
		// = Line.twoPoint(
		// Fraction.valueOf(3),Fraction.valueOf(4), Fraction.ZERO,Fraction.ZERO,
		// mc);
		// print(l1);
		//information participation
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
		MathCalculator<Long> mct = MathCalculatorAdapter.getCalculatorLong();
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
	MathCalculator<Long> mct = MathCalculatorAdapter.getCalculatorLong();

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

	void m5() {
		Point<Fraction> a = new Point<>(mct, -7l, 1l).mapTo(Fraction::valueOf, mc);
		Point<Fraction> b = new Point<>(mct, -5l, 2l).mapTo(Fraction::valueOf, mc);
		Line<Fraction> l = Line.generalFormula(2l, -1l, -5l, mct).mapTo(Fraction::valueOf, mc);
		// b = l.symmetryPoint(b);
		print(l.intersectPoint(Line.twoPoint(a, b, mc)));
	}

	Point<Fraction> of(int a, int b) {
		return new Point<Fraction>(mc, Fraction.valueOf(a), Fraction.valueOf(b));
	}

	Line<Fraction> of(int a, int b, int c) {
		return Line.generalFormula(Fraction.valueOf(a), Fraction.valueOf(b), Fraction.valueOf(c), mc);
	}
	
	Line<Fraction> sim(Line<Fraction> l){
		return LineSup.simplify(l, Fraction.getFractionSimplifier());
	}
	
	FlexibleMathObject<Fraction> map(FlexibleMathObject<Long> obj){
		return obj.mapTo(Fraction::valueOf, mc);
	}

	/*
	 * values here
	 */
	Point<Fraction> a, b, c, d, e, f, g, m, n, o, p;
	Line<Fraction> l1, l2, l3, l4, l5, l6, l;

	void m6() {
		l1 = Line.parallelX(Fraction.ZERO, mc);
		a = of(5, 2);
		b = l1.symmetryPoint(a);
		l2 = of(1, -1, 0);
		c = l2.symmetryPoint(b);
		print(of(10, 9).distanceSq(c));
	}

	void m7() {
		l1 = of(1, 1, -2);
		a = of(-1, 5);
		b = of(0, -1);
		c = l1.symmetryPoint(b);
		print(Line.twoPoint(a, c, mc));
	}

	void m8() {
		a = of(8, -1);
		b = of(1, 7);
		l1 = of(3, 4, -12);
		c = l1.symmetryPoint(a);
		d = l1.intersectPoint(Line.twoPoint(b, c, mc));

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
		FractionalPoly[] fps = new FractionalPoly[6];
		for (int i = 0; i < 6; i++) {
			fps[i] = FractionalPoly.valueOf(pos[i]);
		}
		MathCalculator<FractionalPoly> mcfp = FractionalPoly.getCalculator();
		Point<FractionalPoly> pa = new Point<>(mcfp, fps[0], fps[1]);
		Point<FractionalPoly> pb = new Point<>(mcfp, fps[2], fps[3]);
		Point<FractionalPoly> pc = new Point<>(mcfp, fps[4], fps[5]);
		Triangle<FractionalPoly> tri = Triangle.fromVertex(mcfp, pa, pb, pc);
//		print(dealWith(tri.centerG().toString()));
//		print(dealWith(tri.centerH().toString()));
		ps[0] = tri.centerO();
		print(dealWith(ps[0].toString()));
//		print(dealWith(ps[0].distanceSq(pa).toString()));

	}
	FormulaCalculator fc = FormulaCalculator.getCalculator();
	MathCalculator<Polynomial> fcp = Polynomial.getCalculator();
	MathCalculator<FractionalPoly> mcfp = FractionalPoly.getCalculator(false);
	
	final int def_size = 32;
	Polynomial[] pos = new Polynomial[def_size];
	FractionalPoly[] fps = new FractionalPoly[def_size];
	@SuppressWarnings("unchecked")
	Point<FractionalPoly>[] ps = new Point[def_size];
	
	
	
	void compute(){
		for (int i = 0; i < pos.length; i++) {
			if (pos[i] != null)
				fps[i] = FractionalPoly.valueOf(pos[i]);
		}
	}
	
	void m15() {
		
		
//		Polynomial[] pos = new Polynomial[10];
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
		fps[6] = mcfp.subtract(FractionalPoly.ONE, fps[5]);
		Point<FractionalPoly> A = new Point<>(mcfp, fps[1], fps[1]);
		Point<FractionalPoly> B = new Point<>(mcfp, fps[0], mcfp.negate(fps[1]));
		Point<FractionalPoly> D = new Point<>(mcfp, fps[2], fps[0]);
		Point<FractionalPoly> C = new Point<>(mcfp, fps[3], fps[4]);
		Point<FractionalPoly> E = A.proportionPoint(C, fps[5]);
		Point<FractionalPoly> F = B.proportionPoint(C, fps[6]);
		Line<FractionalPoly> ef = Line.twoPoint(E, F, mcfp);
		Line<FractionalPoly> cd = Line.twoPoint(C, D, mcfp);
		A = ef.intersectPoint(cd);
		print(A);
		print_();
		FractionalPoly fpx = A.x;
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
	
	void m16() {
		a = of(2, 1);
		b = of(0, -2);
		c = a.middle(b);
		l1 = Line.twoPoint(a, b, mc);
		print(sim(l1.perpendicular(c)));

	}
	
	void inputPoly(String...data){
		for(int i=0;i<data.length;i++){
			pos[i] = new Polynomial(fc, data[i]);
		}
		compute();
	}
	
	void inputPoly(String datas){
		String[] ss = datas.split(",");
		inputPoly(ss);
	}
	
	void m17(){
		inputPoly("0","3","b","b+8");
		compute();
		ps[0] = new Point<>(mcfp,fps[0],fps[1]);
		ps[1] = new Point<>(mcfp,fps[2],fps[0]);
		ps[2] = new Point<>(mcfp,fps[3],fps[0]);
		Triangle<FractionalPoly> tri = Triangle.fromVertex(mcfp, ps[0], ps[1], ps[2]);
		print(tri.centerO());
	}
	void m18(){
		inputPoly("0","2","2");
		compute();
		Circle<FractionalPoly> cr = Circle.centerAndRadius(new Point<>(mcfp,fps[0],fps[0]), fps[1], mcfp);
		print(cr);
		Line<FractionalPoly> l = Line.slopeIntercept(fps[2], fps[0], mcfp);
		print(l);
		print(cr.intersectPoints(l));
				
	}
	
	void m19(){
		MathCalculator<Double> cal = MathCalculatorAdapter.getCalculatorDouble();
		Circle<Double> cr = Circle.generalFormula(0d, -12d, 27d, cal);
		Point<Double> p = new Point<>(cal,0d,0d);
		Line<Double> l = cr.radicalAxis(p);
		print(cr.getDiameter());
		print(cr.getCenter());
		print(l);
		Double d = cr.chordLength(l);
		print(d);
		print(cr.getCentralAngle(d, Math::acos));
		
	}
	
	void m20(){
		inputPoly("a,b,3-b,3-a,2,3,1");
		ps[0] = Point.valueOf(fps[0], fps[1], mcfp);
		ps[1] = Point.valueOf(fps[2], fps[3], mcfp);
		Line<FractionalPoly> l = LineSup.perpendicularMiddleLine(ps[0], ps[1]);
		print(l);
		Circle<FractionalPoly> cir = Circle.centerAndRadius(
				Point.valueOf(fps[4], fps[5], mcfp), fps[6], mcfp);
		l = Line.generalFormula(fps[5],fps[5], fps[4], mcfp);
		cir = cir.symmetryCircle(l);
		print(cir.getCenter());
		print(cir.getRadius());
	}

	void m21(){
		print(MathFunctions.lcm(5, 5));
		a = of(-1,-3);
		b = of(3,-1);
		Circle<Fraction> c1 = Circle.centerAndRadius(a, Fraction.valueOf(1), mc);
		Circle<Fraction> c2 = Circle.centerAndRadius(b, Fraction.valueOf(3), mc);
		Circle<FractionalPoly> c1f = c1.mapTo(CodePlace::mapperFF, mcfp);
		Circle<FractionalPoly> c2f = c2.mapTo(CodePlace::mapperFF, mcfp);
		c1f.outerCommonTangentLine(c2f).forEach(l -> 
		{
//			print(l.mapTo(CodePlace::mapperPF, mc).simplify(Fraction.getFractionSimplifier()));
			print(l.simplify(sim));
			});
	}
	
	void m22(){
		inputPoly("0,1,-1,a,a^2,k,1-k");
		Point<FractionalPoly> point_A = Point.valueOf(fps[1], fps[1], mcfp);
		GeneralConicSection<FractionalPoly> c = GeneralConicSection.generalFormula(
				Arrays.asList(fps[1],fps[0],fps[0],fps[0],fps[2],fps[0]), mcfp);
		print(c);
		Line<FractionalPoly> line_AB = c.tangentLine(point_A);
		Point<FractionalPoly> point_B = Point.valueOf(fps[0],line_AB.computeY(fps[0]),mcfp);
		Point<FractionalPoly> point_C = Point.valueOf(fps[3], fps[4], mcfp);
		Point<FractionalPoly> point_D = line_AB.intersectPoint(of(0,1,0).mapTo(CodePlace::mapperFF, mcfp));
		Point<FractionalPoly> point_E = point_A.proportionPoint(point_C, fps[5]);
		Point<FractionalPoly> point_F = point_B.proportionPoint(point_C, fps[6]);
		Line<FractionalPoly> line_EF = Line.twoPoint(point_E, point_F, mcfp);
		Line<FractionalPoly> line_CD = Line.twoPoint(point_C, point_D, mcfp);
		Point<FractionalPoly> point_P = line_EF.intersectPoint(line_CD);
		
		print(simp(point_P.x));
		print_();
		print(simp(point_P.y));
//		ps[1] = 
	}
	
	
	private static FractionalPoly mapperFF(Fraction f){
		return FractionalPoly.valueOf(new Polynomial(PolyCalculator.DEFALUT_CALCULATOR.getCal(),f.toString()));
	}
	
	private static Fraction mapperPF(FractionalPoly fp){
		Fraction n = getFraction(fp.getNume());
		Fraction d = getFraction(fp.getDeno());
		return n.divide(d);
	}
	
	private static Fraction getFraction(Polynomial fp){
		return Fraction.valueOf(getLongN(fp), getLongD(fp));
	}
	
	private static long getLongN(Polynomial p){
		Formula f = p.getFormulaList().get(0);
		return f.getSignum()*f.getNumerator().longValueExact();
	}
	private static long getLongD(Polynomial p){
		return p.getFormulaList().get(0).getDenominator().longValueExact();
	}
	
	private static final Simplifier<FractionalPoly> sim = FractionalPoly.getSimplifier();
	private static FractionalPoly simp(FractionalPoly fp){
		
		return sim.simplify(Arrays.asList(fp)).get(0);
	}
	
	void m23(){
		Fraction f1 = Fraction.valueOf(5l);
		Fraction f2 = Fraction.valueOf(4l);
		EllipseV<Fraction> ell1 = EllipseV.standardEquation(f1, f2, mc);
		print(ell1);
		print(ell1.getCoefficients());
	}
	
	void m24(){
		inputPoly("12,5,1,1,0");
		EllipseV<FractionalPoly> ell = EllipseV.standardEquation(fps[0], fps[1],true, mcfp);
		Line<FractionalPoly> l = Line.generalFormula(fps[2], fps[3],fps[4], mcfp);
		Vector<FractionalPoly> vec = l.directionVector();
		ell.directTanLine(vec.getNumber(0), vec.getNumber(1))
		.forEach(line -> print(sim(line.mapTo(CodePlace::mapperPF, mc))));
	}
	
	
	void m25(){
		inputPoly("1,2,4,Sqr3,82/7");
		EllipseV<FractionalPoly> ell = EllipseV.generalFormula(fps[0], fps[1],fps[2], mcfp);
		Line<FractionalPoly> line = Line.slopeIntercept(fps[3],fps[3], mcfp);
		print(ell);
		print(line);
		print(ell.chordLength(line));
		print(ell.intersectPoints(line));
		print(ell.computeY(fps[4]));
	}
	
	void m26(){
		inputPoly("2,Sqr3,-1/4,1");
		EllipseV<FractionalPoly> e = EllipseV.standardEquation(fps[0], fps[1], true, mcfp);
		print(e.directTanLine(fps[2], fps[3]));
	}
	
	
	
	void m27(){
		inputPoly("3,2,Pi/3");
		EllipseV<FractionalPoly> ell = EllipseV.standardEquation(fps[0], fps[1],true, mcfp);
		print(ell);
		print(ell.trianlgeArea(fps[2], fp -> FractionalPoly.valueOf(new PolyCalculator().tan(fp.getNume()))));
	}
	
	public static FractionalPoly tanF(FractionalPoly fp){
		return FractionalPoly.valueOf(new PolyCalculator().tan(fp.getNume()));
	}
	
	void m28(){
		Line<Fraction> l1 = of(3,-2,5);
		Line<Fraction> l2 = of(5,4,-3);
		Line<Fraction> l3 = of(5,4,-6);
		Point<Fraction> p = l1.intersectPoint(l2);
		Point<Fraction> p2 = Point.valueOf(Fraction.valueOf(1l), Fraction.valueOf(3l), mc);
		print(sim(Line.twoPoint(p, p2, mc)));
		print(l1.symmetryPoint(p2));
		print(l2.intersectPoint(l3));
		print(l2.relationWith(l3));
//		print(l1.mapTo(f -> f.doubleValue(), MathCalculatorAdapter.getCalculatorDouble()));
	}
	
//	void m29(){
//		MathCalculator<Double> mcfp = MathCalculatorAdapter.getCalculatorDouble();
//		inputPoly("0,-1,1,2,Sqr3,3,2Sqr2");
//		Point f1 = Point.valueOf(0d, -Math.sqrt(2), mcfp);
//		Point f2 = Point.valueOf(0d, Math.sqrt(2), mcfp);
//		
//		Point[] ps = new Point[3];
//		for(int i=1;i<4;i++){
//			double d = i;
//			Point<Double> p = Point.valueOf(d, Math.sqrt(1D+d*d), mcfp);
//			p = Triangle.fromVertex(mcfp, f1, f2, p).centerI();
//			ps[i-1] = p;
//		}
//		Circle<Double> c = Circle.threePoints(ps[0], ps[1], ps[2], mcfp);
//		for(int i=1;i<100000000;i*=10){
//			double d = i;
//			Point<Double> p = Point.valueOf(d, Math.sqrt(1D+d*d), mcfp);
//			p = Triangle.fromVertex(mcfp, f1, f2, p).centerI();
//			print(p+" : "+c.relation(p));
//		}
//	}
	void m30(){
		MathCalculator<Double> mc = MathCalculatorAdapter.getCalculatorDouble();
		HyperbolaV<Double> hy = HyperbolaV.standardEquation(2d, 3d/2, false, mc);
		print(hy);
		print(hy.getEccentricity());
	}
	
	@SuppressWarnings("unchecked")
	void m31(){
//		inputPoly("a,b,k,d,x,y,1+k^2,-x+kb-ky,-2yb+b^2+y^2+x^2,a,b,c,aa,bb,cc");
//		mcfp = FractionalPoly.getCalculator(true);
//		EllipseV<FractionalPoly> ell = EllipseV.create0(fps[9], fps[10], fps[11], fps[12], fps[13], fps[14], true, mcfp);
//		Line<FractionalPoly> l = Line.slopeIntercept(fps[2], fps[3], mcfp);
//		QEquation<FractionalPoly> q = ell.createEquationX(l);
//		FractionalPoly re = mcfp.multiply(fps[6], q.rootsMul());
//		re = mcfp.add(re, mcfp.multiply(fps[7], q.rootsSum()));
//		re = mcfp.add(re, fps[8]);
//		Polynomial p = re.getNume();
//		List<Formula>[] times = new List[5];
//		Formula[] tis = new Formula[4];
//		tis[0] = Formula.valueOf("k");
//		tis[1] = Formula.valueOf("k^2");
//		tis[2] = Formula.valueOf("k^3");
//		tis[3] = Formula.valueOf("k^4");
//		Arrays.setAll(times, i-> new LinkedList<>());
//		FormulaCalculator pc = FormulaCalculator.getCalculator();
//		BigDecimal[] bds = new BigDecimal[4];
//		for(int i=0;i<bds.length;i++){
//			bds[i] = BigDecimal.valueOf(i+1);
//		}
//		
//		p.getFormulaList().forEach(f -> {
//			BigDecimal bd = f.getCharacterPower("k");
//			for(int i=0;i<bds.length;i++){
//				if(bds[i].equals(bd)){
//					times[i+1].add(pc.divide(f, tis[i]));
//					break;
//				}
//			}
//		});
//		for(List<?> li : times){
//			print(li);
//		}
//		
//		print(re);
	}
	
	void m32(){
		inputPoly("3,1,Pi/6");
		//�������ݣ�������Բ�Ĺ��캯������a,b����������3,1
		EllipseV<FractionalPoly> ell = EllipseV.standardEquation(fps[0], fps[1], mcfp);
		//����һ����Բ��fps[0] = 3,fps[1] = 1 , mcfp �Ǽ�����
		Point<FractionalPoly> f1 = ell.foci().get(0);
		//ell.foci()����������Բ���������㣬get(0)����󽹵�
		Line<FractionalPoly> line = Line.pointSlope(f1, tanF(fps[2]), mcfp);
		//������󽹵��ֱ�ߡ�
		print(ell);
		print(ell.chordLength(line));
		//���
		Line<FractionalPoly> l2 = ell.chordMPL(line);
		//��ù����ĺ����е��ֱ��
		Point<FractionalPoly> m = l2.intersectPoint(line);
		//��ֱ�߽��㣬�õ����ĵ�����
		print(f1.distance(m));
		//���
	}
	
	void m33(){
//		inputPoly("a,b,c,aa,bb,cc,k,d");
//		HyperbolaV<FractionalPoly> hyp = new HyperbolaV<>()
		Progression<Fraction> pf = ProgressionSup
				.createArithmeticProgression(Fraction.valueOf(1l), Fraction.ONE, mc);
//		pf = pf.limit(100)
//			.computeProgression(mc, f, pro)
//			.stream()
//			.collect(ProgressionSup.getCollector(mc));
////		print(pf.toArray());
		Progression.computeProgression(mc, (Fraction f)->f.multiply(f), pf)
			.limit(100)
			.stream()
			.map(f -> f.minus(1))
			.collect(ProgressionSup.getCollector(mc))
			.forEach(Printer::print);
	}
	
	void m34(){
		inputPoly("3,1");
//		HyperbolaV<FractionalPoly> hv = HyperbolaV.standardEquation(fps[0],fps[1], true, mcfp);
//		Line<FractionalPoly> l = Line.slopeIntercept(fps[2], fps[3], mcfp);
//		print(hv);
//		print(l);
		MathCalculator<Formula> mc = Formula.getCalculator();
		EllipseV<FractionalPoly> ell = EllipseV.standardEquationSqrt(Formula.valueOf("3"), Formula.ONE, true, mc)
				.mapTo(CodePlace::mapperFF2, mcfp);
		print(ell);
		Circle<FractionalPoly> cir = Circle.centerAndRadius(Point.pointO(mc), Formula.valueOf("Sqr6"), mc).mapTo(CodePlace::mapperFF2, mcfp);;
		print(cir);
		Point<FractionalPoly> p = Point.valueOf(Formula.valueOf("cSqr6"), Formula.valueOf("sSqr6"), mc).mapTo(CodePlace::mapperFF2, mcfp);;
		print(p);
		Line<FractionalPoly> line = Line.pointSlope(p, mapperFF2(Formula.valueOf("k")), mcfp);
		print(line);
		QEquation<FractionalPoly> eq = ell.createEquationX(line);
		print(eq);
		FractionalPoly delta = eq.delta();
		print(delta);
	}
	static FractionalPoly mapperFF2(Formula f){
		return FractionalPoly.valueOf(new Polynomial(PolyCalculator.DEFALUT_CALCULATOR.getCal(),f));
	}
	
	
	void m35(){
		
		//first: a parabola
		inputPoly("p,d[y1],d[y2],d[y3]");
		GeneralConicSection<FractionalPoly> parabola = GeneralConicSection.parabola(fps[0], mcfp);
		print(parabola);
		//computes y
		fps[4] = toX(fps[1]);
		fps[5] = toX(fps[2]);
		fps[6] = toX(fps[3]);
		ps[0] = Point.valueOf(fps[4],fps[1] , mcfp);
		Line<FractionalPoly> l1 = parabola.tangentLine(ps[0]);
		Line<FractionalPoly> l2 = parabola.tangentLine(Point.valueOf(fps[5],fps[2] , mcfp));
		Line<FractionalPoly> l3 = parabola.tangentLine(Point.valueOf(fps[6],fps[3] , mcfp));
		Line<FractionalPoly> l4 ;
		print(dealWith(l1.toString()));
		ps[0] = l1.intersectPoint(l2);
		ps[1] = l2.intersectPoint(l3);
		ps[2] = l3.intersectPoint(l1);
		ps[3] = Point.valueOf(mcfp.divideLong(fps[0],2l), mcfp.getZero(), mcfp);
		ps[0] = Simplifier.singleSimplify(sim, ps[0]);
		ps[1] = Simplifier.singleSimplify(sim, ps[1]);
		ps[2] = Simplifier.singleSimplify(sim, ps[2]);
		print(dealWith(ps[0]));
		print(dealWith(ps[1]));
		print(dealWith(ps[2]));
//		if(System.err!=null)
//			return;
		l1 = Line.twoPoint(ps[1], ps[0],mcfp);
		l2 = Line.twoPoint(ps[1], ps[3],mcfp);
		l3 = Line.twoPoint(ps[2], ps[0],mcfp);
		l4 = Line.twoPoint(ps[2], ps[3],mcfp);
		l1 = Simplifier.singleSimplify(sim, l1);
		l1.simplify(sim);
		l2.simplify(sim);
		l3.simplify(sim);
		l4.simplify(sim);
		print("k="+dealWith(l1.slope()));
//		print(dealWith(l1));
//		print(dealWith(l3));
		
		fps[0] = l1.intersectTanDirected(l3);
		fps[1] = l2.intersectTanDirected(l4);
		sim.simplify(Arrays.asList(fps[0]));
		print(fps[0]);
		print(fps[1]);
//		print(fps[1]);
		print("SUM = "+dealWith(mcfp.subtract(fps[0], fps[1])));
		print("SUB = "+dealWith(mcfp.add(fps[0], fps[1])));
	}
	
	FractionalPoly toX(FractionalPoly fp){
		return mcfp.divide(mcfp.multiply(fp, fp),mcfp.multiplyLong(fps[0], 2l));
	}
	
	
	
	void m36(){
		inputPoly("d[x1],d[x2],d[x3],d[y1],d[y2],d[y3],x,y");
		Triangle<FractionalPoly> tri = Triangle.fromVertex(mcfp, fps[0], fps[3], 
				fps[1], fps[4], 
				fps[2], fps[5]);
		Point<FractionalPoly> i = Point.valueOf(fps[6],fps[7], mcfp);
		ps[0] = tri.vertexA();
		ps[1] = tri.vertexB();
		ps[2] = tri.vertexC();
		PVector<FractionalPoly> v1 , v2 ,v3;
		v1 = PVector.vector(ps[0],i).multiplyNumber(Triangle.fromVertex(mcfp, i,ps[1],ps[2]).areaPN());
		v2 = PVector.vector(ps[1],i).multiplyNumber(Triangle.fromVertex(mcfp, i,ps[2],ps[0]).areaPN());
		v3 = PVector.vector(ps[2],i).multiplyNumber(Triangle.fromVertex(mcfp, i,ps[0],ps[1]).areaPN());
		print(dealWith(v1));
		print(dealWith(v2));
		print(dealWith(v3));
		print(PVector.sum(v1,v2,v3));
	}
	
	void m37(){
		Fraction[][] mat = new Fraction[5][6];
		Random rd = new Random();
		for(int i=0;i<5;i++){
			for(int j=0;j<6;j++){
				mat[i][j] = Fraction.valueOf(rd.nextInt(10)+1, 1+rd.nextInt(10));
			}
		}
		
		for(int i=0;i<5;i++){
			print(mat[i]);
		}
		LinearEquationSolution<Fraction> solution = MatrixSup.solveLinearEquation(mat, Fraction.getCalculator());
		print(solution.getSolutionSituation());
		print(solution.getBase());
		print(solution.getSolution());
		Fraction sum = Fraction.ZERO;
		for(int i=0;i<5;i++){
			sum = sum.add(mat[0][i].multiply(solution.getBase().getNumber(i)));
		}
		print("Sum = "+ sum);
	}

	void m38(){
		int d = 4; 
		int len = d*(d+2);
		pos = new Polynomial[len];
		fps = new FractionalPoly[len];
		for(int i=0;i<=d+1;i++){
			for(int j=0;j<d;j++){
				pos[d*i+j] = Polynomial.valueOf("x["+i+","+(j+1)+"]");
			}
		}
		compute();
		@SuppressWarnings("unchecked")
		Vector<FractionalPoly>[] vs = new Vector[d+1];
		for(int i=0;i<=d+1;i++){
			FractionalPoly[] fp = Arrays.copyOfRange(fps, d*i, d*i+d);
			vs[i] = Vector.createVector(mcfp, fp);
		}
		//input part 
		
		
		
		for(int i=1;i<=d+1;i++){
			FractionalPoly[][] mat = new FractionalPoly[d+1][d+1];
			for(int a=0;a<=d;a++){
				for(int b=0;b<d;b++){
//					mat[a][b] = 
					//TODO
				}
			}
			Matrix<FractionalPoly> matrix = Matrix.valueOf(mat, mcfp);
		}
	}
	
	void m39(){
		long[][] mat = new long[][]{
			{1,3,5,7,1},
			{2,4,3,0,2},
			{-3,-7,0,6,8},
			{-5,-12,13,-27,-9}
		};
		Matrix<Long> mat1 = Matrix.valueOf(mat);
		Matrix<Fraction> mat2 = mat1.mapTo(l -> Fraction.valueOf(l), Fraction.getCalculator());
		LinearEquationSolution<Fraction> solution = MatrixSup.solveLinearEquation(mat2);
		print(solution);
		print(solution.getBase());
	}
	
	
	
	
	
	
	
	
}
