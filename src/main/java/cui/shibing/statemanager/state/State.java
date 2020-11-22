package cui.shibing.statemanager.state;

import cui.shibing.statemanager.event.Event;

public interface State {

    /**
     * 状态Name
     */
    String getName();

    /**
     * 进入该状态时被调用，该方法被调用时状态还未设置
     */
    default void entryAction(Stateful context, Event event) {
    }

    /**
     * 离开该状态被调用
     */
    default void exitAction(Stateful context, Event event) {
    }


}
