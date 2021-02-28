package EDU.UCSD.Extension;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
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

class AtomicThread implements Runnable {
    private Resource resource;

    private long count;

    public AtomicThread(Resource resource) {
        this.resource = resource;
    }

    public long getCount() {
        return count;
    }

    @Override
    public void run() {
        System.out.printf("%s starting…%n", this.getClass().getSimpleName());
        try {
            var l = Lesson5Concurrent.atomicLong.addAndGet(resource.countTheCharacters());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
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
        System.out.printf("%s starting…%n", this.getClass().getSimpleName());
        try {
            Lesson5Concurrent.setCount(resource.countTheCharacters());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}

class ReentrantThread implements Runnable {
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
        System.out.printf("%s starting…%n", this.getClass().getSimpleName());
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                Lesson5Concurrent.setCount(resource.countTheCharacters());
            }
        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace();

        } finally {
            //release lock
            lock.unlock();
        }
    }
}

public class Lesson5Concurrent {

    private int numberOfThreads;
    public static long count = 0;
    public static AtomicLong atomicLong = new AtomicLong();

    private enum MODE {REENTRANT, NOLOCKING, ATOMIC}

    private MODE mode = MODE.NOLOCKING;

    public Lesson5Concurrent(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public Lesson5Concurrent(int numberOfThreads, MODE mode) {
        this.numberOfThreads = numberOfThreads;
        this.mode = mode;
    }

    public static void setCount(long count) {
        Lesson5Concurrent.count += count;
    }

    public static long getCount() {
        return count;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public static void syntaxSummary() {
        var commandName = "Lesson5Concurrent";
        System.out.printf("%n%-7s%-18s%-20s%s%n%-7s%-18s%-20s%s%n%-7s%-18s%-20s%s%n%-7s%-18s%-20s%s%n",
                "Usage:",
                commandName,
                "[--help]",
                "# Displays this command syntax summary",
                "",
                commandName,
                "{--num-threads <n>}",
                "# Specifies the number of threads to create",
                "",
                commandName,
                "[--ReentrantLock]",
                "# Use locking. By default, locking is not used.",
                "",
                commandName,
                "[--AtomicLong]",
                "# Use locking. By default, locking is not used.");
    }

    public void processFiles() throws InterruptedException {
        // Just how many processes are we talkin' about?
        System.out.printf("%nThis system has %d processors.%n%n", Runtime.getRuntime().availableProcessors());
        ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
        switch (mode) {
            case REENTRANT:
                ReentrantThread reentrantThread1 = new ReentrantThread(new Resource("Data4.txt"));
                ReentrantThread reentrantThread2 = new ReentrantThread(new Resource("Data5.txt"));
                Runnable runnable1 = reentrantThread1;
                Runnable runnable2 = reentrantThread2;
                Thread thread1 = new Thread(runnable1);
                Thread thread2 = new Thread(runnable2);
                thread1.start();
                thread2.start();
                if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                    // pool.shutdown();
                    System.out.println("\nThe combined character count is " + count);
                }
                break;
            case NOLOCKING:
                NoLockThread noLockThread1 = new NoLockThread(new Resource("Data4.txt"));
                NoLockThread noLockThread2 = new NoLockThread(new Resource("Data5.txt"));
                Runnable runnable3 = noLockThread1;
                Runnable runnable4 = noLockThread2;
                Thread thread3 = new Thread(runnable3);
                Thread thread4 = new Thread(runnable4);
                thread3.start();
                thread4.start();
                if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                    // pool.shutdown();
                    System.out.println("\nThe combined character count is " + count);
                }
                break;
            case ATOMIC:
                AtomicThread atomicThread1 = new AtomicThread(new Resource("Data4.txt"));
                AtomicThread atomicThread2 = new AtomicThread(new Resource("Data5.txt"));
                Runnable runnable5 = atomicThread1;
                Runnable runnable6 = atomicThread2;
                Thread thread5 = new Thread(runnable5);
                Thread thread6 = new Thread(runnable6);
                thread5.start();
                thread6.start();
                if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                    // pool.shutdown();
                    System.out.println("\nThe combined character count is " + Lesson5Concurrent.atomicLong.get());
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
        System.out.println("\nIf you scroll through the output, you'll notice the interleaving of the\n" +
                "messages as the system alternated between the threads.");
    }

    @Override
    public String toString() {
        return String.format("%nObject of: %s%nNumber of threads: %d%nMode: %s%n",
                this.getClass().getSimpleName(),
                this.getNumberOfThreads(),
                this.mode);
    }

    public static void main(String[] options) throws InterruptedException {
        String option = null;
        MODE mode = MODE.NOLOCKING;
        int numberOfThreads = 0;

        try {
            option = options[0];
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            System.err.println("\nOops, a required option is missing!");
            Lesson5Concurrent.syntaxSummary();
            System.exit(1);
        }
        // Process the commandline options…
        for (int index = 0; index < options.length; index++) {
            switch (options[index]) {
                case "--help":
                    Lesson5Concurrent.syntaxSummary();
                    break;
                case "--num-threads":
                    try {
                        String argument = options[++index];
                        numberOfThreads = Integer.parseInt(argument);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                        System.err.printf("%nThe \"%s\" option requires a numeric argument.%n", options[--index]);
                        System.exit(1);
                    }
                    break;
                case "--ReentrantLock":
                    mode = MODE.REENTRANT;
                    break;
                case "--AtomicLong":
                    mode = MODE.ATOMIC;
                    break;
                default:
                    System.err.printf("%n\"%s\" is not a valid option!%n", options[index]);
                    Lesson5Concurrent.syntaxSummary();
                    System.exit(1);
            }
        }

        if (numberOfThreads == 0) {
            System.err.println("\nThe \"--num-threads\" option is required!");
            Lesson5Concurrent.syntaxSummary();
        } else {
            // Instantiate a Lesson5Concurrent object and pass it the number of
            // threads and the mode as specified on the commandline.
            Lesson5Concurrent lesson5Concurrent = new Lesson5Concurrent(numberOfThreads, mode);
            // Execute the Lesson5Concurrent object's processFiles method using
            // it's current state.
            lesson5Concurrent.processFiles();
            System.out.println(lesson5Concurrent);
        }
    }
}
