package cn.timelives.java.utilities.swingTools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.border.BevelBorder;

/**
 * A Picture box to show an image.And it is very easy to use and it can provide
 * 
 * @author lyc
 *
 */
public class FixedPictureBox extends PictureBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1526545954002201593L;

	/**
	 * the way of the Image shown in this Picture Box
	 */
	private int fitMode;
	private Location location = Location.UPPER_LEFT;
	

	/**
	 * the default fitting mode.Only show the image from the top-left corner
	 * without scaling it. The part out of the PictureBox will not be shown,and
	 * the empty part of the PictureBox will be it's background's color.
	 */
	public static final int MODE_DEFAULT = 0;
	/**
	 * scale the picture to fit the width and the height of the PictureBox
	 */
	public static final int MODE_AUTOSIZE = 1;
	/**
	 * scale the picture to fit the width and the height without changing the
	 * ratio of the original picture's length and width.The picture will be
	 * shown completely with scaling, and some empty part may be remained.
	 */
	public static final int MODE_RESIZE_KEEP_RATIO = 2;

	public enum Location {
		UPPER_LEFT, DOWNER_LEFT, UPPER_RIGHT, DOWNER_RIGHT, CENTER;
	}

	/**
	 * Create the panel.
	 */
	public FixedPictureBox() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setBackground(Color.WHITE);
	}

	/**
	 * create a PictureBox and set it's mode
	 * 
	 * @param mode
	 *            the mode of the PictureBox
	 */
	public FixedPictureBox(int mode) {
		this.fitMode = mode;
	}

	/**
	 * Create the PictureBox with given mode and picture path
	 * 
	 * @param mode
	 *            the mode of the PictureBox
	 * @param picturePath
	 *            the path of the picture
	 * 
	 */
	public FixedPictureBox(int mode, String picturePath) {
		this.fitMode = mode;
		if (!pictureSet(picturePath)) {
			throw new RuntimeException("Cannot load picture");
		}
	}

	public void pictureModeSet(int mode) {
		fitMode = mode;
	}
	public void pictureLocationSet(Location location) {
		this.location = location;
	}
	/**
	 * draw the picture when painting
	 */
	public void paintPicture(Graphics g) {
		if (picture != null) {
			int sizex, sizey;
			switch (fitMode) {
			case MODE_AUTOSIZE: {
				sizex = getWidth();
				sizey = getHeight();
				break;
			}
			case MODE_RESIZE_KEEP_RATIO: {
				int pW = picture.getWidth(ob);
				int pH = picture.getHeight(ob);
				double rW = (double) getWidth() / pW;
				double rH = (double) getHeight() / pH;
				double r = rW > rH ? rH : rW;

				sizex = (int) (pW * r / 100);
				sizey = (int) (pH * r / 100);
				break;
			}
			default: {
				sizex = picture.getWidth(ob);
				sizey = picture.getHeight(ob);
			}
			}
			int x = 0, y=0;
			switch (location) {
			case CENTER: {
				x = (getWidth()-sizex)/2;
				y = (getHeight()-sizey)/2;
				break;
			}
			case DOWNER_LEFT: {
				x = 0;
				y = getHeight()-sizey;
				break;
			}
			case DOWNER_RIGHT: {
				x = getWidth()-sizex;
				y = getHeight()-sizey;
				break;
			}
			case UPPER_LEFT: {
				x=y=0;
				break;
			}
			case UPPER_RIGHT: {
				x = getWidth()-sizex;
				y =0;
				break;
			}
			default: {
				break;
			}
			}
			g.drawImage(picture, x, y, sizex, sizey, ob);
		}
	}

	@Override
	protected void pictureChanged() {
		this.repaint();
	}
}
