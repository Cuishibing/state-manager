package cui.shibing.statemanager.state;

import cui.shibing.statemanager.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transition {
    private EventType eventType;
    private State from;
    private State to;

    private TransitionAction action;
}
