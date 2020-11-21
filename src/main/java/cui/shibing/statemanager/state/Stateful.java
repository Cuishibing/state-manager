package cui.shibing.statemanager.state;

public interface Stateful<T extends Stateful<T>> {
    State<T> getState();

    void setState(State<T> state);
}
