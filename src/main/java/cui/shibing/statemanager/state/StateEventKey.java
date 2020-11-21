package cui.shibing.statemanager.state;

import cui.shibing.statemanager.event.Event;
import lombok.Getter;

import java.util.Objects;

@Getter
public final class StateEventKey {
    private final State<?> state;
    private final Event event;

    public StateEventKey(State<?> state, Event event) {
        this.state = state;
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateEventKey that = (StateEventKey) o;
        String thisStateName = state == null ? null : state.getName();
        String thatStateName = that.state == null ? null : that.state.getName();

        String thisEventTypeName = event == null ? null : event.getEventType().getName();
        String thatEventTypeName = that.event == null ? null : that.event.getEventType().getName();

        return Objects.equals(thisStateName, thatStateName)
                && Objects.equals(thisEventTypeName, thatEventTypeName);
    }

    @Override
    public int hashCode() {
        String thisStateName = state == null ? null : state.getName();
        String thisEventTypeName = event == null ? null : event.getEventType().getName();
        return Objects.hash(thisStateName, thisEventTypeName);
    }
}
