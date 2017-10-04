package cn.timelives.java.utilities.content_generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import cn.timelives.java.utilities.content_generator.Name.NameBuilder;
/**
 * A name generator is a content generator which can generate random
 * names.
 * @see Name
 * @author rw185035
 *
 */
public class NameGenerator implements ContentGenerator<Name> {
	/**
	 * first names
	 * @deprecated
	 */
	public static List<String> FN = new ArrayList<String>(16);
	static {
		FN.add("Sam");
		FN.add("David");
	}
	
	private RandomSelector rs ;
	private Random rd;
	
	
	private static int range = 100;//the random number bound of rd
	
	private static int middleExcept[] = {25,85,115,120,121};//75% of the name has middle name
	private static int characterAsName = 25;//35% of like T. kind of name
	
	public NameGenerator(int seed){
		rs = new RandomSelector(seed);
		rd = new Random(seed);
	}
	/**
	 * Create a name generator , the seed will be set properly
	 */
	public NameGenerator(){
		rs = new RandomSelector();
		rd = new Random();
	}
	
	
	
	@Override
	public Name next() {
		NameBuilder nb = Name.getBuilder();
		nb.setFirstName(createFirst());
		int r = rd.nextInt(middleExcept[middleExcept.length-1]);
		for(int i=0;i<middleExcept.length;i++){
			if(r<middleExcept[i]){
				for(int c=0;c<i;c++){
					nb.addMiddle(createMiddle());
				}
				break;
			}
		}
		
		nb.setLastName(createLast());
		return nb.build();
	}
	
	
	
	
	private String createFirst(){
		StringBuilder sb = getFirstChar();
		//first name is not very long , so one middle part is enough.
		sb = addConChar(sb);
		sb = appendPart(sb);
		sb.append(rs.select(CharSet.CHARS));
		getLastChar(sb);
		return sb.toString();
	}
	
	private StringBuilder getFirstChar(){
		StringBuilder sb = new StringBuilder();
		int light_per = 45;
		int hea = 80;
		int r = rd.nextInt(range);
		char c ;//to 
		if(r<light_per){
			c = lig();
		}else if(r<hea){
			c=  hea();
		}else{
			c = all();
		}
		c = Character.toUpperCase(c);
		sb.append(c);
		return sb;
	}
	
	private StringBuilder getLastChar(StringBuilder sb){
		int r = rd.nextInt(range);
		int down = 30;
		int tall = 50;
		char c;
		if(r<down){
			c = dow();
		}else if(r<tall){
			c = tal();
		}else{
			c = all();
		}
		sb.append(c);
		int aei = 35;
		r = rd.nextInt(range);
		if(r<aei){
			c =  aei();
		}else{
			c = all();
		}
		
		
		return sb;
	}
	


	private StringBuilder addConChar(StringBuilder sb){
		int r = rd.nextInt(range);
		//need a connecting character 
		int mix = 25;
		if(r<mix){
			sb.append(rs.select(CharSet.MIX_MID));
			return sb;
		}
		r = ran();
		int li = 10;
		int he = 20;//light and heavy is the same 
		int ta = 40,dow = 50,oth = 65;
		int ae = 90 ; //the remaining part is for e
		if(r<li){
			sb.append(rs.select(CharSet.LIGHT));
		}else if(r<he){
			sb.append(rs.select(CharSet.HEAVY));
		}else if(r<ta){
			sb.append(rs.select(CharSet.TALL_CHAR));
		}else if(r<dow){
			sb.append(rs.select(CharSet.DOWN_CHAR));
		}else if(r<oth){
			sb.append(rs.select(CharSet.OTHER));
		}else if(r< ae){
			sb.append(rs.select(CharSet.AEIOU));
		}else{
			sb.append('e');
		}
		return sb;
	}
	
	private StringBuilder addDivChar(StringBuilder sb){
		int r = rd.nextInt(range);
		//need a dividing character
		int mix = 20;
		if(r<mix){
			sb.append(rs.select(CharSet.MIX_START));
			return sb;
		}
		r = ran();
		int li = 25;
		int he = 50;//light and heavy is the same 
		int ta = 65,dow = 75,oth = 90;
		int ae = 95 ; //the remaining part is for e
		if(r<li){
			sb.append(rs.select(CharSet.LIGHT));
		}else if(r<he){
			sb.append(rs.select(CharSet.HEAVY));
		}else if(r<ta){
			sb.append(rs.select(CharSet.TALL_CHAR));
		}else if(r<dow){
			sb.append(rs.select(CharSet.DOWN_CHAR));
		}else if(r<oth){
			sb.append(rs.select(CharSet.OTHER));
		}else if(r< ae){
			sb.append(rs.select(CharSet.AEIOU));
		}else{
			sb.append(all());
		}
		return sb;
	}
	
	
	
	/**
	 * the middle parts of a word,may be 2-3 length
	 * @return
	 */
	private StringBuilder appendPart(StringBuilder sb){
		int needDouble = 20;
		int r = rd.nextInt(range);
		if(r<needDouble){
			sb.append(rs.select(CharSet.MIX_START));
		}else{
			sb.append(rs.select(CharSet.LIGHT));
		}
		char c = rs.select(CharSet.AEIOU);
		sb.append(c);
		if(rd.nextBoolean()){
			sb.append(rs.select(CharSet.AEIOU));
		}else{
			int re = 13;
			r = ran();
			if(r<re){
				sb.append(c);
			}
		}
		
		
		return sb;
	}
	private StringBuilder appendLongPart(StringBuilder sb){
		int needDouble = 30;
		int triple = 75;
		int r = ran();
		if(r<needDouble){
			sb.append(mix());
			addConChar(sb);
			addDivChar(sb);
		}else{
			appendPart(sb);
			int hasMixed = 35;
			int noDiv = 80;
			r = ran();
			if(r<hasMixed){
				sb.append(mix());
				addConChar(sb);
			}else if(r<noDiv){
				appendPart(sb);
				addConChar(sb);
			}else{
				addDivChar(sb);
				appendPart(sb);
				addDivChar(sb);
			}
		}
		if(r>triple){
			//need some longer part appended
			addDivChar(sb);
			appendPart(sb);
			addConChar(sb);
			if(rd.nextBoolean()){
				addConChar(sb);
			}
		}
		return sb;
	}
	
	
	
	private String createLast(){
		StringBuilder sb = new StringBuilder();
		int r = ran();
		int down = 20;
		int round = 35;
		int ran = 75;
		int oth = 90;
		char c;
		if(r<down){
			c = dow();
		}else if(r<round){
			c = rou();
		}else if(r<ran){
			c = all();
		}else if(r<oth){
			c = oth();
		}else{
			c = aei();
		}
		sb.append(Character.toUpperCase(c));
		int shortName = 35;
		int mid = 80;
		r = ran();
		if(r<shortName){
			// length will be 3 or 4
			r = ran();
			int aei = 75;
			if(r < aei){
				c = aei();
			}else{
				c = all();
			}
			sb.append(c);
			int li = 40,he = 80;
			r = ran();
			if( r < li){
				c = lig();
			}else if( c < he){
				c = hea();
			}else{
				c = oth();
			}
			sb.append(c);
		}else if(c<mid){
			//5 - 8
			appendLongPart(sb);
		}else{
			appendPart(sb);
			appendLongPart(sb);
		}
		if(rd.nextBoolean())
			getLastChar(sb);
		return sb.toString();
	}
	
	private String createMiddle(){
		StringBuilder sb = new StringBuilder();
		int r = ran();
		if(r<characterAsName){
			sb.append(Character.toUpperCase(all())).append('.');
			return sb.toString();//no need for full name
		}else{
			r = ran();
			getFirstChar(sb);
			int sho = 30;
			int mid = 80;
//			int lon = 100;
			appendPart(sb);
			if(r>sho){
				addConChar(sb);
				appendPart(sb);
			}
			if(r>mid){
				addDivChar(sb);
				appendLongPart(sb);
			}
			if(r>sho){
				if(rd.nextBoolean()){
					addDivChar(sb);
				}else{
					addConChar(sb);
				}
			}
			getLastChar(sb);
			return sb.toString();
		}
		
		
	}
	/**
	 * this method is for middle name
	 * @param sb
	 * @return
	 */
	private StringBuilder getFirstChar(StringBuilder sb){
		int r = ran();
		int tall = 30;
		int aei  = 45;
		int oth = 55;
		char c;
		if(r<tall){
			c = tal();
		}else if(r<aei){
			c = aei();
		}else if(r<oth){
			c =oth();
		}else{
			c = all();
		}
		c = Character.toUpperCase(c);
		sb.append(c);
		return sb;
	}
	
	private int ran(){
		return rd.nextInt(range);
	}
	
	
	
	private char all(){
		return rs.select(CharSet.CHARS);
	}
	
	private char lig(){
		return rs.select(CharSet.LIGHT);
	}
	private char dow(){
		return rs.select(CharSet.DOWN_CHAR);
	}
	private char hea(){
		return rs.select(CharSet.HEAVY);
	}
	private char oth(){
		return rs.select(CharSet.OTHER);
	}
	
	private char aei(){
		return rs.select(CharSet.AEIOU);
	}
	private char tal() {
		return rs.select(CharSet.TALL_CHAR);
	}
	private char rou(){
		return rs.select(CharSet.ROUND_CHAR);
	}
	private String mix(){
		return rs.select(CharSet.MIX_START);
	}
	
	public static void main(String[] args){
		NameGenerator ng = new NameGenerator();
		Scanner scn = new Scanner(System.in);
		String line=  null;
		while(scn.hasNextLine()){
			line = scn.nextLine();
			if(line.equals("end")){
				break;
			}
			try{
				int t = Integer.parseInt(line);
				for(int i=0;i<t;i++){
					System.out.println(ng.next());
				}
			}catch(NumberFormatException e){
			}
		}
		scn.close();
	}

}
