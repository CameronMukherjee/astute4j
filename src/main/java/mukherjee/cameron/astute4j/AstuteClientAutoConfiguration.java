package mukherjee.cameron.astute4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AstuteClientAutoConfiguration {

    @Bean
    @ConditionalOnBean(DiscoveryClient.class)
    public AstuteClient astuteClient(@Value("${spring.application.name}") String applicationName,
                                     DiscoveryClient discoveryClient,
                                     ApplicationEventPublisher applicationEventPublisher) {
        return new AstuteClient(applicationName, discoveryClient, applicationEventPublisher);
    }
}
