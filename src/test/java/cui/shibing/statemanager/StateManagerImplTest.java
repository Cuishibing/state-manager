package cui.shibing.statemanager;

import cui.shibing.statemanager.event.Event;
import cui.shibing.statemanager.event.EventType;
import cui.shibing.statemanager.listener.TransitionListener;
import cui.shibing.statemanager.listener.TransitionTailListener;
import cui.shibing.statemanager.state.State;
import cui.shibing.statemanager.state.Stateful;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StateManagerImplTest {

    private StateManager<SimpleStateful> stateManager;

    @Before
    public void before() {
        stateManager = new StateManagerImpl<>();
    }

    @Test
    public void testGetState() {
        StringState s1 = new StringState("s1");
        StringState s2 = new StringState("s2");

        StringEventType s1s2 = new StringEventType("s1s2");

        stateManager.registerTransition(s1s2, s1, s2);

        Assert.assertEquals(s1, stateManager.getState("s1"));
        Assert.assertEquals(s2, stateManager.getState("s2"));
    }

    /**
     * 测试递归转移状态，初始状态为s1，发送s1s2事件后，最终状态为s4
     * [s1] --(s1s2)--> [s2]
     * [s2] --(s2s3)--> [s3]
     * [s3] --(s3s4)--> [s4]
     */
    @SneakyThrows
    @Test
    public void testRecursiveStep() {
        StringState s1 = new StringState("s1");
        StringState s2 = new StringState("s2");
        StringState s3 = new StringState("s3");
        StringState s4 = new StringState("s4");

        StringEventType s1s2 = new StringEventType("s1s2");
        StringEventType s2s3 = new StringEventType("s2s3");
        StringEventType s3s4 = new StringEventType("s3s4");

        stateManager.registerTransition(s1s2, s1, s2);
        stateManager.registerTransition(s2s3, s2, s3);
        stateManager.registerTransition(s3s4, s3, s4);

        // 当从s1转移到s2时发送事件（s2s3）尝试转移到s3状态
        TransitionTailListener<SimpleStateful> sendS2S3Listener = new TransitionTailListener<SimpleStateful>() {
            @Override
            public boolean supports(StateManagerImpl<SimpleStateful> machine, SimpleStateful context,
                                    State<SimpleStateful> from, State<SimpleStateful> to, Event event) {
                return from == s1 && to == s2;
            }

            @SneakyThrows
            @Override
            public void afterTransition(StateManagerImpl<SimpleStateful> machine, SimpleStateful context,
                                        State<SimpleStateful> from, State<SimpleStateful> to, Event event) {
                machine.step(context, s2s3.createEvent());
            }
        };
        // 当从s2转移到s3时发送事件（s3s4）尝试转移到s4状态
        TransitionTailListener<SimpleStateful> sendS3S4Listener = new TransitionTailListener<SimpleStateful>() {
            @Override
            public boolean supports(StateManagerImpl<SimpleStateful> machine, SimpleStateful context,
                                    State<SimpleStateful> from, State<SimpleStateful> to, Event event) {
                return from == s2 && to == s3;
            }

            @SneakyThrows
            @Override
            public void afterTransition(StateManagerImpl<SimpleStateful> machine, SimpleStateful context,
                                        State<SimpleStateful> from, State<SimpleStateful> to, Event event) {
                machine.step(context, s3s4.createEvent());
            }
        };

        // 打印状态转移过程
        TransitionListener<SimpleStateful> printTransitionInfo = new TransitionListener<SimpleStateful>() {
            @Override
            public boolean supports(State<SimpleStateful> from, State<SimpleStateful> to, Event event) {
                return true;
            }

            @Override
            public void afterTransition(State<SimpleStateful> from, State<SimpleStateful> to, Event event) {
                System.out.printf("[%s] --(%s)--> [%s]%n", from.getName(), event.getEventType().getName(), to.getName());
            }
        };
        stateManager.registerTailTransitionListener(sendS2S3Listener);
        stateManager.registerTailTransitionListener(sendS3S4Listener);
        stateManager.registerTransitionListener(printTransitionInfo);

        SimpleStateful stateful = new SimpleStateful();
        stateful.setState(s1);

        stateManager.step(stateful, s1s2.createEvent());
        Assert.assertEquals(s4, stateful.getState());
    }

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

    @AllArgsConstructor
    @Data
    private static class StringEventType implements EventType {
        private String name;
    }

}