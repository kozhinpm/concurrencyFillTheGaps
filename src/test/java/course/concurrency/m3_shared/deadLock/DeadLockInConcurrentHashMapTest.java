package course.concurrency.m3_shared.deadLock;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeadLockInConcurrentHashMapTest {

    @Test
    public void tryDeadloc() throws InterruptedException {
      Map<String, Integer> map = new ConcurrentHashMap<>();

        Thread t1 = new Thread(() -> {
            map.computeIfAbsent("A", k -> {
                sleep();
                return map.computeIfAbsent("B", v -> 1);
            });
        });
        Thread t2 = new Thread(()-> {
            map.computeIfAbsent("B", k -> {
                sleep();
                return map.computeIfAbsent("A", v -> 1);
            });
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
    }
}

