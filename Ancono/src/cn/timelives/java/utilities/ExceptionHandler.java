package cn.timelives.java.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * handle the given exception and get the exception
 * @author lyc
 *
 */
public class ExceptionHandler {
	private List<Exception> exs;
	
	
	
	ExceptionHandler(){
		exs = new ArrayList<Exception>();
	}
	
	public List<Exception> getException(){
		return exs;
	}
}
