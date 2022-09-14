package cloud.huel.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerPing;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.Server;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author 张晓华
 * @date 2022-9-13
 * IPing接口的实现类
 */
@Slf4j
public class CustomPingRule extends AbstractLoadBalancerPing {

	@Override
	public void initWithNiwsConfig(IClientConfig iClientConfig) {

	}

	/**
	 自定义Ping实现,远程服务必须提供一个ping接口,返回一个固定值,如果获取到的结果和指定的值相同,
	 那么就认为该服务正常运行,请求可以正常处理.
	 返回的返回值 false代表服务不可达,true即可达
	 这里参照的Redis中的ping命令,返回PONG
	 可以自己修改
	 */
	@Override
	public boolean isAlive(Server server) {
		String hostPort = server.getHostPort();
		log.info("hostAndPort{}", hostPort);
		String url = "http://" + hostPort + "/ping";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(url);
		try(CloseableHttpResponse httpResponse = httpClient.execute(request);){
			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				return false;
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
				//  可以修改pong的值,最好直接定义为常量.
				if ("pong".equalsIgnoreCase(result)) {
					log.info("result {}", result);
					log.info("服务正常,URL地址 {}" + url);
					return true;
			}
		}
	} catch (Exception e) {
			return false;
		}
		return false;
	}


}
