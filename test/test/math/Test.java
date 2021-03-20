package test.math;

import cn.ancono.math.algebra.Progression;
import cn.ancono.math.algebra.ProgressionSup;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.MathCalculator;
import cn.ancono.math.set.Interval;
import cn.ancono.math.set.IntervalI;
import cn.ancono.utilities.Printer;
import cn.ancono.utilities.Timer;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

import static cn.ancono.utilities.Printer.print;
import static cn.ancono.utilities.Printer.print_;

class Test {
	
	
	/*
//	static void intervalT1(){
//		Fraction db = Fraction.valueOf(-10);
//		Fraction ub = Fraction.valueOf(10);
//		OpenedIntervalF in = new OpenedIntervalF(db, ub);
//		print(in.downerBound());
//		print(in.upperBound());
//		print(in.toString());
//		print("Contain:"+in.isInRange(Fraction.valueOf(1, 4)));
//		print_();
//		print(in.downerPart(Fraction.valueOf(5)));
//		print(in.upperPart(Fraction.valueOf(5)));
//		print_();
//		OpenedIntervalF in2 = new OpenedIntervalF(Fraction.valueOf(-2), ub);
//		print(in.isInRange(in2));
//		print_();
////		print(in.expandDownerBound(15));
////		in.
//	}
	
	
	/*
	
	static void t4(){
		Vector<Long> v1 = Vector.createVector(false, new long[]{1,2});
		Vector<Long> v2 = Vector.createVector(false, new long[]{-1,-2});
		print(Vector.areParallel(v1, v2));
	}
	
	static void t3(){
		long n = 7;
		int p = 10;
		MathFunctions.power(n, p);
		long re = MathFunctions.power(n, p);
		print(re);
		
		
	}
	
	static void t2(){
		Matrix<Fraction> mat = Matrix.valueOf(new long[][]{
			{1,1,-1,2,3},
			{2,1,0,-3,1},
			{-2,0,-2,10,4}
		}).mapTo(l -> Fraction.valueOf(l),Fraction.getCalculator());
		LinearEquationSolution ls = MatrixSup.solveLinearEquation(mat);
//		ls.printSolution();
		
		mat = Matrix.valueOf(new long[][]{
			{3,5,1,1},
			{2,0,3,-2},
			{-4,2,-6,6}
		}).mapTo(l -> Fraction.valueOf(l),Fraction.getCalculator());
		ls = MatrixSup.solveLinearEquation(mat);
		ls.printSolution();
		
		
	}
	
	
	
	 * Identify the given expression
	 /
	private static final Pattern p = Pattern.compile(" *([\\+\\-]?\\d+(\\/\\d+)?) *");
	
	
	
	public static void start(){
		Scanner scn= null;
		try{
			while(true){
				try{
					scn = new Scanner(System.in);
					print("Enter row and column");
					int row = scn.nextInt();
					int column = scn.nextInt();
					print("Enter number:");
					scn.nextLine();
					Fraction[][] ma= new Fraction[row][column];
					for(int i=0;i<row;i++){
						String str = scn.nextLine();
						Matcher mach = p.matcher(str);
						for(int j=0;j<column;j++){
							mach.find();
							ma[i][j] = Fraction.valueOf(mach.group(1));
						}
					}
//					Matrix mat = Matrix.valueOf(ma);
					Matrix<Fraction> mat = null;
					mat.printMatrix();
					while(true){
						print_();
						print("Enter operation:");
						try {
							String ope = scn.nextLine();
							if(ope.contains("exc")){
								//exchange column
								print("Enter two column:");
								int r1 = scn.nextInt();
								int r2 = scn.nextInt();
								scn.nextLine();
								mat = mat.exchangeColumn(r1, r2);
								print("Exchange column:"+r1+"<->"+r2);
								
							}else if(ope.contains("exr")){
								//exchange row
								print("Enter two row:");
								int r1 = scn.nextInt();
								int r2 = scn.nextInt();
								scn.nextLine();
								mat = mat.exchangeRow(r1, r2);
								print("Exchange row:"+r1+"<->"+r2);
								
							}else if(ope.contains("mur")){
								//multiply row
								print("Enter row:");
								int l = scn.nextInt();
								scn.nextLine();
								print("Enter multiplier:");
								Matcher mach = p.matcher(scn.nextLine());
								mach.find();
								Fraction fra = Fraction.valueOf(mach.group(1));
								mat = mat.multiplyNumberRow(fra, l);
								print("Multiply row: "+l+" by "+fra.toString());
								
							}else if(ope.contains("muc")){
								//multiply column
								print("Enter column:");
								int l = scn.nextInt();
								scn.nextLine();
								print("Enter multiplier:");
								Matcher mach = p.matcher(scn.nextLine());
								mach.find();
								Fraction fra = Fraction.valueOf(mach.group(1));
								mat = mat.multiplyNumberColumn(fra, l);
								print("Multiply column: "+l+" by "+fra.toString());
								
							}else if(ope.contains("mar")){
								//multiply-add row
								print("Enter row to mul and to add");
								int l1 = scn.nextInt();
								int l2 = scn.nextInt();
								scn.nextLine();
								print("Enter multiplier:");
								String rowa = scn.nextLine();
								Matcher mach = p.matcher(rowa);
								mach.find();
								Fraction fra = Fraction.valueOf(mach.group(1));
								mat = mat.multiplyAndAddRow(fra, l1, l2);
								print("Mul row "+l1+" by "+ fra.toString()+" add to row "+l2);
								
							}else if(ope.contains("mac")){
								//multiply-add column
								print("Enter column to mul and to add");
								int l1 = scn.nextInt();
								int l2 = scn.nextInt();
								scn.nextLine();
								print("Enter multiplier:");
								Matcher mach = p.matcher(scn.nextLine());
								mach.find();
								Fraction fra = Fraction.valueOf(mach.group(1));
								mat = mat.multiplyAndAddColumn(fra, l1, l2);
								print("Mul column "+l1+" by "+ fra.toString()+" add to column "+l2);
								
							}
							
							mat.printMatrix();
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
					}
					
				}catch(RuntimeException ex){
					ex.printStackTrace();
				}
			}
		}finally{
			scn.close();
		}
		
		
		
	}
	
	
	public static void detTest(){
		Printer.reset(System.out);
		Matrix<?> mat = Matrix.valueOf(new long[][]{
			{1,2,3,4,5},{0,0,0,6,7},{0,0,7,8,9}
		});
		mat.printMatrix();
//		print(mat.calDet());
		mat = mat.toUpperTriangle();
		mat.printMatrix();
	}
	
	public static void detTest2(){
		Matrix<?> mat = Matrix.valueOf(new long[][]{
			{1,-1,0,-1,-2},{-1,2,1,3,6},{0,1,1,2,4},{0,-1,-1,1,5}
		});
		mat.printMatrix();
		mat = mat.toUpperTriangle();
		mat.printMatrix();
	}
	
	public static void detTest3(){
		/*
  86 23 35 63 14  5 62  7 89 69 ��
  3 48 30 60 37 50 17 56 85 64 ��
 64 19 40 96 38 11 99 19 58 43 ��
 52 62 78 34 94 13 17 91  1 50 ��
 14 28 99 91 76 96 94 16  0 76 ��
  0 75 26 36  1  3 66 58 81 40 ��
 81 87 51 94 57 28 51 91 69 28 ��
 38 27 36  0 69 61 37 51 62  5 ��
 28 95 43 25 96 79  8 97 43 24 ��
 35 36 71  6 10 63 77 61 40 77
		 /
		MatrixSup.class.getName();
		int n = 10;
		int[][] mat = new int[n][];
		for(int i=0;i<n;i++){
			
			mat[i] = ArraySup.ranArr(n, 10);
		}
//		mat = new int[][]{
//			{0,0, 0, 8, 7, 8,}, 
//			{ 1, 1, 5, 1, 1, 3, },
//			{ 8, 5, 3, 4, 5, 0, },
//			{ 8 ,5 ,8 ,0 ,1 ,3},
//			{ 5, 3, 7, 1, 1, 8,}, 
//			{ 0, 8, 0, 7, 6, 5,}
//		};
		
		Matrix<Fraction> m = Matrix.valueOf(mat).mapTo(l -> Fraction.valueOf(l),Fraction.getCalculator());
		m.printMatrix();
		Timer t = new Timer();
		t.start();
		print(m.calDet());
		print("Use time:"+t.end());
		t.start();
		print(MatrixSup.fastDet(m));
		print("Use time:"+t.end());
		
		
	}
	
	public static void rankTest(){
		Matrix<?> m1 = Matrix.valueOf(new int[][]{
			{3,2,1,1},{1,2,-3,2},{4,4,-2,3}
		});
		m1.printMatrix();
		print("Rank = "+m1.calRank());
		//should be two
		Matrix<?> m2 = Matrix.valueOf(new int[][]{
			{2,-1,3,3},{3,1,-5,0},{4,-1,1,3},{1,3,-13,-6}
		});
		m2.printMatrix();
		print("Rank = "+m2.calRank());
	}
	public static void rankTest2(){
		Matrix<?> m1 = Matrix.valueOf(new int[][]{
			{3,2,1,-1},{0,2,3,0},{-3,4,8,1}
		});
		m1.printMatrix();
		print("Rank = "+m1.calRank());
		//should be two
//		Matrix m2 = Matrix.valueOf(new int[][]{
//			{2,-1,3,3},{3,1,-5,0},{4,-1,1,3},{1,3,-13,-6}
//		});
//		m2.printMatrix();
//		print("Rank = "+m2.calRank());
	}
	
	
	public static void inverseTest(){
		Matrix<Fraction> m1 = Matrix.valueOf(new long[][]{
			{1,2,3},{2,1,2},{1,3,3}
		}).mapTo(l -> Fraction.valueOf(l),Fraction.getCalculator());
		m1.printMatrix();
		List<MatrixOperation<Fraction>> ops = m1.toIdentityWay();
		Matrix<Fraction> mi = Matrix.identityMatrix(m1.column,Fraction.getCalculator());
		mi=mi.doOperation(ops);
		mi.printMatrix();
		Matrix<Fraction> re = Matrix.multiplyMatrix(m1, mi);
	}
	public static void inverseTest2(){
		Matrix<Fraction> m1 = Matrix.valueOf(new long[][]{
			{1,-2,-1,-2},{4,1,2,1},{2,5,4,-1},{1,1,1,1}
		}).mapTo(l -> Fraction.valueOf(l),Fraction.getCalculator());
		m1.printMatrix();
		List<MatrixOperation<Fraction>> ops = m1.toIdentityWay();
		Matrix<Fraction> mi = Matrix.identityMatrix(m1.column,Fraction.getCalculator());
		mi=mi.doOperation(ops);
		mi.printMatrix();
	}
	
	public static void inverstTest3(){
		Matrix<Fraction> m = MatrixSup.upperTriWithOne(5);
		m.printMatrix();
		Matrix<Fraction> im = m.inverse();
		im.printMatrix();
		Matrix<Fraction> mul = Matrix.multiplyMatrix(m, im);
		mul.printMatrix();
	}
	
	public static void stepTest1(){
		Matrix<Fraction> m1 = Matrix.valueOf(new long[][]{
			{1,-1,-1,2},{2,-2,-3,1},{3,2,-5,0}
		}).mapTo(l -> Fraction.valueOf(l),Fraction.getCalculator());
		m1.printMatrix();
		MatResult<Fraction> mr = m1.toStepMatrix();
		mr.result.printMatrix();
		for(MatrixOperation<Fraction> op : mr.ops){
			print(op.toDetail());
		}
	}
	public static void stepTest2(){
		Matrix<Fraction> m1 = Matrix.valueOf(new long[][]{
			{1,1,1,1,1,},
			{2,3,1,1,-3},
			{1,0,2,2,6},
			{4,5,3,3,-1}
		}).mapTo(l -> Fraction.valueOf(l),Fraction.getCalculator());
		m1.printMatrix();
		MatResult<Fraction> mr = m1.toStepMatrix();
		mr.result.printMatrix();
		for(MatrixOperation<Fraction> op : mr.ops){
			print(op.toDetail());
		}
	}
	public static void stepTest(){
		Matrix<Fraction> m1 = Matrix.valueOf(new long[][]{
			{2,1,0,0,0},
			{1,2,0,0,0},
			{1,2,1,0,0},
			{0,-3,0,1,0},
			{3,0,0,0,1}
		}).mapTo(l -> Fraction.valueOf(l),Fraction.getCalculator());;
		m1.printMatrix();
		MatResult<Fraction> mr = m1.toStepMatrix();
		List<MatrixOperation<Fraction>> ops = m1.toIdentityWay();
		mr.result.printMatrix();
		for(MatrixOperation<Fraction> op : mr.ops){
			print(op.toDetail());
		}
	}
		
	
	
	public static void toNormativeTest(){
		Matrix<?> m1 = Matrix.valueOf(new long[][]{
			{1,-1,-1,2},{2,-2,-3,1},{3,2,-5,0}
		}).mapTo(l -> Fraction.valueOf(l), Fraction.getCalculator());
		m1.printMatrix();
		MatResult<?> mr = m1.toStepMatrix();
		mr.result.printMatrix();
	}
	*/
	
	static <T> void progerssionTest(T a){
        Progression<Long> p2 = ProgressionSup.asFirstElementAndDifferece(1L, 1L, Calculators.longCal());
        p2 = Progression.combinedProgression(Calculators.longCal(), ls -> ls[0] * ls[0], p2);
        p2.limit(100).stream().forEachOrdered(l -> print(l));

    }
	
	static void progerssionTest2() {
        Progression<Long> p1 = ProgressionSup.asFirstElementAndDifferece(1L, 1L, Calculators.longCal());
        Progression<Double> p2 = p1.mapTo(l -> Math.log(Math.abs(l * l - l) + 2), Calculators.doubleCal());
        p2 = Progression.computeProgression(p2.getMathCalculator(), l -> Math.cos(l * 2 - l * l), p2);

        Progression<BigDecimal> pr = p2.mapTo(d -> new BigDecimal(d * d * d - d * d), Calculators.bigDecimal(MathContext.DECIMAL128));

        Progression<BigDecimal> prc = Progression.cachedProgression(pr, 0, 100, true);
        Timer t = new Timer();
        t.start();
        for (int i = 0; i < 100; i++) {
            pr.get(i);
        }
        print(t.end());
        t.start();
        for (int i = 0; i < 100; i++) {
			prc.get(i);
		}
		print(t.end());
		prc.limit(105).iteratorFrom(95).forEachRemaining(bd -> print(bd.toString()));
		print_();
		pr.limit(105).iteratorFrom(95).forEachRemaining(bd -> print(bd.toString()));
	}
	
	static void progerssionTest3() {
        Progression<Long> p1 = ProgressionSup.
                asFirstElementAndDifferece(1L, 2L, Calculators.longCal());
        Progression<Long> p2 = ProgressionSup.
                createGeometricProgression(1L, 2L, Calculators.longCal());
        Progression<Long> pr = Progression.combinedProgression(Calculators.longCal(), ls -> ls[0] * ls[1], p1, p2);
        pr.limit(20).forEach(l -> print(l));
        print("sum = " + pr.sumOf(0, 20));
    }
	
	static void progressionTest4() {
        MathCalculator<Long> mc = Calculators.longCal();
        Progression<Long> p0 = Progression.createProgression(l -> l * l - 10 * l - 100, -1, mc);
        Interval<Long> iv = new IntervalI<>(mc, null, 10L, false, true);
        p0.limit(100).stream().filter(l -> iv.contains(l)).forEach(l -> print(l));
    }
	
	static void progressionTest5() {
        MathCalculator<Double> mc = Calculators.doubleCal();
        Progression<Double> ps = Progression.createProgressionRecur1(d -> (d * (3.5d - d)), 0.7d, -1, mc);
        ps.stream().limit(30).forEach(Printer::print);
    }
	
	static void progressionSum(){
		MathCalculator<Fraction> mc = Fraction.getCalculator();
        Progression<Fraction> a = Progression.createProgression(
                l -> Fraction.of(l).add(Fraction.ONE).squareOf().reciprocal(), -1, mc);
        Progression<Fraction> b = Progression.createProgression(
                value -> {
                    Fraction sum = Fraction.ONE;
                    for (long l = 1; l < value; l++) {
                        sum = sum.multiply(Fraction.ONE.minus(a.get(l)));
                    }
                    return sum;
                }, -1, mc);
		a.limit(20).forEach(Printer::print);
		b.limit(20).forEach(Printer::print);
	}
	
	static void progressionDemo() {

        MathCalculator<Double> mc = Calculators.doubleCal();
        Progression<Double> ps = Progression.
                createProgression(Math::sqrt, -1, mc);
//		Progression<Long> p2 = ps.mapTo(d -> Math.round(d),
//				Calculators.getCalculatorLong());
//		p2.limit(100).forEach(Printer::print);
        Interval<Double> iv = new IntervalI<Double>(mc, 4d, 7d, true, true);
        ps.limit(100).stream()
                .filter(iv::contains)
                .forEach(Printer::print);
        ;
		
		
		
	}
	
	static void progression6() {
        MathCalculator<Long> mc = Calculators.longCal();
        Progression<Long> p1 = ProgressionSup.createArithmeticProgression(5L, 0L, mc);
        Progression<Long> p2 = Progression.createPeriodicProgression(
                Arrays.copyOf(p1.limit(25).toArray(), 25, Long[].class), mc, Progression.UNLIMITED);
        p2.limit(100).forEach(Printer::print);

    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
