/**
 * 2017-11-18
 */
package cn.ancono.math.geometry.visual.visual2D;

import java.awt.*;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A decorator that will fill the image with a simple color.
 *
 * @author liyicheng
 * 2017-11-18 16:14
 */
public class BackGroundDecorator implements ImageDecorator {
    private final Color c;

    /**
     *
     */
    BackGroundDecorator(Color c) {
        this.c = Objects.requireNonNull(c);
    }

    /*
     * @see cn.ancono.math.visual.ImageDecorator2D#process(java.awt.image.BufferedImage, java.awt.geom.Rectangle2D.Double)
     */
    @Override
    public BufferedImage process(BufferedImage image, Graphics2D g, Double rect) {
        g.setColor(c);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        return image;
    }

    private static final Map<Color, BackGroundDecorator> map = new HashMap<>();

    public static BackGroundDecorator ofColor(Color c) {
        BackGroundDecorator bgd = map.get(c);
        if (bgd == null) {
            bgd = new BackGroundDecorator(c);
            synchronized (map) {
                map.put(c, bgd);
            }
        }
        return bgd;
    }


}
