/**
 * 2017-11-16
 */
package cn.ancono.math.geometry.visual.visual2D;

import java.awt.*;
import java.util.HashMap;

/**
 * @author liyicheng
 * 2017-11-16 21:36
 */
public class SimpleCurveColorer implements CurveColorer {
    private final Color c;

    /**
     *
     */
    SimpleCurveColorer(Color c) {
        this.c = c;
    }

    /*
     * @see cn.ancono.math.visual.CurveColorer#getColor(int, int, double[][], double, double)
     */
    @Override
    public Color getColor(int i, int j, double[][] data) {
        return c;
    }

    public static SimpleCurveColorer ofColor(Color c) {
        SimpleCurveColorer col = map.get(c);
        if (col == null) {
            synchronized (map) {
                col = new SimpleCurveColorer(c);
                map.put(c, col);
            }
        }
        return col;
    }

    private static final HashMap<Color, SimpleCurveColorer> map = new HashMap<>();

}
