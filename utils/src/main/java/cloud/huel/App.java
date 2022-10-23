package cloud.huel;

import cloud.huel.proxy.ProxyManager;
import cloud.huel.service.HelloService;
import cloud.huel.service.impl.HelloServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * Hello world!
 */
@SpringBootApplication
public class App implements CommandLineRunner {

	@Resource
	private HelloService helloService;

	@Resource
	private ProxyManager proxyManager;

	public static void main(String[] args) {
		SpringApplication.run(App.class);
	}


	@Override
	public void run(String... args) throws Exception {
		HelloServiceImpl proxy = (HelloServiceImpl) proxyManager.getProxy(HelloServiceImpl.class);
		System.out.println(proxy.hello());
		tt();
	}

	public void tt() {
		HelloServiceImpl proxy = (HelloServiceImpl) proxyManager.getProxy(HelloServiceImpl.class);
		System.out.println(proxy.hello());
	}


}
