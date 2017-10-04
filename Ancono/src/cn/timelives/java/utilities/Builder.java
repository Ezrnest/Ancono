package cn.timelives.java.utilities;


/**
 * A builder interface that define the two basic build operation of {@link #append(Object)} and 
 * {@link #build()},the builder allows user to use chain operation so the {@link #append(Object)} 
 * method shall return {@code this}.<br />
 * The builder only allows single build() call and after building there shouldn't be any attempts 
 * to operate the object. 
 * 
 *
 * @param <T> the higher level of builder that will be returned by {@link #build()}
 * @param <S> the object itself that will be returned by {@link #append(Object)}
 * 
 * @param <I> the thing to add to build.
 */
public interface Builder<T,S extends Builder<T,S,I>,I> {
	/**
	 * Append an object to the builder.
	 * Return the object itself.
	 * @param in
	 * @return a builder 
	 */
	S append(I in);
	/**
	 * Finish the building and return the corresponding result.
	 * @return the result
	 */
	T build();
	/**
	 * Check the state of this builder whether it finishes building.
	 * @return
	 */
	boolean isBuilt();
}
