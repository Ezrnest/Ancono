package cn.timelives.java.utilities.swingTools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.Objects;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
/**
 * a PictureBox is used to show pictures in it,
 * @author lyc
 *
 */
public abstract class PictureBox extends JPanel implements ImageObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 12137124176L;
	/**
	 * the Image to be shown in the PictureBox
	 */
	protected Image picture;
	
	protected ImageObserver ob = new PictureObserver();
	/**
	 * the default setting of the pictureBox
	 */
	{
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setBackground(Color.WHITE);
	}
	
	/**
	 * Create the PictureBox with default 
	 */
	public PictureBox() {
		super();
	}
	
	/**
	 * set the picture according to the path given
	 * @param picturePath the path of the picture
	 * @return true if the picture has been loaded, false if there is a failure 
	 */
	public boolean pictureSet(String picturePath){
		try{
			picture = Toolkit.getDefaultToolkit().getImage(picturePath);
			pictureChanged();
			return true;
		}catch(SecurityException se){
			return false;
		}
	}
	/**
	 * set the picture according to the URL given
	 * @param picturePath the path of the picture
	 * @return true if the picture has been loaded, false if there is a failure 
	 */
	public boolean pictureSet(URL picturePath){
		try{
			picture = Toolkit.getDefaultToolkit().getImage(picturePath);
			pictureChanged();
			return true;
		}catch(SecurityException se){
			return false;
		}
	}
	/**
	 * set the picture according to the Image given
	 * @param picture
	 */
	public void pictureSet(Image picture){
		this.picture=Objects.requireNonNull(picture);
		pictureChanged();
	}
	
	/**
	 * Clear the picture in this picture box.
	 */
	public void pictureClear(){
		picture = null;
		pictureChanged();
	}
	
	
	/**
	 * Override the method to paint the picture 
	 * @param g the Graphics to use
	 */
	protected abstract void paintPicture(Graphics g);
	/**
	 * this method will additionally use the paintPicture method to paint the picture and the component 
	 */
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		paintPicture(g);
	}
	/**
	 * this method will be call whenever an Image is set
	 * <p>Override this method .
	 */
	protected void pictureChanged(){
		repaint();
	}
		
	protected int getPicWidth(){
		if(picture==null)
			return -1;
		return picture.getWidth(ob);
	}
	
	protected int getPicHeight(){
		if(picture==null)
			return -1;
		return picture.getHeight(ob);
	}
	/**
	 * the observer to observe the change of the Image
	 * @author lyc
	 *
	 */
	protected class PictureObserver implements ImageObserver{
		PictureObserver(){
			
		}
		
		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
			if((infoflags&ImageObserver.HEIGHT)!=0 || (infoflags & ImageObserver.WIDTH)!=0){
				pictureChanged();
			}
			return (infoflags & (ALLBITS|ABORT)) == 0;
		}
		
	}
	
}
