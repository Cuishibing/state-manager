package cui.shibing.statemanager.state;

public class SimpleStateful implements Stateful {
    private State state;

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
}
