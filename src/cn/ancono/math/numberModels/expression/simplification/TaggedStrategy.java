/**
 * 2017-11-26
 */
package cn.ancono.math.numberModels.expression.simplification;

import java.util.Set;

/**
 * @author liyicheng
 * 2017-11-26 12:12
 */
public interface TaggedStrategy extends SimplificationStrategy {
    /**
     * Returns the taps of the strategy.
     *
     * @return
     */
    public Set<String> getTags();

    /**
     * Determines whether this tagged strategy should be performed.
     *
     * @return
     */
    public boolean isAcceptable(Set<String> tags);
}
