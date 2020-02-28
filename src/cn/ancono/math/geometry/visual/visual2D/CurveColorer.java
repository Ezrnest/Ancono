/**
 * 2017-11-16
 */
package cn.ancono.math.geometry.visual.visual2D;

import java.awt.*;

/**
 * @author liyicheng
 * 2017-11-16 21:33
 */
public interface CurveColorer {

    Color getColor(int i, int j, double[][] data);
}
