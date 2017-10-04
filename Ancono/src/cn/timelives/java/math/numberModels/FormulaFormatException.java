package cn.timelives.java.math.numberModels;

public class FormulaFormatException extends IllegalArgumentException
{
	final String formula;
	final int pos;
	String details="";
	
	/**
	 * 在读取字符串时发生的错误
	 */
	private static final long serialVersionUID = 1L;
	public FormulaFormatException(String formula){
		this(formula,-1);
	}
	public FormulaFormatException(String formula,int pos){//在某个位置处出现了读取错误,pos小于0则意味不显示在哪里出现问题
		super();
		this.formula=formula;
		this.pos=pos;
	}
	public FormulaFormatException(String details,String formula,int pos){//在某个位置处出现了读取错误,pos小于0则意味不显示在哪里出现问题
		super();
		this.details=""+details;
		this.formula=formula;
		this.pos=pos;
	}
	public FormulaFormatException(String details,String formula){//在
		this(formula,-1);
		this.details=details;
	}
	@Override
	public String getMessage(){
		
		if(details.isEmpty()){
			return (this.pos<0)?("The expression is uncorrect:"+formula):("The expression is uncorrect:\""+formula+"\" at position "+(this.pos+1));
		}
		else{
			return (this.pos<0)?(details+formula):(details+":\""+formula+"\" at position "+(this.pos+1));
		}
		
	}
}
/*class FormulaNumberFollowCharException extends FormulaFormatException
{

	/**
	 * 在表达式中出现了：如‘a1’的情况,可以指出
	 
	private static final long serialVersionUID = 1L;
	FormulaNumberFollowCharException(String formula){
		super(formula);
	}
	FormulaNumberFollowCharException(String formula,int pos){
		super(formula,pos);
	}
	public String getMessage(){
		return (this.pos<0)?("表达式字母后直接有数字:"+formula):("表达式字母后直接有数字:\""+formula+"\" 第"+(pos+1)+"字符处");
	}
}
class FormulaInvalidCharException extends FormulaFormatException
{

	/**
	 * 表达式中出现了奇怪的东西如：@#$%}{:" 等等
	 
	private static final long serialVersionUID = 1L;
	FormulaInvalidCharException(String formula){
		super(formula);
	}
	FormulaInvalidCharException(String formula,int pos){
		super(formula,pos);
	}
	public String getMessage(){
		return (this.pos<0)?("表达式含有不合法字符:"+formula):("表达式含有不合法字符:\""+formula+"\" 第"+(pos+1)+"字符处");
	}
}
class FormulaNumberFormatException extends FormulaFormatException
{
	private static final long serialVersionUID = 1L;
	FormulaNumberFormatException(String formula){
		super(formula);
	}
	FormulaNumberFormatException(String formula,int pos){
		super(formula,pos);
	}
	public String getMessage(){
		return (this.pos<0)?("表达式数字格式出现问题:"+formula):("表达式含有不合法字符:\""+formula+"\" 第"+(pos+1)+"字符处");
	}
}*/
