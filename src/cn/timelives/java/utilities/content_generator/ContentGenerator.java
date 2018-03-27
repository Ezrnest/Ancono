package cn.timelives.java.utilities.content_generator;

public interface ContentGenerator<T> {
	/**
	 * Generate an element
	 * @return the object generated. 
	 */
	public T next();
	
	
}
