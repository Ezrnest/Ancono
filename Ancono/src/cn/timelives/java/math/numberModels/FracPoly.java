package cn.timelives.java.math.numberModels;

import static cn.timelives.java.utilities.Printer.print;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;;

/**
 * A fraction use polynomial as its numerator and denominator.
 * @author lyc
 *
 */
public class FracPoly {
	
	private final Polynomial nume,deno;
	
	public static final FracPoly ZERO = new FracPoly(Polynomial.ZERO, Polynomial.ONE);
	public static final FracPoly ONE = new FracPoly(Polynomial.ONE, Polynomial.ONE);
	
	
	FracPoly(Polynomial nume,Polynomial deno){
		this.nume = nume;
		this.deno = deno;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return nume.hashCode() * 31 +deno.hashCode();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof FracPoly)) {
			return false;
		}
		FracPoly fp = (FracPoly) obj;
		return nume.equals(fp.nume) && deno.equals(fp.deno);
	}
	
	@Override
	public String toString(){
		if(deno.equals(Polynomial.ONE)){
			return nume.toString();
		}
		if(nume.equals(Polynomial.ZERO)){
			return "0";
		}
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(nume.toString()).append(')');
		sb.append('/').append('(');
		sb.append(deno.toString()).append(')');
		return sb.toString();
	}
	
	public Polynomial getNume(){
		return nume;
	}
	public Polynomial getDeno(){
		return deno;
	}
	
	/**
	 * Returns a FracPoly that is equal to {@code nume/deno}.This method will not try to simplify the fraction.
	 * @param nume 
	 * @param deno!=0
	 * @return
	 */
	public static FracPoly valueOf(Polynomial nume,Polynomial deno){
		if(Polynomial.ZERO.equals(deno)){
			throw new ArithmeticException("Deno = 0");
		}
		return new FracPoly(requireNonNull(nume).clone(), requireNonNull(deno).clone());
	}
	
	public static FracPoly valueOf(Polynomial p){
		return new FracPoly(requireNonNull(p),Polynomial.ONE);
	}
	
	private static final FPCalculator cal = new FPCalculator();
	private static final FPCalculator calD = new FPCalculatorD();
	
	/**
	 * Get a calculator for FracPoly,the calculator will try to simplify the fraction while calculating.
	 * @return a calculator for FracPoly
	 */
	public static MathCalculator<FracPoly> getCalculator(){
		return cal;
	}
	/**
	 * Get a calculator for FracPoly,if {@code disableCompare == true},the calculator will 
	 * throw an UnsupportedCalculationException in {@code compare()} method.
	 * @param disableCompare whether to disable compare
	 * @return a calculator for FracPoly
	 */
	public static MathCalculator<FracPoly> getCalculator(boolean disableCompare){
		if(disableCompare){
			return calD;
		}
		return cal;
	}
	
	static class FPCalculator extends MathCalculatorAdapter<FracPoly>{
		
		
		
		protected PolyCalculator pc = new PolyCalculator(1);
		protected final FracPoly pi;
		protected final FracPoly e;
		private FPCalculator() {
			pi = FracPoly.valueOf(pc.constantValue(STR_PI));
			e = FracPoly.valueOf(pc.constantValue(STR_E));
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculatorAdapter#isZero(java.lang.Object)
		 */
		@Override
		public boolean isZero(FracPoly para) {
			return pc.isEqual(para.nume, Polynomial.ZERO);
		}
		
		
		@Override
		public boolean isEqual(FracPoly para1, FracPoly para2) {
			return pc.multiply(para1.nume, para2.deno).equals(pc.multiply(para1.deno, para2.nume));
		}

		@Override
		public int compare(FracPoly para1, FracPoly para2) {
//			throw new UnsupportedCalculationException("Cannot compare polynomial");
			Polynomial p1 = pc.multiply(para1.nume, para2.deno);
			Polynomial p2 = pc.multiply(para2.nume, para1.deno);
			return pc.compare(p1, p2);
		}
		
		private FracPoly createPoly(Polynomial re1,Polynomial re2){
			if(re1.equals(Polynomial.ZERO)){
				re2 = Polynomial.ONE;
			}else if(re2.getNumOfFormula()==1){
				re1 = pc.divide(re1, re2);
				re2 = Polynomial.ONE;
			}else {
				//try divide
				try {
					re1 = pc.divide(re1, re2);
					re2 = Polynomial.ONE;
				}catch(UnsupportedCalculationException ex) {}
			}
			return new FracPoly(re1, re2); 
		}
		
		
		@Override
		public FracPoly add(FracPoly para1, FracPoly para2) {
			Polynomial re1 = pc.add(pc.multiply(para1.nume, para2.deno), pc.multiply(para1.deno, para2.nume));
			Polynomial re2 = pc.multiply(para1.deno, para2.deno);
			return createPoly(re1, re2);
		}

		@Override
		public FracPoly negate(FracPoly para) {
			return new FracPoly(pc.negate(para.nume), para.deno);
		}

		@Override
		public FracPoly abs(FracPoly para) {
			return new FracPoly(pc.abs(para.nume), pc.abs(para.deno));
		}

		@Override
		public FracPoly subtract(FracPoly para1, FracPoly para2) {
			Polynomial re1 = pc.subtract(pc.multiply(para1.nume, para2.deno), pc.multiply(para1.deno, para2.nume));
			Polynomial re2 = pc.multiply(para1.deno, para2.deno);
			return createPoly(re1, re2);
		}

		@Override
		public FracPoly getZero() {
			return FracPoly.ZERO;
		}

		@Override
		public FracPoly multiply(FracPoly para1, FracPoly para2) {
			Polynomial p1n = para1.nume;
			Polynomial p1d = para1.deno;
			Polynomial p2n = para2.nume;
			Polynomial p2d = para2.deno;
			try{
				p1n = pc.divide(p1n, p2d);
				p2d = Polynomial.ONE;
			}catch(UnsupportedCalculationException ex){
			}
			try{
				p2n = pc.divide(p2n, p1d);
				p1d = Polynomial.ONE;
			}catch(UnsupportedCalculationException ex){
			}
			
			Polynomial re1 = pc.multiply(p1n, p2n);
			Polynomial re2 = pc.multiply(p1d, p2d);
			return createPoly(re1, re2);
		}

		@Override
		public FracPoly divide(FracPoly para1, FracPoly para2) {
			if(para2.nume.equals(Polynomial.ZERO)){
				throw new ArithmeticException("Divide by zero: /0");
			}
			Polynomial p1n = para1.nume;
			Polynomial p1d = para1.deno;
			Polynomial p2n = para2.deno;
			Polynomial p2d = para2.nume;
			try{
				p1n = pc.divide(p1n, p2d);
				p2d = Polynomial.ONE;
			}catch(UnsupportedCalculationException ex){
			}
			try{
				p2n = pc.divide(p2n, p1d);
				p1d = Polynomial.ONE;
			}catch(UnsupportedCalculationException ex){
			}
			
			Polynomial re1 = pc.multiply(p1n, p2n);
			Polynomial re2 = pc.multiply(p1d, p2d);
			
			return createPoly(re1, re2);
			
		}

		@Override
		public FracPoly getOne() {
			return FracPoly.ONE;
		}

		@Override
		public FracPoly reciprocal(FracPoly p) {
//			pc.reciprocal(p);
			Polynomial re1 = p.deno,re2 = p.nume;
			return createPoly(re1, re2);
		}

		@Override
		public FracPoly multiplyLong(FracPoly p, long l) {
			return new FracPoly(pc.multiplyLong(p.nume, l),p.deno);
		}

		@Override
		public FracPoly divideLong(FracPoly p, long l) {
			if(p.deno.getNumOfFormula()==1){
				return new FracPoly(pc.divideLong(p.nume, l),p.deno);
			}
			return new FracPoly(p.nume,pc.multiplyLong(p.deno, l));
		}

		@Override
		public FracPoly squareRoot(FracPoly p) {
			return new FracPoly(pc.squareRoot(p.nume),pc.squareRoot(p.deno));
		}

		@Override
		public FracPoly pow(FracPoly p, long exp) {
			return new FracPoly(pc.pow(p.nume, exp), pc.pow(p.deno, exp));
		}
		
		
		
		@Override
		public FracPoly constantValue(String name) {
			if(name.equalsIgnoreCase(STR_PI)){
				return pi;
			}
			if(name.equalsIgnoreCase(STR_E)){
				return e;
			}
			throw new UnsupportedCalculationException("No constant value avaliable");
		}

		@Override
		public FracPoly exp(FracPoly a, FracPoly b) {
			throw new UnsupportedCalculationException();
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.MathCalculator#getNumberClass()
		 */
		@Override
		public Class<FracPoly> getNumberClass() {
			return FracPoly.class;
		}
		
	}
	
	static class FPCalculatorD extends FPCalculator{
		@Override
		public int compare(FracPoly para1, FracPoly para2) {
			throw new UnsupportedCalculationException("Cannot compare polynomial");
		}
	}
	
	public static Simplifier<FracPoly> getSimplifier(){
		return simfp;
	}
	private static final SimplifierFP simfp = new SimplifierFP();
	
	static class SimplifierFP implements Simplifier<FracPoly>{
		
		private final Simplifier<Polynomial> simp = PolyCalculator.getSimplifier();
		
		@Override
		public List<FracPoly> simplify(List<FracPoly> numbers) {
			//find the GCD 
			FracPoly[] fps = numbers.toArray(new FracPoly[]{}); 
			
			simplifyFraction(fps);
			List<Polynomial> pns = new ArrayList<>(fps.length);
			List<Polynomial> pds = new ArrayList<>(fps.length);
			for(FracPoly fp : fps){
				pns.add(fp.nume);
				pds.add(fp.deno);
			}
			pns = simp.simplify(pns);
			pds = simp.simplify(pds);
			
			Polynomial ds = Polynomial.ONE;
			PolyCalculator pc = PolyCalculator.DEFAULT_CALCULATOR;
			for(Polynomial p : pds){
				ds = pc.multiply(ds, p);
			}
			
			for(int i=0;i<fps.length;i++){
				Polynomial n = pns.get(i);
				Polynomial m ;
				try{
					m = pc.divide(ds, pds.get(i));
				}catch(UnsupportedCalculationException uoe){
					m = Polynomial.ONE;
					for(int j=0;j<fps.length;j++){
						if(j==i)
							continue;
						m = pc.multiply(m, pds.get(j));
					}
				}
				n = pc.multiply(n, m);
				pns.set(i, n);
				pds.set(i, Polynomial.ONE);
			}
			pns = simp.simplify(pns);
			
			for(int i=0;i<fps.length;i++){
				fps[i] = FracPoly.valueOf(pns.get(i), pds.get(i));
			}
			
			return Arrays.asList(fps);
			
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.Simplifier#simplify(java.lang.Object)
		 */
		@Override
		public FracPoly simplify(FracPoly x) {
			List<Polynomial> nd = Arrays.asList(x.nume,x.deno);
			nd = PolyCalculator.getSimplifier().simplify(nd);
			return new FracPoly(nd.get(0), nd.get(1));
		}
		
		private void simplifyFraction(FracPoly[] fps){
			List<Polynomial> list = new ArrayList<>(2);
			list.add(null);
			list.add(null);
			for(int i =0 ;i < fps.length;i++){
				list.set(0, fps[i].nume);
				list.set(1, fps[i].deno);
				List<Polynomial> re = simp.simplify(list);
				fps[i] = FracPoly.valueOf(re.get(0), re.get(1));
			}
			return;
		}
		
		
		
	}
	
	
}
