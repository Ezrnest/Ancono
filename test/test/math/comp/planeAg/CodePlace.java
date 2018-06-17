/**
 * 2017-10-29
 */
package test.math.comp.planeAg;

import cn.timelives.java.math.equation.SVPEquation.QEquation;
import cn.timelives.java.math.function.QuadraticFunction;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.expression.ExprCalculator;
import cn.timelives.java.math.numberModels.expression.Expression;
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies;
import cn.timelives.java.math.geometry.analytic.planeAG.*;
import cn.timelives.java.math.geometry.analytic.planeAG.Point;
import cn.timelives.java.math.geometry.analytic.planeAG.curve.EllipseV;
import cn.timelives.java.math.geometry.visual.visual2D.PlaneDrawer;
import test.math.comp.studyUtils.Run;
import test.math.comp.studyUtils.StudyMethodRunner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static cn.timelives.java.utilities.Printer.print;

/**
 * @author liyicheng
 * 2017-10-29 10:52
 *
 */
public final class CodePlace {
	static final ExprCalculator mc = ExprCalculator.getInstance();
	static final MathCalculator<Double> mcd = Calculators.getCalculatorDoubleDev();
	Point<Expression> A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T;
	PVector<Expression> v1,v2,v3,v4,v5,v6;
	Line<Expression> l1,l2,l3,l4,l5,l6;
	Expression x,y,z,a,b,c,d,e,f,t,θ,α,β,k;
	/**
	 * 
	 */
	public CodePlace() {
		sp.setShowInput(true);
		sp.giveCodePlane(this);
		
	}
	PlaneObjectHandler<Expression> sp = new PlaneObjectHandler<>(CodePlace::of,
			mc);
	
	public static Expression of(String str) {
		return Expression.fromMultinomial(Multinomial.valueOf(str));
	}
	static void throwFor(){
	    throw new RuntimeException();
    }
	public static void main(String[] args) {
		StudyMethodRunner.runStudyClass(CodePlace.class);
	}
	/**
	 * Point
	 * <pre>p:A(x,y)</pre>
	 * Line
	 * <pre>l:a(x,y)v(vx,vy)</pre>
	 * Vector
	 * <pre>v:v(x,y)</pre>
	 * Number
	 * <pre>x=a</pre>
	 */
	private void input(String str) {
		sp.input(str);
	}
	
	private void enableSimplify() {
		SimplificationStrategies.setCalRegularization(mc);
	}

	private EllipseV<Expression> standardEllipse(){
		Expression a = of("a"),
				b = of("b");
		return EllipseV.standardEquation(a, b, true, mc);
	}

//	@Run
	public void m1() {
		input("p:A(-3,-3)|x=-4|y=7");
		Circle<Expression> cir = Circle.generalFormula(x, x, y, mc);
		print(cir);
		print(A);
		for(Line<Expression> l : cir.tangentLines(A)) {
			print(l);
			print(l.slope());
			B = cir.intersectPoints(l).get(0);
			print(A.distance(B));
		}
	}
	
//	@Run
	public void m2() {
		input("a=a|b=b|x=x");
		input("v:v1(k,1)");
        enableSimplify();
		EllipseV<Expression> cur = EllipseV.standardEquation(a, b, true, mc);
		y = cur.computeY(x);
		A = Point.valueOf(x, y, mc);
		l1 = Line.pointDirection(A, v1);
		l2 = Line.pointNormal(A, v1);
		B = cur.intersectPointAnother(A, l1);
		C = cur.intersectPointAnother(A, l2);
		l3 = Line.twoPoint(B, C);
		print(l3);
	}
	
	public void m3() {
		input("a=a|b=b|θ=θ|k=k|d=d");
		EllipseV<Expression> cur = EllipseV.standardEquation(a, b, true, mc);
		l1 = Line.slopeIntercept(k, d, mc);
		QEquation<Expression> equation = cur.createEquationX(l1);
		
	}
	static void saveImage(BufferedImage image,String name) {
		File f = new File("C:\\Users\\liyicheng\\Desktop\\新建文件夹",name+".png");
		try {
			OutputStream out = Files.newOutputStream(
					f.toPath(), StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE);
			ImageIO.write(image, "png",out);
			out.close();
			print("Image: ["+name+"] is saved");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Run
	public void m4() {
		PlaneDrawer drawer = new PlaneDrawer();
		double a = 0.24,b=0.5;
		drawer.addCurve(Color.BLUE,Utilities.mapToDouble(EllipseV.generalFormula(a, b, 1d, mcd)));
		drawer.addCurve(Color.RED, (x,y)->{
			double x2 = Math.pow(x, 2),y2 = Math.pow(y, 2);
			return Math.pow(x, 4)+Math.pow(y, 4)+2*x2*y2-a*x2-b*y2;
		});
		Rectangle2D.Double rect = new Rectangle2D.Double(-3, -3, 6, 6);
		saveImage(drawer.draw(rect, 500,500),"1");
	}

//	@Run
	public void m5(){
	    input("x=x|y=y|a=a|b=b");
	    enableSimplify();
		var ell = standardEllipse();
		P = Point.valueOf(x,y,mc);
		l1 = ell.polarLine(P);
        var list = ell.intersectPoints(l1);
        E = list.get(0);
        F = list.get(1);

		l3 = Line.twoPoint(P,E);
		l4 = Line.twoPoint(P,F);

		I = Point.valueOf(a,b,mc);

		l3 = l3.parallel(I);
		l4 = l4.parallel(I);

        list = ell.intersectPoints(l3);
		A = list.get(0);
		B = list.get(1);
        list = ell.intersectPoints(l4);
        C = list.get(0);
        D = list.get(1);
//            PE^2/PF^2 - AI*IB / CI*ID = 0
        var PE = P.distanceSq(E);
        var CI = C.distance(I);
        var ID = I.distance(D);
        var PF = P.distanceSq(F);
        var AI = A.distance(I);
        var IB = I.distance(B);

        var result = mc.subtract(mc.multiplyX(PE,CI,ID),mc.multiplyX(PF,AI,IB));
        print("result = "+result);
	}

	public void m6(){
        input("x=A|y=B|c=c|d=d");
		var ell = standardEllipse();
		enableSimplify();
		l1 = Line.slopeIntercept(x,c,mc);
		l2 = Line.slopeIntercept(y,d,mc);
		var list = ell.intersectPoints(l1);
		A = list.get(0);
		B = list.get(1);
		list = ell.intersectPoints(l2);
		C = list.get(0);
		D = list.get(1);

		I = l1.intersectPoint(l2);
		print(mc.multiply(A.distance(I),B.distance(I)));
    }


	public void m7(){
		input("a=A|b=B|c=C");
		input("p:P(x,y)");
		l1 = Line.generalFormula(a,b,c,mc);
		l2 = l1.perpendicular(P);
		print(l1.intersectPoint(l2));
	}

//	@Run
	public void m8(){
		/*
		high school entrance exam real exercise:
		2011.23
		 */
		PlaneDrawer drawer = new PlaneDrawer();
		var A = Point.valueOf(1d,3d,mcd);
		var B = Point.valueOf(1d,0d,mcd);
		var C = Point.valueOf(-1d,3d,mcd);
		var D = Point.valueOf(-1d,0d,mcd);
		var AB = Segment.twoPoints(A,B);
		var CD = Segment.twoPoints(C,D);
		drawer.addCurve(Color.BLUE,Utilities.mapToDouble(AB));
		drawer.addCurve(Color.BLUE,Utilities.mapToDouble(CD));
		drawer.addCurve(Color.RED,(x,y)->{
			var p = Point.valueOf(x,y,mcd);
			return AB.distanceSq(p)-CD.distanceSq(p);
		});
		Rectangle2D.Double rect = new Rectangle2D.Double(-5, -5, 10, 10);
		saveImage(drawer.draw(rect, 500,500),"1");

		var D2 = Point.valueOf(-1d,-2d,mcd);
		var CD2 = Segment.twoPoints(C,D2);
		drawer = new PlaneDrawer();
		drawer.addCurve(Color.BLUE,Utilities.mapToDouble(AB));
		drawer.addCurve(Color.BLUE,Utilities.mapToDouble(CD2));
		drawer.addCurve(Color.RED,(x,y)->{
			var p = Point.valueOf(x,y,mcd);
			return AB.distanceSq(p)-CD2.distanceSq(p);
		});
		saveImage(drawer.draw(rect, 500,500),"2");

		var A3 = Point.valueOf(0d,1d,mcd);
		var O = Point.pointO(mcd);
		var D3 = Point.valueOf(2d,0d,mcd);
		var AB3 = Segment.twoPoints(A3,O);
		var CD3 = Segment.twoPoints(O,D3);
		drawer = new PlaneDrawer();
		drawer.addCurve(Color.BLUE,Utilities.mapToDouble(AB3));
		drawer.addCurve(Color.BLUE,Utilities.mapToDouble(CD3));
		drawer.addCurve(Color.RED,(x,y)->{
			var p = Point.valueOf(x,y,mcd);
			return AB3.distanceSq(p)-CD3.distanceSq(p);
		});
		saveImage(drawer.draw(rect, 500,500),"3");
	}
	/**题目：
	 * 抛物线，直线l，交于AB，AB中点为M，
	 * 过M垂直于x轴作l2交抛物线于G，
	 * 抛物线上任取点P，AP,BP分交l2于E,F,
	 * 证明G为EF中点
	 * y = ax^2 + bx + c
	 * y = kx + d	                      */
//	@Run
	public void m9(){
		input("a=a|b=b|c=c|k=k|d=d|x=x");
		//将题目所用变量输入到程序里
		enableSimplify();
		//启用化简
		var parabola = QuadraticFunction.generalFormula(a,b,c,mc);//QuadraticFunction:二次函数
		//创建抛物线：y = ax^2 + bx + c
		print(parabola.toConicSection());//.toConicSection():将二次函数表示成圆锥曲线(ConicSection)
		//输出抛物线解析式，
		var line = Line.slopeIntercept(k,d,mc);
		//创建直线：y = kx + d
		var list = parabola.toConicSection().intersectPoints(line);
		//获得抛物线与直线的交点，intersect points:交点
		A = list.get(0);
		B = list.get(1);
		//将交点命名为A，B
		M = A.middle(B);
		print(M);
		//M为AB中点
		l2 = Line.parallelY(M.x,mc);//M.x 获得M的横坐标
		//过M作平行于y轴直线l2, parallelY : 平行于y轴（垂直于x轴）
		G = parabola.getPoint(M.x);
		//l2与抛物线交点
		P = parabola.getPoint(x);
		//任取一点P，横坐标为x
		var AP = Line.twoPoint(A,P);
		//两点创建直线AP
		var BP = Line.twoPoint(B,P);
		//两点创建直线BP
		E = AP.intersectPoint(l2);
		//AP，l2交点E
		F = BP.intersectPoint(l2);
		//BP，l2交点F
		print(l2);
		//输出l2表达式
		N = E.middle(F);
		//得到EF中点N
		print("A:"+A);
		print("G:"+G);
		print("N:"+N);
		//输出A，G，N点坐标
		print("NG"+N.directVector(G));//directVector：方向向量
		//输出NG的向量，为0则说明EF中点就是G
	}

//	@Run
	public void m10(){
		var ell = EllipseV.standardEquation(2d,1d,mcd);
		print(ell);
		var line = Line.slopeIntercept(1.666d,0.328d,mcd);
		print(line);
		var list = ell.intersectPoints(line);
		var A = list.get(0);
		var B = list.get(1);
		var M = A.middle(B);
		var l2 = Line.parallelY(M.x,mcd);
		double[] vals = {1.5,1.3,1,0.5,0.3,-0.3,-0.5,-1};
		for(double x : vals){
			var P1 = Point.valueOf(x,ell.computeY(x),mcd);
			var P2 = Point.valueOf(x,-ell.computeY(x),mcd);
			var AP = Line.twoPoint(A,P1);
			var BP = Line.twoPoint(B,P2);
			var E = AP.intersectPoint(l2);
			var F = BP.intersectPoint(l2);
			print(E.distance(M)*F.distance(M));
		}

	}

	@Run
	public void m11(){
		input("k=k|d=d|x=x");
		enableSimplify();
		var ell = standardEllipse();
		print(ell);
		l1 = Line.slopeIntercept(k,d,mc);
		print(l1);
		var list = ell.intersectPoints(l1);
		var A = list.get(0);
		var B = list.get(1);
		var M = A.middle(B);
		var l2 = Line.parallelY(M.x,mc);
		var P1 = Point.valueOf(x,ell.computeY(x),mc);
		var P2 = Point.valueOf(P1.x,mc.negate(P1.y),mc);
		var AP1 = Line.twoPoint(A,P1);
		var BP2 = Line.twoPoint(B,P2);
		var E = AP1.intersectPoint(l2);
		var F = BP2.intersectPoint(l2);
		var result = mc.multiply(mc.subtract(E.y,M.y),mc.subtract(F.y,M.y));
		print(result);
		print(result.computeDouble( ch -> {
		    switch (ch){
                case "a" : return 2d;
                case "b" : return 1d;
                case "k" : return 1.666d;
                case "d" : return 0.328d;
            }
            return 1d;
        }));

	}

}
