/**
 * 2017-11-17
 */
package cn.timelives.java.math.visual2D;


/**
 * @author liyicheng
 * 2017-11-17 18:31
 *
 */
public abstract class BufferedCurve implements DrawableCurve {
	protected final SubstitutableCurve f;
	
	
	/**
	 * The buffer of the computed data, the actual width and height is 
	 * the width and height required plus two. 
	 * Therefore, shifting is needed: <P>
	 * <b>data[i+1][j+1]</b>
	 */
	protected double[][] data;
	/**
	 * 
	 */
	public BufferedCurve(SubstitutableCurve f) {
		this.f = f;
	}
	
	protected double getData(int i,int j) {
		return data[i+1][j+1];
	}
	
	/*
	 * @see cn.timelives.java.math.visual.DrawableCurve#compute(double, double)
	 */
	@Override
	public double compute(double x, double y) {
		return f.compute(x, y);
	}

	@Override
	public void assignWorkingArea(java.awt.geom.Rectangle2D.Double rect, int width, int height, double dx,
			double dy) {
		data = new double[width+2][height+2];
		double tx = rect.x - dx;
		double ty = rect.y - dy;
		for(int j=0;j<height+2;j++) {
			tx = rect.x - dx;
			for(int i=0;i<width+2;i++) {
				data[i][j] = f.compute(tx, ty);
				tx+= dx;
			}
			ty += dy;
		}
		
	}

	/*
	 * @see cn.timelives.java.math.visual.DrawableCurve#clear()
	 */
	@Override
	public void clear() {
		data = null;
	}
	
	

}
