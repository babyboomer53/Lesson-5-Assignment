package EDU.UCSD.Extension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

class Resource {
    private String filename;

    public Resource(String filename) {
        this.filename = filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long countTheCharacters() throws IOException {
        FileInputStream fileInput = new FileInputStream(filename);
        int value;
        long count = 0;
        while ((value = fileInput.read()) != -1) {
            char character = (char) value;
            count++;
            System.out.printf("Character number %d from %s.%n", count, filename);
        }
        fileInput.close();
        return count;
    }
}

public class Reentrant_Lock implements Runnable {
    private Resource resource;
    private Lock lock;
    private long count;

    public Reentrant_Lock(Resource resource) {
        this.resource = resource;
        this.lock = new ReentrantLock();
    }

    public long getCount() {
        return count;
    }

    @Override
    public void run() {
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                count = resource.countTheCharacters();
            }
        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace();
        } finally {
            //release lock
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Reentrant_Lock reentrantLock1 = new Reentrant_Lock(new Resource("Data4.txt"));
        Reentrant_Lock reentrantLock2 = new Reentrant_Lock(new Resource("Data5.txt"));
        System.out.printf("\nThis system has %d processors.%n", Runtime.getRuntime().availableProcessors());
        ExecutorService pool = Executors.newFixedThreadPool(16);
        Runnable runnable1 = reentrantLock1;
        Runnable runnable2 = reentrantLock2;
        /**
         Runnable runnable1 = new Reentrant_Lock(new Resource("Data4.txt"));
         Runnable runnable2 = new Reentrant_Lock(new Resource("Data5.txt"));
         pool.execute(runnable1);
         pool.execute(runnable2);
         pool.awaitTermination(5, TimeUnit.SECONDS);
         pool.shutdown();
         */
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        thread1.start();
        thread2.start();
        pool.awaitTermination(3, TimeUnit.SECONDS);
        pool.shutdown();
        //thread2.join();
        System.out.println("\nThe combined character count is " + (reentrantLock1.getCount() + reentrantLock2.getCount()));
    }
}
