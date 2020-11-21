package cui.shibing.statemanager.state;

import cui.shibing.statemanager.event.Event;

@FunctionalInterface
public interface TransitionAction<T extends Stateful<T>> {

    /**
     * 状态转移动作
     *
     * @param context 状态持有者
     * @param event   事件
     * @return true：继续状态转移，false：终止状态转移
     */
    boolean onTransition(T context, Event event) throws Exception;
}
