package cui.shibing.statemanager;

import cui.shibing.statemanager.event.Event;
import cui.shibing.statemanager.event.EventType;
import cui.shibing.statemanager.exception.NoMatchedEventException;
import cui.shibing.statemanager.exception.StateTransitionException;
import cui.shibing.statemanager.listener.StateTransitionListener;
import cui.shibing.statemanager.state.State;
import cui.shibing.statemanager.state.Stateful;
import cui.shibing.statemanager.state.TransitionAction;

public interface StateManager<T extends Stateful<T>> {

    /**
     * 根据id获取已注册的一个状态
     *
     * @param name 状态name
     * @return 已注册的一个状态
     * @throws IllegalArgumentException 要获取的状态不存在
     */
    State<T> getState(String name);

    /**
     * 注册一个状态
     *
     * @param state 要注册的状态
     * @throws NullPointerException  state为null
     * @throws IllegalStateException 已经注册过该状态
     */
    void registerState(State<T> state);

    /**
     * 注册一个空的转移动作，从 from 到 to 的状态转移不会有任何动作
     *
     * @param eventType 事件类型
     * @param from      现态
     * @param to        次态
     * @throws IllegalStateException 一个状态重复的事件类型
     */
    void registerTransition(EventType eventType, State<T> from, State<T> to);

    /**
     * 注册一个转移动作，从 from 到 to 的状态转移会调用action指定的动作
     *
     * @param eventType 事件类型
     * @param from      现态
     * @param to        次态
     * @param action    转移动作
     * @throws IllegalStateException 一个状态重复的事件类型
     */
    void registerTransition(EventType eventType, State<T> from, State<T> to, TransitionAction<T> action);

    /**
     * 注册一个状态转移监听器
     *
     * @param listener 监听器
     */
    default void registerStateTransitionListener(StateTransitionListener<T> listener) {
        throw new RuntimeException("not support operation");
    }

    void step(T context, Event event) throws NoMatchedEventException, StateTransitionException;

}
