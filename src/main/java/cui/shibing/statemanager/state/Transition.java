package cui.shibing.statemanager.state;

import cui.shibing.statemanager.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transition<T extends Stateful<T>> {
    private EventType eventType;
    private State<T> from;
    private State<T> to;

    private TransitionAction<T> action;
}
