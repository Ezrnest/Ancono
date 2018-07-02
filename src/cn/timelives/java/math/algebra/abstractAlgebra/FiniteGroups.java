/**
 * 2018-02-27
 */
package cn.timelives.java.math.algebra.abstractAlgebra;

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.algebra.abstractAlgebra.group.finite.AbstractFiniteGroup;
import cn.timelives.java.math.algebra.abstractAlgebra.structure.finite.FiniteGroup;
import cn.timelives.java.math.function.Bijection;
import cn.timelives.java.math.function.MathBinaryOperator;
import cn.timelives.java.math.numberTheory.combination.Permutation;
import cn.timelives.java.math.numberTheory.combination.Permutations;
import cn.timelives.java.math.set.FiniteSet;
import cn.timelives.java.math.set.MathSet;
import cn.timelives.java.math.set.MathSets;
import cn.timelives.java.utilities.ArraySup;
import cn.timelives.java.utilities.CollectionSup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static cn.timelives.java.utilities.Printer.print;

/**
 * A utility class for finite groups.
 * @author liyicheng
 * 2018-02-27 17:52
 *
 */
public final class FiniteGroups {

	/**
	 * 
	 */
	private FiniteGroups() {
	}
	
	static class FiniteGroupImpl<T> extends AbstractFiniteGroup<T>{
		
		private final FiniteSet<T> set;
		
		/**
		 * @param gc
		 */
		public FiniteGroupImpl(GroupCalculator<T> gc,FiniteSet<T> set) {
			super(gc);
			this.set = set;
		}

		
		


		/*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.LimitedGroup#getSet()
		 */
		@Override
		public FiniteSet<T> getSet() {
			return set;
		}





		/*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.finite.FiniteGroup#getSubgroups()
		 */
		@Override
		public FiniteSet<AbstractFiniteGroup<T>> getSubgroups() {
			// TODO Auto-generated method stub
			return null;
		}




		/*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#index()
		 */
		@Override
		public long index() {
			return set.size();
		}




		/*
		 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#isSubgroup(cn.timelives.java.math.algebra.abstractAlgebra.structure.Group)
		 */
		@Override
		public boolean isSubgroup(AbstractFiniteGroup<T> g) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	/**
	 * Generates a group table, the group table is a two-dimension array. The length
	 * of each dimension is {@code 1+ <i>size of the group</i>}. The element
	 * arr[0][0] is {@code null} and the remaining part of the first row and first
	 * column contains all the elements of the group in order. The rest of the array
	 * satisfies {@code arr[i][j] = g.getOperation().apply(arr[i][0],arr[0][j])}.
	 * 
	 * @return
	 */
	public static <T> T[][] generateGroupTable(FiniteGroup<T,?> g) {
		FiniteSet<T> set = g.getSet();
		int size = ArraySup.castToArrayLength(set.size() + 1);
		MathBinaryOperator<T> f = g.getCalculator();
		@SuppressWarnings("unchecked")
		T[][] arr = (T[][]) Array.newInstance(g.identity().getClass(), size, size);
		{
			int i = 1;
			for (T t : set) {
				arr[0][i] = t;
				arr[i][0] = t;
				i++;
			}
		}
		for (int i = 1; i < size; i++) {
			for (int j = 1; j < size; j++) {
				arr[i][j] = f.apply(arr[i][0], arr[0][j]);
			}
		}

		return arr;
	}


	/**
	 * Creates a group containing all the given elements and the elements that they generate.
     *
	 * @param gc a group calculator
	 * @param elements an array of elements
	 * @return a finite group
	 */
	@SafeVarargs
	public static <T> AbstractFiniteGroup<T> createGroup(GroupCalculator<T> gc,T...elements){
        List<T> list = new ArrayList<>(elements.length+1);
        list.add(gc.getIdentity());
        OUTER:
        for(T x : elements) {
            for(T y : list){
                if(gc.isEqual(x,y)){
                    continue OUTER;
                }
            }
            list.add(x);
        }
        listGenerate(list,gc);
        FiniteSet<T> set = MathSets.fromCollection(list,GroupCalculators.toMathCalculatorAdd(gc));
        return new FiniteGroupImpl<>(gc, set);
	}

    /**
     * Creates a group containing all the given elements and the elements that they generate.
     * @param gc a group calculator
     * @param elements a collection of elements,
     * @return
     */
	public static <T> AbstractFiniteGroup<T> createGroup(GroupCalculator<T> gc,Collection<T> elements){
        return createGroup0(gc,elements.size(),elements);
    }

    /**
     * Creates a group containing all the given elements and the elements that they generate.
     * @param gc a group calculator
     * @param elements a finite set of elements,
     * @return
     */
    public static <T> AbstractFiniteGroup<T> createGroup(GroupCalculator<T> gc,FiniteSet<T> elements){
        return createGroup0(gc,Math.toIntExact(elements.size()),elements);
    }

    /**
     * Creates a finite group from the group table.
     * @param table
     * @param <T>
     * @return
     */
    public static <T> AbstractFiniteGroup<T> createFromGroupTable(T[][] table){
        return null;
    }

    private static <T> AbstractFiniteGroup<T> createGroup0(GroupCalculator<T> f,int size,Iterable<T> elements){
        List<T> list = new ArrayList<>(size+1);
        list.add(f.getIdentity());
        OUTER:
        for(T x : elements) {
            for(T y : list){
                if(f.isEqual(x,y)){
                    continue OUTER;
                }
            }
            list.add(x);
        }
        listGenerate(list,f);
        FiniteSet<T> set = MathSets.fromCollection(list,GroupCalculators.toMathCalculatorAdd(f));
        return new FiniteGroupImpl<>(f, set);
    }

    private static <T> void listGenerate(List<T> list,GroupCalculator<T> f){
        for(int i=1;i<list.size();i++) {
            T x = list.get(i);
            for(int j=1;j<list.size();j++) {
                T y = list.get(j);
                T z = f.apply(x, y);
                boolean contains = false;
                for(T t : list) {
                    if(f.isEqual(t, z)) {
                        contains = true;
                        break;
                    }
                }
                if(!contains) {
                    list.add(z);
                }

            }
        }
    }

    /**
     * Creates a group that only contains the identity element.
     * @param gc a group calculator
     * @return a group
     */
    public static <T> AbstractFiniteGroup<T> identityGroup(GroupCalculator<T> gc){
        return new FiniteGroupImpl<>(gc,MathSets.singleton(gc.getIdentity(),gc));
    }




    public static boolean homoEquals(FiniteGroup<?,?> g1,FiniteGroup<?,?> g2){
	    if(g1.index()!=g2.index()){
	        return false;
        }
	    int[][] table1 = g1.groupTable(),
                table2 = g2.groupTable();
	    int size = Math.toIntExact(g1.index());
	    int[] map = ArraySup.fillArr(size,-1);
	    //identity element must be equal
	    map[0] = 0;
	    map = recurEqual(size,table1,table2,map,0);
	    return map!=null;
    }

    public static Optional<Permutation> homoEqualsAndPermutation(FiniteGroup<?,?> g1, FiniteGroup<?,?> g2){
        if(g1.index()!=g2.index()){
            return Optional.empty();
        }
        int[][] table1 = g1.groupTable(),
                table2 = g2.groupTable();
        int size = Math.toIntExact(g1.index());
        int[] map = ArraySup.fillArr(size,-1);
        //identity element must be equal
        map[0] = 0;
        map = recurEqual(size,table1,table2,map,0);
        if(map==null){
            return Optional.empty();
        }
        return Optional.of(Permutations.valueOf(map));
    }

    public static <T,R> Optional<Bijection<T,R>> homoEqualsAndMap(FiniteGroup<T,?> g1,FiniteGroup<R,?> g2){
        if(g1.index()!=g2.index()){
            return Optional.empty();
        }
        int[][] table1 = g1.groupTable(),
                table2 = g2.groupTable();
        int size = Math.toIntExact(g1.index());
        int[] map = ArraySup.fillArr(size,-1);
        //identity element must be equal
        map[0] = 0;
        map = recurEqual(size,table1,table2,map,0);
        if(map == null){
            return Optional.empty();
        }
        int[] fmap = map;
        var bi = new Bijection<T,R>(){
            @SuppressWarnings("unchecked")
            T[] arr1 = (T[])CollectionSup.iteratorToArray(g1.getSet().iterator(),size);
            @SuppressWarnings("unchecked")
            R[] arr2 = (R[])CollectionSup.iteratorToArray(g2.getSet().iterator(),size);

            @Override
            public R apply(T x) {
                int idx1 = ArraySup.firstIndexOf(arr1,y -> g1.getCalculator().isEqual(x,y));
                int idx2 = fmap[idx1];
                return arr2[idx2];
            }

            @Override
            public T deply(R y) {
                int idx2 = ArraySup.firstIndexOf(arr2,x -> g2.getCalculator().isEqual(x,y));
                int idx1 = ArraySup.firstIndexOf(idx2,fmap);
                return arr1[idx1];
            }
        };
        return Optional.of(bi);
    }

    /**
     *
     * @param t1
     * @param t2
     * @param map
     * @param cur
     * @return
     */
    static int[] recurEqual(int size,int[][] t1,int[][] t2,int[] map,int cur){
	    for(int i=0;i<size;i++){
	        if(map[i] == -1){
	            //assign the value
                for(int val=0;val<size;val++){
                    if(ArraySup.firstIndexOf(val,map)>-1){
                        continue;
                    }
                    map[i] = val;
                    int[] tMap = recurEqual(size,t1,t2,map,i);
                    if(tMap == null){
                        //failed
                        map[i] = -1;
                    }else{
                        return tMap;
                    }
                }
                if(map[i] == -1){
                    //failed
                    return null;
                }
            }else{
                int a = t1[i][cur];
                int b = t2[map[i]][map[cur]];
                int aMap = map[a];
                if(aMap == -1){
                    map[a] = b;
                    return recurEqual(size,t1,t2,map,a);
                }
                if(aMap!=b){
                    return null;
                }

                a = t1[cur][i];
                b = t2[map[cur]][map[i]];
                aMap = map[a];
                if(aMap == -1){
                    map[a] = b;
                    return recurEqual(size,t1,t2,map,a);
                }
                if(aMap!=b){
                    return null;
                }
            }
        }
        return map;
    }

//    public static void main(String[] args) {
//        var g1 = PermutationGroup.symmetricGroups(3);
//        print(g1.getSet());
//        print(homoEqualsAndPermutation(g1,g1));
//    }
}
