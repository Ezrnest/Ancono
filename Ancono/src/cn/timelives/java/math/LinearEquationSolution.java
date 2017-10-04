package cn.timelives.java.math;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.printnb;

import cn.timelives.java.math.linearAlgebra.Matrix;
import cn.timelives.java.math.linearAlgebra.MatrixSup;
import cn.timelives.java.math.linearAlgebra.Vector;
/**
 * A wrapper for the solution of a linear equation.This class objects are often 
 * returned by {@link MatrixSup#solveLinearEquation(Matrix)}.
 * @author lyc
 *
 */
public class LinearEquationSolution<T> {
	
	public static enum Situation{
		NO_SOLUTION,
		SINGLE_SOLUTION,
		UNBOUNDED_SOLUTION,
	}
	
	
	private final Matrix<T> equation;
	private final Situation sit ;
	
	
	private final Vector<T> base ;
	private final Vector<T>[] solution;
	
	LinearEquationSolution(Matrix<T> equ,Situation sit,Vector<T> base,Vector<T>[] solution){
		equation = equ;
		this.sit = sit;
		this.base = base;
		this.solution = solution;
	}
	
	/**
	 * Return a solution that represent no solution
	 * @return
	 */
	public static <T> LinearEquationSolution<T> noSolution(Matrix<T> equ){
		return new LinearEquationSolution<T>(equ,Situation.NO_SOLUTION,null,null);
	}
	
	public Situation getSolutionSituation(){
		return sit;
	}
	
	public static <T> SolutionBuilder<T> getBuilder(){
		return new SolutionBuilder<T>();
	}
	
	/**
	 * @return the equation
	 */
	public Matrix<T> getEquation() {
		return equation;
	}

	/**
	 * @return the base
	 */
	public Vector<T> getBase() {
		return base;
	}

	/**
	 * Get the part of k*vector
	 * @return the solution
	 */
	public Vector<T>[] getSolution() {
		return solution;
	}
	/**
	 * Show the solution through printer .
	 * @see {@link cn.timelives.java.utilities.Printer}
	 */
	public void printSolution(){
		switch(sit){
		case NO_SOLUTION:
			print("NO solution");
			break;
		case SINGLE_SOLUTION:
			//print the solution
			base.printMatrix();
			break;
		case UNBOUNDED_SOLUTION:
			printSolu0();
			break;
		default:
			break;
		
		}
	}
	
	private void printSolu0(){
		base.transportMatrix().printMatrix();
		for(int k=0;k<solution.length;k++){
			printnb("+k"+k);
			solution[k].transportMatrix().printMatrix();
		}
	}
	

	public static class SolutionBuilder<T>{
		private boolean isBuilding = true;
		private SolutionBuilder(){
			
		}
		private Matrix<T> equation;
		private Situation situation ;
		private Vector<T> base ;
		private Vector<T>[] ss;
		/**
		 * @param equation the equation to set
		 */
		public void setEquation(Matrix<T> equation) {
			if(isBuilding)
				this.equation = equation;
			else
				throw new IllegalStateException("Build complete");
		}
		/**
		 * @param situation the situation to set
		 */
		public void setSituation(Situation situation) {
			if(isBuilding)
				this.situation = situation;
			else
				throw new IllegalStateException("Build complete");
		}
		/**
		 * @param base the base to set
		 */
		public void setBase(Vector<T> base) {
			if(base.isRow()){
				this.base = base.transportMatrix();
			}else{
				this.base = base;
			}
		}
		
		public void setVariableSolution(Vector<T>[] ss){
			for(int i=0;i<ss.length;i++){
				if(ss[i].isRow()){
					ss[i] = ss[i].transportMatrix();
				}
			}
			this.ss = ss;
		}
		
		
		public LinearEquationSolution<T> build(){
			if(equation!=null&&situation!=null){
				boolean pass = false;
				switch(situation){
				case NO_SOLUTION:
					break;
				case SINGLE_SOLUTION:
					if(base!=null){
						pass = true;
					}
					break;
				case UNBOUNDED_SOLUTION:
					if(base!=null&&ss!=null){
						pass = true;
					}
					break;
				default:
					break;
				}
				if(pass){
					isBuilding = false;
					return new LinearEquationSolution<T>(equation,
														situation,
														base,ss);
				}
				
			}
			throw new IllegalArgumentException("Lack of argument");
			
		}
		
	}
	
	
}
