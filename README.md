# Lesson-5-Assignment

The Lesson5Concurrent program accepts a number of commandline options which it uses to determine how it will count the characters in 2 files simultaneously. The commandline options are "--help", "--num-threads", "--ReentrantLock" and "--AtomicLong".

The Lesson5Concurrent program's commandline options may be specified in any order. Of the four options, only the "--num-threads" (i.e., number of threads) option is required. It is used to specify the number of threads that Lesson5Concurrent will use when running each process. Although Lesson5Concurrent imposes no restriction on the magnitude of this parameter, the architecture on which the program runs might impose a practical limit. For this exercise, only the numbers 8 and 16 were tested.

The "--num-threads" option requires a numeric argument, which specifies the number of threads to create. When the num-threads option's corresponding argument is missing in action, an error is generated and the following message is displayed:
```
The "--num-threads" option requires a numeric argument.
```
Although the commandline options may be specified in any number, order or combination, the "--ReentrantLock" and the "--AtomicLong" options are mutually exclusive. These two options specify the "locking mechanism" that the program uses when running concurrent processes. Since Lesson5Concurrent can use only one locking strategy at a time, it doesn't make sense to specify both. However, in the event that both options appear on the commandline together, the last one in the sequence (reading from left to right) takes precedence. All other locking options will be ignored.

When invoked with the "--help" option, Lesson5Concurrent displays the following syntax diagram:
```
Usage: Lesson5Concurrent [--help]            # Displays this command syntax summary
       Lesson5Concurrent {--num-threads <n>} # Specifies the number of threads to create
       Lesson5Concurrent [--ReentrantLock]   # Use locking. By default, locking is not used.
       Lesson5Concurrent [--AtomicLong]      # Use locking. By default, locking is not used.
```

The  presence of the "--help" option on the command line supersedes all other
options.  When the  command  line  includes the  "--help"  option, all  other
options  will  be  ignored.  Only  the "--help"  option  will  be  processed.

Invoking the Lesson5Concurrent program without any options will generate an error and result in the display of the syntax diagram.

When the "--ReentrantLock" option is specified, the Lesson5Concurrent program attempts to use a reentrant locking strategy to manage conflicts and ensure the integrity of the results.

When the "--AtomicLong" option is specified, the Lesson5Concurrent program uses as an accumulator, an instance of the AtomicLong type to manage conflicts and ensure the integrity of the results.

When neither of these locking options is specified, no locking mechanism is used.

Contrary to the original specification, this test suite uses a different set of input files. The reason for making the change was to ensure that the size of the result set generated could adequately demonstrate the concepts. The files used for testing are named "Data4.txt" and "Data5.txt" and are located in this project's root directory. The Lesson5Concurrent.processFiles() method accepts two String arguments containing the names of the files to process. It is invoked once near the end of the source code file. At the time this was written, processFiles appeared on line 409. To change the input files used, move them to the project's root directory and modify the call to processFiles() on line 409.

The source code for the five classes comprising this program is contained in a single file named Lesson5Concurrent.java.
