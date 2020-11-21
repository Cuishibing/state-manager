package cui.shibing.statemanager.state;

public interface Stateful {
    <T extends Stateful> State<T> getState();

    <T extends Stateful> void setState(State<T> state);
}
