/**
 * 
 */
package cn.timelives.java.math.numberModels;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import cn.timelives.java.utilities.ArraySup;
import cn.timelives.java.utilities.structure.Pair;

/**
 * A class for a math calculator to perform a series of operation.
 * @author liyicheng
 *
 */
public class ComputeExpression{
	enum Operation1{
		ADD,
		SUBTRACT,
		MULTIPLY,
		DIVIDE,
		EXP,
		ADDX,
		MULTIPLYX,
		LOG;
		private final String s;
		/**
		 * 
		 */
		private Operation1() {
			s = this.name().toLowerCase();
		}
		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return s;
		}
	}
	enum Operation2{
		NEGATE,
		RECIPROCAL,
		SQR,
		EXP,//exp(x)
		LN,
		SIN,
		COS,
		TAN,
		ARCSIN,
		ARCCOS,
		ARCTAN,
		ABS;//|x|
		private final String s;
		/**
		 * 
		 */
		private Operation2() {
			s = this.name().toLowerCase();
		}
		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return s;
		}
	}
	Node root;
	final int argLen;
	
	/**
	 * 
	 */
	ComputeExpression(int argLength,Node root) {
		argLen = argLength;
		this.root = root;
	}
	protected void paraLengthCheck(int l){
		if(l<argLen){
			throw new IllegalArgumentException("Insufficient parameter size.");
		}
	}
	/**
	 * Computes the expression's result according to the given arguments. 
	 * The argument array's length must not be smaller to the number of 
	 * arguments required.<br>
	 * In this method, the calculator is assigned.
	 * @param args
	 * @param mc a {@link MathCalculator}
	 * @return
	 */
	@SafeVarargs
	public final <T> T compute(MathCalculator<T> mc,T...paras){
		paraLengthCheck(paras.length);
		return root.compute(mc,paras,null);
	}
	
	/**
	 * Generates the code in java representing the computation.
	 * @param mathCalculatorName
	 * @return
	 */
	public String generateCode(String mathCalculatorName){
		StringBuilder sb = new StringBuilder();
		root.generateCode(mathCalculatorName, sb);
		return sb.toString();
	}
	
	
	static abstract class Node{
		public boolean deepEquals(Node x){
			if(this==x){
				return true;
			}
			return false;
		}
		
		public abstract <T> T compute(MathCalculator<T> mc,T[] args,T[] cons);
		
		public abstract void generateCode(String mc,StringBuilder sb);
		
	}
	static class LongNode extends Node{
		final long l;
		/**
		 * 
		 */
		public LongNode(long l) {
			this.l = l;
		}
		
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#compute(cn.timelives.java.math.number_models.MathCalculator, java.lang.Object[])
		 */
		@Override
		public <T> T compute(MathCalculator<T> mc, T[] args,T[] cons) {
			return mc.multiplyLong(mc.getOne(), l);
		}


		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#generateCode(java.lang.String, java.lang.StringBuilder)
		 */
		@Override
		public void generateCode(String mc, StringBuilder sb) {
			sb.append(mc).append(".multiplyLong(")
				.append(mc)
				.append(".getOne(),").append(l).append("l)");
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#deepEquals(cn.timelives.java.math.number_models.ComputeExpression.Node)
		 */
		@Override
		public boolean deepEquals(Node x) {
			if(x instanceof LongNode){
				LongNode n = (LongNode)x ;
				return l == n.l;
			}
			return false;
		}
	}
	
	static class OpNode extends Node{
		final Operation1 op;
		private final Node[] subNode;
		OpNode(Operation1 op,Node[] subNode){
			this.op = op;
			this.subNode = subNode;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#compute(cn.timelives.java.math.number_models.MathCalculator)
		 */
		@Override
		public <T> T compute(MathCalculator<T> mc,T[] args,T[] cons) {
			switch(op){
			case ADD:
				return mc.add(subNode[0].compute(mc, args,cons), subNode[1].compute(mc, args, cons));
			case DIVIDE:
				return mc.divide(subNode[0].compute(mc, args, cons), subNode[1].compute(mc, args, cons));
			case EXP:
				return mc.exp(subNode[0].compute(mc, args,cons), subNode[1].compute(mc, args,cons));
			case MULTIPLY:
				return mc.multiply(subNode[0].compute(mc, args,cons), subNode[1].compute(mc, args,cons));
			case SUBTRACT:
				return mc.subtract(subNode[0].compute(mc, args,cons), subNode[1].compute(mc, args,cons));
			case ADDX:{
				Object[] axs = new Object[subNode.length];
				for(int i=0;i<subNode.length;i++){
					axs[i] = subNode[i].compute(mc, args,cons);
				}
				return mc.addX(axs);
			}
			case MULTIPLYX:{
				Object[] mxs = new Object[subNode.length];
				for(int i=0;i<subNode.length;i++){
					mxs[i] = subNode[i].compute(mc, args,cons);
				}
				return mc.multiplyX(mxs);
			}
			
			case LOG:
				return mc.log(subNode[0].compute(mc, args,cons),subNode[1].compute(mc, args,cons));
			default:
				break;
			}
			return null;
		}
		public Operation1 getOp() {
			return op;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#generateCode(java.lang.String, java.lang.StringBuilder)
		 */
		@Override
		public void generateCode(String mc, StringBuilder sb) {
			sb.append(mc).append(".").append(op.toString()).append('(');
			for(Node n : subNode){
				n.generateCode(mc, sb);
				sb.append(',');
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(')');
		}
		
		
	}
	
	static class SNode extends Node{
		final Operation2 op;
		final Node sn;
		
		/**
		 * 
		 */
		public SNode(Operation2 op,Node sn) {
			this.op = op;
			this.sn = sn;
		}
		@Override
		public <T> T compute(MathCalculator<T> mc,T[] args,T[] cons) {
			T x = sn.compute(mc, args,cons);
			switch(op){
			case SQR:
				return mc.squareRoot(x);
			case ARCCOS:
				return mc.arccos(x);
			case ARCSIN:
				return mc.arcsin(x);
			case ARCTAN:
				return mc.arctan(x);
			case COS:
				return mc.cos(x);
			case EXP:
				return mc.exp(x);
			case LN:
				return mc.ln(x);
			case SIN:
				return mc.sin(x);
			case TAN:
				return mc.tan(x);
			case ABS:
				return mc.abs(x);
			case NEGATE:
				return mc.negate(x);
			case RECIPROCAL:
				return mc.reciprocal(x);
			default:
				throw new AssertionError();
			}
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#generateCode(java.lang.String, java.lang.StringBuilder)
		 */
		@Override
		public void generateCode(String mc, StringBuilder sb) {
			sb.append(mc).append(".");
			if(op==Operation2.SQR){
				sb.append("squareRoot");
			}else{
				sb.append(op.toString());
			}
			sb.append("(");
			sn.generateCode(mc, sb);
			sb.append(")");
		}
	}
	static class POWNode extends Node{
		final Node x;
		final long exp;
		/**
		 * 
		 */
		public POWNode(Node x,long exp) {
			this.x = x;
			this.exp = exp;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#compute(cn.timelives.java.math.number_models.MathCalculator, java.lang.Object[])
		 */
		@Override
		public <T> T compute(MathCalculator<T> mc, T[] args,T[] cons) {
			return mc.pow(x.compute(mc, args,cons), exp);
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#generateCode(java.lang.String, java.lang.StringBuilder)
		 */
		@Override
		public void generateCode(String mc, StringBuilder sb) {
			sb.append(mc).append(".pow(");
			x.generateCode(mc, sb);
			sb.append(",").append(exp).append("l)");
		}
	}
	static class MULNode extends Node{
		final Node x;
		final long n;
		/**
		 * 
		 */
		public MULNode(Node x,long exp) {
			this.x = x;
			this.n = exp;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#compute(cn.timelives.java.math.number_models.MathCalculator, java.lang.Object[])
		 */
		@Override
		public <T> T compute(MathCalculator<T> mc, T[] args,T[] cons) {
			return mc.multiplyLong(x.compute(mc, args,cons), n);
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#generateCode(java.lang.String, java.lang.StringBuilder)
		 */
		@Override
		public void generateCode(String mc, StringBuilder sb) {
			sb.append(mc).append(".multiplyLong(");
			x.generateCode(mc, sb);
			sb.append(",").append(n).append("l)");
		}
	}
	static class DIVNode extends Node{
		final Node x;
		final long n;
		/**
		 * 
		 */
		public DIVNode(Node x,long exp) {
			this.x = x;
			this.n = exp;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#compute(cn.timelives.java.math.number_models.MathCalculator, java.lang.Object[])
		 */
		@Override
		public <T> T compute(MathCalculator<T> mc, T[] args,T[] cons) {
			return mc.divideLong(x.compute(mc, args,cons), n);
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#generateCode(java.lang.String, java.lang.StringBuilder)
		 */
		@Override
		public void generateCode(String mc, StringBuilder sb) {
			sb.append(mc).append(".divideLong(");
			x.generateCode(mc, sb);
			sb.append(",").append(n).append("l)");
		}
	}
	
	static class ArgNode extends Node{
		final int index;
		/**
		 * 
		 */
		public ArgNode(int index) {
			this.index = index;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#compute(cn.timelives.java.math.number_models.MathCalculator, java.lang.Object[])
		 */
		@Override
		public <T> T compute(MathCalculator<T> mc, T[] args,T[] cons) {
			return args[index];
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#generateCode(java.lang.String, java.lang.StringBuilder)
		 */
		@Override
		public void generateCode(String mc, StringBuilder sb) {
			sb.append("paras[").append(index).append("]");
		}
	}
	static class ConstNode extends Node{
		final int index;
		/**
		 * 
		 */
		public ConstNode(int index) {
			this.index = index;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#compute(cn.timelives.java.math.number_models.MathCalculator, java.lang.Object[])
		 */
		@Override
		public <T> T compute(MathCalculator<T> mc, T[] args ,T[] cons) {
			return cons[index];
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.Node#generateCode(java.lang.String, java.lang.StringBuilder)
		 */
		@Override
		public void generateCode(String mc, StringBuilder sb) {
			sb.append("consts[").append(index).append("]");
		}
	}
	static <T> Node createNode(Operation1 op,Node[] subNode){
		if(op== Operation1.EXP){
			Node n2 = subNode[1];
			if(n2 instanceof LongNode){
				long l = ((LongNode)n2).l;
				return new POWNode(subNode[0], l);
			}
		}
		return new OpNode(op, subNode);
	}
//	/* (non-Javadoc)
//	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
//	 */
//	@Override
//	public <N> ComputeExpression<?> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
//		return null;
//	}
//	/* (non-Javadoc)
//	 * @see cn.timelives.java.math.FlexibleMathObject#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object obj) {
//		return this==obj;
//	}
//	/* (non-Javadoc)
//	 * @see cn.timelives.java.math.FlexibleMathObject#hashCode()
//	 */
//	@Override
//	public int hashCode() {
//		return root.hashCode() + mc.hashCode()*31;
//	}
//	/* (non-Javadoc)
//	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
//	 */
//	@Override
//	public boolean valueEquals(FlexibleMathObject<T, ?> obj) {
//		if(obj instanceof ComputeExpression){
//			ComputeExpression<?> cp = (ComputeExpression<?>) obj;
//			return exprEqual(cp);
//		}
//		return false;
//	}
//	
	
	public boolean expressionEquals(ComputeExpression ce){
		return this==ce;
	}
	
	
//	/* (non-Javadoc)
//	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
//	 */
//	@Override
//	public <N> boolean valueEquals(FlexibleMathObject<N, ?> obj, Function<N, T> mapper) {
//		if(obj instanceof ComputeExpression){
//			ComputeExpression<?> cp = (ComputeExpression<?>) obj;
//			return exprEqual(cp);
//		}
//		return false;
//	}
	
	static class ExprParser{
		int flag;
		String expr;
		int argLen;
		/**
		 * 
		 */
		public ExprParser(String str,int flag) {
			this.flag = flag;
			this.expr = str;
		}
		public ComputeExpression parse(){
			//first 
			Node root = partAdd(expr,0);
			return new ComputeExpression(argLen, root);
		}
		Node negateOrNot(Node n,Boolean isPositive){
			if(!isPositive){
				if(n instanceof LongNode){
					return new LongNode(-((LongNode)n).l);
				}
				return new SNode(Operation2.NEGATE,n);
			}
			return n;
		}
		/**
		 * Separates the expression into multiply parts which only contains 
		 * multiplication as the outer operation.
		 * @param str
		 * @return
		 */
		Node partAdd(String str,int offset){
			//split first:
			List<Pair<String,Boolean>> list = splitByAdd(str,offset);
			if(list.size()<=0){
				throwFor("Empty: ",offset);
			}
			if(list.size()==1){
				Pair<String,Boolean> p = list.get(0);
				String s = p.getFirst();
				try{
					long l = Long.parseLong(s);
					return new LongNode(p.getSecond() ? l : -l);
				}catch(NumberFormatException e){}
				return negateOrNot(partMultiply(s,offset), p.getSecond());
			}
			if(list.size()==2){
				Node[] ns = new Node[2];
				Pair<String,Boolean> p1 = list.get(0),p2 = list.get(1);
				Node n1 = partMultiply(p1.getFirst(),offset);
				ns[0] = negateOrNot(n1,p1.getSecond());
//				if(!p1.getSecond()){
//					ns[0] = new SNode(Operation2.NEGATE, n1);
//				}else{
//					ns[0] = n1;
//				}
				Operation1 op = p2.getSecond() ? Operation1.ADD : Operation1.SUBTRACT;
				ns[1] = partMultiply(p2.getFirst(), offset+p1.getFirst().length());
				return new OpNode(op,ns);
			}else{
				if((flag & ENABLE_ADDX) != 0){
					int len = list.size();
					int index = 0;
					Node[] ns = new Node[len];
					for(Pair<String,Boolean> p : list){
						Node n = partMultiply(p.getFirst(),offset);
						offset+= p.getFirst().length();
						ns[index++] = negateOrNot(n, p.getSecond());
					}
					return new OpNode(Operation1.ADDX, ns);
				}else{
					Iterator<Pair<String,Boolean>> it = list.iterator();
					Pair<String,Boolean> p = it.next();
					Node n = negateOrNot(partMultiply(p.getFirst(),offset),p.getSecond());
					while(it.hasNext()){
						Node[] ns = new Node[2];
						ns[0] = n;
						offset+=p.getFirst().length();
						p = it.next();
						Operation1 op = p.getSecond() ? Operation1.ADD : Operation1.SUBTRACT;
						ns[1] = partMultiply(p.getFirst(), offset);
						n = new OpNode(op,ns);
					}
					return n;
				}
			}
		}
		
		Node repOrNot(Node n,boolean isDown){
			if(isDown){
				return new SNode(Operation2.RECIPROCAL,n);
			}
			return n;
		}
		
		/**
		 * Main points: recognize single variable or call partAdd().
		 * @param str
		 * @param offset
		 * @return
		 */
		Node partMultiply(String str,final int offset){
			//split first
			List<Pair<Node,Boolean>> list = fillList(str,offset);
			//part two: deal with up and down :
			if(list.size()<=0){
				throwFor("Empty: ",offset);
			}
			if(list.size()==1){
				//single 
				return list.get(0).getFirst();
			}
			if(list.size()==2){
				//deal with form like : n/(expr) first
				Pair<Node,Boolean> p1 = list.get(0);
				Pair<Node,Boolean> p2 = list.get(1);
				Node n1 = p1.getFirst(),n2 = p2.getFirst();
				if(n1 instanceof LongNode){
					Node n = repOrNot(n2, p2.getSecond());
					return new MULNode(n, ((LongNode)n1).l);
				}
				if(n2 instanceof LongNode){
					long l = ((LongNode)n2).l;
					if(p2.getSecond()){
						return new DIVNode(n1, l);
					}else{
						return new MULNode(n1, l);
					}
				}
				Operation1 op = p2.getSecond() ? Operation1.DIVIDE : Operation1.MULTIPLY;
				return new OpNode(op,new Node[]{n1,n2});
			}else{
				List<Pair<Node,Boolean>> nodes = new LinkedList<>();
				List<Pair<Long,Boolean>> longs = new LinkedList<>();
				for(Pair<Node,Boolean> p : list){
					Node n = p.getFirst();
					if(n instanceof LongNode){
						Long l = ((LongNode)n).l;
						longs.add(new Pair<>(l,p.getSecond()));
					}else{
						nodes.add(p);
					}
				}
				Node result;
				if(nodes.size()==0){
					Pair<Long,Boolean> p = longs.remove(0);
					result = repOrNot(new LongNode(p.getFirst()), p.getSecond()) ;
				}else if(nodes.size() == 1){
					Pair<Node,Boolean> p = nodes.get(0);
					result = repOrNot(p.getFirst(), p.getSecond()) ;
				}else{
					if((flag & ENABLE_MULTIPLYX) != 0){
						Node[] ns = new Node[nodes.size()];
						int index = 0;
						for(Pair<Node,Boolean> p : nodes){
							ns[index++] = repOrNot(p.getFirst(), p.getSecond());
						}
						result = new OpNode(Operation1.MULTIPLYX, ns);
					}else{
						Iterator<Pair<Node,Boolean>> it = nodes.iterator();
						
						Pair<Node,Boolean> p = it.next();
						Node n = p.getFirst();
						while(it.hasNext()){
							Node[] ns = new Node[2];
							ns[0] = n;
							p = it.next();
							ns[1] = p.getFirst();
							Operation1 op = p.getSecond() ? Operation1.DIVIDE : Operation1.MULTIPLY;
							n = new OpNode(op,ns);
						}
						result = n;
					}
				}
				
				for(Pair<Long,Boolean> p : longs){
					if(p.getSecond()){
						result = new DIVNode(result, p.getFirst());
					}else{
						result = new MULNode(result, p.getFirst());
					}
				}
				return result;
				
			}
			
		}
		int checkValidConstant(int index,int offset){
			throwFor("Constant value is not supported: ",offset-Integer.toString(index).length());
			return index;
		}
		
		List<Pair<Node,Boolean>> fillList(String str,final int offset){
			List<Pair<Node,Boolean>> list = new LinkedList<>();
			int start = 0;
			boolean down = false;
			boolean hasOp = true;
			/**
			 * str.length()
			 */
			final int length = str.length();
			while(start < length){
				//should set next to an unrecognized position, waiting for * or /
				int next;
				char c = str.charAt(start);
				Node n;
				//variable or constant
				if(c == ' '){
					start++;
					continue;
				}else if(c  == PARAMETER_IDENTIFIER || c == CONSTANT_IDENTIFIER){
					start++;
					if(start == length){
						throwFor("Undefined: ",offset+start);
					}
					next = findIntEnd(str,start);
					if(next == start){
						throwFor(offset+start);
					}
					int paraIndex;
					try{
						paraIndex = Integer.valueOf(str.substring(start,next));
					}catch(NumberFormatException ex){
						throwFor("Value Exceeds: ",offset+next);
						throw new RuntimeException();
					}
					if(c == PARAMETER_IDENTIFIER){
						argLen = Math.max(argLen, paraIndex);
						n = new ArgNode(paraIndex);
					}else{
						n = new ConstNode(checkValidConstant(paraIndex,offset+next-1));
					}
				}else if(isDigit(c)){
					if(!hasOp){
						throwFor("No Operator: ",offset+start);
					}
					next = findIntEnd(str,start);
					try{
						long cons = Long.valueOf(str.substring(start,next));
						n = new LongNode(cons);
					}catch(NumberFormatException e){
						throwFor("Value Exceeds: ",offset+next);
						throw new RuntimeException();
					}
					
				}else if(c == '('){
					//a (...)
					next = findMatchBrac(str,start+1);
					if(next == length){
						throwFor("Missing right bracket: ",offset+length);
					}
					n = partAdd(str.substring(start+1,next), offset+start+1);
					next++;
				}else{
					n = null;
					next = str.length();
					//function
					for (Operation1 op : Operation1.values()) {
						if (!str.startsWith(op.toString(), start)) {
							continue;
						}
						// check
						int index = start + op.toString().length();
						if (index >= length) {
							continue;
						}
						if (str.charAt(index) != '(') {
							continue;
						}
						index++;
						int end = findMatchBrac(str, index);
						if (end == length) {
							continue;
						}
						int comma = findQuoteChar(str, index, ',');
						if (comma >= length) {
							continue;
						}
						Node[] sn = new Node[2];
						sn[0] = partAdd(str.substring(index, comma), offset + index);
						sn[1] = partAdd(str.substring(comma + 1, end), offset + comma + 1);
						n = createNode(op,sn);
						next = end + 1;
					}
					if(n==null){
						for(Operation2 op : Operation2.values()){
							if(!str.startsWith(op.toString(), start)){
								continue;
							}
							//check
							int index = start + op.toString().length();
							if(index >= length){
								continue;
							}
							if(str.charAt(index)!='('){
								continue;
							}
							index++;
							int end = findMatchBrac(str, index);
							if(end == length){
								continue;
							}
							next = end +1;
							n = new SNode(op,partAdd(str.substring(index,end),offset+index));
						}
					}
					if(n == null){
						throwFor("Undefined: ",offset+start);
					}
					
				}
				
				Pair<Node,Boolean> p = new Pair<>();
				p.setSecond(down);
				if(next==length){
					p.setFirst(n);
					list.add(p);
					break;
				}
				down = false;
				hasOp = false;
				c = str.charAt(next);
				if(c == '*'){
					hasOp = true;
					next++;
				}else if(c == '/'){
					hasOp = true;
					down = true;
					next++;
				}else if(c == '^'){
					next++;
					//find number
					if(next==length){
						throwFor("Unfinished expression: ",offset+next);
					}
					int index = findIntEnd(str,next);
					if(index == next){
						c = str.charAt(index);
						if(c != '('){
							throwFor("Invalid power: ",offset+next);
						}
						index ++;
						next = findMatchBrac(str, index);
						Node expo = partAdd(str.substring(index,next), offset+index);
						n = createNode(Operation1.EXP, new Node[]{n,expo});
						next ++;
					}else{
						long pow;
						try{
							pow = Long.valueOf(str.substring(next,index));
						}catch(NumberFormatException e){
							throwFor("Value Exceeds: ",offset+next);
							throw new RuntimeException();
						}
						next = index;
						n = new POWNode(n, pow);
					}
					
				}
				start = next;
				p.setFirst(n);
				list.add(p);
				
			}
			return list;
		}
		/**
		 * 
		 * @param str
		 * @param start the starting position in str
		 * @param length the length of str
		 * @param offset the offset in the whole expression, used for exception message.
		 * @return
		 */
		Node parseOp1(String str,int start,int length,int offset){
			return null;
		}
		Node parseOp2(String str,int start,int length,int offset){
			
			return null;
		}
		/**
		 * finds the ending of the integer, returns the index of the first  non-digit character.
		 * @param str
		 * @param start
		 * @return exclusive
		 */
		static int findIntEnd(String str,int start){
			for(int i=start;i<str.length();i++){
				if(!isDigit(str.charAt(i))){
					return i;
				}
			}
			return str.length();
		}
		/**
		 * Returns the index of the matching bracket, start should be 
		 * (1+<i>the index of the bracket to match</i>)
		 * @param str
		 * @param start
		 * @return inclusive for ).
		 */
		static int findMatchBrac(String str,int start){
			int bra = 1;
			for(int i=start;i<str.length();i++){
				char c = str.charAt(i);
				if(c == '('){
					bra++;
				}else if(c == ')'){
					bra--;
				}
				if(bra==0){
					return i;
				}
			}
			return str.length();
		}
		static int findQuoteChar(String str,int start,char ch){
			int bra = 0;
			for(int i=start;i<str.length();i++){
				char c = str.charAt(i);
				if(c == '('){
					bra++;
				}else if(c == ')'){
					bra--;
				}
				if(bra==0 && c == ch){
					return i;
				}
			}
			return str.length();
		}
		
		
		
		
		static boolean isDigit(char c){
			return c >='0' && c<='9';
		}
		
		
		
		
		List<Pair<String,Boolean>> splitByAdd(String expr,final int offset){
			if(expr.isEmpty()){
				throwFor("Empty: ",offset);
			}
			List<Pair<String,Boolean>> list = new LinkedList<>();
			int i=0;
			int end = expr.length()-1;
			while(expr.charAt(i)==' '){
				i++;
			}
			boolean first = true;
			int prev = i;
			int brac = 0;
			Boolean positive = !Boolean.valueOf(expr.charAt(i)=='-');
			for(;i<=end;i++){
				char c = expr.charAt(i);
				if(brac == 0){
					if(c =='+' || c=='-'){
						if(!first){
							if(prev!= 0 && i == prev+1){
								throwFor(offset+i);
							}
							String part = expr.substring(prev, i);
							Pair<String,Boolean> p = new Pair<String, Boolean>(part, positive);
							list.add(p);
						}
						prev = i+1;
						positive = Boolean.valueOf(c == '+');
						
						first = false;
						continue;
					}
				}
				if (c == '(') {
					brac++;
				} else if (c == ')') {
					brac--;
					if(brac <0){
						throwFor("Missing left bracket: ",offset+i);
					}
				}
				first = false;
				
			}
			//deal with the last one
			if(prev > end){
				throwFor(offset+end);
			}else if(brac!=0){
				throwFor("Missing right bracket: ",offset+end);
			}else{
				String part = expr.substring(prev,end+1);
				Pair<String,Boolean> p = new Pair<String, Boolean>(part, positive);
				list.add(p);
			}
			return list;
		}
		
		
		void throwFor(String msg,int index){
			throw new ComputeExpressionParseException(System.lineSeparator()+msg+expr+System.lineSeparator()+
					String.valueOf(ArraySup.fillArr(index+msg.length(), ' '))+'^');
		}
		void throwFor(int index){
			throwFor("Wrong format: ",index);
			
		}
		
		
		
	}
	
	static class TEP<T> extends ExprParser{
		private T[] cons;
		private MathCalculator<T> mc;
		/**
		 * @param str
		 * @param flag
		 */
		public TEP(String str, int flag,T[] cons,MathCalculator<T> mc) {
			super(str, flag);
			this.mc = Objects.requireNonNull(mc);
			this.cons  = ArraySup.notEmpty(cons);
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.ExprParser#parse()
		 */
		@Override
		public TypeComputeExpression<T> parse() {
			Node root = partAdd(expr,0);
			return new TypeComputeExpression<>(argLen, root,cons,mc);
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression.ExprParser#checkValidConstant(int, int)
		 */
		@Override
		int checkValidConstant(int index, int offset) {
			if(index>= cons.length){
				throwFor("Constant index out of bound: ",offset);
			}
			return index;
		}
		
	}
	
	
	public static final class ComputeExpressionParseException extends RuntimeException{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -853062998298062178L;
		/**
		 * 
		 */
		ComputeExpressionParseException(String msg) {
			super(msg);
		}
		
		
	}
	

	
	/**
	 * Call {@code compile(expr,0)}.
	 * @param expr the expression
	 * @return the ComputeExpression
	 * @see #compile(String, int)
	 */
	public static ComputeExpression compile(String expr){
		return compile(expr,0);
	}
	public static final int ENABLE_ADDX = 0x1;
	public static final int ENABLE_MULTIPLYX = 0x2;
//	public static final int ENABLE_PRESIMPLIFY = 0x4;
	public static final char CONSTANT_IDENTIFIER = '#';
	public static final char PARAMETER_IDENTIFIER = '$';
	/**
	 * Compiles the input expression and returns a corresponding
	 * ComputeExpression. The expression may contain operations, parameters, constants and brackets. 
	 * The rules of computing will be the same as real math. 
	 * <h3>Operation:</h3>
	 * The basic operations
	 * <pre>+ - / * ^</pre>
	 * Notice:The "^" operator should always be followed by either a single positive number or 
	 * an expression quoted by bracket.
	 * <ul>Functions
	 * <li>exp(x,y)
	 * <li>log(x,y)
	 * <li>sqr(x)
	 * <li>exp(x)
	 * <li>ln(x)
	 * <li>sin(x)
	 * <li>cos(x)
	 * <li>tan(x)
	 * <li>arcsin(x)
	 * <li>arccos(x)
	 * <li>arctan(x)
	 * <li>abs(x)
	 * </ul>
	 * The character {@code x} and {@code y} may represent any parameter or expression. 
	 * But notice that the brackets and comma must not be omitted.<br>
	 * The "*" operator 
	 * The operations in the String-form expression and the methods actually performed using a {@link MathCalculator} 
	 * are generally the same. For example, if {@code exp($0,$1)} is input, then {@code mc.exp(paras[0],paras[1]} will 
	 * be performed when computing. 
	 * But there are some exceptions:
	 * <p>
	 * The "^" operator or {@code exp(x,y)} will be translated as {@link MathCalculator#pow(Object, long)} if the latter number is a plain long
	 * number, or be translated as {@link MathCalculator#exp(Object, Object)}. Multiplication will invoke {@link MathCalculator#multiplyX(Object...)}
	 * if there are more than two terms and the {@code flags} permits, otherwise it will be reduced to several parts invoking
	 * {@link MathCalculator#multiply(Object, Object)}, which is the same to addition. 
	 * <h3>Parameters:</h3>
	 * The parameters in the expression should always be input as
	 * <pre>$n</pre>, where {@code n} is an non-negative integer, representing the 
	 * index of this parameter in the array input when calling the method {@link #compute(Object...)}
	 * <p>
	 * <h3>Constants:</h3>
	 * <b>Note: Only TypeComputeExpression supports constant values.</b><br>The parameters in the expression should always be input as
	 * <pre>#n</pre>, where {@code n} is an non-negative integer, representing the 
	 * index of this parameter in the array {@code cons}
	 * <p>
	 * <h3>Special cases</h3>
	 * If the expression is empty, then "0" will be returned.
	 * @param expr
	 * @param mc
	 * @return
	 */
	public static ComputeExpression compile(String expr,int flags){
		ExprParser par = new ExprParser(expr,flags);
		return par.parse();
	}
	/**
	 * Builds a compute expression of a genetic type, use {@link #mapTo(Function, MathCalculator)} operation to 
	 * convert it to a required type. This type of compute expression should not maintain constant values. 
	 * <P>
	 * 
	 * @param expr
	 * @param flag
	 * @return
	 */
	public static <T> TypeComputeExpression<T> compileTyped(String expr,int flags,T[] cons,MathCalculator<T> mc){
		TEP<T> par = new TEP<>(expr,flags, cons, mc);
		return par.parse();
	}
	
	/**
	 * Builds a compute expression of a genetic type, use {@link #mapTo(Function, MathCalculator)} operation to 
	 * convert it to a required type. This type of compute expression should not maintain constant values.
	 * @param expr
	 * @param flag
	 * @return
	 */
	public static <T> TypeComputeExpression<T> compileTyped(String expr,T[] cons,MathCalculator<T> mc){
		return compileTyped(expr, 0,cons,mc);
	}
	/**
	 * 
	 * @param ce
	 * @param mc
	 * @return
	 */
	public static <T> TypeComputeExpression<T> convertToTyped(ComputeExpression ce,MathCalculator<T> mc){
		return new TypeComputeExpression<>(ce.argLen,ce.root,null,mc);
	}
	
	public static class TypeComputeExpression<T> extends ComputeExpression{
		final T[] cons;
		final MathCalculator<T> mc;
		/** 
		 * @param mc
		 * @param argLength
		 * @param root
		 * @param cons
		 */
		TypeComputeExpression(int argLength,
				Node root,T[] cons,MathCalculator<T> mc) {
			super(argLength,root);
			this.cons = cons;
			this.mc = mc;
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression#compute(java.lang.Object[])
		 */
		@SuppressWarnings("unchecked")
		public T compute(T... paras) {
			super.paraLengthCheck(paras.length);
			return root.compute(mc, paras, cons);
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.ComputeExpression#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
		 */
		public <N> TypeComputeExpression<N> mapTo(Function<Object, N> mapper, MathCalculator<N> newCalculator) {
			N[] ncons = null;
			if(cons!=null)
				ncons = ArraySup.mapTo(cons, mapper);
			return new TypeComputeExpression<>(this.argLen, root, ncons, newCalculator);
		}
		
		
	}
	
	
//	public static void main(String[] args) {
//		MathCalculator<Double> mc = MathCalculatorAdapter.getCalculatorDouble();
//		EasyConsole ec = EasyConsole.getSwingImpl();
//		ec.open();
////		String expr = "(2$1(exp(1,2)))(23)";
////		ComputeExpression ce = compile(expr);
////		print(ce.generateCode("mc"));
//		Printer.reSet(ec.getOutput());
//		while(true){
//			String expr = ec.nextLine();
//			try{
//				ComputeExpression ce = compile(expr);
//				print(ce.generateCode("mc"));
//			}catch(ComputeExpressionParseException e){
//				print(e.getMessage());
//			}
//			
//			
//		}
//	}
	
	
	
}
