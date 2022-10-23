package cloud.huel.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

/**
 * @author 张晓华
 * @date 2022-10-23
 */
public class CglibRetryProxy implements InvocationHandler {

	private Object target;

	private int retryTimes;

	// 重试的延迟时间
	private long delay;

	// 延迟时间是否需要翻倍
	private int multiplier;

	public CglibRetryProxy(Object target) {
		this.retryTimes = 3;
		this.delay = 0;
		this.multiplier = 1;
		this.target = target;
	}

	public CglibRetryProxy(Object target, int retryTimes) {
		this.delay = 0;
		this.multiplier = 1;
		this.target = target;
		this.retryTimes = retryTimes;
	}

	public CglibRetryProxy(Object target, int retryTimes, long delay, int multiplier) {
		this.target = target;
		this.retryTimes = retryTimes;
		this.delay = delay;
		this.multiplier = multiplier;
	}

	@Override
	public Object invoke(Object o, Method method, Object[] args) throws Throwable {
		return new RetryProxyImpl(this.retryTimes, this.delay, this.multiplier).call(method, this.target, args);
	}

	public Object getProxy() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target.getClass());
		enhancer.setCallback(this);
		return enhancer.create();
	}

}
