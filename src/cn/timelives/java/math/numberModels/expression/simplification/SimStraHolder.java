/**
 * 2017-11-26
 */
package cn.timelives.java.math.numberModels.expression.simplification;

import cn.timelives.java.math.numberModels.expression.ExprCalculator;
import cn.timelives.java.math.numberModels.expression.Node;
import cn.timelives.java.math.numberModels.expression.Node.Type;
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies;
import cn.timelives.java.math.numberModels.expression.anno.AllowModify;
import cn.timelives.java.utilities.structure.Pair;

import java.util.*;

/**
 * A simplification strategy holder manages the simplification strategy added to it.
 * @author liyicheng
 * 2017-11-26 13:28
 *
 */
public final class SimStraHolder {
	
	private final List<SimplificationStrategy> generalTypes;
	private final EnumMap<Type, Pair<List<SpecificStrategy>, Map<String, List<SpecificStrategy>>>> specifices;
	private final List<TaggedStrategy> tagged;
	/**
	 *
	 */
	public SimStraHolder() {
		generalTypes = new ArrayList<>();
		specifices = new EnumMap<>(Type.class);
		for(Type ty : Node.Type.values()) {
			List<SpecificStrategy> list = new ArrayList<>();
			Map<String,List<SpecificStrategy>> map = new HashMap<>();
			specifices.put(ty, new Pair<>(list, map));
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

	public List<SimplificationStrategy> getStrategies(Node node, Set<String> tags) {
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


	private void addAfterCheckTags(Set<String> tags, List<? extends SimplificationStrategy> slist, List<SimplificationStrategy> list) {
		if(slist.isEmpty()) {
			return;
		}
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
	
	private int hash;
	
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		if (hash == 0) {
			int result = 1;
			result = prime * result + ((generalTypes == null) ? 0 : generalTypes.hashCode());
			result = prime * result + ((specifices == null) ? 0 : specifices.hashCode());
			result = prime * result + ((tagged == null) ? 0 : tagged.hashCode());
			hash = result;
		}
		return hash;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SimStraHolder))
			return false;
		SimStraHolder other = (SimStraHolder) obj;
		if (generalTypes == null) {
			if (other.generalTypes != null)
				return false;
		} else if (!generalTypes.equals(other.generalTypes))
			return false;
		if (specifices == null) {
			if (other.specifices != null)
				return false;
		} else if (!specifices.equals(other.specifices))
			return false;
		if (tagged == null) {
			if (other.tagged != null)
				return false;
		} else if (!tagged.equals(other.tagged))
			return false;
		return true;
	}

	private Node performAfterCheckTags(Set<String> tags, List<? extends SimplificationStrategy> slist, final Node node, ExprCalculator mc) {
		if(slist.isEmpty()) {
			return node;
		}
		
		for (SimplificationStrategy ss : slist) {
			if (ss instanceof TaggedStrategy) {
				TaggedStrategy ts = (TaggedStrategy) ss;
				if (!ts.isAcceptable(tags)) {
					continue;
				}
			}
			Node result = ss.simplifyNode(node,mc);
			if(result != null) {
				//changed
				return result;
			}
		}
		return null;
	}
	/**
	 * Performs a single simplification.
	 */
	public Node performSimplification(@AllowModify final Node node, Set<String> tags, ExprCalculator mc) {
		Node result;
		Type ty = node.getType();
		Pair<List<SpecificStrategy>, Map<String, List<SpecificStrategy>>> p = specifices.get(ty);
		String name = Node.getFunctionName(node);
		if (name != null) {
			List<SpecificStrategy> candidates = p.getSecond().get(name);
			if (candidates != null) {
				result = performAfterCheckTags(tags,candidates,node,mc);
				if(result != null) {
					return result;
				}
			}
		}
		result = performAfterCheckTags(tags,p.getFirst(),node,mc);
		if(result != null) {
			return result;
		}
		result = performAfterCheckTags(tags,tagged,node,mc);
		if(result != null) {
			return result;
		}
		result = performAfterCheckTags(tags,generalTypes,node,mc);
		if(result != null) {
			return result;
		}
		return node;
	}
	
	public static SimStraHolder getDefault() {
		SimStraHolder ssh = new SimStraHolder();
		ssh.addStrategy(SimplificationStrategies.getDefaultStrategies());
		return ssh;
	}
	
	/**
	 * Create a new SimStraHolder with the list of {@link SimplificationStrategy}
	 * @param list
	 * @return
	 */
	public static SimStraHolder createHolder(List<SimplificationStrategy> list) {
		SimStraHolder ssh = new SimStraHolder();
		ssh.addStrategy(list);
		return ssh;
	}

}
