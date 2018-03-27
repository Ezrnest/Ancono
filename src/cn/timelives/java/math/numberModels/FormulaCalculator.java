package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.addableSet.MathAdder;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


/**
 * calculator ֻ�ܼ��㵥��formula�ļӼ��˳��Ϳ���
 * @author lyc
 *
 */
public class FormulaCalculator extends MathCalculatorAdapter<Formula> implements  MathAdder<Formula>
{
	
	static final FormulaCalculator DEFAULT_FORMULA_CALCULATOR = new FormulaCalculator();
	
	
	
	
	
	private final int state;
	/*��������״̬��
	 * 0��������С�������㣬��֧�ּ��������ʽ��ʽ��ʾ
	 * 1��֧�ּ�������ȱ�ʾ
	 * 		1ΪĬ��״̬
	 */
	FormulaCalculator(int state){
		this.state=state;
	}
	FormulaCalculator(){
		this(1);
	}
	public int getState() {
		return state;
	}
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.MathCalculator#getNumberClass()
	 */
	@Override
	public Class<Formula> getNumberClass() {
		return Formula.class;
	}
	/*
	 * 0��null������ʽ�����
	 */
	@Override
	public boolean canAdd(Formula f1,Formula f2){
		if(f1==null||f2==null){
			return true;
		}
		if(f1.equalsIgnoreDecimal(Formula.ZERO)||f2.equalsIgnoreDecimal(Formula.ZERO)){
			return true;
		}
		if(f1.haveSameChar(f2)){
			if(this.state==0||(f1.isDecimal()&&f2.isDecimal())){
				return true;//��Ϊ����ΪС��״̬С�����Լ�
			}
			else if(f1.isDecimal()||f2.isDecimal()){
				return false;//��һ����ΪС����һ��ΪС�������Լ�
			}
			else{
				if(f1.getRadical().compareTo(f2.getRadical())==0){
					return true;//������Ϊ�������Ҹ��Ų�����ͬ���Լ�
				}
				else{
					return false;
				}
			}
		}
		else{
			return false;//��ĸϵ����һ�������Լ�
		}
		
	}
	/*
	 * ������null���
	 */
	
	private Formula addFormula(Formula f1,Formula f2){
		if(f1==null||f1.equalsIgnoreDecimal(Formula.ZERO)){
			return (f2==null)?(Formula.ZERO):(f2);
		}
		else if(f2==null||f2.equalsIgnoreDecimal(Formula.ZERO)){
			return f1;
		}
		BigInteger[] ndr = new BigInteger[3];
		if(this.state==0||f1.isDecimal()||f2.isDecimal()){
			throw new ArithmeticException(f1.toString()+"   "+f2.toString()+"---"+state);
		}
		else{
			ndr[0]= ((f1.getSignum()>=0)?(f1.getNumerator()):(f1.getNumerator().negate())).multiply( f2.getDenominator() )
					.add(((f2.getSignum()>=0)?(f2.getNumerator()):(f2.getNumerator().negate())).multiply( f1.getDenominator() ) ) ;
			ndr[1]=f1.getDenominator().multiply(f2.getDenominator());
			ndr[2]=f1.getRadical();
		}
		int signum  =ndr[0].compareTo(BigInteger.ZERO);
		if(signum!=0){
			ndr[0]=ndr[0].abs();
			return Formula.sameCharFormula(signum, ndr, f1);
		}else{
			return Formula.ZERO;
		}
	}
	
	
	/**
	 * 
	 * @param f1
	 * @param f2
	 * @return f1-f2
	 */
	@Override
	public Formula subtract(Formula f1,Formula f2){
		return this.addFormula(f1, f2.negate());
	}
	
	/*multiFormula������
	 * ������ʽ����ˣ���ĸϵ�����
	 * 
	 */
	@Override
	public Formula multiply(Formula f1,Formula f2){
		int signum=0;
		BigInteger[] ndr = new BigInteger[3];
		
		if(this.state==0||f1.isDecimal()||f2.isDecimal()){//������������С��״̬ʱ������С������
			throw new ArithmeticException();
		}
		signum = f1.getSignum()*f2.getSignum();
		if(signum==0){
			return Formula.ZERO;
		}
		ndr[0]=f1.getNumerator().multiply( f2.getNumerator()) ;
		ndr[1]=f1.getDenominator().multiply( f2.getDenominator() );
		ndr[2]=f1.getRadical().multiply( f2.getRadical() ) ;
		
		//������ĸϵ��
		HashMap<String,BigDecimal> ch = new HashMap<String,BigDecimal>(f1.getCharacterS());
		for(Entry<String,BigDecimal> e: f2.getCharacterS().entrySet()){
			Formula.addChar(ch, e.getKey(),e.getValue());
		}
		return Formula.newInstanceP(signum, ndr, ch);
	}
	
	
	/**
	 * return f1/f2 
	 * <p> this method would not change f1 or f2
	 * @param f1
	 * @param f2
	 * @return f1/f2
	 * @throws ArithmeticException if f2.number == 0
	 */
	@Override
	public Formula divide(Formula f1,Formula f2){
//		Formula result = new Formula();
//		if(f2.equalsIgnoreDecimal(Formula.ZERO)){//can't divide by zero
//			throw new ArithmeticException("Can't divide by Zero:  "+f1.toString());
//		}
//		if(this.state==0||f1.isDecimal()||f2.isDecimal()){//������������С��״̬ʱ������С������
//			result.setNumber(f1.getNumber().divide(f2.getNumber()));
//			result.setDecimal(true);
//		}
//		else{
//			result.setPositive(!f1.isPositive()^f2.isPositive());
//			result.setDecimal(false);
//			result.setNumerator(  f1.getNumerator().multiply( f2.getDenominator() )  );  // a1/b1 / (a2/b2) = a1*b2 /(b1*a2) 
//			result.setDenominator(  f1.getDenominator().multiply( f2.getNumerator() ).
//									multiply( f2.getRadical() ).multiply( f2.getRadical() )  );
//			result.setRadical(  f1.getRadical().multiply( f2.getRadical() )  );
//		}
//		//������ĸϵ��
//		result.setCharacter(f1.getCharacter());
//		for(Entry<String,BigDecimal> e : f2.getCharacter().entrySet()){
//			result.addCharacter(e.getKey(), e.getValue().negate());// add negate exp of each character
//		}
//		result.sort();
		return multiply(f1,f2.reciprocal());
	}
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof FormulaCalculator)){
			return ((FormulaCalculator)obj).getState()==this.state;
		}
		return false;
	}
	
	/*
	 * ����ĳ�����ʽ��ƽ������
	 * ��������״̬���� 0 (С��)ֱ�ӿ�������
	 * ��������״̬���� 1 (����)
	 * 		--fΪС����ʾ����f���и��ű��ʽ ��ֱ�ӿ�������
	 * 		--fΪ������ʾ�Ҳ������ű��ʽ���������ֵ
	 */
	
	@SuppressWarnings("unused")
	private Formula sprtFormula0(Formula f){
		Formula result=Formula.ONE;
//		switch (this.state){
//		case 0:{
////			BigDecimal[] characterT=f.getCharacterT().clone();		
////			for(int i=0;i<characterT.length;i++){
////				characterT[i]=characterT[i].divide(new BigDecimal("2"));
////			}
////			result.setCharacter(f.getCharacter().clone(), characterT);
//			Set<String> cha=f.getCharacter().keySet();
//			for(String s:cha){
//				result.addCharacter(s,f.getCharacter().get(s).divide(new BigDecimal("2")));
//			}
//			if(f.getNumber().compareTo(BigDecimal.ZERO)<0){
//				result.addCharacter(Formula.I_STR,BigDecimal.ONE);
//			}
//			result.setNumber(new BigDecimal(Math.sqrt(f.getNumber().abs().doubleValue())));
//			result.setDecimal(true);
//			break;
//		}
//		case 1:{
////			BigDecimal[] characterT=f.getCharacterT().clone();		
////			for(int i=0;i<characterT.length;i++){
////				characterT[i]=characterT[i].divide(new BigDecimal("2"));
////			}
////			result.setCharacter(f.getCharacter().clone(), characterT);
//			Set<String> cha=f.getCharacter().keySet();
//			for(String s:cha){
//				result.addCharacter(s,f.getCharacter().get(s).divide(new BigDecimal("2")));
//			}
//			if(f.isDecimal()||f.getRadical().compareTo(BigInteger.ONE)>0){
//				if(f.getNumber().compareTo(BigDecimal.ZERO)<0){
//					result.addCharacter(Formula.I_STR,BigDecimal.ONE);
//				}
//				result.setNumber(new BigDecimal(Math.sqrt(f.getNumber().abs().doubleValue())));
//				result.setDecimal(true);
//			}
//			else{
//				if(false==f.isPositive()){
//					result.addCharacter(Formula.I_STR,BigDecimal.ONE);
//				}
//				result.setRadical(  f.getDenominator().multiply( f.getNumerator() )  );
//				result.setDenominator( f.getDenominator().abs() );
//			}
//			break;
//		}
//		}
		return result;
	}
	

	
	/**
	 * calculate the square root of the Formula:
	 * if f has radical the method will throw an Exception
	 * @param f
	 * @return
	 */
	@Override
	public Formula squareRoot(Formula f){
		if(f.getSignum()==0){
			return Formula.ZERO;
		}
		if(f.getRadical().equals(BigInteger.ONE) == false){
//			if(f.getRadical().equals(BigInteger.ZERO)){
//				return Formula.ZERO;
//			}
			throw new ArithmeticException();
		}
		HashMap<String,BigDecimal> character = new HashMap<String,BigDecimal>();
		BigDecimal temp = BigDecimal.valueOf(2L);
		for(Entry<String,BigDecimal> e : f.getCharacterS().entrySet()){
			character.put(e.getKey(), e.getValue().divide(temp));
		}
		
		if(f.getSignum()<0){
			character.compute(Formula.I_STR, (s,b) -> b == null ? BigDecimal.ONE : b.add(BigDecimal.ONE));
		}
		return Formula.newInstanceP(1, BigInteger.ONE, f.getDenominator(), 
				f.getDenominator().multiply(f.getNumerator()),character);
	}
	/**
	 * A faster method for square.
	 * @param f
	 * @return
	 */
	public Formula square(Formula f){
		if(f.getSignum()==0){
			return Formula.ZERO;
		}
		int signum = 1;
		HashMap<String,BigDecimal> character = new HashMap<String,BigDecimal>();
		for(Entry<String,BigDecimal> e : f.getCharacterS().entrySet()){
			BigDecimal bd = e.getValue();
			character.put(e.getKey(), bd.add(bd));
		}
		//deal with i
		if (character.containsKey(Formula.I_STR)) {
			BigDecimal[] rd = character.get(Formula.I_STR).divideAndRemainder(new BigDecimal("2"));
			if (rd[1].compareTo(BigDecimal.ZERO) == 0) {
				character.remove(Formula.I_STR);
			} else {
				character.put(Formula.I_STR, rd[1]);
			}
			signum = (rd[0].remainder(new BigDecimal("2")).compareTo(BigDecimal.ZERO) == 0) ? (signum) : (-signum);
		}
		
		
		//only calculate necessary gcd here: 
		BigInteger nume = f.getNumerator().multiply(f.getNumerator());
		BigInteger deno = f.getDenominator().multiply(f.getDenominator());
		BigInteger rad = f.getRadical();
		
		BigInteger temp = deno.gcd(rad);
		rad = rad.divide(temp);
		deno = deno.divide(temp);
		
		nume = nume.multiply(rad);
		return Formula.createWithoutCheck(signum, new BigInteger[]{nume,deno,BigInteger.ONE}, character);
	}
	
	
	
	/*������������������������������������������������������������������������������������������������
	 * ���Խ�һ�����ʽ���мӷ�����
	 * 	�Ὣ���ʽ�����ܵ����..
	 * 
	 
//	public Formula[] addFormula(Formula[] fs){
//		for(int i=0;i<fs.length-1;i++){
//			for(int j=i+1;j<fs.length;j++){
//				if(this.canAdd(fs[i],fs[j])){
//					fs[i]=this.addFormula(fs[i],fs[j]);
//					fs[j]=null;
//				}
//			}
//		}
//		int count=0;
//		for(int i=0;i<fs.length;i++){
//			if(fs[i]!=null&&(!fs[i].equalsIgnoreDecimal(Formula.ZERO))){
//				count++;
//			}
//		}
//		Formula[] result=new Formula[count];
//		count=0;
//		for(int i=0;i<fs.length;i++){
//			if(fs[i]!=null&&(!fs[i].equalsIgnoreDecimal(Formula.ZERO))){				
//				result[count++]=fs[i];
//			}
//		}
//		return result;
//	}
	/*���ʽ���������0�����null
	 * ��һ����˵����Ҫ��addFormula��print
	 * ������ʽ:�ӷ�����
	 
//	public void printExpression(Formula[] fs){
//		if(fs.length==0){
//			System.out.println("0");
//			return;
//		}
//		for(int i=0;i<fs.length;i++){
//			System.out.print(fs[i].toString());
//		}
//		System.out.println();
//	}
	/*
	 * ����������ʽ��
	 
//	public Formula[] multiFormula(Formula[] fs1,Formula[] fs2){
//		Formula[] result=new Formula[fs1.length*fs2.length];
//		for(int i=0;i<fs1.length;i++){
//			for(int j=0;j<fs2.length;j++){
//				result[i*fs2.length+j]=this.multiFormula(fs1[i], fs2[j]);
//			}
//		}
//		result=this.addFormula(result);
//		return result;
//	}
	
//	/**
//	 * ���ڶ�ȡ�����ֻ��-+/*Sqr�������ı��ʽ
//	 
//	public static Formula[] readAddedFormula(String expression){
//		if(expression.trim().isEmpty()){
//			return new Formula[0];
//		}
//		char c;
//		int count=0;
//		for(int i=0;i<expression.length();i++){
//			c=expression.charAt(i);
//			if(i==0){
//				count++;
//			}
//			else if(c=='+'||c=='-'){
//				if(expression.charAt(i-1)!='+'&&expression.charAt(i-1)!='-'){
//					count++;
//				}
//			}
//		}
//		String temp="";
//		Formula[] fs=new Formula[count];
//		count=0;
//		for(int i=0;i<expression.length();i++){
//			c=expression.charAt(i);
//			if(c=='+'||c=='-'){
//				if(i!=0&&expression.charAt(i-1)!='+'&&expression.charAt(i-1)!='-'){
//					fs[count]=new Formula(temp);
//					count++;
//					temp="";
//				}
//			}
//			temp+=c;
//		}
//		fs[count]=new Formula(temp);
//		return fs;
//	}*/
	

	@Override
	public Formula addEle(Formula e1, Formula e2) {
		return this.addFormula(e1,e2);
	}
	@Override
	public boolean isEqual(Formula para1, Formula para2) {
		if(para1==para2)
			return true;
		return para1.equals(para2);
	}
	@Override
	public int compare(Formula para1, Formula para2) {
		if(para1==para2)
			return 0;
		return para1.compareTo(para2);
	}
	@Override
	public Formula add(Formula para1, Formula para2) {
		return addFormula(para1, para2);
	}
	@Override
	public Formula negate(Formula para) {
		return para.negate();
	}
	@Override
	public Formula abs(Formula para) {
		return para.compareTo(Formula.ZERO) > 0 ? para : para.negate();
	}
	@Override
	public Formula getZero() {
		return Formula.ZERO;
	}
	@Override
	public Formula getOne() {
		return Formula.ONE;
	}
	@Override
	public Formula reciprocal(Formula p) {
		return p.reciprocal();
	}
	@Override
	public Formula multiplyLong(Formula p, long l) {
		if(l==0) {
			return Formula.ZERO;
		}
		if(l==1) {
			return p;
		}
		if(l==-1) {
			return p.negate();
		}
		return multiply(p, Formula.valueOf(BigInteger.valueOf(l)));
	}
	@Override
	public Formula divideLong(Formula p, long l) {
		if(l == 0) {
			throw new ArithmeticException("Divide by zero");
		}
		if(l==1) {
			return p;
		}
		if(l==-1) {
			return p.negate();
		}
		return divide(p, Formula.valueOf(BigInteger.valueOf(l)));
	}
	@Override
	public Formula pow(Formula p, long exp) {
		if(exp<0){
			return pow(p.reciprocal(),-exp);
		}else if(exp==0){
			if(p.getSignum()!=0){
				return Formula.ONE;
			}
			throw new ArithmeticException("0^0");
		}
		Formula re = Formula.ONE;
		while(exp!=0){
			if((exp&1l)!=0){
				re = multiply(re, p);
			}
			p = square(p);
			exp>>=1;
		}
		return re;
//		throw new UnsupportedCalculationException();
	}
	
	private static final Formula pi = Formula.valueOf(Formula.PI_STR);
	private static final Formula e = Formula.valueOf(Formula.E_STR);
	
	@Override
	public Formula constantValue(String name) {
		if(name.equalsIgnoreCase(STR_PI)){
			return pi;
		}
		if(name.equalsIgnoreCase(STR_E)){
			return e;
		}
		throw new UnsupportedCalculationException("No constant value avaliable");
	}
	
	@Override
	public Formula exp(Formula a, Formula b) {
		try{
			if((b.getSignum()==0)||(b.getCharacterS().isEmpty() 
					&& b.getDenominator().equals(BigInteger.ONE) 
					&& b.getRadical().equals(BigInteger.ONE))){
				return pow(a,b.getNumerator().intValueExact());
			}
		}catch(ArithmeticException ae){
			throw new UnsupportedCalculationException("Too big");
		}
		
		
		throw new UnsupportedCalculationException("Too complex!");
	}
	

	private static final SimplifierF sp = new SimplifierF();
	
	public static final Simplifier<Formula> getSimplifier(){
		return sp;
	}
	
	static class SimplifierF implements Simplifier<Formula>{
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.Simplifier#simplify(java.lang.Object)
		 */
		@Override
		public Formula simplify(Formula x) {
			return x;
		}
		
		@Override
		public List<Formula> simplify(List<Formula> numbers) {
			Formula[] fs = numbers.toArray(new Formula[]{});
			Formula.gcdAndDivide(fs);
			return Arrays.asList(fs);
		}
		
	}
	
	public static FormulaCalculator getCalculator(){
		return DEFAULT_FORMULA_CALCULATOR;
	}
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////
	//test area
//	public static void main(String[] args) {
////		FractionalPoly fp = FractionalPoly.valueOf(, Polynomial.valueOf("-d[y1]p+d[y3]p"));
//		
//		Formula f1  = Formula.valueOf("-1/16*x^2*y");
//		Formula f2  = Formula.valueOf("1/16*x^2*y");
////		print(sp.simplify(Arrays.asList(f1, f2)));
//		print(DEFAULT_FORMULA_CALCULATOR.canAdd(f1, f2));
//	}
	
	
	
}
