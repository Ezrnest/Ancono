/**
 * 2017-06-13
 */
package test.math.comp.studyUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static cn.ancono.utilities.Printer.print;

/**
 * @author liyicheng
 * 2017-06-13 17:45
 *
 */
public final class StudyMethodRunner {
	
	/**
	 * Runs the class.
	 * @param clazz
	 */
	public static void runStudyClass(Class<?> clazz){
		Method[] methods = clazz.getDeclaredMethods();
		Object obj;
		try {
			 obj = clazz.getConstructor().newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		for(Method mod : methods){
			if(mod.isAnnotationPresent(Run.class)){
				print(mod.getName()+"   =================================");
				print();
				boolean troubled = false;
				try {
					mod.setAccessible(true);
					mod.invoke(obj);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					troubled = true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					troubled = true;
				} catch (InvocationTargetException e) {
					e.getCause().printStackTrace();
					troubled = true;
				}
				if(troubled){
					//wait for finishing painting, need a better implement.
					try {
						TimeUnit.MILLISECONDS.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				print();
				print("===================Method ended========================");
			}
		}
	}
}
