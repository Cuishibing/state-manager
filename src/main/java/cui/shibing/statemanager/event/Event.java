package cui.shibing.statemanager.event;

public interface Event {

    /**
     * 获取事件的类型
     *
     * @return 事件的类型
     */
    EventType getEventType();

    /**
     * 获取该事件的参数
     *
     * @return 事件的参数
     */
    default Object getArgs() {
        return null;
    }
}
