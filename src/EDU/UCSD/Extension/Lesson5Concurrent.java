package EDU.UCSD.Extension;

public class Lesson5Concurrent {


    public void syntaxSummary() {
        var commandName = getClass().getSimpleName();
        System.out.printf("%n%-7s%-20s%-20s%s%n%-7s%-20s%-20s%s%n%-7s%-20s%-20s%s%n%-7s%-20s%-20s%s%n",
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

    public static void main(String[] arguments) {
        Lesson5Concurrent lesson5Concurrent = new Lesson5Concurrent();
        String argument;
        try {
            argument = arguments[0];
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            System.err.println("\nOops, a required option is missing!");
            lesson5Concurrent.syntaxSummary();
            System.exit(1);
        }
    }
}
