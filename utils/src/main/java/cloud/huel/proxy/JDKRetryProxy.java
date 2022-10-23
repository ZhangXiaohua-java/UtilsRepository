package cloud.huel.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 基于JDK的尝试重试代理,必须是实现的接口中的抽象方法才可以被代理
 *
 * @author 张晓华
 * @date 2022-10-23
 */
@Slf4j
public class JDKRetryProxy implements InvocationHandler {

	private Object target;

	private int retryTimes;

	// 重试的延迟时间
	private long delay;

	// 延迟时间是否需要翻倍
	private int multiplier;

	public JDKRetryProxy(Object target) {
		this.retryTimes = 3;
		this.delay = 0;
		this.multiplier = 1;
		this.target = target;
	}

	public JDKRetryProxy(Object target, int retryTimes) {
		this.delay = 0;
		this.multiplier = 1;
		this.target = target;
		this.retryTimes = retryTimes;
	}

	public JDKRetryProxy(Object target, int retryTimes, long delay, int multiplier) {
		this.target = target;
		this.retryTimes = retryTimes;
		this.delay = delay;
		this.multiplier = multiplier;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return new RetryProxyImpl(this.retryTimes, this.delay, this.multiplier).call(method, target, args);
	}


}
