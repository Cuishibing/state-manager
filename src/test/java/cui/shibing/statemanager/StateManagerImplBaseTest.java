package cui.shibing.statemanager;

import cui.shibing.statemanager.state.State;
import cui.shibing.statemanager.state.Stateful;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StateManagerImplBaseTest {

    @Getter
    private static class SimpleStateful implements Stateful<SimpleStateful> {
        private State<SimpleStateful> state;

        @Override
        public State<SimpleStateful> getState() {
            return state;
        }

        @Override
        public void setState(State<SimpleStateful> state) {
            this.state = state;
        }
    }

    @AllArgsConstructor
    @Data
    private static class StringState implements State<SimpleStateful> {
        private String name;
    }

    private StateManager<SimpleStateful> stateManager;

    @Before
    public void before() {
        stateManager = new StateManagerImpl<>();
    }

    @Test
    public void testGetState0() {
        State<SimpleStateful> ans = stateManager.getState("init");
        Assert.assertNull(ans);
    }

    @Test
    public void testGetState1() {
        StringState init = new StringState("init");
        StringState first = new StringState("first");
        stateManager.registerState(init);
        stateManager.registerState(first);

        State<SimpleStateful> init1 = stateManager.getState("init");
        Assert.assertEquals(init.getName(), init1.getName());

        State<SimpleStateful> first1 = stateManager.getState("first");
        Assert.assertEquals(first.getName(), first1.getName());
    }

    @Test
    public void testRegisterState0() {
        StringState init = new StringState("init");
        stateManager.registerState(init);
    }

    @Test
    public void testRegisterState1() {
        StringState init = new StringState("init");
        stateManager.registerState(init);

        try {
            stateManager.registerState(null);
        } catch (NullPointerException e) {
            Assert.assertNotNull(e);
        }

        try {
            stateManager.registerState(init);
        } catch (IllegalStateException e) {
            Assert.assertNotNull(e);
        }
    }

}