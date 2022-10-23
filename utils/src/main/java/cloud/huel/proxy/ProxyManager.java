package cloud.huel.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 张晓华
 * @date 2022-10-23
 */
@Slf4j
@Component
public class ProxyManager {

	private final String PROXY_BEAN_SUFFIX = ":retry_proxy";
	@Resource
	private ConfigurableApplicationContext configurableApplicationContext;

	public Object getProxy(Class cla) {
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();
		String proxyBeanName = cla.getName() + PROXY_BEAN_SUFFIX;
		Map<String, Object> entries = beanFactory.getBeansOfType(cla);
		Object proxy = null;
		if (entries.size() == 0) {
			throw new RuntimeException("查找的bean实例不存在");
		} else if (entries.containsKey(proxyBeanName)) {
			proxy = entries.get(proxyBeanName);
			log.info("在容器中查找到了代理bean{}", proxy);
			return proxy;
		}
		// 直接查找会导致程序启动失败
		//Object proxy = beanFactory.getBean(proxyBeanName);
		Object bean = entries.entrySet().iterator().next().getValue();
		proxy = RetryProxyFactory.getProxyFromCglib(bean);
		beanFactory.registerSingleton(proxyBeanName, proxy);
		log.info("将代理bean注入到了容器中{}", proxy);
		return proxy;
	}

	
}
