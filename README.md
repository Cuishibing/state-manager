# 状态机
## 基本使用
```java
// 定义状态
SimpleState s1 = new SimpleState("s1");
SimpleState s2 = new SimpleState("s2");
// 定义转移事件
SimpleEventType s1s2 = new SimpleEventType("s1s2");
// 注册转移规则：当前是s1状态，发生s1s2事件时转移到s2状态
stateManager.registerTransition(s1s2, s1, s2);

// 使用Listener机制打印状态转移过程
TransitionListener printTransitionInfo = new TransitionListener() {
    @Override
    public boolean supports(State from, State to, Event event) {
        return true;
    }

    @Override
    public void afterTransition(State from, State to, Event event) {
        System.out.printf("[%s] --(%s)--> [%s]%n", from.getName(), event.getEventType().getName(), to.getName());
    }
};
// 注册Listener
stateManager.registerTransitionListener(printTransitionInfo);
// 状态持有者
SimpleStateful stateful = new SimpleStateful();
// 初始状态为s1
stateful.setState(s1);
// 发送事件
stateManager.step(stateful, s1s2.createEvent());
Assert.assertEquals(s2, stateful.getState());
```
```text
输出：
[s1] --(s1s2)--> [s2]
```