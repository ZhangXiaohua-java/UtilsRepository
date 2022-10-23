package cloud.huel.aspect;

import cloud.huel.annotation.Retry;
import cloud.huel.ex.RetryException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author 张晓华
 * @date 2022-10-23
 */
@Component
@Aspect
@Slf4j
public class RetryAspect {


	@Pointcut("@annotation(cloud.huel.annotation.Retry)")
	public void watch() {
	}

	@Around("watch()")
	public Object retryProxy(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		Method method = methodSignature.getMethod();
		String methodSignatureName = method.getName();
		// 获取注解信息
		Retry retry = method.getAnnotation(Retry.class);
		int retryTimes = retry.retryTimes();
		long delay = retry.delay();
		int multiplier = retry.multiplier();

		log.info("方法{}开始执行", methodSignatureName);
		int times = 0;
		while (times <= retryTimes) {
			try {
				Object result = proceedingJoinPoint.proceed();
				log.info("方法{}执行完毕", methodSignatureName);
				return result;
			} catch (Exception e) {
				times++;
				if (times > 1) {
					log.error("目标方法执行出错了,当前重试{}次", times - 1);
				}
				if (delay > 0) {
					if (multiplier > 0) {
						TimeUnit.MILLISECONDS.sleep(delay * multiplier);
					} else {
						TimeUnit.MILLISECONDS.sleep(delay);
					}
				}
				if (times - 1 == retryTimes) {
					throw new RetryException(e);
				}
			}
		}
		throw new RetryException("重试失败");
	}


}
