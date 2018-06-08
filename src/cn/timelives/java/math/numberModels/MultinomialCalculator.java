package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;

import java.math.BigInteger;
import java.util.*;

import static cn.timelives.java.math.numberModels.Multinomial.*;
public class MultinomialCalculator implements MathCalculator<Multinomial>,NTCalculator<Multinomial> {
    private static Pair ofVal(long n, long d){
        return new Pair(BigInteger.valueOf(n),BigInteger.valueOf(d));
    }



    private static class Pair{
        final BigInteger n,d;
        Pair(BigInteger n,BigInteger d){
            this.n = n;
            this.d = d;
        }
        @Override
        public int hashCode() {
            return n.hashCode() * 31 + d.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Pair){
                Pair p = (Pair) obj;
                return n.equals(p.n)&&d.equals(p.d);
            }
            return false;
        }
        @Override
        public String toString() {
            return "[" + n.toString() +","+d.toString()+"]";
        }
        static Pair of(BigInteger[] arr){
            return new Pair(arr[0],arr[1]);
        }
    }

    private static void initValue(){
//		try {
//			PolyCalculator.class.getClassLoader().loadClass(Multinomial.class.getName());
//		} catch (ClassNotFoundException e1) {
//			e1.printStackTrace();
//		}

        SIN_VALUE.put(ofVal(0l,1l),ZERO);
        // sin(0) = 0
        SIN_VALUE.put(ofVal(1l,6l),monomial(
                Term.asFraction(1,2,1)));
        //sin(Pi/6) = 1 / 2
        SIN_VALUE.put(ofVal(1l,4l),monomial(
                Term.asFraction(1,2,2)));
        //sin(Pi/4) =sqr(2)/2
        SIN_VALUE.put(ofVal(1l,3l),monomial(
                Term.asFraction(1,2,3)));
        //sin(Pi/3) = sqr(3) / 2
        SIN_VALUE.put(ofVal(1l,2l),ONE);
        //sin(Pi/2) = 1

        SIN_VALUE.put(ofVal(1l,12l),valueOf("Sqr6/4-Sqr2/4"));
        //sin(Pi/12) = sqr6/4-sqr2/4

        SIN_VALUE.put(ofVal(5l,12l),valueOf("Sqr6/4+Sqr2/4"));
        //sin(Pi/12) = sqr6/4-sqr2/4

        TAN_VALUE.put(ofVal(0l,1l),ZERO);
        // tan(0) = 0
        TAN_VALUE.put(ofVal(1l,6l),monomial(
                Term.asFraction(1,3,3)));
        //tan(Pi/6) = Sqr(3)/3
        TAN_VALUE.put(ofVal(1l,4l),monomial(Term.ONE));
        //tan(Pi/4) = 1
        TAN_VALUE.put(ofVal(1l,3l),monomial(
                Term.asFraction(1,1,3)));
        //tan(Pi/3) = Sqr(3)

        TAN_VALUE.put(ofVal(1l,12l),valueOf("2-Sqr3"));
        //tan(Pi/12) = 2-Sqr3
        TAN_VALUE.put(ofVal(5l,12l),valueOf("2+Sqr3"));
        //tan(Pi/12) = 2+Sqr3 

        for(Map.Entry<Pair,Multinomial> e : SIN_VALUE.entrySet()){
            Pair p = e.getKey();
            ARCSIN_VALUE.put(e.getValue(), monomial(Term.asFraction(p.n, p.d, BigInteger.ONE)));
        }
        for(Map.Entry<Pair,Multinomial> e : TAN_VALUE.entrySet()){
            Pair p = e.getKey();
            ARCTAN_VALUE.put(e.getValue(), monomial(Term.asFraction(p.n, p.d, BigInteger.ONE)));
        }
    }


    /**
     * SIN_VALUE stores the sin result stored in 0 to Pi/2
     *
     *
     */
    public static final Map<Pair,Multinomial> SIN_VALUE=new HashMap<>();

    public static final Map<Pair,Multinomial> TAN_VALUE=new HashMap<>();

    /**
     * this Map contains arcsin values 
     * @see #SIN_VALUE
     */
    public static final Map<Multinomial,Multinomial> ARCSIN_VALUE=new TreeMap<Multinomial,Multinomial>();
    /**
     * this Map contains arctan values 
     * @see #TAN_VALUE
     */
    public static final Map<Multinomial,Multinomial> ARCTAN_VALUE=new TreeMap<Multinomial,Multinomial>();


    @Override
    public boolean isEqual(Multinomial para1, Multinomial para2) {
        return para1.equals(para2);
    }

    @Override
    public int compare(Multinomial para1, Multinomial para2) {
        return para1.compareTo(para2);
    }

    @Override
    public boolean isComparable() {
        return true;
    }



    @Override
    public Multinomial add(Multinomial para1, Multinomial para2) {
        return para1.add(para2);
    }

    @Override
    public Multinomial addX(Object... ps) {
        if(ps.length==0){
            return ZERO;
        }
        NavigableSet<Term> result = getSet();
        for(Object m : ps){
            mergingAddAll(result,((Multinomial)m).terms);
        }
        return new Multinomial(result);
    }

    @Override
    public Multinomial negate(Multinomial para) {
        return para.negate();
    }

    @Override
    public Multinomial abs(Multinomial para) {
        return para;
    }

    @Override
    public Multinomial subtract(Multinomial para1, Multinomial para2) {
        return para1.subtract(para2);
    }

    @Override
    public Multinomial getZero() {
        return ZERO;
    }

    @Override
    public boolean isZero(Multinomial para) {
        return para.equals(ZERO);
    }

    @Override
    public Multinomial multiply(Multinomial para1, Multinomial para2) {
        return para1.multiply(para2);
    }

    @Override
    public Multinomial multiplyX(Object... ps) {
        if(ps.length == 0){
            return ONE;
        }
        NavigableSet<Term> result = singleTerm(Term.ONE);
        for(Object m : ps){
            result = mergingMultiply(result,((Multinomial)m).terms);
        }
        return new Multinomial(result);
    }

    @Override
    public Multinomial divide(Multinomial para1, Multinomial para2) {
        return para1.divide(para2);
    }

    @Override
    public Multinomial getOne() {
        return ONE;
    }

    @Override
    public Multinomial reciprocal(Multinomial p) {
        return p.reciprocal();
    }

    @Override
    public Multinomial multiplyLong(Multinomial p, long l) {
        return p.multiply(Term.valueOf(l));
    }

    @Override
    public Multinomial divideLong(Multinomial p, long l) {
        return p.divide(Term.valueOf(l));
    }

    @Override
    public Multinomial squareRoot(Multinomial p) {
        if(p.isMonomial()){
            return monomial(p.getFirst().squareRoot());
        }
        throw new UnsupportedCalculationException("Too complex");
    }

    @Override
    public Multinomial nroot(Multinomial x, long n) {
        if(n<0){
            return nroot(x,n).reciprocal();
        }
        if(n==0){
            throw new ArithmeticException("nroot for n = 0");
        }
        if(n==1){
            return x;
        }
        if(n==2){
            return squareRoot(x);
        }
        if(x.isMonomial()){
            return monomial(Term.nroot(x.getFirst(),n));
        }
        throw new UnsupportedCalculationException("Too complex");
    }

    @Override
    public Multinomial pow(Multinomial p, long exp) {
        return p.pow(Math.toIntExact(exp));
    }

    @Override
    public Multinomial constantValue(String name) {
        switch (name){
            case STR_PI:return PI;
            case STR_E:return E;
            case STR_I:return I;
        }
        throw new UnsupportedCalculationException(name);
    }

    @Override
    public Multinomial exp(Multinomial a, Multinomial b) {
        if(a.equals(ZERO)){
            if(b.equals(ZERO)){
                throw new ArithmeticException("0^0");
            }
            return ZERO;
        }
        if(a.equals(ONE)){
            return ONE;
        }
        if(b.isMonomial()){
            Term t = b.getFirst();
            if(t.isInteger() && t.isPositive()){
                return pow(a,t.numerator.longValueExact());
            }
        }
        throw new UnsupportedCalculationException();
    }

    @Override
    public Multinomial exp(Multinomial x) {
        if(x.isMonomial()){
            Term t = x.getFirst();
            if(t.isInteger() && t.isPositive()){
                long p = t.numerator.longValueExact();
//                    throw new ArithmeticException("Too big for pow = "+p);
                return monomial(Term.E.pow(Math.toIntExact(p)));
            }
        }
        throw new UnsupportedCalculationException();
    }

    @Override
    public Multinomial log(Multinomial a, Multinomial b) {
        return null;
    }

    @Override
    public Multinomial ln(Multinomial x) {
        return null;
    }

    @Override
    public Multinomial sin(Multinomial x) {
        if(x.isMonomial()){
            Term t = x.getFirst();
            Multinomial re = sinf(t);
            if(re != null){
                return re;
            }
        }
        throw new UnsupportedCalculationException("Can't calculate sin");
    }

    @Override
    public Multinomial cos(Multinomial x) {
        if(x.isMonomial()){
            Term t = x.getFirst();
            Multinomial re = cosf(t);
            if(re != null){
                return re;
            }
        }
        throw new UnsupportedCalculationException("Can't calculate cos");
    }

    private Multinomial sinf(Term f){
        if(f.isZero()) {
            //sin(0) = 1
            return ZERO;
        }
        if(f.haveSameChar(Term.PI)){
            if(!f.radical.equals(BigInteger.ONE)) {
                return null;
            }
            // ... pi
            boolean nega = !f.isPositive();
            if(nega)
                f=f.negate();
            BigInteger[] nd = new BigInteger[]{f.numerator,f.denominator};
            //now in [0,2Pi]
            reduceByTwoPi(nd);
            nega = reduceIntoPi(nd,nega);
            //into pi.
            //sin(x) = sin(pi-x)
            subtractToHalf(nd,true);

//			f = Formula.c
            Multinomial result = SIN_VALUE.get(Pair.of(nd));
            if(result != null){
                if(nega){
                    result = negate(result);
                }
            }
            return result;
        }
        return null;
    }
    private Multinomial cosf(Term f){
        if(f.isZero()) {
            //cos(0) = 1
            return ONE;
        }
        if(f.haveSameChar(Term.PI)){
            if(f.radical.equals(BigInteger.ONE)==false) {
                return null;
            }
            // ... pi
            boolean nega = false;
            if(f.isPositive()==false) {
                f = f.negate();
            }

            BigInteger[] nd = new BigInteger[]{f.numerator,f.denominator};
            //cos(x) = sin(pi/2+x)
            //add 1/2 to nd
            addHalfPi(nd);
            reduceByTwoPi(nd);
            //now in [0,2Pi]
            nega = reduceIntoPi(nd,nega);
            //sin(x) = sin(pi-x)
            subtractToHalf(nd, nega);

            Multinomial result = SIN_VALUE.get(Pair.of(nd));//cos(x) = sin(pi/2+x),in the first we added pi/2.
            if(result != null){
                if(nega){
                    result = negate(result);
                }
            }
            return result;
        }
        return null;
    }

    private void addHalfPi(BigInteger[] nd){
        BigInteger[] mod = nd[1].divideAndRemainder(BigInteger.valueOf(2));
        if(mod[1].equals(BigInteger.ZERO)){
            nd[0] = nd[0].add(mod[0]);
        }else{
            nd[0] = nd[0].add(nd[0]).add(nd[1]);
            nd[1] = nd[1].add(nd[1]);
        }
    }

    private static void reduceByTwoPi(BigInteger[] arr){
        //firstly get the Numerator
        BigInteger nume = arr[0];
        BigInteger deno = arr[1];
        deno = deno.add(deno);
        // reduce by two.
        nume = nume.mod(deno);
        arr[0] = nume;
    }

    private static void reduceByPi(BigInteger[] arr){
        // firstly get the Numerator
        BigInteger nume = arr[0];
        BigInteger deno = arr[1];
        // reduce by two.
        nume = nume.mod(deno);
        arr[0] = nume;
    }

    private static boolean reduceIntoPi(BigInteger[] nd,boolean nega){
        if(nd[0].compareTo(nd[1])>0){
            nega = !nega;
            nd[0] = nd[0].subtract(nd[1]);
        }
        return nega;
    }

    private static boolean subtractToHalf(BigInteger[] nd,boolean nega){
        BigInteger half = nd[1].divide(BigInteger.valueOf(2));
        if(nd[0].compareTo(half)>0){
            nd[0] = nd[1].subtract(nd[0]);
            nega = !nega;
        }
        return nega;

    }

    @Override
    public Multinomial tan(Multinomial x) {
        if(x.isMonomial()){
            Term t = x.getFirst();
            Multinomial re = tanf(t);
            if(re != null){
                return re;
            }
        }
        throw new UnsupportedCalculationException("Can't calculate tan");
    }

    private Multinomial tanf(Term f){
        if(f.isZero()) {
            //tan(0) = 0
            return ZERO;
        }
        if(f.haveSameChar(Term.PI)){
            if(f.radical.equals(BigInteger.ONE)==false) {
                return null;
            }
            boolean nega =!f.isPositive();
            if(nega)
                f=f.negate();
            BigInteger[] nd = new BigInteger[]{f.numerator(),f.denominator()};
            reduceByPi(nd);
            //into pi,but we need in [0,1/2)pi.
            if(nd[1].equals(nd[0].multiply(BigInteger.valueOf(2l)))){
                throw new ArithmeticException("tan(Pi/2)");
            }
            nega = subtractToHalf(nd, nega);
            Multinomial result = TAN_VALUE.get(Pair.of(nd));
            if(!(result==null)){
                if(nega)
                    result = negate(result);
                return result;
            }
        }
        return null;

    }

    public Multinomial cot(Multinomial x){
        if(x.isMonomial()){
            Term t = x.getFirst();
            Multinomial re = cotf(t);
            if(re != null){
                return re;
            }
        }
        throw new UnsupportedCalculationException("Can't calculate cot");
    }

    private Multinomial cotf(Term f){
        if(f.haveSameChar(Term.PI)){
            boolean nega =f.isPositive();
            if(!nega)
                f=f.negate();
            BigInteger[] nd = new BigInteger[]{f.numerator(),f.denominator()};
            addHalfPi(nd);
            reduceByPi(nd);
            nega = !nega;//cot(x) = -cot(x+Pi/2)
            //into pi,but we need in [0,1/2)pi.
            if(nd[1].equals(nd[0].multiply(BigInteger.valueOf(2l)))){
                throw new ArithmeticException("cot(0)");
            }
            nega = subtractToHalf(nd, nega);
            Multinomial result = TAN_VALUE.get(Pair.of(nd));
            if(!(result==null)){
                if(!nega)
                    result = result.negate();
                return result;
            }
        }
        return null;

    }

    @Override
    public Multinomial arcsin(Multinomial x) {
        Multinomial result = ARCSIN_VALUE.get(x);
        if(result!= null)
            return result;
        result = ARCSIN_VALUE.get(negate(x));
        //try negative value
        if(result!=null)
            return negate(result);
        //this step deals with the undefined number
        if(x.isMonomial()){
            Term f = x.getFirst();
            //f should be a constant value = [-1,1]
            if(f.hasNoChar()){
                if(f.compareTo(Term.ONE)> 0 || f.compareTo(Term.NEGATIVE_ONE) < 0)
                    throw new ArithmeticException("Arcsin undifined  :  "+f.toString());
            }
        }
        throw new UnsupportedCalculationException();
    }

    @Override
    public Multinomial arccos(Multinomial x) {
        //arccos(x) + arcsin(x) = Pi/2 --> arccos(x) = Pi/2 - arcsin(x)
        return monomial(Term.valueOf("Pi/2")).subtract(arcsin(x));
    }

    @Override
    public Multinomial arctan(Multinomial x) {
        Multinomial result = ARCTAN_VALUE.get(x);
        if(result!= null)
            return result;
        result = ARCTAN_VALUE.get(x.negate());
        //try negative value
        if(result!=null)
            return negate(result);
        throw new UnsupportedCalculationException();
    }
    @Override
    public boolean isInteger(Multinomial x) {
        return true;
    }

    @Override
    public boolean isQuotient(Multinomial x) {
        return true;
    }

    @Override
    public Multinomial mod(Multinomial a, Multinomial b) {
        return a.divideAndRemainder(b)[1];
    }

    @Override
    public Multinomial divideToInteger(Multinomial a, Multinomial b) {
        return a.divideAndRemainder(b)[0];
    }

    @Override
    public cn.timelives.java.utilities.structure.Pair<Multinomial, Multinomial> divideAndReminder(Multinomial a, Multinomial b) {
        var arr =  a.divideAndRemainder(b);
        return new cn.timelives.java.utilities.structure.Pair<>(arr[0],arr[1]);
    }

    @Override
    public Class<Multinomial> getNumberClass() {
        return Multinomial.class;
    }

    static class MSimplifier implements Simplifier<Multinomial>{

        @Override
        public List<Multinomial> simplify(List<Multinomial> numbers) {
            numbers = Multinomial.reduceGcd(numbers);
            return null;
        }

        @Override
        public Multinomial simplify(Multinomial x) {
            return x;
        }
    }




}
