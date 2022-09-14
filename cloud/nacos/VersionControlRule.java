package cloud.huel.rule;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.ribbon.ExtendBalancer;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 张晓华
 * @date 2022-9-13
 * 对NacosRule的功能增加根据版本号调用的功能的扩展
 * 需要服务提供者和消费者在Nacos的元数据中指定版本号信息
 * 如:
 * spring.cloud.nacos.discovery.metadata.version=2.0
 */
@Slf4j
public class VersionControlRule extends AbstractLoadBalancerRule {


	private ILoadBalancer loadBalancer;


	@Resource
	private NacosDiscoveryProperties discoveryProperties;

	@Resource
	private NacosServiceManager serviceManager;


	// Nothing
	@Override
	public void initWithNiwsConfig(IClientConfig iClientConfig) {
	}

	@Override
	public Server choose(Object key) {
		try {
			Map<String, String> currentMetaData = discoveryProperties.getMetadata();
			String toCallVersion = currentMetaData.get("version");
			log.info("当前服务要调用的服务的版本为 {}", toCallVersion);
			loadBalancer = getLoadBalancer();
			DynamicServerListLoadBalancer balancer = (DynamicServerListLoadBalancer) loadBalancer;
			String clusterName = discoveryProperties.getClusterName();
			String group = discoveryProperties.getGroup();
			String name = balancer.getName();
			log.info("当前要调用的服务的集群{}, 组名{}, 服务名{}", clusterName, group, name);
			NamingService namingService = serviceManager.getNamingService(discoveryProperties.getNacosProperties());
			List<Instance> availableInstances = namingService.selectInstances(name, group, true);
			if (CollectionUtils.isEmpty(availableInstances)) {
				log.warn("无可用服务");
				return null;
			}
			List<Instance> instancesToChoose = availableInstances;
			// 如果当前服务指定了集群名称,则需要进到这个if中处理
			if (StringUtils.hasText(clusterName)) {
				// 收集位于指定集群中的所有可用实例,就是通过判断实例的集群名和当前要调用的集群名是否相同.
				List<Instance> sameClusterInstances = instancesToChoose.stream()
						.filter(i -> Objects.equals(clusterName, i.getClusterName()))
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(sameClusterInstances)) {
					// 如果获取到了指定集群下的可以服务实例,则准备从获取到的实例中根据权重来选择一个实例调用
					instancesToChoose = sameClusterInstances;
				} else {
					log.warn("当前集群中已无可用服务, 准备发起跨集群服务的调用");
				}
			}
			// 如果指定了版本号,则需要进行再次选择
			List<Instance> sameVersionToChoose = instancesToChoose;
			if (StringUtils.hasText(toCallVersion)) {
				List<Instance> sameVersionInstances = sameVersionToChoose.stream()
						.filter(i -> toCallVersion.equalsIgnoreCase(i.getMetadata().get("version")))
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(sameVersionInstances)) {
					instancesToChoose = sameVersionInstances;
				} else {
					// 当没有指定版本的服务时,具体的业务逻辑自定义...
					log.warn("无指定版本的服务");
					return null;
				}
			}
			// 根据权重的算法选择一个实例
			Instance instance = ExtendBalancer.getHostByRandomWeight2(instancesToChoose);
			String version = instance.getMetadata().get("version");
			log.info("获取到的服务实例, host: {}, port: {}, version {}", instance.getIp(), instance.getPort(), version);
			return new NacosServer(instance);
		} catch (NacosException e) {
			throw new RuntimeException(e);
		}

	}


}
