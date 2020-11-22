package cui.shibing.statemanager;

import cui.shibing.statemanager.event.Event;
import cui.shibing.statemanager.event.EventType;
import cui.shibing.statemanager.exception.MultiTailListenerException;
import cui.shibing.statemanager.exception.NoMatchedEventException;
import cui.shibing.statemanager.exception.StateTransitionException;
import cui.shibing.statemanager.listener.OrderComparator;
import cui.shibing.statemanager.listener.TransitionListener;
import cui.shibing.statemanager.listener.TransitionTailListener;
import cui.shibing.statemanager.state.State;
import cui.shibing.statemanager.state.Stateful;
import cui.shibing.statemanager.state.Transition;
import cui.shibing.statemanager.state.TransitionAction;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class StateManagerImpl implements StateManager {
    private final Map<String, State> stateRegistry = new HashMap<>();
    private final List<TransitionListener> listeners = new ArrayList<>();
    private final List<TransitionTailListener> tailListeners = new ArrayList<>();

    private final Map<String, List<Transition>> transitionRegistry = new HashMap<>();

    private final Map<String, Transition> transitionActionCache = new ConcurrentHashMap<>();

    private final OrderComparator<? super Object> comparator = new OrderComparator<>();

    @Override
    public State getState(String name) {
        return stateRegistry.get(name);
    }

    private void registerTransition(Transition transition) {
        if (transition == null) {
            throw new NullPointerException("transition is null");
        }
        if (transition.getEventType() == null) {
            throw new NullPointerException("transition's event type is null");
        }
        State from = transition.getFrom();
        State to = transition.getTo();
        stateRegistry.putIfAbsent(from.getName(), from);
        stateRegistry.putIfAbsent(to.getName(), to);

        transitionRegistry.compute(transition.getFrom().getName(), (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            Set<String> eventTypes = v.stream().map(o -> o.getEventType().getName()).collect(Collectors.toSet());
            if (eventTypes.contains(transition.getEventType().getName())) {
                throw new IllegalStateException(String.format("duplicate event type [%s] on state [%s]",
                        transition.getEventType().getName(), k));
            }
            v.add(transition);
            return v;
        });
    }

    @Override
    public synchronized void registerTransition(EventType eventType, State from, State to) {
        registerTransition(eventType, from, to, (c, e) -> true);
    }

    @Override
    public synchronized void registerTransition(EventType eventType, State from, State to, TransitionAction action) {
        registerTransition(new Transition(eventType, from, to, action));
    }

    @Override
    public synchronized void registerTransitionListener(TransitionListener listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("duplicate listener " + listener);
        }
        listeners.add(listener);
        listeners.sort(comparator);
    }

    @Override
    public synchronized void registerTailTransitionListener(TransitionTailListener listener) {
        if (tailListeners.contains(listener)) {
            throw new IllegalArgumentException("duplicate listener " + listener);
        }
        tailListeners.add(listener);
        tailListeners.sort(comparator);
    }

    @Override
    public final void step(Stateful context, Event event) throws NoMatchedEventException, StateTransitionException {
        try {
            State from = context.getState();
            Transition targetTransition = getTransition(from, event);
            State to = targetTransition.getTo();

            for (TransitionListener listener : listeners) {
                if (!listener.supports(from, to, event)) {
                    continue;
                }
                if (!listener.beforeTransition(from, to, event)) {
                    return;
                }
            }

            from.exitAction(context, event);

            if (!targetTransition.getAction().onTransition(context, event)) {
                return;
            }

            to.entryAction(context, event);

            // 改变状态
            context.setState(to);

            for (TransitionListener listener : listeners) {
                if (!listener.supports(from, to, event)) {
                    continue;
                }
                listener.afterTransition(from, to, event);
            }

            List<TransitionTailListener> candidates = tailListeners.stream()
                    .filter(l -> l.supports(this, context, from, to, event))
                    .collect(Collectors.toList());
            if (candidates.size() > 1) {
                throw new MultiTailListenerException();
            }
            if (candidates.size() == 1) {
                candidates.get(0).afterTransition(this, context, from, to, event);
            }

        } catch (Exception e) {
            if (e instanceof NoMatchedEventException) {
                throw (NoMatchedEventException) e;
            }
            throw new StateTransitionException(e);
        }
    }

    private Transition getTransition(State from, Event event) throws NoMatchedEventException {
        Transition targetTransition = transitionActionCache.computeIfAbsent(
                from.getName() + event.getEventType().getName(),
                k -> {
                    List<Transition> transitions = transitionRegistry.getOrDefault(from.getName(),
                            Collections.emptyList());

                    return transitions.stream()
                            .filter(t -> Objects.equals(t.getEventType().getName(), event.getEventType().getName()))
                            .findAny().orElse(null);
                });
        if (targetTransition == null) {
            String msg = String.format("current state [%s] does not respond to event type [%s]",
                    from.getName(), event.getEventType());
            log.warn(msg);
            throw new NoMatchedEventException(msg);
        }
        return targetTransition;
    }
}
