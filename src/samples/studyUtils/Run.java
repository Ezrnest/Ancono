package samples.studyUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates which methods should be run
 * @author liyicheng
 * 2017-06-13 17:45
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Run {

}
