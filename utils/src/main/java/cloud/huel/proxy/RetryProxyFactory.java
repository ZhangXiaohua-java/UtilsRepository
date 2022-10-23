package cloud.huel.proxy;

import java.lang.reflect.Proxy;

/**
 * @author 张晓华
 * @date 2022-10-23
 */
public final class RetryProxyFactory {

	private RetryProxyFactory() {
	}

	public static Object getProxyFromJDK(Object target) {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(),
				target.getClass().getInterfaces(),
				new JDKRetryProxy(target));
	}

	public static Object getProxyFromJDK(Object target, int retryTimes) {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(),
				target.getClass().getInterfaces(),
				new JDKRetryProxy(target, retryTimes, 2000, 2));
	}


	public static Object getProxyFromJDK(Object target, int retryTimes, long delay, int multiplier) {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(),
				target.getClass().getInterfaces(),
				new JDKRetryProxy(target, retryTimes, delay, multiplier));
	}


	public static Object getProxyFromCglib(Object target) {
		return new CglibRetryProxy(target).getProxy();
	}

	
}
