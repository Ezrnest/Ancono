/**
 * 
 */
package cn.timelives.java.math.combination;

import cn.timelives.java.math.exceptions.NumberValueException;
import cn.timelives.java.utilities.ArraySup;

import java.util.*;
import java.util.function.Predicate;

/**
 * A ordered enumerationer which returns an 
 * int array as result and supports filter.
 * @author liyicheng
 *
 */
public abstract class Enumer implements Iterable<int[]>{
	protected final int n;
	/**
	 * Gives the element count.
	 * @param n
	 */
	Enumer(int n){
		this.n = n;
	}
	
	/**
	 * Returns all the int[] arrays, ordered in a proper way. 
	 * @return
	 */
	public abstract List<int[]> enumration();
	/**
	 * Returns an iterator, this iterator will always returns an copy of the array.
	 */
	@Override
	public abstract Iterator<int[]> iterator();
	
	public int getElementCount(){
		return n;
	}
	
	public abstract long getEnumCount();
	
	static class LEnumer extends Enumer{
		private long total;
		private final int toSelect;
		private final Predicate<int[]> filter;
		
		private static final int UNSELECTED = -1;
		
		/**
		 * 
		 */
		public LEnumer(int n,int m) {
			super(m);
			toSelect = n;
			total = CFunctions.permutation(n, m);
			filter = (a)->true;
		}
		/**
		 * 
		 */
		public LEnumer(int n,int m,Predicate<int[]> filter) {
			super(m);
			toSelect = n;
			total = CFunctions.permutation(n, m);
			this.filter = filter;
		}
		
		
		private List<int[]> enumeration;
		
		private List<int[]> en0(){
			if(total>Integer.MAX_VALUE){
				throw new NumberValueException("Too many");
			}
			List<int[]> re = new ArrayList<>((int)total);
			en1(re,ArraySup.fillArr(n,UNSELECTED),0,new boolean[toSelect]);
			total = re.size();
			return re;
		}
		
		private void en1(List<int[]> list,int[] cur,int pos,boolean[] selected){
			for(int i=0;i<selected.length;i++){
				if(!selected[i]){
					//put this
					cur[pos] = i;
					selected[i] = true;
					if(pos == n-1){
						if(filter.test(cur)){
							list.add(cur.clone());
						}
					}else{
						en1(list,cur,pos+1,selected);
					}
					selected[i] = false;
					cur[pos] = UNSELECTED;
				}
			}
		}
		
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.prob.Enumer#enumration()
		 */
		@Override
		public List<int[]> enumration() {
			List<int[]> list ;
			if(enumeration!=null){
				list = new ArrayList<>((int)total);
				for(int[] arr: enumeration){
					list.add(arr.clone());
				}
			}else{
				enumeration = en0();
				list = enumeration;
			}
			return list;
		}

		/* (non-Javadoc)
		 * @see cn.timelives.java.math.prob.Enumer#iterator()
		 */
		@Override
		public Iterator<int[]> iterator() {
			return new Lit();
		}
		
		private class Lit implements Iterator<int[]>{
			
			private int[] en;
			private boolean[] selected;
			Lit(){
				en = new int[n];
				selected = new boolean[toSelect];
				for(int i=0;i<n-1;i++){
					en[i] = i;
					selected[i] = true;
				}
				//a trick to increase firstly
				en[n-1] = n-2;
			}
			private boolean increase(){
				int i = n - 1;
				while (i>-1) {
					// pos is equal to i in the loop
					int pos = en[i] + 1;
					while (pos < toSelect && selected[pos]) {
						pos++;
					}
					selected[en[i]] = false;
					if (pos == toSelect) {
						// need to change further
						i--;
						continue;
					} else {
						selected[pos] = true;
						en[i] = pos;
						i++;
						int lastPos = 0;
						while (i < n) {
							for (int j = lastPos; j < toSelect; j++) {
								if (!selected[j]) {
									// put this
									en[i] = j;
									selected[j] = true;
									lastPos = j+1;
									break;
								}
							}
							i++;
						}
						return true;
					}
				}
				return false;
			}
			
			
			private boolean finded = false;
			private boolean ended = false;
			/* (non-Javadoc)
			 * @see java.util.Iterator#hasNext()
			 */
			@Override
			public boolean hasNext() {
				if(ended){
					return false;
				}
				if(!finded){
					//search for it.
					do{
						if(!increase()){
							ended = true;
							return false;
						}
					}while(filter.test(en)==false);
					finded = true;
				}
				return true;
			}

			/* (non-Javadoc)
			 * @see java.util.Iterator#next()
			 */
			@Override
			public int[] next() {
				if(hasNext()){
					int[] toRe = en.clone();
					finded = false;
					return toRe;
				}
				throw new NoSuchElementException();
			}
			
			
			/*
			 * //increase the last element ( exchange it )
				if(ar[n-2]<ar[n-1]){
					int t = ar[n-2];
					ar[n-2] = ar[n-1];
					ar[n-1] = t;
				}else{
					int max = ar[n-2];
					int i;
					for(i=n-3;i>-1;i--){
						int x = ar[i];
						if(x<max){
							max = x;
							break;
						}else{
							max = x;
						}
					}
					int j;
					//find the small one
					for(j=n-3;i>-1;i--){
						if(max<ar[j]){
							break;
						}
					}
					//swap them
					ar[i] = ar[j];
					ar[j] = max;
					//flip
					ArraySup.flip(ar, i+1, n);
				}
			 */
			
		}


		/* (non-Javadoc)
		 * @see cn.timelives.java.math.prob.Enumer#getEnumCount()
		 */
		@Override
		public long getEnumCount() {
			return total;
		}
	}
	
	public static Enumer permutation(int n,int m){
		return new LEnumer(n, m);
	}
	
	
	public static EnumBuilder getBuilder(int n,int m){
		return new EnumBuilder(n, m);
	}
	
	public static class EnumBuilder{
		private final int n,m;
		private List<Predicate<int[]>> rs = new LinkedList<>(); 
		EnumBuilder(int n,int m){
			this.n = n;
			this.m = m;
		}
		
		public EnumBuilder addRule(Predicate<int[]> rule){
			rs.add(rule);
			return this;
		}
		
		public Enumer build(){
			@SuppressWarnings("unchecked")
			Predicate<int[]>[] rsa = rs.toArray(new Predicate[]{});
			return new LEnumer(n, m,a->{
				for(int i=0;i<rsa.length;i++){
					if(!rsa[i].test(a)){
						return false;
					}
				}
				return true;
			});
		}
		
	}
	
	
//	public static void main(String[] args) {
//		Enumer en ;
//		en = getBuilder(4, 3).addRule(a->a[0]!=0).build();
//		en.forEach(Printer::print);
//	}
}
