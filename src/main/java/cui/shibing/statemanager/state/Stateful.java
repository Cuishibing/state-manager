package cui.shibing.statemanager.state;

public interface Stateful {
    State getState();

    void setState(State state);
}
