package cui.shibing.statemanager.state;

import cui.shibing.statemanager.event.Event;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class SimpleState implements State {

    private final TransitionAction entryAction;
    private final TransitionAction exitAction;

    private final String name;

    public SimpleState(String name) {
        this(name, (t, e) -> true, (t, e) -> true);
    }

    public SimpleState(String name, TransitionAction entryAction, TransitionAction exitAction) {
        this.name = name;
        this.entryAction = entryAction;
        this.exitAction = exitAction;
    }

    @SneakyThrows
    @Override
    public void entryAction(Stateful context, Event event) {
        if (entryAction != null) {
            entryAction.onTransition(context, event);
        }
    }

    @SneakyThrows
    @Override
    public void exitAction(Stateful context, Event event) {
        if (exitAction != null) {
            exitAction.onTransition(context, event);
        }
    }
}
