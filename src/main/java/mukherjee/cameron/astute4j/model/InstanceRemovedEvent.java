package mukherjee.cameron.astute4j.model;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class InstanceRemovedEvent {

    private String instanceId;
    private OffsetDateTime occurredAt;
}
