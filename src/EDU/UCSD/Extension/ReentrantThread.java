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

class NoLockThread implements Runnable {
    private Resource resource;

    private long count;

    public NoLockThread(Resource resource) {
        this.resource = resource;
    }

    public long getCount() {
        return count;
    }

    @Override
    public void run() {
        try {
            count = resource.countTheCharacters();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}

public class ReentrantThread implements Runnable {
    private Resource resource;
    private Lock lock;
    private long count;

    public ReentrantThread(Resource resource) {
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
        // I didn't want to do it this way, but I needed a reference to these objects so that
        // I could retrieve the value of their accumulator fields.
        ReentrantThread reentrantLock1 = new ReentrantThread(new Resource("Data4.txt"));
        ReentrantThread reentrantLock2 = new ReentrantThread(new Resource("Data5.txt"));
        // Just how many processes are we talkin' about?
        System.out.printf("%nThis system has %d processors.%n%n", Runtime.getRuntime().availableProcessors());
        ExecutorService pool = Executors.newFixedThreadPool(16);
        Runnable runnable1 = reentrantLock1;
        Runnable runnable2 = reentrantLock2;
        /**
         Runnable runnable1 = new ReentrantThread(new Resource("Data4.txt"));
         Runnable runnable2 = new ReentrantThread(new Resource("Data5.txt"));
         pool.execute(runnable1);
         pool.execute(runnable2);
         pool.awaitTermination(5, TimeUnit.SECONDS);
         pool.shutdown();
         */
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        thread1.start();
        thread2.start();
        if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
            // pool.shutdown();
            //thread2.join();
            System.out.println("\nThe combined character count is " +
                    (reentrantLock1.getCount() + reentrantLock2.getCount()));
            System.out.println("\nIf you scroll through the output, you'll notice the interleaving of the\n" +
                    "messages as the system alternated between the threads.");
        }


        // I didn't want to do it this way, but I needed a reference to these objects so that
        // I could retrieve the value of their accumulator fields.
        NoLockThread noLockThread1 = new NoLockThread(new Resource("Data4.txt"));
        NoLockThread noLockThread2 = new NoLockThread(new Resource("Data5.txt"));
        // Just how many processes are we talkin' about?
        System.out.printf("%nThis system has %d processors.%n%n", Runtime.getRuntime().availableProcessors());
        // ExecutorService pool = Executors.newFixedThreadPool(16);
        Runnable runnable3 = noLockThread1;
        Runnable runnable4 = noLockThread2;
        /**
         Runnable runnable1 = new ReentrantThread(new Resource("Data4.txt"));
         Runnable runnable2 = new ReentrantThread(new Resource("Data5.txt"));
         pool.execute(runnable1);
         pool.execute(runnable2);
         pool.awaitTermination(5, TimeUnit.SECONDS);
         pool.shutdown();
         */
        Thread thread3 = new Thread(runnable3);
        Thread thread4 = new Thread(runnable4);
        thread3.start();
        thread4.start();
        if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
            pool.shutdown();
            //thread2.join();
            System.out.println("\nThe combined character count is " +
                    (noLockThread1.getCount() + noLockThread2.getCount()));
            System.out.println("\nIf you scroll through the output, you'll notice the interleaving of the\n" +
                    "messages as the system alternated between the threads.");
        }
    }
}

