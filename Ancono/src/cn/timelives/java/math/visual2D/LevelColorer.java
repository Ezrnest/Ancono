/**
 * 2017-11-17
 */
package cn.timelives.java.math.visual2D;

import java.awt.Color;
import java.util.Objects;
import java.util.function.IntFunction;

import cn.timelives.java.utilities.ArraySup;

/**
 * @author liyicheng
 * 2017-11-17 18:42
 *
 */
public class LevelColorer implements CurveColorer {
	
	private final LeveledPredicate pre;
	private final IntFunction<Color> levelMap;
	/**
	 * 
	 * @param pre
	 * @param levelMap a function to map the level to color.
	 */
	public LevelColorer(LeveledPredicate pre,IntFunction<Color> levelMap) {
		
		this.pre = Objects.requireNonNull(pre);
		this.levelMap = Objects.requireNonNull(levelMap);
		
	}
	
	

	/*
	 * @see cn.timelives.java.math.visual.CurveColorer#getColor(int, int, double[][])
	 */
	@Override
	public Color getColor(int i, int j, double[][] data) {
		int level = pre.getDrawLevel(data, i, j);
		return levelMap.apply(level);
	}
	
	public static LevelColorer getLeveled(LeveledPredicate pre,Color[] colors) {
		ArraySup.notEmpty(colors);
		if(colors.length != pre.getLevelNum()) {
			throw new IllegalArgumentException();
		}
		return new LevelColorer(pre, x -> x <0 ? null : colors[x]);
	}

}
