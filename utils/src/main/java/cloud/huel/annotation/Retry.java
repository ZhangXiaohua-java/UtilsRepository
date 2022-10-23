package cloud.huel.annotation;

import java.lang.annotation.*;

/**
 * @author 张晓华
 * @date 2022-10-23
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {

	int retryTimes() default 3;

	long delay() default 0;

	int multiplier() default 1;

}
