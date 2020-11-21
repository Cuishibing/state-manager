package cui.shibing.statemanager;

import cui.shibing.statemanager.event.Event;
import cui.shibing.statemanager.event.EventType;
import cui.shibing.statemanager.exception.NoMatchedEventException;
import cui.shibing.statemanager.exception.StateTransitionException;
import cui.shibing.statemanager.listener.StateTransitionListener;
import cui.shibing.statemanager.listener.StateTransitionListenerComparator;
import cui.shibing.statemanager.state.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class StateManagerImpl<T extends Stateful<T>> implements StateManager<T> {

    private final ThreadLocal<Boolean> runningRecorder = ThreadLocal.withInitial(() -> false);

    private final List<StateTransitionListener<T>> listeners = new ArrayList<>();

    private final Map<String, State<T>> stateRegistry = new HashMap<>();

    private final Map<String, List<Transition<T>>> transitionRegistry = new HashMap<>();

    private final Map<String, Transition<T>> transitionActionCache = new ConcurrentHashMap<>();

    private final StateTransitionListenerComparator<T> comparator = new StateTransitionListenerComparator<>();

    @Override
    public State<T> getState(String name) {
        return stateRegistry.get(name);
    }

    @Override
    public synchronized void registerState(State<T> state) {
        if (state == null) {
            throw new NullPointerException("state is null");
        }
        if (stateRegistry.containsKey(state.getName())) {
            throw new IllegalStateException(String.format("state [%s] already in state manager",
                    state.getName()));
        }
        stateRegistry.put(state.getName(), state);
        log.info("register state [{}]", state);
    }

    private void registerTransition(Transition<T> transition) {
        if (transition == null) {
            throw new NullPointerException("transition is null");
        }
        if (transition.getEventType() == null) {
            throw new NullPointerException("transition's event type is null");
        }
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
    public synchronized void registerTransition(EventType eventType, State<T> from, State<T> to) {
        registerTransition(eventType, from, to, (c, e) -> true);
    }

    @Override
    public synchronized void registerTransition(EventType eventType, State<T> from, State<T> to, TransitionAction<T> action) {
        registerTransition(new Transition<>(eventType, from, to, action));
    }

    @Override
    public synchronized void registerStateTransitionListener(StateTransitionListener<T> listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("duplicate listener " + listener);
        }
        listeners.add(listener);
        listeners.sort(comparator);
    }

    @Override
    public final void step(T context, Event event) throws NoMatchedEventException, StateTransitionException {
        Boolean running = runningRecorder.get();
        if (running != null && running) {
            throw new StateTransitionException("state machine is running");
        }
        try {
            runningRecorder.set(true);

            State<T> from = context.getState();
            Transition<T> targetTransition = getTransition(from, event);
            State<T> to = targetTransition.getTo();

            for (StateTransitionListener<T> transitionListener : listeners) {
                if (!transitionListener.beforeTransition(this, context, from, to, event)) {
                    return;
                }
            }

            from.exitAction(context, event);

            if (!targetTransition.getAction().onTransition(context, event)) {
                return;
            }

            to.entryAction(context, event);

            context.setState(to);

            for (int i = 0; i < listeners.size(); i++) {
                if (i == listeners.size() - 1) {
                    runningRecorder.set(false);
                }
                StateTransitionListener<T> transitionListener = listeners.get(i);
                transitionListener.afterTransition(this, context, from, to, event);
            }

        } catch (Exception e) {
            if (e instanceof NoMatchedEventException) {
                throw (NoMatchedEventException) e;
            }
            throw new StateTransitionException(e);
        } finally {
            runningRecorder.set(false);
        }
    }

    private Transition<T> getTransition(State<T> from, Event event) throws NoMatchedEventException {
        Transition<T> targetTransition = transitionActionCache.computeIfAbsent(
                from.getName() + event.getEventType().getName(),
                k -> {
                    List<Transition<T>> transitions = transitionRegistry.getOrDefault(from.getName(),
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
