package cn.timelives.java.utilities.dynamic;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.printnb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;
public class Reflectioner {
	
	public static void main(String[] args) {
	}
	public static void method(Class<?> clazz){
		Field[] fs = clazz.getDeclaredFields();
		Method[] ms  =clazz.getMethods();
		Pattern p = Pattern.compile("(\\w+\\.)+");
		print("Field:");
		for(Field f : fs){
			printnb(p.matcher(f.toGenericString()).replaceAll(""));
			print(";");
		}
		print();
		print("Method:");
		
		for(Method m : ms){
			printnb(p.matcher(m.toGenericString()).replaceAll(""));
			print(";");
		}
	}
}
