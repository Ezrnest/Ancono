package cn.timelives.java.math.algebra.abstractAlgebra.group.finite;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.algebra.abstractAlgebra.FiniteGroups;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.EqualPredicate;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.set.FiniteSet;
import cn.timelives.java.math.set.MathSets;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.Printer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.printMatrix;

/**
 * An implementation of cyclic group using the type of Integer.
 */
public class CyclicGroup extends AbstractFiniteGroup<Integer>{
    /**
     * number of elements
     */
    final int size;
    /**
     * The minimum generator
     */
    final int generator;
    /**
     * size * generator
     */
    final int mod;

    /**
     * size * generator
     * @param size
     * @param generator
     * @param mod
     */
    CyclicGroup(int size,int generator,int mod){
        super(new CyclicCalInt(mod));
        this.size = size;
        this.generator = generator;
        this.mod = mod;
    }

    CyclicGroup(int size){
        this(size,1,size);
    }

    @Override
    public FiniteSet<Integer> getSet() {
        var list = new ArrayList<Integer>(size);
        for(int n=0;n<mod;n+=generator){
            list.add(n);
        }
        return MathSets.fromCollection(list,Calculators.getCalculatorInteger());
    }

    /**
     * Gets a set of generators of this cyclic group.
     * @return a finite set
     */
    public FiniteSet<Integer> generators(){
        var list = new ArrayList<Integer>(size/2);
        for(int i=1,n=generator;i<size;i++){
            if(MathUtils.gcd(i,size) == 1){
                list.add(n);
            }
            n+=generator;
        }
        list.trimToSize();
        return MathSets.fromCollection(list,Calculators.getCalculatorInteger());
    }

    @Override
    public FiniteSet<AbstractFiniteGroup<Integer>> getSubgroups() {
        int num = Math.toIntExact(MathUtils.factorCount(size));
        var list = new ArrayList<AbstractFiniteGroup<Integer>>(num);
        list.add(IDENTITY);
        list.add(this);
        for(int i=2,n=2*generator;i<size;i++){
            if(size % i == 0){
                list.add(new CyclicGroup(size/i,n,mod));
            }
            n+= generator;
        }
        return MathSets.fromCollection(list, EqualPredicate.Companion.naturalEqual());
    }

    public FiniteSet<CyclicGroup> getSubgroupsAsCyclic(){
        int num = Math.toIntExact(MathUtils.factorCount(size));
        var list = new ArrayList<CyclicGroup>(num);
        list.add(IDENTITY);
        for(int i=2;i<size;i++){
            if(size % i == 0){
                list.add(new CyclicGroup(i,generator*size/i,mod));
            }
        }
        list.add(this);
        return MathSets.fromCollection(list, EqualPredicate.Companion.naturalEqual());
    }

    /**
     * Determines whether the group can be a subgroup of this after
     * homomorphism.
     * @param cg
     * @return
     */
    public boolean isSupgroupHomo(CyclicGroup cg){
        return size % cg.size == 0;
    }

    static class CyclicCalInt implements GroupCalculator<Integer>{
        final int mod;
        //int range: 0<=x < mod
        CyclicCalInt(int mod){
            this.mod = mod;
        }

        @Override
        public Integer inverse(Integer x) {
            int _x = x;
            if(_x == 0){
                return x;
            }else{
                return mod-_x;
            }
        }

        @Override
        public Integer getIdentity() {
            return 0;
        }

        @Override
        public Integer apply(Integer x, Integer y) {
            return (x+y) % mod;
        }

        @Override
        public boolean isEqual(Integer x, Integer y) {
            return x.equals(y);
        }

        @NotNull
        @Override
        public Integer gpow(@NotNull Integer x, long n) {
            return (Integer) GroupCalculator.DefaultImpls.gpow(this, x, n);
        }
    }

    private static final CyclicGroup IDENTITY = new CyclicGroup(1);

    public static CyclicGroup getIdentityGroup(){
        return IDENTITY;
    }

    /**
     * Creates a cyclic group of the given size.
     * @param size
     * @return
     */
    public static CyclicGroup createGroup(int size){
        return new CyclicGroup(size);
    }

    public static void main(String[] args) {
        createGroup(12).getSubgroupsAsCyclic().forEach( x -> Printer.print(CollectionSup.iteratorToArray(x.getSet().iterator(),x.size)));
//        printMatrix(new CyclicGroup(3).groupTable());
//        PermutationGroup.alternatingGroups(3).getSet().forEach(Printer::print);
//        printMatrix(PermutationGroup.alternatingGroups(3).groupTable());
//        print(FiniteGroups.homoEquals(new CyclicGroup(3),PermutationGroup.alternatingGroups(3)));

    }
}
