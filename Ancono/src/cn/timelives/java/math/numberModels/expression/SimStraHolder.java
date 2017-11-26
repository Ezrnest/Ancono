/**
 * 2017-11-26
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.timelives.java.math.numberModels.expression.Node.Type;
import cn.timelives.java.utilities.structure.Pair;

/**
 * A simplification strategy holder manages the simplification strategy added to it.
 * @author liyicheng
 * 2017-11-26 13:28
 *
 */
public final class SimStraHolder {
	
	private final List<SimplificationStrategy> generalTypes;
	private final EnumMap<Node.Type, Pair<List<SpecificStrategy>, Map<String, List<SpecificStrategy>>>> specifices;
	private final List<TaggedStrategy> tagged;
	/**
	 * 
	 */
	public SimStraHolder() {
		generalTypes = new ArrayList<>();
		specifices = new EnumMap<>(Node.Type.class);
		for(Type ty : Node.Type.values()) {
			List<SpecificStrategy> list = new ArrayList<>();
			Map<String,List<SpecificStrategy>> map = new HashMap<>();
			specifices.put(ty, new Pair<List<SpecificStrategy>, Map<String,List<SpecificStrategy>>>(list, map));
		}
		tagged = new ArrayList<>();
	}
	
	public void addStrategy(SimplificationStrategy ss) {
		if(ss instanceof SpecificStrategy) {
			addSpecificStrategy((SpecificStrategy)ss);
			return;
		}
		if(ss instanceof TaggedStrategy) {
			tagged.add((TaggedStrategy)ss);
			return;
		}
		generalTypes.add(ss);
	}
	
	public void addStrategy(List<? extends SimplificationStrategy> list) {
		for(SimplificationStrategy ss : list) {
			addStrategy(ss);
		}
	}
	
	public void addSpecificStrategy(SpecificStrategy ss) {
		String name = ss.registerFunctionName();
		if(name == null) {
			for(Type ty : ss.registerTypes()) {
				specifices.get(ty).getFirst().add(ss);
			}
		}else {
			for(Type ty : ss.registerTypes()) {
				specifices.get(ty).getSecond().compute(name, (n,list)->{
					if(list == null) {
						list = new ArrayList<>();
					}
					list.add(ss);
					return list;
				}
				);
			}
		}
	}
	
	public List<SimplificationStrategy> getStrategies(Node node,Set<String> tags){
		List<SimplificationStrategy> list = new ArrayList<>(generalTypes.size()+5);
		list.addAll(generalTypes);
		for(TaggedStrategy ts : tagged) {
			if(ts.isAcceptable(tags)) {
				list.add(ts);
			}
		}
		Type ty = node.getType();
		Pair<List<SpecificStrategy>, Map<String, List<SpecificStrategy>>> p = specifices.get(ty);
		if (p == null) {
			return list;
		}
		addAfterCheckTags(tags, p.getFirst(), list);
		String name = Node.getFunctionName(node);
		if (name != null) {
			List<SpecificStrategy> candidates = p.getSecond().get(name);
			if (candidates != null) {
				addAfterCheckTags(tags, candidates, list);
			}
		}
		return list;
	}
	
	
	
	private void addAfterCheckTags(Set<String> tags,List<? extends SimplificationStrategy> slist,List<SimplificationStrategy> list) {
		for (SimplificationStrategy ss : slist) {
			if (ss instanceof TaggedStrategy) {
				TaggedStrategy ts = (TaggedStrategy) ss;
				if (ts.isAcceptable(tags)) {
					list.add(ts);
				}
			} else {
				list.add(ss);
			}
		}
	}
	
	private Node performAfterCheckTags(Set<String> tags,List<? extends SimplificationStrategy> slist,Node node,ExprCalculator mc) {
		Node result = node;
		for (SimplificationStrategy ss : slist) {
			if (ss instanceof TaggedStrategy) {
				TaggedStrategy ts = (TaggedStrategy) ss;
				if (!ts.isAcceptable(tags)) {
					continue;
				}
			}
			result = ss.simplifyNode(result,mc);
			if(result != node) {
				//changed
				return result;
			}
		}
		return node;
	}
	
	/**
	 * Performs a single simplification.
	 * @param n
	 * @param tags
	 * @return
	 */
	Node performSimplification(final Node node,Set<String> tags,ExprCalculator mc) {
		Node result = node;
		Type ty = node.getType();
		Pair<List<SpecificStrategy>, Map<String, List<SpecificStrategy>>> p = specifices.get(ty);
		String name = Node.getFunctionName(node);
		if (name != null) {
			List<SpecificStrategy> candidates = p.getSecond().get(name);
			if (candidates != null) {
				result = performAfterCheckTags(tags,candidates,result,mc);
				if(result != node) {
					return result;
				}
			}
		}
		result = performAfterCheckTags(tags,p.getFirst(),result,mc);
		if(result != node) {
			return result;
		}
		result = performAfterCheckTags(tags,tagged,result,mc);
		if(result != node) {
			return result;
		}
		result = performAfterCheckTags(tags,generalTypes,result,mc);
		return result;
		
	}
	
	public static SimStraHolder getDefault() {
		SimStraHolder ssh = new SimStraHolder();
		ssh.addStrategy(SimplificationStrategies.getDefaultStrategies());
		return ssh;
	}
	

}
