package cn.timelives.java.utilities;

import static cn.timelives.java.utilities.Printer.printMatrix;

import java.util.Arrays;
import java.util.Objects;
/**
 * A matrix is like a two dimension integer array,but the number in the matrix is unchangeable.
 * A n¡Ám matrix has n lines and m rows. m,n=1,2,3,4,5...
 * <p>To create a Matrix , you can either use a two-dimension array as parameter or directly call  
 * some prepared method.
 * @author lyc
 * @deprecated this class is replaced by {@link cn.timelives.java.math.linearAlgebra.Matrix}
 */
public abstract class Matrix {
	
	
	
	/**
	 * Line count
	 */
	final int line ;
	/**
	 * Row count:
	 */
	final int row;
	
	protected Matrix(int line,int row){
		this.line = line;
		this.row = row;
	}
	
	/**
	 * data:int[line][row],the numbers in this matrix
	 */
	
	/**
	 * Return the line count of this matrix.
	 * @return line count of this matrix
	 */
	public int getLineCount(){
		return line;
	}
	/**
	 * Return the line row of this matrix.
	 * @return line row of this matrix
	 */
	public int getRowCount(){
		return row;
	}
	/**
	 * Get the number of the matrix in position i,j.The index is start from 0.
	 * @param i the line index
	 * @param j the row index
	 * @return
	 */
	public abstract int getNumber(int i,int j);
	
	
	
	/**
	 * Return a Matrix = {@code -this} 
	 * @return {@code -this}
	 */
	public abstract Matrix negative();
	/**
	 * Return a Matrix = this<sup>T</sup>.The new Matrix's line count = this.row , 
	 * new Matrix's row count = this.line.
	 * @return <tt>this<sup>T</sup></tt>
	 */
	public abstract Matrix transportMatrix();
	/**
	 * Return a Matrix = {@code n*this} .
	 * <p>This is one of the elementary transformations.
	 * @return {@code n*this}
	 */
	public abstract Matrix multiplyNumber(int n);
	/**
	 * Exchange two line in the matrix and return the new Matrix.
	 * <p>This is one of the elementary transformations.
	 * @param l1 a line 
	 * @param l2 another line
	 * @return an exchanged Matrix 
	 */
	public Matrix exchangeLine(int l1,int l2){
		lineRangeCheck(l1,l2);
		if(l1==l2){
			return this;
		}
		int[][] re = getValues();
		for(int c=0;c<row;++c){
			int t =	re[l1][c];
			re[l1][c] = re[l2][c];
			re[l2][c] = t;
		}
		return new DMatrix(re,line,row); 
	}
	/**
	 * Exchange two row in the matrix and return the new Matrix.
	 * <p>This is one of the elementary transformations.
	 * @param l1 a row 
	 * @param l2 another row
	 * @return an exchanged Matrix 
	 */
	public Matrix exchangeRow(int c1,int c2){
		rowRangeCheck(c1,c1);
		if(c1==c2){
			return this;
		}
		int[][] re = getValues();
		for(int l=0;l<row;++l){
			int t =	re[l][c1];
			re[l][c1] = re[l][c2];
			re[l][c2] = t;
		}
		return new DMatrix(re,line,row); 
	}
	
	/**
	 * Multiply one line in this matrix with k and add the result to another line.
	 * <p>This is one of the elementary transformations.
	 * @param k multiplier
	 * @param l1 the line to multiply 
	 * @param l2 the line to add to
	 * @return the result Matrix
	 */
	public Matrix multiplyAndAddLine(int k,int l1,int l2){
		lineRangeCheck(l1,l2);
		if(l1==l2){
			throw new IllegalArgumentException("The same line:"+l1);
		}
		int[][] re = getValues();
		for(int i=0;i<row;i++){
			re[l2][i] += re[l1][i]*k;
		}
		return new DMatrix(re,line,row);
	}
	/**
	 * Multiply one row in this matrix with k and add the result to another row.
	 * @param k multiplier
	 * @param c1 the row to multiply 
	 * @param c2 the row to add to
	 * @return the result Matrix
	 */
	public Matrix multiplyAndAddRow(int k,int c1,int c2){
		rowRangeCheck(c1,c1);
		if(c1==c2){
			throw new IllegalArgumentException("The same row:"+c1);
		}
		int[][] re = getValues();
		for(int i=0;i<line;i++){
			re[i][c1] += re[i][c2]*k;
		}
		return new DMatrix(re,line,row);
	}
	
	/**
	 * Return a copy of the values in this matrix.Notice that the returned {@code int[][]} is {@code int[line][row]}.
	 * The returned array is a copy of the value so the change of it won't change the original values in this Matrix. 
	 * @return a two-dimension array containing the values in this matrix
	 */
	public abstract int[][] getValues();
	/**
	 * Range check
	 * @param ls
	 */
	protected void lineRangeCheck(int...ls){
		for(int l : ls){
			if(l<0 || l>=line){
				throw new IndexOutOfBoundsException("Line="+line+":"+l);
			}
		}
	}
	/**
	 * Range check
	 * @param cs
	 */
	protected void rowRangeCheck(int...cs){
		for(int c : cs){
			if(c<0 || c>=line){
				throw new IndexOutOfBoundsException("Row="+row+":"+c);
			}
		}
	}
	
	
	/**
	 * Return a sub matrix.{@code (i1,j1)-(i2,j2)}.The new matrix's line count will be {@code i2-i1+1}
	 * @param i1
	 * @param j1
	 * @param i2
	 * @param j2
	 * @return a sub matrix containing a sub matrix in the given range
	 */
	public Matrix subMatrix(int i1,int j1,int i2,int j2){
		//do range check
		if(i1<0||j1<0||i2>=line||j2>=row || i1>=i2 || j1>=j2){
			throw new IllegalArgumentException("Illegal Argument:"+line+"¡Á"+row+":("+i1+","+j1+")-("+i2+","+j2+")");
		}
		return null;//
	}
	
	
	static class DMatrix extends Matrix{
		final int[][] data;
		/**
		 * This method won't check mat's size and all the changed to the mat will be reflected to 
		 * this Matrix.
		 * @param mat
		 */
		DMatrix(int[][] mat,int line,int row){
			super(line,row);
			data = mat;
		}
		
		public boolean equals(Object obj){
			if(obj instanceof DMatrix){
				DMatrix m2 = (DMatrix) obj;
//				if(m2.line==this.line&&m2.row==this.row){
					return Arrays.deepEquals(data, m2.data); 
//				}
			}
			return super.equals(obj);
		}
		
		public int hashCode(){
			return data.hashCode();
		}
		
		public String toString(){
			return Arrays.deepToString(data);
		}
		public Matrix negative(){
			int[][] ne = new int[line][row];
			for(int i=0;i<line;i++){
				for(int j=0;j<row;j++){
					ne[i][j] = -data[i][j];
				}
			}
			return new DMatrix(ne,line,row);
		}
		/**
		 * Return a Matrix = {@code n*this} 
		 * @return {@code n*this}
		 */
		public Matrix multiplyNumber(int n){
			int[][] re = new int[line][row];
			for(int i=0;i<line;i++){
				for(int j=0;j<row;j++){
					re[i][j] = n*data[i][j];
				}
			}
			return new DMatrix(re,line,row);
		}

		@Override
		public int getNumber(int i, int j) {
			return data[i][j];
		}

		@Override
		public Matrix subMatrix(int i1, int j1, int i2, int j2) {
			super.subMatrix(i1, j1, i2, j2);
			//range check
			SubMatrix sm = new SubMatrix(data,i1,j1,i2-i1+1,j2-j1+1);
			return sm;
		}

		@Override
		public int[][] getValues() {
			int[][] re = new int[line][row];
			for(int i=0;i<line;i++){
				System.arraycopy(data[i], 0, re[i], 0, row);
			}
			return re;
		}

		@Override
		public Matrix transportMatrix() {
			int[][] re = new int[row][line];
			for(int l=0;l<line;++l){
				for(int c=0;c<row;++c){
					re[c][l] = data[l][c];
				}
			}
			return new DMatrix(re,row,line);
		}
		
		
		@Override
		public Matrix exchangeLine(int l1,int l2){
			lineRangeCheck(l1,l2);
			if(l1==l2)
				return this;
			//override to reduce memory cost
			int[][] re = new int[line][];
			for(int l=0;l<line;++l){
				re[l] = data[l];
			}
			re[l1] = data[l2];
			re[l2] = data[l1];
			return new DMatrix(re,row,line);
		}
		
		@Override
		public Matrix multiplyAndAddLine(int k,int l1,int l2){
			lineRangeCheck(l1,l2);
			if(l1==l2){
				throw new IllegalArgumentException("The same line:"+l1);
			}
			int[][] re = new int[line][];
			for(int l=0;l<line;++l){
				re[l] = data[l];
			}
			
			re[l2] = new int[row];
			//just copy this line
			
			for(int i=0;i<row;++i){
				re[l2][i] = data[l1][i]*k + data[l2][i]; 
			}
			return new DMatrix(re,line,row);
		}
		
	}
	/**
	 * SubMatrix is a matrix base on another Matrix.To reduce memory,the data in this Matrix is the 
	 * same as the original Matrix but additional delta x and y are added. 
	 * @author lyc
	 *
	 */
	static class SubMatrix extends Matrix{
		final int[][] data;
		
		final int dx,dy;
		
		SubMatrix(int[][] data,int dx,int dy,int line,int row){
			super(line,row);
			this.data = data;
			this.dx = dx;
			this.dy = dy;
		}

		@Override
		public int getNumber(int i, int j) {
			//range check
			if(i<0||i>=line||j<0||j>=row){
				throw new IndexOutOfBoundsException("Out of range:"+line+"¡Á"+row+" "+i+","+j);
			}
			return data[i+dx][j+dy];
		}

		@Override
		public Matrix negative() {
			int[][] ne = new int[line][row];
			for(int i=dx;i<line+dx;i++){
				for(int j=dy;j<row+dy;j++){
					ne[i][j] = -data[i][j];
				}
			}
			return new DMatrix(ne,line,row);
		}

		@Override
		public Matrix multiplyNumber(int n) {
			int[][] re = new int[line][row];
			for(int i=dx;i<line+dx;i++){
				for(int j=dy;j<row+dy;j++){
					re[i][j] = n*data[i][j];
				}
			}
			return new DMatrix(re,line,row);
		}

		@Override
		public Matrix subMatrix(int i1, int j1, int i2, int j2) {
			super.subMatrix(i1, j1, i2, j2);
			//range check
			SubMatrix sm = new SubMatrix(data,dx+i1,dy+j1,i2-i1+1,j2-j1+1);
			return sm;
		}

		@Override
		public int[][] getValues() {
			int[][] re = new int[line][row];
			for(int i=0;i<line;i++){
				System.arraycopy(data[i+dx], dy, re[i], 0, row);
			}
			return re;
		}

		@Override
		public Matrix transportMatrix() {
			int[][] re = new int[row][line];
			for(int l=0;l<line;++l){
				for(int c=0;c<row;++c){
					re[c][l] = data[l+dx][c+dy];
				}
			}
			return new DMatrix(re,row,line);
		}
		@Override
		public Matrix exchangeLine(int l1,int l2){
			lineRangeCheck(l1,l2);
			if(l1==l2)
				return this;
			//override to reduce memory cost
			int[][] re = new int[line][];
			for(int l=0;l<line;++l){
				re[l] = data[l+dx];
			}
			re[l1] = data[l2+dx];
			re[l2] = data[l1+dx];
			return new DMatrix(re,row,line);
		}
		
		@Override
		public Matrix multiplyAndAddLine(int k,int l1,int l2){
			lineRangeCheck(l1,l2);
			if(l1==l2){
				throw new IllegalArgumentException("The same line:"+l1);
			}
			int[][] re = new int[line][];
			for(int l=0;l<line;++l){
				re[l] = data[l+dx];
			}
			
			re[l2] = new int[row];
			//just create this line as a new array
			
			int lt =l2 +dy;
			l1+= dy;
			for(int i=dy;i<row+dy;++i){
				re[l2][i] = data[l1][i]*k+data[lt][i];
			}
			return new DMatrix(re,line,row);
		}
	}
	
	/**
	 * Create a matrix according to the given array.The line count of the matrix will 
	 * be the first dimension's length of the array,and the row count of the matrix will be 
	 * the second dimension's maximum length of the array. 
	 * 
	 * @param mat
	 * @return
	 */
	public static Matrix valueOf(int[][] mat){
		Objects.requireNonNull(mat);
		
		int line = mat.length;
		int row = -1;
		for(int[] arr:mat){
			row = Math.max(row, arr.length);
		}
		if(line<1 || row<1){
			throw new IllegalArgumentException("Illegal size:"+line+"¡Á"+row);
		}
		
		int[][] mat2 = new int[line][];
		for(int i=0;i<line;i++){
			mat2[i] = Arrays.copyOf(mat[i], row);
		}
		return new DMatrix(mat2,line,row);
	}
	
	/**
	 * Return a diagonal matrix containing the given numbers.M[i][i] = arr[i]
	 * @param arr
	 * @return
	 */
	public static Matrix diag(int[] arr){
		Objects.requireNonNull(arr);
		
		int n = arr.length;
		if(n<1){
			throw new IllegalArgumentException("Illegal size:"+n+"¡Á"+n);
		}
		int[][] mat = new int[n][n];
		for(int i=0;i<n;i++){
			mat[i][i] = arr[i];
		}
		return new DMatrix(mat,n,n);
	}
	/**
	 * Return an identity matrix whose size is n¡Án
	 * @param n
	 * @return
	 */
	public static Matrix identityMatrix(int n){
		if(n<1){
			throw new IllegalArgumentException("Illegal size:"+n+"¡Á"+n);
		}
		int[][] mat = new int[n][n];
		for(int i=0;i<n;i++){
			mat[i][i] = 1;
		}
		return new DMatrix(mat,n,n);
	}
	
	public static Matrix zeroMatrix(int n){
		if(n<1){
			throw new IllegalArgumentException("Illegal size:"+n+"¡Á"+n);
		}
		int[][] mat = new int[n][n];
		return new DMatrix(mat,n,n);
	}
	/**
	 * Add two matrix.The size of two matrix should be the same.
	 * @param m1 a matrix
	 * @param m2 another matrix
	 * @return a new matrix mat = m1+m2
	 * @throws IllegalArgumentException if size doesn't match
	 * @throws NullPointerException if m1==null || m2==null
	 */
	public static Matrix addMatrix(Matrix m1,Matrix m2){
		if(m1.line!=m2.line || m1.row!=m2.row){
			throw new IllegalArgumentException("Cannot add two matrix:"+m1.line+"¡Á"+m1.row+" + "+m2.line+"¡Á"+m2.row);
		}
		int line = m1.line;
		int row = m1.row;
		int[][] re = new int[line][row];
		if(m1 instanceof DMatrix && m2 instanceof DMatrix){
			int[][] d1 = ((DMatrix)m1).data;
			int[][] d2 = ((DMatrix)m2).data;
			for(int i=0;i<line;i++){
				for(int j=0;j<row;j++){
					re[i][j] = d1[i][j] + d2[i][j];
				}
			}
		}else{
			for(int i=0;i<line;i++){
				for(int j=0;j<row;j++){
					re[i][j] = m1.getNumber(i, j)+m2.getNumber(i, j);
				}
			}
		}
		return new DMatrix(re,line,row);
	}
	/**
	 * Return m1-m2.The size of two matrix should be the same.
	 * @param m1 a matrix
	 * @param m2 another matrix
	 * @return a new matrix mat = m1-m2
	 * @throws IllegalArgumentException if size doesn't match
	 * @throws NullPointerException if m1==null || m2==null
	 */
	public static Matrix minusMatrix(Matrix m1,Matrix m2){
		if(m1.line!=m2.line || m1.row!=m2.row){
			throw new IllegalArgumentException("Cannot minus two matrix:"+m1.line+"¡Á"+m1.row+" - "+m2.line+"¡Á"+m2.row);
		}
		int line = m1.line;
		int row = m1.row;
		int[][] re = new int[line][row];
		if(m1 instanceof DMatrix && m2 instanceof DMatrix){
			int[][] d1 = ((DMatrix)m1).data;
			int[][] d2 = ((DMatrix)m2).data;
			for(int i=0;i<line;i++){
				for(int j=0;j<row;j++){
					re[i][j] = d1[i][j] - d2[i][j];
				}
			}
		}else{
			for(int i=0;i<line;i++){
				for(int j=0;j<row;j++){
					re[i][j] = m1.getNumber(i, j) - m2.getNumber(i, j);
				}
			}
		}
		
		return new DMatrix(re,line,row);
	}
	
	/**
	 * Multiply two matrix.The row count of m1 should be equal to the line count of m2.This method 
	 * only provide O(n^3) time performance.
	 * @param m1 a matrix
	 * @param m2 another matrix
	 * @return m1¡¤m2
	 * @throws IllegalArgumentException if size doesn't match
	 * @throws NullPointerException if m1==null || m2==null
	 */
	public static Matrix multiplyMatrix(Matrix m1,Matrix m2){
		if(m1.row!=m2.line){
			throw new IllegalArgumentException("Cannot multiply two matrix:"+m1.line+"¡Á"+m1.row+" ¡¤ "+m2.line+"¡Á"+m2.row);
		}
		int n = m1.row;
		int line = m1.line;
		int row = m2.row;
		int[][] re = new int[line][row];
		if(m1 instanceof DMatrix && m2 instanceof DMatrix){
			int[][] d1 = ((DMatrix) m1).data;
			int[][] d2 = ((DMatrix) m2).data;
			for (int i = 0; i < line; i++) {
				for (int j = 0; j < row; j++) {
					int sum = 0;
					for (int k = 0; k < n; k++) {
						sum += d1[i][k] * d2[k][j];
					}
					re[i][j] = sum;
				}
			}
		}else{
			for (int i = 0; i < line; i++) {
				for (int j = 0; j < row; j++) {
					int sum = 0;
					for (int k = 0; k < n; k++) {
						sum += m1.getNumber(i, k) * m2.getNumber(k, j);
					}
					re[i][j] = sum;
				}
			}
		}
		return new DMatrix(re,line,row);
	}
	
	/**
	 * Calculate the result of {@code mat^pow},the given matrix must have the same column and 
	 * row count.Negative power values are illegal and if {@code pow==0} then this method is 
	 * the same as call {@link #identityMatrix(int)} which parameter is the given matrix's column 
	 * or row count.
	 * 
	 * @param mat the base 
	 * @param pow the exponent
	 * @return a Matrix that equals to {@code (mat)}<sup>{@code pow}</sup>.
	 */
	public static Matrix matrixPower(Matrix mat,int pow){
		//do range check
		if(mat.line!=mat.row)
			throw new IllegalArgumentException("Cannot calculate"+mat.line+"¡Á"+mat.row);
		if(pow ==0 )
			return identityMatrix(mat.line);
		if(pow == 1)
			return mat;
		
		//calculate two power:
		int p = 1;
		int n = 1;
		while(n<=pow){
			n *= 2;
			p++;
		}
		
		Matrix[] twoPowers = new Matrix[p];
		twoPowers[0] = mat;
		for(int i=1;i<p;i++){
			twoPowers[i] = Matrix.multiplyMatrix(twoPowers[i-1],twoPowers[i-1]);
		}
		
		//calculate  
		Matrix re = twoPowers[p-1];
		n = n >>> 1;
		for(int i=p-2;i>-1;i--){
			n = n >>> 1;
			if((pow & n )>0){
				re = Matrix.multiplyMatrix(re, twoPowers[i]);
			}
		}
		return re;
	}
	
	
	public static void main(String[] args) {
//		Matrix m1 = identityMatrix(10);
//		Matrix m2 = identityMatrix(10);
//		System.out.println(m1.equals(m2));
		int[][] mat = new int[10][];
		for(int i=0;i<mat.length;i++){
			mat[i] = ArraySup.ranArr(10, 100);
		}
//		int[][] ma2 = new int[10][10];
//		Matrix m = Matrix.valueOf(mat);
//		Matrix m2 = m.subMatrix(2, 2, 7, 7);
//		System.arraycopy(mat, 0, ma2, 0, 10);
		printMatrix(mat);
	}
}
