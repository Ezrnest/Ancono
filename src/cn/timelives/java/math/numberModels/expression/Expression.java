/**
 * 2017-11-23
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.function.SVFunction;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.ParserUtils;
import cn.timelives.java.math.numberModels.api.Computable;
import cn.timelives.java.utilities.Printer;
import cn.timelives.java.utilities.StringSup;
import cn.timelives.java.utilities.structure.Pair;
import cn.timelives.java.utilities.structure.WithInt;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import static cn.timelives.java.math.numberModels.expression.ExprFunction.createBasicCalculatorFunctions;

/**
 * Expression is the most universal number model to show a number.
 * 
 * @author liyicheng 2017-11-23 21:31
 *
 */
public final class Expression implements Computable,Serializable {

	/**
	 * The root node of the expression.
	 */
	final Node root;

	/**
	 *
	 */
	Expression(Node root) {
		this.root = Objects.requireNonNull(root);
	}

	private String expr;

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (expr == null) {
			expr = root.toString();
		}
		return expr;
	}

	public void listNode(PrintWriter out) {
		PrintWriter pw = Printer.getOutput();
		Printer.reSet(out);
		root.listNode(0);
		Printer.reSet(pw);
	}

	public void listNode() {
		root.listNode(0);
	}

    /**
     * Gets the root of this expression
     * @return
     */
	public Node getRoot() {
		return root;
	}

    @Override
    public double computeDouble(ToDoubleFunction<String> valueMap) {
	    return root.computeDouble(valueMap);
    }

    @Override
    public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
        return root.compute(valueMap,mc);
    }

    /**
     * Returns a single variable function from this expression.
     * @param varName the variable of the function
     * @param mc a expression calculator
     * @return a function
     */
    public SVFunction<Expression> asFunction(String varName,ExprCalculator mc){
	    return x-> mc.substitute(this,varName,x);
    }

    /**
	 * Creates an expression from a multinomial.
	 *
	 * @param p
	 * @return
	 */
	public static Expression fromMultinomial(Multinomial p) {
		return new Expression(Node.newPolyNode(p, null));
	}

	/**
	 * Creates an expression from a string without performing any simplification.
	 * <h3>Variables:</h3>
	 * The expression
	 * <h3>Functions:</h3>
	 *
	 * @param expr
	 * @return
	 */
	public static Expression valueOf(String expr) {
		return new ExprParser(expr).parse();
	}

	public static final char FUNCTION_IDENTIFIER = '_';

	private static final Set<String> FUNCTION_NAMES;
	static{
	    FUNCTION_NAMES = new HashSet<>();
	    for(ExprFunction f : createBasicCalculatorFunctions(Multinomial.getCalculator())){
	        FUNCTION_NAMES.add(f.getName());
        }
    }

	static class ExprParser {
		String expr;
        Set<String> functionName;
		ExprParser(String expr) {
			this.expr = expr;
			functionName = FUNCTION_NAMES;
		}

		public Expression parse(){
		    Node root = partAdd(expr,0,null);
		    Expression re = new Expression(root);
		    return re;
        }

		void throwFor(String msg, int index) {
			ParserUtils.throwFor(expr, msg, index);
		}

		void throwFor(int index) {
			throwFor("Wrong format: ", index);
		}

		Node negateOrNot(Node n, Boolean isPositive) {
			if (!isPositive) {
				if (Node.isPolynomial(n)) {
					Node.Poly p = (Node.Poly) n;
					return Node.newPolyNode(p.p.negate(), p.parent);
				}
				Node.NodeWithChildren parent = n.parent;
				Node re = Node.wrapNodeMultiply(n, Multinomial.NEGATIVE_ONE);
				re.parent = parent;
				return re;
			}
			return n;
		}

		Node partPoly(String str,int offset,Node.NodeWithChildren parent){
		    return Node.newPolyNode(parseWithExceptionDetail(str,offset),parent);
        }

        /**
         *
         * @param str
         * @param offset indicating the string's position in the expr
         * @param parent
         * @return
         */
		Node partAdd(String str, int offset, Node.NodeWithChildren parent) {
		    if(!str.contains("(")){
                return partPoly(str,offset,parent);
            }
			//split first:
			List<Pair<String, Boolean>> list = ParserUtils.splitByAdd(str, offset);
			if (list.size() <= 0) {
				throwFor("Empty: ", offset);
			}
			if (list.size() == 1) {
				Pair<String, Boolean> p = list.get(0);
				String s = p.getFirst();
				try {
					Multinomial l = Multinomial.valueOf(s);
					return new Node.Poly(parent, p.getSecond() ? l : l.negate());
				} catch (NumberFormatException e) {
				}
				return negateOrNot(partMultiply(s, offset,parent), p.getSecond());
			} else {
			    var nodeList = new ArrayList<Node>(list.size());
			    for(var p : list){
                    Node n = negateOrNot(partMultiply(p.getFirst(), offset,null), p.getSecond());
                    offset+=p.getFirst().length();
                    nodeList.add(n);
                }
                var addNode = Node.wrapNodeAM(true,nodeList);
			    addNode.parent = parent;
                return addNode;
            }
		}

		Node partMultiply(String str,int offset,Node.NodeWithChildren parent){
            if(!str.contains("(")){
                return partPoly(str,offset,parent);
            }
            int pos = 0;
            Multinomial nume = Multinomial.ONE;
            List<Node> nNode = new ArrayList<>(4);
            List<Node> dNode = new ArrayList<>(4);
            while(pos<str.length()){
                int nextLeft = findLeftFrac(str,pos);
                if(nextLeft < 0){
                    //failed to find, no bracket
                    String expr = str.substring(pos);
                    nume = nume.multiply(parseRemaining(expr,pos));
                    break;
                }
//                String expr = str.substring(pos,nextLeft);
                int endPos = nextLeft;
                boolean nextDeno = false;
                String fName;
                //determine function
                WithInt<String> temp = backFindFunction(str,endPos,offset);
                fName = temp.getObj();
                endPos = temp.getInt();
                if(StringSup.endWith(str,"*",endPos)){
                    endPos--;
                }else if(StringSup.endWith(str,"/",endPos)){
                    endPos--;
                    nextDeno = true;
                }
                if(endPos>pos){
                    Multinomial m = parseRemaining(str.substring(pos,endPos),pos);
                    nume = nume.multiply(m);
                }


                int nextRight = findRightFrac(str,nextLeft+1);
                if(nextRight<0){
                    throwFor("Missing bracket: ",nextLeft);
                }
                String sub = str.substring(nextLeft+1,nextRight);
                Node n;
                if(fName == null){
                    n = partAdd(sub,offset+nextLeft+1,null);
                }else{
                    n = parseToFunction(fName,sub,offset,nextLeft);
                }
                if(nextDeno){
                    dNode.add(n);
                }else{
                    nNode.add(n);
                }

                pos = nextRight+1;
            }
            Node result = buildMultiply(nume,nNode,dNode);
            result.parent = parent;
            return result;
		}

		int findLeftFrac(String expression, int start ) {
            return expression.indexOf('(', start);
        }

        int findRightFrac(String expression,int start) {
            int count = 1;
            for (int i = start; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') {
                    count++;
                } else if (expression.charAt(i) == ')') {
                    count--;
                    if (count == 0) {
                        return i;
                    }
                }
            }
            return -1;
        }



        /**
         *
         * @param str a str
         * @param endPos position of (
         * @param offset offset of str.0
         * @return
         */
        WithInt<String> backFindFunction(String str, int endPos, int offset){
		    if(endPos == 0){
		        return new WithInt<>(endPos);
            }
            if(str.charAt(endPos-1) == FUNCTION_IDENTIFIER){
		        //find function
                int pos = endPos-2;
                while(pos>-1){
                    if(Character.isLetter(str.charAt(pos))){
                        pos--;
                    }else{
                        break;
                    }
                }
                pos++;
                if(pos == endPos-1){
                    throwFor("Empty function name",offset+pos);
                }
                String fName = str.substring(pos,endPos-1);
                return new WithInt<>(pos,fName);
            }

            String fName = null;
            for(String candidateName : functionName){
                if(StringSup.endWith(str,candidateName,endPos)){
                    if(fName==null || candidateName.length()>fName.length()){
                        fName = candidateName;
                    }
                }
            }
            if(fName==null){
                return new WithInt<>(endPos);
            }
            return new WithInt<>(endPos-fName.length(),fName);
        }

        Node parseToFunction(String fName,String sub,int offset,int nextLeft){
            String[] ss = StringSup.splitWithMatching(sub,',');
            if(ss.length==1){
                //single
                return Node.wrapNodeSF(fName,partAdd(sub,offset+nextLeft+1,null));
            }else if(ss.length == 2){
                Node c1 = partAdd(ss[0],offset+nextLeft+1,null);
                Node c2 = partAdd(ss[1],offset+nextLeft+ss[0].length()+2,null);
                return Node.wrapNodeDF(fName,c1,c2);
            }else{
                List<Node> list = new ArrayList<>(ss.length);
                int toffset = offset+1;
                for(String s : ss){
                    list.add(partAdd(s,toffset,null));
                    toffset+=s.length();
                    toffset++;
                }
                return Node.wrapNodeMF(fName,list,false);
            }
        }

        Node buildMultiply(Multinomial m,List<Node> nNode,List<Node> dNode){
            if(dNode.isEmpty()){
                return Node.wrapNodeAM(false,nNode,m);
            }else{
                Node nume = Node.wrapNodeAM(false,nNode,m);
                Node deno = Node.wrapNodeAM(false,dNode);
                return Node.wrapNodeFraction(nume,deno);
            }
        }

	    Multinomial parseRemaining(String expr,int offset){
		    char c = expr.charAt(0);
		    if(c == '/' || c == '*'){
		        expr = "1" + expr;
            }
            return parseWithExceptionDetail(expr,offset);
        }

        Multinomial parseWithExceptionDetail(String expr, int offset){
            try{
                return Multinomial.valueOf(expr);
            }catch (NumberFormatException ex){
                throwFor(ex.getMessage(),offset);
                //exception here
                return null;
            }
        }
	}


//    public static void main(String[] args){
//	    Expression expr = valueOf("(a+b)/(a-b)+(a+2b)/(a-b)");
//	    ExprCalculator mc = ExprCalculator.getInstance();
//        SimplificationStrategies.setCalRegularization(mc);
//	    print(mc.simplify(expr));
//	    print(mc.substitute(expr,"a",Expression.valueOf("exp(a,2)")));
//	    Function<String,Expression> f = x ->{
//	        switch(x){
//                case "a" : return valueOf("x");
//                case "b" : return valueOf("y");
//            }
//            return valueOf("1");
//        };
//	    ToDoubleFunction<String> f2 = x->{
//	        switch(x){
//                case "a" : return 5d;
//                case "b" : return 2d;
//                case "x" : return Math.PI/2;
//            }
//            return 1d;
//        };
//	    print(mc.simplify(expr).compute(f,mc));
//	    print(mc.simplify(expr).computeDouble(f2));
//	    print(mc.parseExpr("sin(x)").computeDouble(f2));
//    }


}