package cui.shibing.statemanager.listener;

import cui.shibing.statemanager.event.Event;
import cui.shibing.statemanager.state.State;

/**
 * 状态转移监听器<br/>
 * <p>
 * 在状态转移前后被调用，如果一次状态转移过程中匹配到了多个TransitionListener实例，则会按照他们的顺序执行
 *
 */
public interface TransitionListener {

    /**
     * 是否命中
     *
     * @param from  当前状态
     * @param to    要转移的状态
     * @param event 当前事件
     * @return 是否命中
     */
    boolean supports(State from, State to, Event event);

    /**
     * 在状态转移之前调用
     *
     * @param from    当前状态
     * @param to      要转移的状态
     * @param event   当前事件
     * @return true：继续状态转移，false：终止状态转移
     */
    default boolean beforeTransition(State from, State to, Event event) {
        return true;
    }

    /**
     * <pre>
     * 在状态转移之后调用
     *
     * 一般在状态转移之后需要立即持久化状态信息，可以为持久化状态信息单独注册一个listener，
     * 然后使用Order注解调整该listener的顺序为第一个（@Order(Integer.MIN_VALUE)）。
     * </pre>
     *
     * @param from    当前状态
     * @param to      要转移的状态
     * @param event   当前事件
     */
    default void afterTransition(State from, State to, Event event) {
    }
}
