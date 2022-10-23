package cloud.huel.proxy;

import cloud.huel.ex.RetryException;
import cloud.huel.function.RetryProxy;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author 张晓华
 * @date 2022-10-23
 */
@Slf4j
public class RetryProxyImpl implements RetryProxy {

	private int retryTimes;

	// 重试的延迟时间
	private long delay;

	// 延迟时间是否需要翻倍
	private int multiplier;

	public RetryProxyImpl(int retryTimes, long delay, int multiplier) {
		this.retryTimes = retryTimes;
		this.delay = delay;
		this.multiplier = multiplier;
	}

	@Override
	public Object call(Method method, Object target, Object[] args) throws Throwable {
		int times = 0;
		while (times <= retryTimes) {
			try {
				return method.invoke(target, args);
			} catch (Exception e) {
				times++;
				String methodName = method.getName();
				methodName = target.getClass().getName() + ":" + methodName;
				if (times - 1 > 0) {
					log.error("{}方法执行失败,进行重试, 重试次数{}", methodName, times - 1);
				}
				if (times - 1 == this.retryTimes) {
					throw new RetryException(e);
				}
				// 如果指定了延迟重试时间就进行相应的处理
				if (this.delay > 0) {
					// 只有在不是第一次调用时才会进行延迟重试
					if (this.multiplier > 0 && times > 1) {
						TimeUnit.MILLISECONDS.sleep(this.delay * this.multiplier);
					} else {
						// 没有指定翻倍的重试时间则按照指定的重试时间延迟即可
						TimeUnit.MILLISECONDS.sleep(this.delay);
					}

				}
			}
		}
		throw new RetryException("重试结束");
	}
}
