package cui.shibing.statemanager.listener;

import cui.shibing.statemanager.StateManagerImpl;
import cui.shibing.statemanager.event.Event;
import cui.shibing.statemanager.state.State;
import cui.shibing.statemanager.state.Stateful;

/**
 * 状态转移监听器
 *
 * @param <C> 状态持有者的类型
 */
public interface StateTransitionListener<C extends Stateful<C>> {

    /**
     * 在状态转移之前调用
     *
     * @param machine 当前状态机
     * @param context 状态持有者
     * @param from    当前状态
     * @param to      要转移的状态
     * @return true：继续状态转移，false：终止状态转移
     */
    default boolean beforeTransition(StateManagerImpl<C> machine, C context, State<C> from, State<C> to, Event event) {
        return true;
    }

    /**
     * <pre>
     * 在状态转移之后调用，可以在afterTransition方法中启动另一个状态转移过程，
     * 但是必须是在最后一个StateTransitionListener中（类似于尾递归），
     * 可以使用Order注解调整listener的顺序。
     * 
     * 建议：一般在状态转移之后需要立即持久化状态信息，可以为持久化状态信息单独注册一个listener，
     * 然后使用Order注解调整该listener的顺序为第一个（@Order(Integer.MIN_VALUE)）。
     * </pre>
     * @param machine 当前状态机
     * @param context 状态持有者
     * @param from    当前状态
     * @param to      要转移的状态
     */
    default void afterTransition(StateManagerImpl<C> machine, C context, State<C> from, State<C> to, Event event) {

    }
}
