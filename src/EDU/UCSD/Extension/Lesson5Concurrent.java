package EDU.UCSD.Extension;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
class Resource {
    private String filename;

    public Resource(String filename) {
        this.filename = filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return an integer of type long representing the number of characters in a file
     * @throws IOException
     */
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

/**
 *
 */
class AtomicThread implements Runnable {
    private Resource resource;

    /**
     * @param resource an object of type Resource
     */
    public AtomicThread(Resource resource) {
        this.resource = resource;
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

/**
 *
 */
class NoLockThread implements Runnable {
    private Resource resource;

    private long count;

    /**
     * @param resource
     */
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

/**
 *
 */
class ReentrantThread implements Runnable {
    private Resource resource;
    private Lock lock;
    private long count;

    /**
     * @param resource
     */
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

/*
 * The  Lesson5Concurrent program  accepts a number of commandline options which
 * it  uses  to  determine  how  it  will  count  the  characters  in  2  files,
 * simultaneously.  The  commandline options  are "--help", "--num-threads", "--
 * ReentrantLock" and "--AtomicLong".
 *
 * The  Lesson5Concurrent program's  commandline options may be specified in any
 * order.  Of  the four  options,  only  the  "--num-threads" (i.e.,  number  of
 * threads) option is required. It is used to specify the number of threads that
 * Lesson5Concurrent    will   use   when   running   each   process.   Although
 * Lesson5Concurrent  imposes no restriction on the magnitude of this parameter,
 * the  architecture  on which the program  runs might impose a practical limit.
 * For this exercise, only the numbers 8 and 16 were tested.
 *
 * The  "--num-threads" option  requires a numeric argument, which specifies the
 * number  of threads  to create.  When the  num-threads option's  corresponding
 * argument  is missing  in  action, an  error is  generated  and the  following
 * message is displayed:
 *
 * The "--num-threads" option requires a numeric argument.
 *
 * Although  the  commandline options may  be specified  in any number, order or
 * combination,   the  "--ReentrantLock"  and  the  "--AtomicLong"  options  are
 * mutually  exclusive.  These two options  specify the "locking mechanism" that
 * the  program  uses when running concurrent processes. Since Lesson5Concurrent
 * can use only one locking strategy at a time, it doesn't make sense to specify
 * both.  However, in  the event  that both  options appear  on the  commandline
 * together,  the  last one in  the sequence (reading  from left to right) takes
 * precedence. All other locking options will be ignored.
 *
 * When  invoked  with  the  "--help"  option,  Lesson5Concurrent  displays  the
 * following syntax diagram:
 * <pre>
 * Usage: Lesson5Concurrent [--help]            # Displays this command syntax summary
 *        Lesson5Concurrent {--num-threads <n>} # Specifies the number of threads to create
 *        Lesson5Concurrent [--ReentrantLock]   # Use locking. By default, locking is not used.
 *        Lesson5Concurrent [--AtomicLong]      # Use locking. By default, locking is not used.
 * </pre>
 * When  the "--help" option appears on the commandline, only the syntax diagram
 * is  displayed. All  other options  are ignored  (i.e., skipped)  and no  file
 * processing occurs.
 *
 * Invoking  the Lesson5Concurrent  program without any options will generate an
 * error and result in the display of the syntax diagram.
 *
 * When the "--ReentrantLock" option is specified, the Lesson5Concurrent program
 * attempts  to use  a reentrant locking strategy to manage conflicts and ensure
 * the integrity of the results.
 *
 * When  the  "--AtomicLong" option  is specified, the Lesson5Concurrent program
 * uses  as  an accumulator,  an  instance  of  the  AtomicLong type  to  manage
 * conflicts and ensure the integrity of the results.
 *
 * When  neither of  these locking options is specified, no locking mechanism is
 * used.
 *
 * Contrary  to the original specification, this test suite uses a different set
 * of  input files. The reason for making the change was to ensure that the size
 * of  the  result set generated  could adequately demonstrate the concepts. The
 * files  used for testing are named "Data4.txt" and "Data5.txt" and are located
 * in this project's root directory. The Lesson5Concurrent.processFiles() method
 * accepts two String arguments containing the names of the files to process. It
 * is  invoked once  near the end of the  source code file. At the time this was
 * written,  processFiles appeared  on line 339. To change the input files used,
 * move them to the project's root directory and modify the call to processFiles
 * on line 408.
 *
 * The  source code for the five classes comprising this program is contained in
 * this file.
 */

public class Lesson5Concurrent {

    private int numberOfThreads;
    public static long count = 0;
    public static AtomicLong atomicLong = new AtomicLong();

    private enum MODE {REENTRANT, NOLOCKING, ATOMIC}

    private MODE mode = MODE.NOLOCKING;

    /**
     * @param numberOfThreads
     */
    public Lesson5Concurrent(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    /**
     * @param numberOfThreads
     * @param mode
     */
    public Lesson5Concurrent(int numberOfThreads, MODE mode) {
        this.numberOfThreads = numberOfThreads;
        this.mode = mode;
    }

    /**
     * @param count
     */
    public static void setCount(long count) {
        Lesson5Concurrent.count += count;
    }

    /**
     * @return
     */
    public static long getCount() {
        return count;
    }

    /**
     * @param numberOfThreads
     */
    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    /**
     * @return
     */
    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    /**
     * @param mode
     */
    public void setMode(MODE mode) {
        this.mode = mode;
    }

    /**
     *
     */
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

    /**
     * @throws InterruptedException
     */
    public void processFiles(String filename1, String filename2) throws InterruptedException {
        // Just how many processes are we talkin' about?
        System.out.printf("%nThis system has %d processors.%n%n", Runtime.getRuntime().availableProcessors());
        ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
        switch (mode) {
            case REENTRANT:
                ReentrantThread reentrantThread1 = new ReentrantThread(new Resource(filename1));
                ReentrantThread reentrantThread2 = new ReentrantThread(new Resource(filename2));
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
                NoLockThread noLockThread1 = new NoLockThread(new Resource(filename1));
                NoLockThread noLockThread2 = new NoLockThread(new Resource(filename2));
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
                AtomicThread atomicThread1 = new AtomicThread(new Resource(filename1));
                AtomicThread atomicThread2 = new AtomicThread(new Resource(filename2));
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

    /**
     * @return
     */
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
                    System.exit(0);
                    // break;
                case "--num-threads":
                    try {
                        String argument = options[++index];
                        numberOfThreads = Integer.parseInt(argument);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                        // I know! Modifying the control variable from within the loop is a no-no!
                        System.err.printf("%nThe \"%s\" option requires a numeric argument.%n", options[--index]);
                        Lesson5Concurrent.syntaxSummary();
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
            lesson5Concurrent.processFiles("Data4.txt", "Data5.txt");
            System.out.println(lesson5Concurrent);
        }
    }
}
