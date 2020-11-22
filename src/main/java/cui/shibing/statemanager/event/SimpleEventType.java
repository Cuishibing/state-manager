package cui.shibing.statemanager.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleEventType implements EventType {
    private final String name;
}
