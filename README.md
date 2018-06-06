# AtomicInteger Example

An `AtomicInteger` is used in applications such as atomically incremented counters. 

Short sample code:

```java
public class AtomicIntegerExample {
    
    private final ExecutorService execService = Executors.newFixedThreadPool(100);
    private final AtomicInteger counter = new AtomicInteger(100_000);
    
    public static void main(String[] args) {
        AtomicIntegerExample exp = new AtomicIntegerExample();
        exp.start();
    }
    
    private void start() {
        for (int i = 0; i < 10_000; i++) {
            execService.execute(sendRequest(i+1));
        }
    }
    
    private Runnable sendRequest(int i) {
        return () -> {
            int id = counter.incrementAndGet();  
            System.out.printf("Request number: %5s  |  OrderId: [%s]  |  %s\n", i, id, LocalTime.now());
        };
    }
}
```
