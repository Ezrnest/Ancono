package cn.timelives.java.math.numberModels;

public class FormulaFormatException extends IllegalArgumentException
{
	final String formula;
	final int pos;
	String details="";
	
	/**
	 * �ڶ�ȡ�ַ���ʱ�����Ĵ���
	 */
	private static final long serialVersionUID = 1L;
	public FormulaFormatException(String formula){
		this(formula,-1);
	}
	public FormulaFormatException(String formula,int pos){//��ĳ��λ�ô������˶�ȡ����,posС��0����ζ����ʾ�������������
		super();
		this.formula=formula;
		this.pos=pos;
	}
	public FormulaFormatException(String details,String formula,int pos){//��ĳ��λ�ô������˶�ȡ����,posС��0����ζ����ʾ�������������
		super();
		this.details=""+details;
		this.formula=formula;
		this.pos=pos;
	}
	public FormulaFormatException(String details,String formula){//��
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
	 * �ڱ��ʽ�г����ˣ��确a1�������,����ָ��
	 
	private static final long serialVersionUID = 1L;
	FormulaNumberFollowCharException(String formula){
		super(formula);
	}
	FormulaNumberFollowCharException(String formula,int pos){
		super(formula,pos);
	}
	public String getMessage(){
		return (this.pos<0)?("���ʽ��ĸ��ֱ��������:"+formula):("���ʽ��ĸ��ֱ��������:\""+formula+"\" ��"+(pos+1)+"�ַ���");
	}
}
class FormulaInvalidCharException extends FormulaFormatException
{

	/**
	 * ���ʽ�г�������ֵĶ����磺@#$%}{:" �ȵ�
	 
	private static final long serialVersionUID = 1L;
	FormulaInvalidCharException(String formula){
		super(formula);
	}
	FormulaInvalidCharException(String formula,int pos){
		super(formula,pos);
	}
	public String getMessage(){
		return (this.pos<0)?("���ʽ���в��Ϸ��ַ�:"+formula):("���ʽ���в��Ϸ��ַ�:\""+formula+"\" ��"+(pos+1)+"�ַ���");
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
		return (this.pos<0)?("���ʽ���ָ�ʽ��������:"+formula):("���ʽ���в��Ϸ��ַ�:\""+formula+"\" ��"+(pos+1)+"�ַ���");
	}
}*/
