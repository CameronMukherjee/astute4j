package mukherjee.cameron.astute4j;

import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mukherjee.cameron.astute4j.model.InstanceAddedEvent;
import mukherjee.cameron.astute4j.model.InstanceRemovedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@ConditionalOnBean(DiscoveryClient.class)
@Service
public class AstuteClient {

    private final String applicationName;
    private final DiscoveryClient discoveryClient;
    private final ApplicationEventPublisher applicationEventPublisher;
    private Set<String> knownInstanceIds = new HashSet<>();

    public AstuteClient(@Value("${spring.application.name}") String applicationName,
                        DiscoveryClient discoveryClient,
                        ApplicationEventPublisher applicationEventPublisher) {
        if (Objects.isNull(applicationName) || StringUtils.isEmpty(applicationName)) {
            throw new AstuteException("Application name could not be resolved - AstuteClient failed to initialize");
        }

        if (Objects.isNull(discoveryClient)) {
            throw new AstuteException("DiscoveryClient could not be resolved - AstuteClient failed to initialize");
        }

        log.info("Successfully created Astute Client");
        this.applicationEventPublisher = applicationEventPublisher;
        this.applicationName = applicationName;
        this.discoveryClient = discoveryClient;
    }

    @EventListener(HeartbeatEvent.class)
    public void onHeartbeat(HeartbeatEvent event) {
        List<ServiceInstance> instances = resolveInstances();
        Set<String> currentInstanceIds = instances.stream()
          .map(ServiceInstance::getInstanceId)
          .collect(Collectors.toSet());

        // Detect added instances
        for (String id : currentInstanceIds) {
            if (!knownInstanceIds.contains(id)) {
                log.info("Instance Added - [{}]", id);
                applicationEventPublisher.publishEvent(new InstanceAddedEvent(id, OffsetDateTime.now()));
            }
        }

        // Detect removed instances
        for (String id : knownInstanceIds) {
            if (!currentInstanceIds.contains(id)) {
                log.info("Instance Removed - [{}]", id);
                applicationEventPublisher.publishEvent(new InstanceRemovedEvent(id, OffsetDateTime.now()));
            }
        }

        // Update knownInstanceIds
        knownInstanceIds = currentInstanceIds;
    }

    public int getThisInstanceId() {
        final String thisInstanceId =  getThisInstanceName();
        List<ServiceInstance> sortedServices = resolveInstances()
          .stream()
          .sorted(Comparator.comparing(ServiceInstance::getInstanceId))
          .collect(Collectors.toList());

        for (int i = 0; i < sortedServices.size(); i++) {
            if (Objects.equals(sortedServices.get(i).getInstanceId(), thisInstanceId)) {
                return i;
            }
        }

        log.error("Failed to resolve this instance ID");
        return -1;
    }

    public String getThisInstanceName() {
        try {
            // Using host name as unique identifier
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            log.error("Exception occurred when attempting to resolve instance name");
            return "Unknown";
        }
    }

    public List<ServiceInstance> resolveInstances() {
        return discoveryClient.getInstances(applicationName);
    }
}
