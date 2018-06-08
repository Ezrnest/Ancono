package cn.timelives.java.math.linearAlgebra;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.Invertible;
import cn.timelives.java.math.equation.EquationSolver;
import cn.timelives.java.math.equation.SVPEquation;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.*;
import cn.timelives.java.utilities.ArraySup;
import cn.timelives.java.utilities.Printer;
import cn.timelives.java.utilities.structure.Pair;

import java.util.*;
import java.util.function.Function;

/**
 * A matrix is like a two dimension array,but the number in the matrix
 * is unchangeable. A n��m matrix has n rows and m columns. m,n=1,2,3,4,5...,
 * this matrix also provides some basic matrix operations such as elementary 
 * operations of matrix.
 * <p>Numbers in this matrix are flexible,but using Fraction is one of the suggested ways.Using fraction  means 
 * this matrix can do calculations in rational numbers without losing precision.But overflow 
 * or underflow are not considered.This is because this kind of implement is thought better  
 * when considering time performance,number range , memory cost and other issues comprehensively.
 * You can also use primary type such as {@code long} or {@code int} when creating the matrix and 
 * convention will be done automatically.
 * <p>
 * In addition to the operations provided by matrix object,
 * the matrix class itself provides several static methods for basic matrix calculation such as {@link #addMatrix(Matrix, Matrix)}
 * and {@link #multiplyMatrix(Matrix, Matrix)}.To do more complex operations or to have better 
 * time performance , you can seek {@linkplain MatrixSup} for some useful methods.
 * <p>
 * To create a Matrix , you can either use a two-dimension array as parameter or
 * directly call some prepared methods.No construction function is available for the safety of the 
 * immutability.
 * 
 * @author lyc
 *

 */
public abstract class Matrix<T> extends FieldMathObject<T> implements Invertible<Matrix<T>>{

	/**
	 * Row count
	 */
	protected final int row;
	/**
	 * Column count:
	 */
	protected final int column;
	/**
	 * Must set row and column count first.
	 * @param row
	 * @param column
	 */
	protected Matrix(int row, int column,MathCalculator<T> mc) {
		super(mc);
		this.row = row;
		this.column = column;
	}

	/**
	 * data:int[row][column],the numbers in this matrix
	 */

	/**
	 * Return the row count of this matrix.
	 * 
	 * @return row count of this matrix
	 */
	public int getRowCount() {
		return row;
	}

	/**
	 * Return the row column of this matrix.
	 * 
	 * @return row column of this matrix
	 */
	public int getColumnCount() {
		return column;
	}

	/**
	 * Get the number of the matrix in position i,j.The index is start from 0.
	 * 
	 * @param i
	 *            the row index
	 * @param j
	 *            the column index
	 * @return
	 */
	public abstract T getNumber(int i, int j);
	
	/**
	 * Return a copy of the values in this matrix. The returned array is a copy
	 * of the value so the change of it won't change the original values in this
	 * Matrix. The returned array assures that {@code arr[i][j] == getNumber(i,j)}
	 * @return a two-dimension array containing the values in this matrix
	 */
	public abstract Object[][] getValues();
	/**
	 * Returns a copy of the values in this matrix. If the size of the array is not enough,
	 * this method will try to creates a new array.
	 * @param arr
	 * @param N must be a super type of T.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <N> N[][] getValues(N[][] arr){
		if(arr.length<row){
			arr = Arrays.copyOf(arr, row);
		}
		for(int i=0;i<row;i++){
			N[] r = arr[i];
			if(r.length<column)
				r = Arrays.copyOf(r, column);
			for(int j=0;j<column;j++){
				r[j] = (N) getNumber(i, j);
			}
			arr[i] = r;
		}
		return arr;
	}
	
	/**
	 * Check if the numbers in {@code this} and {@code obj} are the same.
	 */
	@Override
	public boolean equals(Object obj){
		if(this==obj){
			return true;
		}
		if(obj instanceof Matrix){
			Matrix<?> mat = (Matrix<?>)obj;
			if(!mc.equals(mat.mc))
				return false;
			return Arrays.deepEquals(getValues(), mat.getValues());
		}
		return false;
//		return super.equals(obj);
	}
	@Override
	public int hashCode() {
		int hash = mc.hashCode();
		hash = hash*37 + column;
		hash = hash*37 + row;
		hash = hash* 31 + getNumber(0, 0).hashCode();
		return hash;
	}
	
	/**
	 * Return a MatrixN = {@code -this}
	 * 
	 * @return {@code -this}
	 */
	public abstract Matrix<T> negative();

	/**
	 * Return a MatrixN = this<sup>T</sup>.The new Matrix's row count = this.column
	 * , new Matrix's column count = this.row.
	 * 
	 * @return <tt>this<sup>T</sup></tt>
	 */
	public abstract Matrix<T> transportMatrix();

	/**
	 * Return a MatrixN = {@code n*this} .
	 * <p>
	 * This is one of the elementary transformations.
	 * 
	 * @return {@code n*this}
	 */
	public abstract Matrix<T> multiplyNumber(long n);
	
	/**
	 * Return a MatrixN = {@code n*this} .
	 * <p>
	 * This is one of the elementary transformations.
	 * 
	 * @return {@code n*this}
	 */
	public abstract Matrix<T> multiplyNumber(T n);
	
	/**
	 * Multiply the specific column with the given T.
	 * <p>
	 * This is one of the elementary transformations.
	 * @param n a number except 0
	 * @param c the index of the column to multiply
	 * @return a MatrixN as the change result.
	 */
	public Matrix<T> multiplyNumberColumn(T n,int c){
		columnRangeCheck(c);
		if(mc.isEqual(n ,mc.getZero())){
			throw new IllegalArgumentException("Multiply by 0");
		}
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for(int i=0;i<row;i++){
			re[i][c] = mc.multiply(re[i][c], n);
		}
		return new DMatrix<>(re, row, column,mc);
	}
	/**
	 * Multiply the specific column with the given T.
	 * <p>
	 * This is one of the elementary transformations.
	 * @param n a number except 0
	 * @param c the index of the column to multiply
	 * @return a MatrixN as the change result.
	 */
	public Matrix<T> multiplyNumberColumn(long n,int c){
		columnRangeCheck(c);
		if(n==0){
			throw new IllegalArgumentException("Multiply by 0");
		}
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for(int i=0;i<row;i++){
			re[i][c] = mc.multiplyLong(re[i][c], n);
		}
		return new DMatrix<>(re, row, column,mc);
	}
	
	/**
	 * Multiply the specific column with the given T.
	 * <p>
	 * This is one of the elementary transformations.
	 * @param n a number except 0
	 * @param r the index of the row to multiply
	 * @return a MatrixN as the change result.
	 */
	public Matrix<T> multiplyNumberRow(T n,int r){
		columnRangeCheck(r);
		if(mc.isEqual(n ,mc.getZero())){
			throw new IllegalArgumentException("Multiply by 0");
		}
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for(int i=0;i<column;i++){
			re[r][i] = mc.multiply(re[r][i], n);
		}
		return new DMatrix<>(re, row, column,mc);
	}
	/**
	 * Multiply the specific column with the given T.
	 * <p>
	 * This is one of the elementary transformations.
	 * @param n a number except 0
	 * @param r the index of the row to multiply
	 * @return a MatrixN as the change result.
	 */
	public Matrix<T> multiplyNumberRow(long n,int r){
		columnRangeCheck(r);
		if(n==0){
			throw new IllegalArgumentException("Multiply by 0");
		}
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for(int i=0;i<column;i++){
			re[r][i] = mc.multiplyLong(re[r][i], n);
		}
		return new DMatrix<>(re, row, column,mc);
	}

	/**
	 * Exchange two row in the matrix and return the new Matrix.
	 * <p>
	 * This is one of the elementary transformations.
	 * 
	 * @param r1
	 *            a row
	 * @param r2
	 *            another row
	 * @return an exchanged Matrix
	 */
	public Matrix<T> exchangeRow(int r1, int r2) {
		rowRangeCheck(r1, r2);
		if (r1 == r2) {
			return this;
		}
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for (int c = 0; c < column; ++c) {
			T t = re[r1][c];
			re[r1][c] = re[r2][c];
			re[r2][c] = t;
		}
		return new DMatrix<>(re, row, column,mc);
	}

	/**
	 * Exchange two column in the matrix and return the new Matrix.
	 * <p>
	 * This is one of the elementary transformations.
	 * 
	 * @param c1
	 *            a column
	 * @param r2
	 *            another column
	 * @return an exchanged Matrix
	 */
	public Matrix<T> exchangeColumn(int c1, int c2) {
		columnRangeCheck(c1, c2);
		if (c1 == c2) {
			return this;
		}
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for (int l = 0; l < column; ++l) {
			T t = re[l][c1];
			re[l][c1] = re[l][c2];
			re[l][c2] = t;
		}
		return new DMatrix<>(re, row, column,mc);
	}

	/**
	 * Multiply one row in this matrix with k and add the result to another
	 * row.
	 * <p>
	 * This is one of the elementary transformations.
	 * 
	 * @param k
	 *            multiplier
	 * @param r1
	 *            the row to multiply
	 * @param r2
	 *            the row to add to
	 * @return the result Matrix
	 */
	public Matrix<T> multiplyAndAddRow(long k, int r1, int r2) {
		rowRangeCheck(r1, r2);
		if (r1 == r2) {
			throw new IllegalArgumentException("The same row:" + r1);
		}
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for (int i = 0; i < column; i++) {
			re[r2][i]=mc.add(re[r2][i], mc.multiplyLong(re[r1][i], k));
		}
		return new DMatrix<>(re, row, column,mc);
	}
	
	/**
	 * Multiply one row in this matrix with k and add the result to another
	 * row.
	 * <p>
	 * This is one of the elementary transformations.
	 * 
	 * @param k
	 *            multiplier
	 * @param r1
	 *            the row to multiply
	 * @param r2
	 *            the row to add to
	 * @return the result Matrix
	 */
	public Matrix<T> multiplyAndAddRow(T k, int r1, int r2) {
		rowRangeCheck(r1, r2);
		if (r1 == r2) {
			throw new IllegalArgumentException("The same row:" + r1);
		}
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for (int i = 0; i < column; i++) {
			re[r2][i]= mc.add(re[r2][i], mc.multiply(re[r1][i], k));
		}
		return new DMatrix<>(re, row, column,mc);
	}

	/**
	 * Multiply one column in this matrix with k and add the result to another column.
	 * 
	 * @param k
	 *            multiplier
	 * @param c1
	 *            the column to multiply
	 * @param c2
	 *            the column to add to
	 * @return the result Matrix
	 */
	public Matrix<T> multiplyAndAddColumn(long k, int c1, int c2) {
		columnRangeCheck(c1, c1);
		if (c1 == c2) {
			throw new IllegalArgumentException("The same column:" + c1);
		}
		@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for (int i = 0; i < row; i++) {
			re[i][c2] = mc.add(re[i][c2], mc.multiplyLong(re[i][c1], k));
		}
		return new DMatrix<>(re, row, column,mc);
	}
	/**
	 * Multiply one column in this matrix with k and add the result to another column.
	 * 
	 * @param k
	 *            multiplier
	 * @param c1
	 *            the column to multiply
	 * @param c2
	 *            the column to add to
	 * @return the result Matrix
	 */
	public Matrix<T> multiplyAndAddColumn(T k, int c1, int c2) {
		columnRangeCheck(c1, c1);
		if (c1 == c2) {
			throw new IllegalArgumentException("The same column:" + c1);
		}@SuppressWarnings("unchecked")
		T[][] re = (T[][]) getValues();
		for (int i = 0; i < row; i++) {
			re[i][c2] = mc.add(re[i][c2], mc.multiply(re[i][c1], k));
		}
		return new DMatrix<>(re, row, column,mc);
	}

	
	/**
	 * Return det(this),this method only provides that the result is correct but time performance may 
	 * not be the best and can vary a lot.
	 * @return det(this)
	 * @throws ArithmeticException if this Matrix is not a square matrix.
	 */
	public T calDet(){
		//just calculate the value by recursion definition.
		if(row!=column){
			throw new ArithmeticException("Cannot calculate det for: "+ row + "��" + column);
		}
		
		//some fast implement when the order is below 4
		if(row==3){
			T sum = mc.multiply(mc.multiply(getNumber(0,0), getNumber(1,1)), getNumber(2,2));
			sum = mc.add(sum, mc.multiply(mc.multiply(getNumber(0,1), getNumber(1,2)), getNumber(2,0)));
			sum = mc.add(sum, mc.multiply(mc.multiply(getNumber(0,2), getNumber(1,0)), getNumber(2,1)));
			sum = mc.subtract(sum, mc.multiply(mc.multiply(getNumber(0,0), getNumber(1,2)), getNumber(2,1)));
			sum = mc.subtract(sum, mc.multiply(mc.multiply(getNumber(0,1), getNumber(1,0)), getNumber(2,2)));
			sum = mc.subtract(sum, mc.multiply(mc.multiply(getNumber(0,2), getNumber(1,1)), getNumber(2,0)));
			return sum;
		}else if(row==1){
			return getNumber(0, 0);
		}else if(row == 2){
			return mc.subtract(mc.multiply(getNumber(0,0), getNumber(1,1)), mc.multiply(getNumber(0,1), getNumber(1,0)));
		}
		T sum = mc.getZero();
		//calculate by separating first row
		boolean turn = false;
		for(int i=0;i<column;i++){
			T det = mc.multiply(getNumber(0,i), cofactor(0, i).calDet());
			if(turn){
				sum = mc.subtract(sum, det);
			}else{
				sum = mc.add(sum, det);
			}
			turn = !turn;
		}
		return sum;
	}
	/**
	 * Return the cofactor of the element in row {@code r} and column {@code c}. 
	 * @return the cofactor matrix.
	 * @throws IndexOutOfBoundsException if r or c is out of range 
	 * @throws ArithmeticException if this Matrix's row count or column count is less than two,
	 * 			which means it doesn't have cofactor matrix
	 */
	public abstract Matrix<T> cofactor(int r,int c); 
	
	/**
	 * Use elementary operations that don't change the det of this matrix to transform this
	 * matrix to an upper triangle matrix.
	 * @return a new upper triangle matrix
	 */
	public Matrix<T> toUpperTriangle(){
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) getValues();
		toUpperTri0(mat,row,column);
		return new DMatrix<T>(mat,row,column,mc);
	}
	/**
	 * Return a series of operations that can transform the matrix to an upper-triangle matrix.
	 * The method is normally used in calculating this^-1.
	 * @return a List of operation,the order is specified. 
	 * @see MatrixOperation
	 */
	public MatResult<T> toUpperTriangleWay(){
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) getValues();
		List<MatrixOperation<T>> ops = toUpperTri0(mat, row, column);
		Matrix<T> res = new DMatrix<T>(mat,row,column,mc);
		return new MatResult<T>(ops,res);
	}
	/**
	 * A basic operation to transform this matrix to upper triangle
	 * @param mat
	 * @return
	 */
	List<MatrixOperation<T>> toUpperTri0(T[][] mat,int row,int column){
		List<MatrixOperation<T>> ops = new LinkedList<MatrixOperation<T>>();
		//use Gaussian elimination
		int target = 0;
		//target row
		for(int i=0;i<row-1 && target < column;target++){
//			Printer.printMatrix(mat);
			//search for a non-zero row
			T f = mat[i][target];
			if(mc.isEqual(f, mc.getZero())){
				//find another one
				boolean found = false;
				for(int a=i+1;a<row;a++){
					if(!mc.isEqual(mat[a][target], mc.getZero())){
						f = mat[a][target];
						T[] t = mat[a];
						mat[a] = mat[i];
						mat[i] = t;
						found = true;
						ops.add(MatrixOperation.exchangeRow(i, a));
					}
				}
				if(!found){
					continue;
				}
			}
			for(int j=i+1;j<row;j++){
				if(!mc.isEqual(mat[j][target], mc.getZero())){
					T mul = mc.negate(mc.divide(mat[j][target], f));
					mat[j][target] = mc.getZero();
					for(int c=target+1;c<column;c++){
						mat[j][c] = mc.add(mat[j][c], mc.multiply(mat[i][c], mul));
					}
					ops.add(MatrixOperation.multiplyAddRow(i, j, mul));
				}
			}
			i++;
		}
		//do exchanged to move 0 lines to below
		Entry[] ens = new Entry[row];
		for(int i=0;i<row;i++){
			int v = column;
			for(int c=0;c<column;c++){
				if(!mc.isEqual(mat[i][c], mc.getZero())){
					v = c;
					break;
				}
			}
			ens[i] = new Entry(i,v);
		}
		//use insert sort to mark operation easier
		for(int i=0;i<row;i++){
			for(int j=row-1;j>i;j--){
				if(ens[j].value<ens[j-1].value){
					Entry et = ens[j];
					ens[j] = ens[j-1];
					ens[j-1] = et;
					ops.add(MatrixOperation.exchangeRow(j, j-1));
				}
			}
		}
		T[][] re = Arrays.copyOf(mat, row);
		for(int i=0;i<row;i++){
			mat[i] = re[ens[i].key];
		}
		return ops;
	}
	
	/**
	 *Transform the matrix to step-matrix.Which is like a step-matrix but implements 
	 * following features:<p>
	 * <ul>
	 * <li>1.The first number in a non-zero line is ONE.
	 * <li>2.Except the first number in a non-zero line,the other numbers in the same column are ZERO.
	 * </ul> 
	 * @param mat
	 * @param row
	 * @param column
	 * @return a list of operations with which the matrix can be transformed to normative step-matrix.
	 */
	List<MatrixOperation<T>> toStepMatrix(T[][] mat,int row,int column){
		List<MatrixOperation<T>> ops = toUpperTri0(mat,row,column);
//		Printer.print("TRI :: ");
//		Printer.printMatrix(mat);
		//transform to step matrix first.
		
		/* First we locate the first non-zero element in each line ,
		 * then we turn the others in the same row to 0.
		 */
		int lastTar = -1;
		for(int i=0;i<row;i++){
			int tar = -1 ;
			for(int c=lastTar+1;c<column;c++){
				if(!mc.isZero(mat[i][c])){
					//non-zero
					tar = c;
					break;
				}
			}
			if(tar<0){
				// zero line
				break;
			}
			//turn to 1 
			
			if(!mc.isEqual(mat[i][tar], mc.getOne())){
				T f = mc.reciprocal(mat[i][tar]);
				mat[i][tar] = mc.getOne();
				for(int j=tar+1;j<column;j++){
					mat[i][j] = mc.multiply(mat[i][j], f);
				}
				ops.add(MatrixOperation.multiplyRow(i, f));
//				Printer.print(ops.get(ops.size()-1).toDetail());
//				Printer.printMatrix(mat);
			}
			
			for(int r=0;r<i;r++){
				if(!mc.isZero(mat[r][tar])){
					T fra = mc.negate(mat[r][tar]);
					for(int j = tar+1;j<column;j++){
						mat[r][j] = mc.add(mat[r][j], mc.multiply(mat[i][j], fra)); 
					}
					mat[r][tar] = mc.getZero();
					ops.add(MatrixOperation.multiplyAddRow(i, r, fra));
//					Printer.print(ops.get(ops.size()-1).toDetail());
//					Printer.printMatrix(mat);
				}
				
			}
		}
		
		return ops;
	}
	
	
	
	
	
	
	/**
	 * A basic operation to transform this matrix to like identity
	 * @param mat will be changed by this method
	 * @return
	 * @throws ArithmeticException if cannot
	 */
	List<MatrixOperation<T>> toIdentity(T[][] mat,int row,int column){
		List<MatrixOperation<T>> ops = toUpperTri0(mat,row,column);
		int bound = Math.min(column, row);
		//check for ability
		for(int i=0;i<bound;i++){
			if(mc.isEqual(mat[i][i], mc.getZero())){
				throw new ArithmeticException("Cannot trans to Identity:Not Full Rank");
			}
		}
		for(int i=0;i<bound;i++){
			if(!mc.isEqual(mat[i][i], mc.getOne())){
				T f = mc.reciprocal(mat[i][i]);
				mat[i][i] = mc.getOne();
				for(int j=i+1;j<column;j++){
					mat[i][j] = mc.multiply(mat[i][j], f);
				}
				ops.add(MatrixOperation.multiplyRow(i, f));
			}
			
		}
		
		for(int i=bound-1;i>-1;i--){
			for(int j=i+1;j<column;j++){
				if(mat[i][j].equals(mc.getZero())==false){
					T f = mc.negate(mat[i][j]);
					mat[i][j] = mc.getZero();
					ops.add(MatrixOperation.multiplyAddRow(j, i, f));
				}
				
				
			}
		}
		return ops;
	}
	/**
	 * 
	 * @param mat
	 * @param row
	 * @param column
	 * @return
	 */
	List<MatrixOperation<T>> toNormativeStep(T[][] mat,int row,int column){
		List<MatrixOperation<T>> list = toStepMatrix(mat, row, column);
		return list;
	}
	/**
	 * Return a List of operation with which the matrix can be transformed to identity matrix.
	 * The sufficient and necessary condition is that this matrix is a full-rank matrix.(Firstly,this matrix should
	 * be a square matrix.)
	 * @return a List of operation,the order is specified. 
	 * @see MatrixOperation
	 */
	@SuppressWarnings("unchecked")
	public List<MatrixOperation<T>> toIdentityWay(){
		if(this.row!=this.column){
			throw new ArithmeticException("Cannot trans to Identity:Not a Square");
		}
		return toIdentity((T[][]) getValues(),row,column);
	}
	
	/**
	 * Transform the matrix to normative step-matrix.Which is like a step-matrix but implements 
	 * following features:<p>
	 * <ul>
	 * <li>1.The first number in a non-zero line is ONE.
	 * <li>2.Except the first number in a non-zero line,the other numbers in the same column are ZERO.
	 * </ul> 
	 * The step will be recorded and returned in the result.The transformed matrix will be created as a new 
	 * matrix and returned too.
	 * @return the operations and the result matrix.
	 */
	public MatResult<T> toStepMatrix(){
		@SuppressWarnings("unchecked")
		T[][] mat =(T[][]) getValues();
		List<MatrixOperation<T>> ops = toStepMatrix(mat, row, column);
		Matrix<T> res = new DMatrix<T>(mat,row,column,mc);
		return new MatResult<T>(ops,res);
	}
	
	/**
	 * Returns the trail if this matrix.
	 * @return {@code tr(this)}
	 */
	public T trail(){
		if(column != row){
			throw new ArithmeticException("Not square");
		}
		T tr = getNumber(0, 0);
		for(int i= 1;i<row;i++){
			tr = mc.add(tr, getNumber(i, i));
		}
		return tr;
	}
	
	/**
	 * Creates the eigenvalue equation of this matrix. It is required that 
	 * this matrix is a square matrix.
	 * @param mat 
	 * @return
	 */
	public SVPEquation<T> eigenvalueEquation(){
		if(column != row){
			throw new ArithmeticException("Not square");
		}
		//transform to a temporary matrix to compute the determinant
		//in multinomial
		MathCalculator<PolynomialX<T>> mct = PolynomialX.getCalculator(mc);
		Matrix<PolynomialX<T>> tmat = this.mapTo(x -> PolynomialX.constant(mc, x), mct),
				eigen = Matrix.diag(PolynomialX.oneX(mc),row, mct);
		tmat = minusMatrix(eigen, tmat);
		PolynomialX<T> expr = tmat.calDet();
		return SVPEquation.fromMultinomial(expr);
	}
	/**
	 * Returns a matrix which is similar to the matrix given and is a diagonal matrix.
	 * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to 
	 * the degree of the equation.
	 * @return 
	 */
	
	public Matrix<T> similarDiag(EquationSolver<T,SVPEquation<T>> equationSolver){
		List<T> eigenvalues = eigenvalues(equationSolver);
		@SuppressWarnings("unchecked")
		T[] arr = (T[])eigenvalues.toArray();
		return Matrix.diag(arr,mc);
	}
	/**
	 * Computes the eigenvalues of this matrix.
	 * <p>For example, assume {@code this} = 
	 * <pre>(1 0)
	 *(0,1)</pre>
	 * then this method will return a list of {@code [1,1]}
	 * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to 
	 * the degree of the equation.
	 * @return
	 */
	public List<T> eigenvalues(EquationSolver<T,SVPEquation<T>> equationSolver){
		SVPEquation<T> equation = eigenvalueEquation();
		return equationSolver.solve(equation);
	}
	/**
	 * Computes the eigenvalues of this matrix and their corresponding vectors.
	 * @param equationSolver a MathFunction to solve the equation, the length of the list should be equal to 
	 * the degree of the equation.
	 * @return
	 */
	public List<Pair<T,Vector<T>>> eigenvaluesAndVectors(EquationSolver<T,SVPEquation<T>> equationSolver){
		List<T> eigenvalues = eigenvalues(equationSolver);
		//vectors
		List<Pair<T,Vector<T>>> result = new ArrayList<>(eigenvalues.size());
		int size = eigenvalues.size();
		for(int i=0;i<size;) {
			T x = eigenvalues.get(i);
			int times = 1;
			while(++i<size) {
				T y = eigenvalues.get(i);
				if(mc.isEqual(x, y)) {
					times++;
				}else {
					break;
				}
			}
			Matrix<T> A = minusMatrix(this, Matrix.diag(x, row, mc));
			LinearEquationSolution<T> solution = MatrixSup.solveHomogeneousLinearEquation(A);
			Vector<T>[] ks = solution.getSolution();
			for (int k = 0; k < ks.length; k++) {
				result.add(new Pair<>(x, ks[k]));
			}
			if (times > ks.length) {
				for(int k=ks.length;k<times;k++) {
					result.add(new Pair<>(x, ks[0]));
				}
			}
		}
		
		return result;
	}
	
	private static class Entry implements Comparable<Entry>{
		private Entry(int key,int value){
			this.key = key;
			this.value = value;
		}
		@Override
		public int compareTo(Entry o) {
			return this.value - o.value;
		}
		final int key;
		final int value;
	}
	/**
	 * A structure to record the result of some operations to matrix.
	 * This object may be returned by some complex operation where both 
	 * of the transformed matrix and the operation process could be useful.
	 * @author lyc
	 *
	 */
	public static class MatResult<T>{
		MatResult(List<MatrixOperation<T>> ops, Matrix<T> result){
			this.ops = ops;
			this.result = result;
		}
		/**
		 * The list of the operations ,ordered.
		 */
		public final List<MatrixOperation<T>> ops;
		/**
		 * The result of the process.
		 */
		public final Matrix<T> result;
	}
	
	/**
	 * Calculate the rank of this matrix.
	 * @return
	 */
	public int calRank(){
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) getValues();
		toUpperTri0(mat,row,column);
//		Printer.printMatrix(mat);
		for(int i=row-1;i>-1;i--){
			for(int j=column-1;j>-1;j--){
				if(mat[i][j].equals(mc.getZero())==false){
					return Math.min(i+1, column);
				}
			}
		}
		return 0;
	}
	/**
	 * Return the matrix {@code mat} that {@code mat•this = E}. If there is not such 
	 * a matrix, then exception will be thrown.
	 * @return the inverse of this.
	 * @throws ArithmeticException if this method failed
	 */
	public Matrix<T> inverse(){
		//do size check first
		if(row!=column){
			throw new ArithmeticException("Cannot Inverse:Not a Square");
		}
		try{
			@SuppressWarnings("unchecked")
			List<MatrixOperation<T>> ops = toIdentity((T[][]) getValues(), row, column);
			Matrix<T> idt = identityMatrix(row,mc);
			idt = idt.doOperation(ops);
			return idt;
		}catch(ArithmeticException ae){
			throw new ArithmeticException("Cannot Inverse:Not Full Rank");
		}
		
	}
	
	
	
	/**
	 * Operate the given operation to this Matrix.If a series of operations should be done,then use 
	 * {@link #doOperation(List)} method.
	 * @param op
	 * @return a new Matrix
	 */
	public Matrix<T> doOperation(MatrixOperation<T> op){
		switch(op.ope){
		case EXCHANGE_COLUMN:
			return exchangeColumn(op.arg0, op.arg1);
		case EXCHANGE_ROW:
			return exchangeRow(op.arg0, op.arg1);
		case MULTIPLY_ADD_COLUMN:
			return multiplyAndAddColumn(op.num, op.arg0, op.arg1);
		case MULTIPLY_ADD_ROW:
			return multiplyAndAddRow(op.num, op.arg0, op.arg1);
		case MULTIPLY_COLUMN:
			return multiplyNumberColumn(op.num, op.arg0);
		case MULTIPLY_ROW:
			return multiplyNumberRow(op.num, op.arg0);
		default:
			return this;
		}
	}
	/**
	 * Operate the given operations to this Matrix.This method is expected to be used with 
	 * {@link #toUpperTriangleWay()}
	 * @param ops
	 * @return
	 */
	public Matrix<T> doOperation(List<MatrixOperation<T>> ops){
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) getValues();
		try{
			for(MatrixOperation<T> op:ops){
				switch(op.ope){
				case EXCHANGE_COLUMN:
					MatrixSup.exchangeColumn(mat,op.arg0, op.arg1);
					break;
				case EXCHANGE_ROW:
					MatrixSup.exchangeRow(mat,op.arg0, op.arg1);
					break;
				case MULTIPLY_ADD_COLUMN:
					multiplyAndAddColumn0(mat, op.arg0, op.arg1,op.num);
					break;
				case MULTIPLY_ADD_ROW:
					multiplyAndAddRow0(mat, op.arg0, op.arg1,op.num);
					break;
				case MULTIPLY_COLUMN:
					multiplyNumberColumn0(mat, op.arg0,op.num);
					break;
				case MULTIPLY_ROW:
					multiplyNumberRow0(mat, op.arg0,op.num);
					break;
				default:
					throw new ArithmeticException("No such operation:"+op.ope.name());
				}
			}
		}catch(RuntimeException ex){
			throw new RuntimeException("Operation failed",ex);
		}
		return new DMatrix<T>(mat,row,column,mc);
	}
	
	void multiplyAndAddColumn0(T[][] mat,int c1,int c2,T f){
		for(int i=0;i<mat.length;i++){
			mat[i][c2] = mc.add(mat[i][c2], mc.multiply(mat[i][c1], f));
		}
	}
	
	void multiplyAndAddRow0(T[][] mat,int r1,int r2,T f){
		for(int i=0;i<mat[r1].length;i++){
			
			mat[r2][i] =mc.add(mat[r2][i], mc.multiply(mat[r1][i], f));
		}
	}
	
	void multiplyNumberColumn0(T[][] mat,int c,T f){
		for(int i=0;i<mat.length;i++){
			mat[i][c] = mc.multiply(mat[i][c], f);
		}
	}
	
	void multiplyNumberRow0(T[][] mat,int r,T f){
		for(int i=0;i<mat[r].length;i++){
			mat[r][i] =mc.multiply(mat[r][i], f);
		}
	}
	
	
	/**
	 * Range check
	 * 
	 * @param ls
	 */
	protected void rowRangeCheck(int... rs) {
		for (int r : rs) {
			rowRangeCheck(r);
		}
	}
	protected void rowRangeCheck(int r){
		if (r < 0 || r >= row) {
			throw new IndexOutOfBoundsException("Row=" + row + ":" + r);
		}
	}
	
	/**
	 * Range check
	 * 
	 * @param cs
	 */
	protected void columnRangeCheck(int... cs) {
		for (int c : cs) {
			columnRangeCheck(c);
		}
	}
	protected void columnRangeCheck(int c) {
		if (c < 0 || c >= column) {
			throw new IndexOutOfBoundsException("Column=" + column + ":" + c);
		}
	}
	/**
	 * Return a sub matrix.{@code (i1,j1)-(i2,j2)}.The new matrix's row count
	 * will be {@code i2-i1+1}
	 * 
	 * @param i1
	 * @param j1
	 * @param i2
	 * @param j2
	 * @return a sub matrix containing a sub matrix in the given range
	 */
	public Matrix<T> subMatrix(int i1, int j1, int i2, int j2) {
		// do range check
		if (i1 < 0 || j1 < 0 || i2 >= row || j2 >= column || i1 > i2 || j1 > j2) {
			throw new IllegalArgumentException(
					"Illegal Argument:" + row + "��" + column + ":(" + i1 + "," + j1 + ")-(" + i2 + "," + j2 + ")");
		}
		return null;//
	}
	
	/**
	 * Replaces the Matrix with a row vector. The size of this vector must be 
	 * the same as the column count in this matrix. 
	 * @param row the row to replace, starts from 0.
	 * @param v a vector
	 * @return a new Matrix
	 */
	@SuppressWarnings("unchecked")
	public Matrix<T> replaceRow(int row, Vector<T> v){
		rowRangeCheck(row);
		if(!v.isRow() || v.getSize() != column){
			throw new IllegalArgumentException();
		}
		T[][] ndata = (T[][]) getValues();
		ndata[row] = (T[]) v.toArray();
		return new DMatrix<>(ndata, this.row, this.column, mc);
	}
	/**
	 * Replaces the Matrix with a column vector. The length of this vector must be 
	 * the same as the row count in this matrix. 
	 * @param column the column to replace, starts from 0.
	 * @param v
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Matrix<T> replaceColumn(int column,Vector<T> v){
		columnRangeCheck(column);
		if(v.isRow() || v.getSize() != row){
			throw new IllegalArgumentException();
		}
		T[][] ndata = (T[][]) getValues();
		for(int i=0;i<row;i++){
			ndata[i][column] = v.getNumber(i);
		}
		return new DMatrix<>(ndata, this.row, this.column, mc);
	}
	
	@Override
	public <N> Matrix<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		Object[][] newData = new Object[row][column];
		for(int i=0;i<row;i++){
			for(int j=0;j<column;j++){
				newData[i][j] = mapper.apply(this.getNumber(i, j));
			}
		}
		return new DMatrix<N>(newData, row, column, newCalculator);
		
	}
	@Override
	public <N> boolean valueEquals(FieldMathObject<N> obj, Function<N, T> mapper) {
		if(obj instanceof Matrix){
			Matrix<N> m = (Matrix<N>)obj;
			if(m.row == this.row && m.column == this.column){
				for(int i=0;i<row;i++){
					for(int j=0;j<column;j++){
						T t = mapper.apply(m.getNumber(i, j));
						if(!mc.isEqual(t, getNumber(i,j))){
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean valueEquals(FieldMathObject<T> obj) {
		if(obj instanceof Matrix){
			Matrix<T> m = (Matrix<T>)obj;
			if(m.row == this.row && m.column == this.column){
				for(int i=0;i<row;i++){
					for(int j=0;j<column;j++){
						if(!mc.isEqual(m.getNumber(i, j), getNumber(i,j))){
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		return "Matrix:row="+row+",column="+column;
	}
	

	/**
	 * SubMatrix is a matrix base on another Matrix.To reduce memory,the data in
	 * this MatrixN is the same as the original MatrixN but additional delta x and
	 * y are added.
	 * 
	 * @author lyc
	 *
	 */
	static class SubMatrix<T> extends Matrix<T> {
		final Object[][] data;

		final int dx, dy;

		SubMatrix(Object[][] data, int dx, int dy, int row, int column,MathCalculator<T> mc) {
			super(row, column,mc);
			this.data = data;
			this.dx = dx;
			this.dy = dy;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T getNumber(int i, int j) {
			// range check
			if (i < 0 || i >= row || j < 0 || j >= column) {
				throw new IndexOutOfBoundsException("Out of range:" + row + "��" + column + " " + i + "," + j);
			}
			return (T)data[i + dx][j + dy];
		}

		@SuppressWarnings("unchecked")
		@Override
		public Matrix<T> negative() {
			Object[][] ne = new Object[row][column];
			for (int i = dx; i < row + dx; i++) {
				for (int j = dy; j < column + dy; j++) {
					
					ne[i][j] = mc.negate((T)data[i][j]);
				}
			}
			return new DMatrix<T>(ne, row, column,mc);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Matrix<T> multiplyNumber(long n) {
			Object[][] re = new Object[row][column];
			for (int i = dx; i < row + dx; i++) {
				for (int j = dy; j < column + dy; j++) {
					re[i][j] = mc.multiplyLong((T)data[i][j], n);
				}
			}
			return new DMatrix<T>(re, row, column,mc);
		}
		@SuppressWarnings("unchecked")
		@Override
		public Matrix<T> multiplyNumber(T n) {
			Object[][] re = new Object[row][column];
			for (int i = dx; i < row + dx; i++) {
				for (int j = dy; j < column + dy; j++) {
					re[i][j] = mc.multiply((T)data[i][j], n);
				}
			}
			return new DMatrix<T>(re, row, column,mc);
		}

		@Override
		public Matrix<T> subMatrix(int i1, int j1, int i2, int j2) {
			super.subMatrix(i1, j1, i2, j2);
			// range check
			SubMatrix<T> sm = new SubMatrix<T>(data, dx + i1, dy + j1, i2 - i1 + 1, j2 - j1 + 1,mc);
			return sm;
		}

		@Override
		public T[][] getValues() {
			@SuppressWarnings("unchecked")
			T[][] re = (T[][])new Object[row][column];
			for (int i = 0; i < row; i++) {
				System.arraycopy(data[i + dx], dy, re[i], 0, column);
			}
			return re;
		}

		@Override
		public Matrix<T> transportMatrix() {
			Object[][] re = new Object[column][row];
			for (int l = 0; l < row; ++l) {
				for (int c = 0; c < column; ++c) {
					re[c][l] = data[l + dx][c + dy];
				}
			}
			return new DMatrix<T>(re, column, row,mc);
		}

		@Override
		public Matrix<T> exchangeRow(int r1, int r2) {
			rowRangeCheck(r1, r2);
			if (r1 == r2)
				return this;
			// override to reduce memory cost
			Object[][] re = new Object[row][];
			for (int l = 0; l < row; ++l) {
				re[l] = data[l + dx];
			}
			re[r1] = data[r2 + dx];
			re[r2] = data[r1 + dx];
			return new DMatrix<T>(re, column, row,mc);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Matrix<T> multiplyAndAddRow(long k, int r1, int r2) {
			rowRangeCheck(r1, r2);
			if (r1 == r2) {
				throw new IllegalArgumentException("The same row:" + r1);
			}
			Object[][] re = new Object[row][];
			for (int l = 0; l < row; ++l) {
				re[l] = data[l + dx];
			}

			re[r2] = new Object[column];
			// just create this row as a new array

			int lt = r2 + dy;
			r1 += dy;
			for (int i = dy; i < column + dy; ++i) {
				re[r2][i] = mc.add((T)data[lt][i], mc.multiplyLong((T)data[r1][i], k));
			}
			return new DMatrix<T>(re, row, column,mc);
		}
		@SuppressWarnings("unchecked")
		@Override
		public Matrix<T> multiplyAndAddRow(T k, int r1, int r2) {
			rowRangeCheck(r1, r2);
			if (r1 == r2) {
				throw new IllegalArgumentException("The same row:" + r1);
			}
			Object[][] re = new Object[row][];
			for (int l = 0; l < row; ++l) {
				re[l] = data[l + dx];
			}

			re[r2] = new Object[column];
			// just create this row as a new array

			int lt = r2 + dy;
			r1 += dy;
			for (int i = dy; i < column + dy; ++i) {
				re[r2][i] = mc.add((T)data[lt][i], mc.multiply((T)data[r1][i], k));
			}
			return new DMatrix<T>(re, row, column,mc);
		}

		
		@Override
		public Matrix<T> cofactor(int r, int c) {
			
			if(row==1||column==1)
				throw new ArithmeticException("Too small for cofactor");
			//check for edge situation which can use sub-matrix instead
			if(r==0){
				if(c==0){
					return subMatrix(1, 1, row-1, column-1);
				}else if(c==column-1){
					return subMatrix(1, 0, row-1, column-2);
				}
			}else if(r==row-1){
				if(c==0){
					return subMatrix(0, 1, row-2, column-1);
				}else if(c==column-1){
					return subMatrix(0, 0, row-2, column-2);
				}
			}
			
			
			//just like what we do in DMatrix,but extra shift is needed. 
			
			//create a new Matrix
			rowRangeCheck(r);
			columnRangeCheck(c);
			Object[][] ma = new Object[row-1][column-1];
			
			//upper-left
			for(int i=0;i<r;i++){
				for(int j=0;j<c;j++){
					ma[i][j] = data[i+dx][j+dy];
				}
			}
			//upper-right
			for(int i=0;i<r;i++){
				for(int j=c+1;j<column;j++){
					ma[i][j-1] = data[i+dx][j+dy];
				}
			}
			//downer-left
			for(int i=r+1;i<row;i++){
				for(int j=0;j<c;j++){
					ma[i-1][j] = data[i+dx][j+dy];
				}
			}
			//downer-right
			for(int i=r+1;i<row;i++){
				for(int j=c+1;j<column;j++){
					ma[i-1][j-1] = data[i+dx][j+dy];
				}
			}
			//copy ends
			return new DMatrix<T>(ma,row-1,column-1,mc);
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof DMatrix){
				DMatrix<?> mat = (DMatrix<?>) obj;
				if(this.column != mat.column | this.row != mat.row){
					return false;
				}
				Object[][] mData = mat.data;
				for(int i=0;i<row;i++){
					for(int j=0;j<column;j++){
						if(!data[i+dx][j+dy].equals(mData[i][j])){
							return false;
						}
					}
				}
				return true;
			}else if(obj instanceof SubMatrix){
				//check whether this two matrix come from a same parent.
				SubMatrix<?> sm = (SubMatrix<?>) obj;
				if(sm.data == this.data 
						&& sm.dx ==this.dx &&sm.dy ==this.dy){
					return true;
				}
				int dx2 = sm.dx;
				int dy2 = sm.dy;
				Object[][] mData = sm.data;
				for(int i=0;i<row;i++){
					for(int j=0;j<column;j++){
						if(!data[i+dx][j+dy].equals(mData[i+dx2][j+dy2])){
							return false;
						}
					}
				}
				return true;
			}
			
			return super.equals(obj);
		}

	}
	/**
	 * Fill the data with {@value T#ZERO}
	 * @param data
	 */
	private static <T> void fillData(T[][] data,T t){
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data[i].length;j++){
				data[i][j] = t;
			}
		}
	}
	/**
	 * Fill the data with {@value T#ZERO} if null
	 * @param data
	 */
	@SuppressWarnings("unused")
	private static <T> void fillDataForNull(T[][] data,MathCalculator<T> mc){
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data[i].length;j++){
				data[i][j] = data[i][j] == null ? mc.getZero() :data[i][j];
			}
		}
	}
	
	
	/**
	 * Create a matrix according to the given array.The row count of the matrix
	 * will be the first dimension's length of the array,and the column count of
	 * the matrix will be the second dimension's maximum length of the array.
	 * 
	 * @param mat
	 * @return
	 */
	public static <T> Matrix<T> valueOf(T[][] mat,MathCalculator<T> mc) {
		Objects.requireNonNull(mat);

		int row = mat.length;
		int column = -1;
		for (T[] arr : mat) {
			column = Math.max(column, arr.length);
		}
		if (row < 1 || column < 1) {
			throw new IllegalArgumentException("Illegal size:" + row + "��" + column);
		}

		@SuppressWarnings("unchecked")
		T[][] mat2 = (T[][]) new Object[row][column];
		for (int i = 0; i < row; i++) {
			for(int j=0;j<column;j++){
				mat2[i][j] = mat[i][j] == null ? mc.getZero() : mat[i][j];
			}
		}
		return new DMatrix<>(mat2, row, column,mc);
	}
	
	static <T> Matrix<T> valueOfNoCopy(T[][] mat,MathCalculator<T> mc) {
		int row = mat.length;
		int column = mat[0].length;
		for(int i=0;i<row;i++){
			if(mat[i].length != column){
				throw new IllegalArgumentException();
			}
			for(int j=0;j<column;j++){
				if(mat[i][j]==null){
					throw new IllegalArgumentException();
				}
			}
		}
		return new DMatrix<>(mat, row, column,mc);
	}
	
	/**
	 * Create a matrix according to the given array.The row count of the matrix
	 * will be the first dimension's length of the array,and the column count of
	 * the matrix will be the second dimension's maximum length of the array.<p>
	 * The returned matrix will hold the type Long.
	 * <p><b>This kind of matrix may not usually support some methods. </b>
	 * @param mat
	 * @return
	 */
	public static Matrix<Long> valueOf(long[][] mat) {
		Objects.requireNonNull(mat);

		int row = mat.length;
		int column = -1;
		for (long[] arr : mat) {
			column = Math.max(column, arr.length);
		}
		if (row < 1 || column < 1) {
			throw new IllegalArgumentException("Illegal size:" + row + "��" + column);
		}

		Long[][] mat2 = new Long[row][column];
		for (int i = 0; i < row; i++) {
			for(int j=0;j<column;j++){
				mat2[i][j] = Long.valueOf(mat[i][j]);
			}
		}
		return new DMatrix<>(mat2, row, column,Calculators.getCalculatorLong());
	}
	/**
	 * Create a matrix according to the given array.The row count of the matrix
	 * will be the first dimension's length of the array,and the column count of
	 * the matrix will be the second dimension's maximum length of the array.<p>
	 * The returned matrix will hold the type Double.<p>
	 * The {@link MathCalculator} will be assigned through method {@link MathCalculatorAdapter#getCalculatorDouble()}.
	 * @param mat
	 * @return
	 */
	public static Matrix<Double> valueOf(double[][] mat) {
		Objects.requireNonNull(mat);

		int row = mat.length;
		int column = -1;
		for (double[] arr : mat) {
			column = Math.max(column, arr.length);
		}
		if (row < 1 || column < 1) {
			throw new IllegalArgumentException("Illegal size:" + row + "��" + column);
		}

		Double[][] mat2 = new Double[row][column];
		for (int i = 0; i < row; i++) {
			for(int j=0;j<column;j++){
				mat2[i][j] = Double.valueOf(mat[i][j]);
			}
		}
		return new DMatrix<>(mat2, row, column,Calculators.getCalculatorDouble());
	}
	
	
	/**
	 * Create a matrix according to the given array.The row count of the matrix
	 * will be the first dimension's length of the array,and the column count of
	 * the matrix will be the second dimension's maximum length of the array.<p>
	 * This method only overload the similar method  {@link #valueOf(long[][])}
	 * @param mat
	 * @return
	 */
	public static Matrix<Integer> valueOf(int[][] mat) {
		Objects.requireNonNull(mat);

		int row = mat.length;
		int column = -1;
		for (int[] arr : mat) {
			column = Math.max(column, arr.length);
		}
		if (row < 1 || column < 1) {
			throw new IllegalArgumentException("Illegal size:" + row + "��" + column);
		}

		Integer[][] mat2 = new Integer[row][column];
		for (int i = 0; i < row; i++) {
			for(int j=0;j<column;j++){
				mat2[i][j] = Integer.valueOf(mat[i][j]);
			}
		}
		return new DMatrix<Integer>(mat2, row, column,Calculators.getCalculatorInteger());
	}
	
	
	/**
	 * Return a diagonal matrix containing the given numbers. M[i][i] = arr[i]
	 * 
	 * @param arr
	 * @return
	 */
	public static <T> Matrix<T> diag(T[] arr,MathCalculator<T> mc) {
		Objects.requireNonNull(arr);

		int n = arr.length;
		if (n < 1) {
			throw new IllegalArgumentException("Illegal size:" + n);
		}
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[n][n];
		fillData(mat,mc.getZero());
		for (int i = 0; i < n; i++) {
			mat[i][i] = arr[i];
		}
		return new DMatrix<T>(mat, n, n,mc);
	}
	/**
	 * Return a diagonal matrix containing the given numbers. M[i][i] = x
	 * 
	 * @param arr
	 * @return
	 */
	public static <T> Matrix<T> diag(T x,int n,MathCalculator<T> mc) {
		Objects.requireNonNull(x);
		if (n < 1) {
			throw new IllegalArgumentException("Illegal size:" + n);
		}
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[n][n];
		fillData(mat,mc.getZero());
		for (int i = 0; i < n; i++) {
			mat[i][i] = x;
		}
		return new DMatrix<T>(mat, n, n,mc);
	}
	/**
	 * Return an identity matrix whose size is n��n
	 * 
	 * @param n
	 * @return
	 */
	public static <T> Matrix<T> identityMatrix(int n,MathCalculator<T> mc) {
		if (n < 1) {
			throw new IllegalArgumentException("Illegal size:" + n);
		}
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[n][n];
		fillData(mat,mc.getZero());
		for (int i = 0; i < n; i++) {
			mat[i][i] = mc.getOne();
		}
		return new DMatrix<T>(mat, n, n,mc);
	}
	/**
	 * A zero matrix is a matrix filled with zero.
	 * @param n
	 * @return
	 */
	public static <T> Matrix<T> zeroMatrix(int n,MathCalculator<T> mc) {
		if (n < 1) {
			throw new IllegalArgumentException("Illegal size:" + n );
		}
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[n][n];
		fillData(mat,mc.getZero());
		return new DMatrix<T>(mat, n, n,mc);
	}
	
	

	/**
	 * Add two matrix.The size of two matrix should be the same.
	 * 
	 * @param m1
	 *            a matrix
	 * @param m2
	 *            another matrix
	 * @return a new matrix mat = m1+m2
	 * @throws IllegalArgumentException
	 *             if size doesn't match
	 * @throws NullPointerException
	 *             if m1==null || m2==null
	 */
	@SuppressWarnings("unchecked")
	public static <T> Matrix<T> addMatrix(Matrix<T> m1, Matrix<T> m2) {
		if (m1.row != m2.row || m1.column != m2.column) {
			throw new IllegalArgumentException(
					"Cannot add two matrix:" + m1.row + "*" + m1.column + " + " + m2.row + "*" + m2.column);
		}
		int row = m1.row;
		int column = m1.column;
		MathCalculator<T> mc = m1.mc;
		T[][] re = (T[][]) new Object[row][column];
		if (m1 instanceof DMatrix && m2 instanceof DMatrix) {
			T[][] d1 = (T[][]) ((DMatrix<T>) m1).data;
			T[][] d2 = (T[][]) ((DMatrix<T>) m2).data;
			
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < column; j++) {
					re[i][j] = mc.add(d1[i][j], d2[i][j]);
				}
			}
		} else {
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < column; j++) {
					re[i][j] = mc.add(m1.getNumber(i, j), m2.getNumber(i, j)) ;
				}
			}
		}
		return new DMatrix<T>(re, row, column,mc);
	}

	/**
	 * Return m1-m2.The size of two matrix should be the same.
	 * 
	 * @param m1
	 *            a matrix
	 * @param m2
	 *            another matrix
	 * @return a new matrix mat = m1-m2
	 * @throws IllegalArgumentException
	 *             if size doesn't match
	 * @throws NullPointerException
	 *             if m1==null || m2==null
	 */
	@SuppressWarnings("unchecked")
	public static <T> Matrix<T> minusMatrix(Matrix<T> m1, Matrix<T> m2) {
		if (m1.row != m2.row || m1.column != m2.column) {
			throw new IllegalArgumentException(
					"Cannot minus two matrix:" + m1.row + "*" + m1.column + " - " + m2.row + "*" + m2.column);
		}
		int row = m1.row;
		int column = m1.column;
		MathCalculator<T> mc = m1.mc;
		T[][] re = (T[][]) new Object[row][column];
		if (m1 instanceof DMatrix && m2 instanceof DMatrix) {
			T[][] d1 = (T[][]) ((DMatrix<T>) m1).data;
			T[][] d2 = (T[][]) ((DMatrix<T>) m2).data;
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < column; j++) {
					re[i][j] = mc.subtract(d1[i][j], d2[i][j]);
				}
			}
		} else {
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < column; j++) {
					re[i][j] = mc.subtract(m1.getNumber(i, j), m2.getNumber(i, j));
				}
			}
		}

		return new DMatrix<T>(re, row, column,mc);
	}

	/**
	 * Multiply two matrix.The column count of m1 should be equal to the row count
	 * of m2.This method only provide O(n^3) time performance.
	 * 
	 * @param m1
	 *            a matrix
	 * @param m2
	 *            another matrix
	 * @return m1��m2
	 * @throws IllegalArgumentException
	 *             if size doesn't match
	 * @throws NullPointerException
	 *             if m1==null || m2==null
	 */
	@SuppressWarnings("unchecked")
	public static <T> Matrix<T> multiplyMatrix(Matrix<T> m1, Matrix<T> m2) {
		if (m1.column != m2.row) {
			throw new IllegalArgumentException(
					"Cannot multiply two matrix:(" + m1.row + "*" + m1.column + ") * (" + m2.row + "*" + m2.column+")");
		}
		int n = m1.column;
		int row = m1.row;
		int column = m2.column;
		MathCalculator<T> mc = m1.mc;
		Object[][] re = new Object[row][column];
		if (m1 instanceof DMatrix && m2 instanceof DMatrix) {
			T[][] d1 = (T[][]) ((DMatrix<T>) m1).data;
			T[][] d2 = (T[][]) ((DMatrix<T>) m2).data;
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < column; j++) {
					T sum = mc.getZero();
					for (int k = 0; k < n; k++) {
						sum = mc.add(sum, mc.multiply(d1[i][k], d2[k][j])) ;
					}
					re[i][j] = sum;
				}
			}
		} else {
			T[][] fs1 = (T[][]) m1.getValues();
			T[][] fs2 = (T[][]) m2.getValues();
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < column; j++) {
					T sum = mc.getZero();
					for (int k = 0; k < n; k++) {
						sum = mc.add(sum, mc.multiply(fs1[i][k], fs2[k][j])) ;  
					}
					re[i][j] = sum;
				}
			}
		}
		return new DMatrix<T>(re, row, column,mc);
	}

	/**
	 * Calculate the result of {@code mat^pow},the given matrix must have the
	 * same column and column count.Negative power values are illegal and if
	 * {@code pow==0} then this method is the same as call
	 * {@link #identityMatrix(int)} which parameter is the given matrix's column
	 * or column count.
	 * 
	 * @param mat
	 *            the base
	 * @param pow
	 *            the exponent
	 * @return a MatrixN that equals to {@code (mat)}<sup>{@code pow}</sup>.
	 */
	public static <T> Matrix<T> matrixPower(Matrix<T> mat, int pow) {
		// do range check
		if (mat.row != mat.column)
			throw new IllegalArgumentException("Cannot calculate" + mat.row + "*" + mat.column);
		if (pow == 0)
			return identityMatrix(mat.row,mat.mc);
		if (pow == 1)
			return mat;

		// calculate two power:
		int p = 1;
		int n = 1;
		while (n <= pow) {
			n *= 2;
			p++;
		}

		@SuppressWarnings("unchecked")
		Matrix<T>[] twoPowers = new Matrix[p];
		twoPowers[0] = mat;
		for (int i = 1; i < p; i++) {
			twoPowers[i] = Matrix.multiplyMatrix(twoPowers[i - 1], twoPowers[i - 1]);
		}
//		for (MatrixN ma : twoPowers) {
//			ma.printMatrix();
//		}

		// calculate
		Matrix<T> re = twoPowers[p - 1];
		n = n >>> 1;
		for (int i = p - 2; i > -1; i--) {
			n = n >>> 1;
			if ((pow & n) > 0) {
				re = Matrix.multiplyMatrix(re, twoPowers[i]);
			}
		}
		return re;
	}
	/**
	 * Apply the function to this matrix and returns the result.
	 * @param mat
	 * @param f
	 * @return
	 */
	public Matrix<T> applyFunction(MathFunction<T, T> f){
		@SuppressWarnings("unchecked")
		T[][] mat = (T[][]) new Object[row][column];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				mat[i][j] = f.apply(getNumber(i, j));
			}
		}
		return new DMatrix<>(mat, row, column, mc);
	}
	
	/**
	 * Print the matrix using the Printer
	 * @param ma
	 */
	public void printMatrix() {
		Printer.printMatrix(getValues());
	}
	/**
	 * Creates a Matrix from several vectors, the vector should have 
	 * the same size and be either all row vector or column vector. 
	 * @param vs at least one vector
	 * @return
	 */
	@SafeVarargs
	public static <T> Matrix<T> fromVectors(Vector<T> v,Vector<T>...vs){
		final boolean isRow = v.isRow();
		final int column,row;
		final int size = v.getSize();
		for(Vector<T> vt : vs){
			if(vt.getSize() != size){
				throw new IllegalArgumentException("Size mismatch!");
			}
		}
		if(isRow){
			row = vs.length+1;
			column = v.getSize();
		}else{
			 row = v.getSize();
			 column = vs.length+1;
		}
		@SuppressWarnings("unchecked")
		Vector<T>[] arr = new Vector[vs.length+1];
		arr[0] = v;
		System.arraycopy(vs, 0, arr, 1, vs.length);
		return new VMatrix<>(arr, row, column, v.getMathCalculator(), isRow);
	}
	
	public static <T> MatrixBuilder<T> getBuilder(int row,int column,MathCalculator<T> mc){
		return new MatrixBuilder<>(row,column,mc);
	}
	
	public static class MatrixBuilder<T> implements Cloneable{
		final MathCalculator<T> mc;
		final int row,column;
		final Object[][] data;
		boolean disposed = false;
		/**
		 * @param mc
		 * @param row
		 * @param column
		 */
		MatrixBuilder( int row, int column,MathCalculator<T> mc) {
			super();
			this.mc = mc;
			this.row = row;
			this.column = column;
			data = new Object[row][column];
			fillData(data,mc.getZero());
		}
		
		/**
		 * @param mc
		 * @param row
		 * @param column
		 */
		private MatrixBuilder( int row, int column,MathCalculator<T> mc,Object[][] data) {
			super();
			this.mc = mc;
			this.row = row;
			this.column = column;
			this.data = data;
		}
		
		protected void rowRangeCheck(int r){
			if (r < 0 || r >= row) {
				throw new IndexOutOfBoundsException("Row=" + row + ":" + r);
			}
		}
		
		protected void columnRangeCheck(int c) {
			if (c < 0 || c >= column) {
				throw new IndexOutOfBoundsException("Column=" + column + ":" + c);
			}
		}
		
		protected void checkDisposed() {
			if(disposed) {
				throw new IllegalStateException("The builder has already built!");
			}
		}
		
		public MatrixBuilder<T> set(T x,int i,int j){
			checkDisposed();
			rowRangeCheck(i);
			columnRangeCheck(j);
			data[i][j] = x;
			return this;
		}
		
		public MatrixBuilder<T> fillRow(T x,int row){
			checkDisposed();
			rowRangeCheck(row);
			for(int j=0;j<column;j++) {
				data[row][j] = x;
			}
			return this;
		}
		
		public MatrixBuilder<T> fillColumn(T x,int column){
			checkDisposed();
			columnRangeCheck(column);
			for(int i=0;i<row;i++) {
				data[i][column] = x;
			}
			return this;
		}
		
		public MatrixBuilder<T> clone(){
			Object[][] ndata = ArraySup.deepCopy(data); 
			return new MatrixBuilder<>(row, column, mc,ndata);
		}
		
		public Matrix<T> build(){
			checkDisposed();
			disposed = true;
			return new DMatrix<>(data, row, column, mc);
		}
	}
	

}
