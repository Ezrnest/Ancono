package cn.timelives.java.utilities.dynamic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * A file object that is in a byte array.
 * @author lyc
 *
 */
public class JavaClassFileObject extends SimpleJavaFileObject{
	
	
	private ByteArrayOutputStream out ;
	
	public JavaClassFileObject(String className, Kind kind) {
		super(URI.create("string:///"+className.replace('.', '/')+kind.extension),kind);
		out = new ByteArrayOutputStream();
	}
	
	@Override
	public OutputStream openOutputStream() throws IOException {
		return out;
	}
	/**
	 * Gets the data stored in this object.
	 * @return
	 */
	public byte[] getClassByteData(){
		return out.toByteArray();
	}
	
}
