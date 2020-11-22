# 状态机
## 状态机概念
状态机可归纳为4个要素，即现态、条件、动作、次态。这样的归纳，主要是出于对状态机的内在因果关系的考虑。“现态”和“条件”是因，“动作”和“次态”是果。详解如下：
1. 现态：是指当前所处的状态。
2. 条件：又称为“事件”，当一个条件被满足，将会触发一个动作，或者执行一次状态的迁移。
2. 动作：条件满足后执行的动作。动作执行完毕后，可以迁移到新的状态，也可以仍旧保持原状态。动作不是必需的，当条件满足后，也可以不执行任何动作，直接迁移到新状态。
4. 次态：条件满足后要迁往的新状态。“次态”是相对于“现态”而言的，“次态”一旦被激活，就转变成新的“现态”了。

`state-manager`简单实现了以上概念。
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