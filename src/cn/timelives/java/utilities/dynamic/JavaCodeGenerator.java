package cn.timelives.java.utilities.dynamic;

import cn.timelives.java.utilities.AbstractBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static cn.timelives.java.utilities.Printer.print;
import static java.util.Objects.requireNonNull;
/**
 * A generator for java code.This allows user to create java codes easily through 
 * codes and reduce the possibility of mistakes.<p>
 * This class only provide basic structure of a java class code,therefore,no detailed check(such as duplicated 
 * field name is not checked).
 * @author lyc
 *
 */
public class JavaCodeGenerator implements Cloneable{
	private List<String> imports;
	
	private String packageName;
	
	private String className;
	
	


	private int classModifier;
	
	private String superClass;
	
	private List<String> impleInterfaces;
	
	private List<String> feilds;
	
	private List<String> methods;
	
	
	/**
	 * Creates a Generator
	 */
	public JavaCodeGenerator(){
		imports = new ArrayList<>();
		packageName = null;
		className = null;
		classModifier = 0 ;
		superClass = null;
		impleInterfaces = new ArrayList<>();
		feilds = new ArrayList<>();
		methods = new ArrayList<>();
	}


	/**
	 * @param packageName
	 *            the packageName to set
	 */
	public JavaCodeGenerator setPackageName(String packageName) {
		this.packageName = packageName;
		return this;
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * @param className
	 *            the className to set
	 */
	public JavaCodeGenerator setClassName(String className) {
		this.className = className;
		return this;
	}
	/**
	 * Set this class to public.
	 * @return
	 */
	public JavaCodeGenerator setPublicClass(){
		this.classModifier |= Modifier.PUBLIC;
		return this;
	}

	/**
	 * @param classModifier
	 *            the classModifier to set
	 * @see Modifier
	 */
	public JavaCodeGenerator setClassModifier(int classModifier) {
		this.classModifier = classModifier;
		return this;
	}

	/**
	 * @param superClass
	 *            the superClass to set
	 */
	public JavaCodeGenerator setSuperClass(String superClass) {
		this.superClass = superClass;
		return this;
	}
	
	public JavaCodeGenerator addImplement(String impl){
		impleInterfaces.add(requireNonNull(impl));
		return this;
	}

	/**
	 * Add the import java class to this class.
	 * 
	 * @param fullName
	 *            a full name of a class , such as java.util.List, or java.util.
	 * *
	 */
	public JavaCodeGenerator addImport(String fullName) {
		imports.add(fullName);
		return this;
	}

	/**
	 * Add the import static java method to this class.
	 * 
	 * @param methodName
	 *            the name of the method.
	 */
	public JavaCodeGenerator addImportStatic(String methodName) {
		imports.add("static " + methodName);
		return this;
	}

	/**
	 * Add a field which is as the description of the code.The code should
	 * contains a ";" at the end.
	 * 
	 * @param code
	 */
	public JavaCodeGenerator addField(String code) {
		feilds.add(code);
		return this;
	}

	/**
	 * Add a method to this class,the full code of this method is required.
	 * 
	 * @param fullCode
	 */
	public JavaCodeGenerator addMethod(String fullCode) {
		methods.add(fullCode);
		return this;
	}
	/**
	 * Returns a clone of this generator.
	 * @return
	 */
	public JavaCodeGenerator clone(){
		try {
			JavaCodeGenerator nobj = (JavaCodeGenerator) super.clone();
			nobj.feilds = new ArrayList<>(feilds);
			nobj.methods = new ArrayList<>(methods);
			nobj.impleInterfaces = new ArrayList<>(impleInterfaces);
			nobj.imports = new ArrayList<>(imports);
			return nobj;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException();
		}
	}
	
	/**
	 * Build the code,this method will always create a new code.
	 * @return
	 */
	public CharSequence buildCode(){
		if(className== null){
			throw new IllegalArgumentException("No class name");
		}
		StringBuilder sb = new StringBuilder();
		appendPackage(sb);
		changeLine(sb);
		
		appendImports(sb);
		changeLine(sb);
		
		appendClassTitle(sb);
		appendGeneratorInfo(sb);
		changeLine(sb);
		
		appendFields(sb);
		changeLine(sb);
		
		appendMethods(sb);
		changeLine(sb);
		changeLine(sb);
		sb.append('}');
		
		return sb;
	}
	
	private void appendGeneratorInfo(StringBuilder sb){
		sb.append("//TODO:Auto-generated class \n");
	}
	
	private void appendMethods(StringBuilder sb) {
		for(String str : methods){
			sb.append(str).append('\n');
		}
	}


	private void appendLineSep(StringBuilder sb){
		sb.append(';').append('\n');
	}
	
	private void changeLine(StringBuilder sb){
		sb.append('\n');
	}
	
	private void appendFields(StringBuilder sb) {
		for(String str : feilds){
			sb.append(str).append('\n');
		}
	}


	private void appendPackage(StringBuilder sb){
		if(packageName!=null){
			sb.append("package ").append(packageName);
			appendLineSep(sb);
		}
	}
	private void appendImports(StringBuilder sb){
		for(String str:imports){
			sb.append("import ").append(str);
			appendLineSep(sb);
		}
	}
	private void appendClassTitle(StringBuilder sb){
		sb.append(Modifier.toString(classModifier))
		.append(" class ")
		.append(className).append(' ');
		if(superClass!=null){
			sb.append("extends ")
			.append(superClass).append(' ');
		}
		if(impleInterfaces.isEmpty()==false){
			sb.append("implements ");
			for(String inter : impleInterfaces){
				sb.append(inter).append(',');
			}
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append('{').append('\n');
	}
	
	public FieldBuilder getFieldBuilder(String name){
		return new FieldBuilder(name);
	}
	
	public MethodBuilder getMethodBuilder(String name){
		return new MethodBuilder(name);
	}
	
	
	public class FieldBuilder extends AbstractBuilder<JavaCodeGenerator,FieldBuilder,StringBuilder,CharSequence>{
		private String fieldName;
		private int modifier;
		private String type;
		private String initValue;
		FieldBuilder(String fieldName){
			this.fieldName = Objects.requireNonNull(fieldName);
		}

		/**
		 * @param modifier
		 *            the modifier to set
		 */
		public FieldBuilder setModifier(int modifier) {
			this.modifier = modifier;
			return this;
		}
		
		public FieldBuilder publicMod(){
			modifier |= Modifier.PUBLIC;
			return this;
		}
		
		public FieldBuilder privateMod(){
			modifier |= Modifier.PRIVATE;
			return this;
		}
		
		public FieldBuilder staticMod(){
			modifier |= Modifier.STATIC;
			return this;
		}
		
		public FieldBuilder finalMod(){
			modifier |= Modifier.FINAL;
			return this;
		}
		
		/**
		 * @param type
		 *            the type to set
		 */
		public FieldBuilder setType(String type) {
			this.type = type;
			return this;
		}
		
		public FieldBuilder setType(Class<?> clazz){
			this.type = Objects.requireNonNull(clazz.getCanonicalName());
			return this;
		}
		
		/**
		 * A method to set the initial value to the field.
		 *  
		 * @param initValue
		 *            the initValue to set
		 */
		public FieldBuilder setInitValue(String initValue) {
			this.initValue = initValue;
			return this;
		}
		/**
		 * Build the field,which will be automatically added to the code that is being built.
		 */
		protected JavaCodeGenerator buildImpl(){
			StringBuilder sb = generate();
			JavaCodeGenerator.this.addField(sb.toString());
			return JavaCodeGenerator.this;
		}
		
		/**
		 * Do nothing
		 */
		@Override
		protected FieldBuilder appendImpl(CharSequence sth) {
			return this;
		}

		protected StringBuilder generate() {
			return generate(new StringBuilder());
		}

		protected StringBuilder generate(StringBuilder toAdd) {
			StringBuilder sb =  toAdd;
			sb.append(Modifier.toString(modifier))
				.append(' ').append(type)
				.append(' ').append(fieldName)
				.append(' ');
			if(initValue!=null){
				sb.append('=')
				.append(' ').append(initValue)
				.append(' ')
				;
			}
			sb.append(';');
			return sb;
		}
	}
	
	public class MethodBuilder extends AbstractBuilder<JavaCodeGenerator,MethodBuilder,StringBuilder,CharSequence>{
		private String methodName;
		private int modifier;
		private List<String> paraTypes,paras,annos;
		private String returnType;
		private BlockBuilder<MethodBuilder> content;
		MethodBuilder(String name){
			this(name,1);
		}
		
		public MethodBuilder(String name,int tab){
			methodName = requireNonNull(name);
			paraTypes = new LinkedList<>();
			paras = new LinkedList<>();
			annos = new LinkedList<>();
			content = new BlockBuilder<>(tab,this);
		}

		/**
		 * @param methodName
		 *            the methodName to set
		 */
		public MethodBuilder setMethodName(String methodName) {
			this.methodName = methodName;
			return this;
		}

		/**
		 * @param modifier
		 *            the modifier to set
		 */
		public MethodBuilder setModifier(int modifier) {
			this.modifier = modifier;
			return this;
		}
		
		public MethodBuilder publicMod(){
			modifier |= Modifier.PUBLIC;
			return this;
		}
		
		public MethodBuilder privateMod(){
			modifier |= Modifier.PRIVATE;
			return this;
		}
		
		public MethodBuilder staticMod(){
			modifier |= Modifier.STATIC;
			return this;
		}
		
		public MethodBuilder abstractMod(){
			modifier |= Modifier.ABSTRACT;
			return this;
		}
		//Annotation
		public MethodBuilder addAnnotation(String anno){
			annos.add(anno);
			return this;
		}

		/**
		 * @param paras
		 *            the paras to set
		 */
		public MethodBuilder addPara(String type,String name) {
			paraTypes.add(requireNonNull(type));
			paras.add(requireNonNull(name));
			return this;
		}
		
		/**
		 * @param paras
		 *            the paras to set
		 */
		public MethodBuilder addPara(Class<?> type,String name) {
			return addPara(type.getCanonicalName(),name);
		}

		/**
		 * @param returnType
		 *            the returnType to set
		 */
		public MethodBuilder setReturnType(String returnType) {
			this.returnType = returnType;
			return this;
		}
		
		/**
		 * @param returnType
		 *            the returnType to set
		 */
		public MethodBuilder setReturnType(Class<?> clazz) {
			return setReturnType(clazz.getCanonicalName());
		}
		/**
		 * Set this method's return type to void.
		 * @return
		 */
		public MethodBuilder returnVoid(){
			returnType = "void";
			return this;
		}
		
		/**
		 * Set the method as the given String,this method will cover the code 
		 * if there is originally be one.
		 * @param body
		 * @return
		 */
		public MethodBuilder setBody(String body){
//			content.add(body);
			if(!content.code.isEmpty()){
				content.code.clear();
			}
			content.line(body);
			return this;
		}
		
		public BlockBuilder<MethodBuilder> block(){
			return content;
		}
		
		/**
		 * Add a line(includes ';' or not),and the line separator('\n') will be added automatically.Notice that 
		 * necessary ';' should be added.
		 * @param line a String of a line
		 * @return 
		 */
		public MethodBuilder line(String line){
			content.line(line);
			return this;
		}
		
		
		
		protected JavaCodeGenerator buildImpl(){
			StringBuilder code = generate();
			JavaCodeGenerator.this.addMethod(code.toString());
			return JavaCodeGenerator.this;
		}
		/**
		 * Do nothing
		 */
		@Override
		protected MethodBuilder appendImpl(CharSequence sth) {
			return this;
		}

		protected StringBuilder generate() {
			return generate(new StringBuilder());
		}

		protected StringBuilder generate(StringBuilder toAdd) {
			for(String s : annos){
				toAdd.append(s).append("\n");
			}
			toAdd.append(Modifier.toString(modifier));
			toAdd.append(' ').append(returnType)
				.append(' ').append(methodName);
			toAdd.append('(');
			if(paras.isEmpty()==false){
				Iterator<String> it1 = paraTypes.iterator();
				Iterator<String> it2 = paras.iterator();
				while(it1.hasNext()){
					toAdd.append(it1.next())
					.append(' ').append(it2.next())
					.append(',');
				}
				toAdd.deleteCharAt(toAdd.length()-1);
			}
			toAdd.append(')');
			content.generate(toAdd);
			return toAdd;
		}
		
	}
	
	public static class BlockBuilder<T extends AbstractBuilder<?,T,StringBuilder,CharSequence>> extends AbstractBuilder<T,BlockBuilder<T>,StringBuilder,CharSequence>{
		
		private final T from ;
		
		BlockBuilder(int tab,T from){
			this.tab = tab;
			this.from = from;
			code = new LinkedList<>();
		}
		protected final int tab;
		
		protected List<String> code;
		
		public BlockBuilder<T> line(String line){
			code.add(requireNonNull(line));
			return this;
		}
		
		public BlockBuilder<BlockBuilder<T>> insertBlock(){
			return new BlockBuilder<>(tab+1,this);
		}
		
		protected T buildImpl(){
			from.append(generate());
			return from;
		}
		
		

		@Override
		protected BlockBuilder<T> appendImpl(CharSequence sth) {
			code.add(sth.toString());
			return this;
		}

		protected StringBuilder generate() {
			return generate(new StringBuilder());
		}

		protected StringBuilder generate(StringBuilder toAdd) {
			StringBuilder lineSepTab = new StringBuilder();
			lineSepTab.append('\n');
			for(int i=0;i<tab;i++){
				lineSepTab.append('	');
			}
			toAdd.append('{');
			for(String str : code){
				toAdd.append(lineSepTab).append(str);
			}
			toAdd.append(lineSepTab).deleteCharAt(toAdd.length()-1).append('}');
			return toAdd;
		}
		

		
		
	}
	
	
	public static void main(String[] args) throws Exception{
		JavaCodeGenerator jcg = new JavaCodeGenerator();
		CharSequence cs = 
				jcg.setClassName("TestClass")
				.addImportStatic("cn.timelives.java.utilities.Printer.*")
				.setPublicClass()
				.addField("public static final String str = \"123\";")
				.getFieldBuilder("text")
					.finalMod()
					.privateMod()
					.staticMod()
					.setType(String.class)
					.setInitValue("str")
					.build()
				.getMethodBuilder("main")
					.publicMod()
					.staticMod()
					.returnVoid()
					.block()
						.line("int i = 0;")
						.insertBlock()
							.line("i++;")
							.line("print(i);")
							.build()
						.line("i = 4;")
						.line("print(i);")
						.build()
					.addPara(String[].class, "args")
					.build()
				.buildCode()
				;
		print(cs);
		DynamicCompileEngine engine = DynamicCompileEngine.getDefaultInstance();
		Class<?> clazz = engine.compileAndLoadClass(jcg.getClassName(), cs);
		if(clazz==null){
			print(engine.getFailureMessage());
		}
		Method me = clazz.getMethod("main", String[].class);
		me.invoke(null, (Object)new String[]{});
	}
	
}
