package cui.shibing.statemanager.listener;

import cui.shibing.statemanager.StateManagerImpl;
import cui.shibing.statemanager.event.Event;
import cui.shibing.statemanager.state.State;
import cui.shibing.statemanager.state.Stateful;

/**
 * 状态转移监听器<br/>
 * <p>
 * 如果一次状态转移过程中匹配到了多个TransitionTailListener实例则会抛出异常，也就是说只会有一个TransitionTailListener实例被调用，
 * 并且执行的顺序在所有的TransitionListener之后
 *
 * @param <C> 状态持有者的类型
 */
public interface TransitionTailListener<C extends Stateful<C>> {

    /**
     * 是否命中
     *
     * @param machine 当前状态机
     * @param context 状态持有者
     * @param from    当前状态
     * @param to      要转移的状态
     * @param event   当前事件
     * @return 是否命中
     */
    boolean supports(StateManagerImpl<C> machine, C context, State<C> from, State<C> to, Event event);

    /**
     * <pre>
     * 在状态转移之后调用
     *
     * 一般在状态转移之后需要立即持久化状态信息，可以为持久化状态信息单独注册一个listener，
     * 然后使用Order注解调整该listener的顺序为第一个（@Order(Integer.MIN_VALUE)）。
     * </pre>
     *
     * @param machine 当前状态机
     * @param context 状态持有者
     * @param from    当前状态
     * @param to      要转移的状态
     * @param event   当前事件
     */
    default void afterTransition(StateManagerImpl<C> machine, C context, State<C> from, State<C> to, Event event) {

    }
}
