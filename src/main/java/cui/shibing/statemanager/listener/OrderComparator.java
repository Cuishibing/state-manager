package cui.shibing.statemanager.listener;

import java.util.Comparator;

public class OrderComparator<T> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        Order o1OrderAnnotation = o1.getClass().getAnnotation(Order.class);
        int o1Order = o1OrderAnnotation == null ? 0 : o1OrderAnnotation.value();

        Order o2OrderAnnotation = o2.getClass().getAnnotation(Order.class);
        int o2Order = o2OrderAnnotation == null ? 0 : o2OrderAnnotation.value();
        return o1Order - o2Order;
    }
}
