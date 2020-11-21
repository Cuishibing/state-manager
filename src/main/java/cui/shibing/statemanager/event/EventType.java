package cui.shibing.statemanager.event;

public interface EventType {

    String getName();

    /**
     * 创建一个不带参数的事件
     *
     * @return 事件
     */
    default Event createEvent() {
        return () -> this;
    }

    /**
     * 创建一个带有参数的事件
     *
     * @param args 该事件的参数
     * @return 事件
     */
    default Event createEvent(Object args) {
        final EventType eventType = this;
        return new Event() {
            @Override
            public EventType getEventType() {
                return eventType;
            }

            @Override
            public Object getArgs() {
                return args;
            }
        };
    }
}
