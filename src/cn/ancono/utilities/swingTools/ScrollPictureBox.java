package cn.ancono.utilities.swingTools;

import javax.swing.*;
import java.awt.*;

public class ScrollPictureBox extends PictureBox implements Scrollable {


    /**
     *
     */
    private static final long serialVersionUID = -7887421166120336096L;
    /**
     * the size rate
     */
    private int r = BASE_RATE;

    public static final int BASE_RATE = 100000;

    public static final int MIN_RATE = 5000;

    public static final int MAX_RATE = 1000000;

    /**
     * Create the panel.
     */
    public ScrollPictureBox() {

    }

    /**
     * set the size rate to make the image bigger or smaller,
     * using a base in 100000 which means 100000 is the original size
     */
    public void setSizeRate(int sizeRate) {
        if (sizeRate < MIN_RATE)
            r = MIN_RATE;
        else if (sizeRate > MAX_RATE)
            r = MAX_RATE;
        else
            r = sizeRate;
        pictureChanged();

    }

    /**
     * get the size rate of the picture
     *
     * @return size rate of the picture
     */
    public int getSizeRate() {
        return r;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(400, 400);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        switch (orientation) {
            case SwingConstants.VERTICAL:
                return visibleRect.height;
            case SwingConstants.HORIZONTAL:
                return visibleRect.width;
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            return parent.getWidth() > getPicWidth();
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        Container parent = SwingUtilities.getUnwrappedParent(this);
        if (parent instanceof JViewport) {
            return parent.getHeight() > getPicHeight();
        }
        return false;
    }

    @Override
    public void paintPicture(Graphics g) {

        g.drawImage(picture, 0, 0, getPicWidth(), getPicHeight(), this);
    }

    @Override
    protected void pictureChanged() {
        this.setSize(getPicWidth(), getPicHeight());
        this.setPreferredSize(new Dimension(getPicWidth(), getPicHeight()));
        super.pictureChanged();
    }


    protected int getPicWidth() {
        return super.getPicWidth() * r / BASE_RATE;
    }

    protected int getPicHeight() {
        return super.getPicHeight() * r / BASE_RATE;
    }
}
