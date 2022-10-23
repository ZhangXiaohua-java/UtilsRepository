package cloud.huel.function;

import java.lang.reflect.Method;

/**
 * @author 张晓华
 * @date 2022-10-23
 */
@FunctionalInterface
public interface RetryProxy {

	Object call(Method method, Object target, Object[] args) throws Throwable;


}
