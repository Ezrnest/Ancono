/**
 * 2017-11-16
 */
package cn.timelives.java.math.visual2D;

import java.awt.Color;
import java.util.Arrays;
import java.util.function.DoubleFunction;

/**
 * @author liyicheng
 * 2017-11-16 21:37
 *
 */
public class MapColorer implements CurveColorer {
	
	protected final DoubleFunction<Color> color;
	
	/**
	 * 
	 */
	MapColorer(DoubleFunction<Color> color) {
		this.color = color;
	}

	/*
	 * @see cn.timelives.java.math.visual.CurveColorer#getColor(int, int, double[][], double, double)
	 */
	@Override
	public Color getColor(int i, int j, double[][] data) {
		return color.apply(data[i][j]);
	}
	
	/**
	 * Gets a leveled color. It is required that {@code levels.length == colors.length-1}
	 * <pre>
	 * Levels:[      -2       0        5     ]
	 * Colors:[ BLUE    GREEN   YELLOW   RED ]
	 * </pre>
	 * @param level a sorted array
	 * @param colors
	 * @return
	 */
	public static MapColorer getLeveled(double[] levels,Color[] colors) {
		if(levels.length != colors.length-1) {
			throw new IllegalArgumentException();
		}
		double x = levels[0];
		for(int i=1;i<levels.length;i++) {
			if(x > levels[i]) {
				throw new IllegalArgumentException();
			}
			x = levels[i];
		}
		return new MapColorer( v ->{
			int t = Arrays.binarySearch(levels, v);
			if(t < -1) {
				t = -t;
			}
			return colors[t];
		});
	}
	

}
