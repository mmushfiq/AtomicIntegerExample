
package az.mm.atomicinteger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

 /**
 *
 * @author MM <mushfiqazeri@gmail.com>
 */
public class AtomicIntegerExample {
    private final int poolSize = 100;
    private final ExecutorService execService = Executors.newFixedThreadPool(poolSize);
    private final int numberOfRequest = 10_000;
    private final AtomicInteger numberOfResponse = new AtomicInteger();
    private final AtomicInteger counter = new AtomicInteger(getLastNumber());
    private final AtomicBoolean exit = new AtomicBoolean();
    private final String fixedValue = "888888";
    
    
    public static void main(String[] args) {
        AtomicIntegerExample exp = new AtomicIntegerExample();
        exp.start();
    }
    
    
    private void start() {
        new EndingCheck().start();
        for (int i = 0; i < numberOfRequest; i++) {
            execService.execute(sendRequest(i+1));
        }
    }
    
    
    private class EndingCheck extends Thread {
        @Override
        public void run() {
            while (!exit.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                int completed = numberOfResponse.get();
                System.out.printf("\n%s/%s request are completed. \n\n", completed, numberOfRequest);
                
                if (completed == numberOfRequest) close();
            }
        }
    }
    
    
    private Runnable sendRequest(int i) {
        return () -> {
            String id = fixedValue + counter.incrementAndGet();  // we need unique id
            System.out.printf("Request number: %5s  |  OrderId: [%s]  |  %s\n", i, id, LocalTime.now()); //you can call your service here
            numberOfResponse.incrementAndGet();
        };
    }
    
    
    private int getLastNumber() {
        int number = 100_000;
        try {
            String fileName = "src/main/resources/number.txt";
            String lastNumber = new String(Files.readAllBytes(Paths.get(fileName)));
            number = Integer.parseInt(lastNumber);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("Last number from file: " + number);
        return number;
    }

    
    private void saveLastNumber() {
        try(PrintWriter writer = new PrintWriter("src/main/resources/number.txt"); ) {
            int lastNumber = counter.get();
            writer.print(lastNumber);
            System.out.println("saved last number: " + lastNumber);
        } catch (FileNotFoundException ex) {
            System.err.println("number.txt file not found");
        } 
    }
    
    
    private void close() {
        execService.shutdown();
        exit.set(true);
        saveLastNumber();
    }
    
}
