package cloud.huel.service.impl;

import cloud.huel.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * @author 张晓华
 * @date 2022-10-23
 */
@Service
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello() {
	
		return "Hello";
	}


}
