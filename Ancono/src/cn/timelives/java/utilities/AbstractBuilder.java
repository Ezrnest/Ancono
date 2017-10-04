package cn.timelives.java.utilities;

import java.util.stream.Stream;

/**
 * The abstract class of {@link Builder}.<br />
 * The builder class is a kind of object that allows the building work performs like operation in {@link Stream}.
 * Such as <pre>
 * Builder b = new Builder();
 * b.nextLevel()
 * 	.append("a")
 * 	.nextLevel()
 * 		.append("b")
 * 		.build()
 * 	.build();
 * 	.append("c")
 * .build();
 *  
 *  </pre>
 * Once the builder finish building the object,the builder will throw an {@code IllegalStateException} if there are further attempts
 * to operate build() on the builder.
 * @param <T> the higher level of builder that will be returned by {@link #build()}
 * @param <S> the object itself that will be returned by {@link #append(Object)}
 * @param <R> the generated object type 
 * @param <I> the thing to add to build.
 */
public abstract class AbstractBuilder<T,S extends AbstractBuilder<T,S,R,I>,R,I> implements Builder<T,S,I>{
	private boolean built = false;
	/**
	 * Checks whether the builder finished building,throws IllegalStateException if 
	 * built == true.
	 */
	protected final void checkState(){
		if(built){
			throw new IllegalStateException("Built");
		}
	}
	
	/**
	 * Append the specific object to the builder.How the builder append the object exactly 
	 * depends on the implementation.
	 * @param sth
	 * @return
	 */
	public final S append(I sth){
		checkState();
		return appendImpl(sth);
	}
	/**
	 * Finish the builder's work and return a higher level builder or the result(if the builder is a top-level builder).
	 * @return
	 */
	public final T build(){
		checkState();
		built = true;
		return buildImpl();
	}
	/**
	 * Generate the result,this method is only for builders' chain.
	 * @return
	 */
	protected abstract R generate();
	/**
	 * Returns true if the builder has built something,which means no more 
	 * operations should be done to this builder,including all the lower 
	 * level builder's build() operation.
	 * @return
	 */
	public boolean isBuilt(){
		return built; 
	}
	/**
	 * Implement this method instead of overriding {@link #build()}.This method will be invoked 
	 * when {@link #build()} is called and the result will be returned.
	 */
	protected abstract T buildImpl();
	/**
	 * Implement this method instead of overriding {@link #append()}.This method will be invoked 
	 * when {@link #append()} is called and the result will be returned.
	 */
	protected abstract S appendImpl(I sth);
	
	/**
	 * Return a lower level builder.The order is considered.
	 * (Optional)
	 * @return
	 */
	public AbstractBuilder<S,?,R,I> nextLevel(){
		throw new UnsupportedOperationException();
	}
	
	
}
