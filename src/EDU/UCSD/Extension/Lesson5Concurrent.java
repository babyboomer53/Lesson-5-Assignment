package EDU.UCSD.Extension;

public class Lesson5Concurrent {

    private int numberOfThreads;
    public static long count = 0;

    private enum MODE {REENTRANT, UNLOCKED, ATOMIC}

    private MODE mode = MODE.UNLOCKED;

    public Lesson5Concurrent(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public Lesson5Concurrent(int numberOfThreads, MODE mode) {
        this.numberOfThreads = numberOfThreads;
        this.mode = mode;
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

    @Override
    public String toString() {
        return String.format("%nObject of: %s%nNumber of threads: %d%nMode: %s%n",
                this.getClass().getSimpleName(),
                this.getNumberOfThreads(),
                this.mode);
    }

    public static void main(String[] options) {
        String option = null;
        MODE mode = MODE.UNLOCKED;
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
                    } catch (IndexOutOfBoundsException | NumberFormatException exception) {
                        System.err.printf("%nThe \"%s\" option requires a numeric argument.%n", options[index]);
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
            Lesson5Concurrent lesson5Concurrent = new Lesson5Concurrent(numberOfThreads, mode);
            System.out.println(lesson5Concurrent);
        }
    }
}
