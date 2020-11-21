package cui.shibing.statemanager.listener;

import cui.shibing.statemanager.state.Stateful;

import java.util.Comparator;

public class StateTransitionListenerComparator<C extends Stateful<C>> implements Comparator<StateTransitionListener<C>> {
    @Override
    public int compare(StateTransitionListener<C> o1, StateTransitionListener<C> o2) {
        Order o1OrderAnnotation = o1.getClass().getAnnotation(Order.class);
        int o1Order = o1OrderAnnotation == null ? 0 : o1OrderAnnotation.value();

        Order o2OrderAnnotation = o2.getClass().getAnnotation(Order.class);
        int o2Order = o2OrderAnnotation == null ? 0 : o2OrderAnnotation.value();
        return o1Order - o2Order;
    }
}
