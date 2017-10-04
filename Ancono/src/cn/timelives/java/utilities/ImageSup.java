package cn.timelives.java.utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

/**
 * A simple but convenient loader for loading {@link BufferedImage} from a file.
 * @author lyc
 *
 */
public class ImageSup{
	/**
	 * The bound of rgb expression for color.
	 */
	public static final int MAX_NUM_FOR_RGB = 256;
	
	
	
	
	private static ImageSup loader = new ImageSup();
	public static ImageSup getInstance(){
		return loader;
	}
	
	
	
	/**
	 * Create a new image loader
	 */
	private ImageSup(){
	}
	
	
	
	static final String[] NAME_PNG = {"PNG","png"};
	static final String[] NAME_JPEG = {"JPEG","jpeg"};
	static final String[] NAME_BMP = {"BMP","bmp"};
	
	
	
	private static final int MASK = 0xff,
							 	ALPHA = 0xff000000;
	/**
	 * return a new array : int[3][scanSize][rgbArray.length/scanSize].
	 * int[0][x][y] contains Red , 1 for green , 2 for blue,the given rgbArray should be like the array returned by
	 * {@link BufferedImage#getRGB(int, int, int, int, int[], int, int)} method.The array should be n * scanSize int length, 
	 * where n is an integer.
	 * 
	 * @param rgbArray
	 * @param scanSize
	 * @return
	 */
	public static int[][][] sparateRGB(int[] rgbArray,int scanSize){
		int len = rgbArray.length/scanSize;
		int[][][] rgb = new int[3][scanSize][len];
		int pos = 0;
		for(int i=0;i<len;i++){
			for(int j=0;j<scanSize;j++){
				int t = rgbArray[pos++];
				rgb[0][j][i] = t & MASK;
				t >>>= 8;
				rgb[1][j][i] = t & MASK;
				t >>>= 8;
				rgb[2][j][i] = t & MASK;
			}
		}
		return rgb;
	}
	/**
	 * A opposite method of {@link #sparateRGB(int[], int)}
	 */
	public static int[] getRGBs(int[][][] rgb,int height,int width){
		int[] arr = new int[width*height];
		int pos = 0;
		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
				int t = ALPHA;
				t |= rgb[0][x][y]; 
				t |= (rgb[1][x][y]<<8);
				t |= (rgb[2][x][y]<<16);
				arr[pos++] = t;
			}
		}
		return arr;
	}
}
