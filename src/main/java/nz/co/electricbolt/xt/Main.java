// Main.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt;

import nz.co.electricbolt.xt.usermode.ProgramRunner;
import nz.co.electricbolt.xt.usermode.interrupts.Interrupts;

import java.io.File;

public class Main {

    private boolean traceCPU = false;
    private boolean traceInterrupt = false;
    private String traceFile = "";
    String emulatedProgramPath = "";
    String emulatedProgramArgs = "";
    String hostWorkingDir = "";
    private final CommandLineParser commandLine;

    private Main(final CommandLineParser commandLine) {
        this.commandLine = commandLine;
    }

    private void printAppVersion() {
        System.out.println("XT version 1.0.0; Copyright © 2025; Electric Bolt Limited.");
    }

    private void haltSyntax(final String message) {
        printAppVersion();
        System.out.println("Syntax:        xt help [run|int]");
        System.out.println("               xt run [options] program [program-args]");
        System.out.println("               xt int");

        if (!message.isEmpty()) {
            System.out.println();
            System.out.println("error: " + message);
        }

        System.exit(255);
    }

    private void haltSyntaxRun(final String message) {
        printAppVersion();
        System.out.println("Syntax:        xt run [-tc -ti file] [-c dir] program [program-args]");
        System.out.println("               Run a .EXE or .COM command line MS-DOS app on your host system.");
        System.out.println("-tc -ti file = Trace CPU and/or interrupts to the tracing host file specified.");
        System.out.println("-c dir       = The host directory that will be the root of the emulated C: drive");
        System.out.println("               If not specified then the current working directory will be used.");
        System.out.println("program      = The .EXE or .COM command line MS-DOS app you want to run. You can");
        System.out.println("               optionally prefix with emulated path.");
        System.out.println("program-args = Optional arguments for the MS-DOS app, max 127 characters.");
        System.out.println();
        System.out.println("The exit code will be 255 if XT terminates the program due to an error,");
        System.out.println("otherwise the exit code will be the exit code of the MS-DOS app.");

        if (!message.isEmpty()) {
            System.out.println();
            System.out.println("error: " + message);
        }

        System.exit(255);
    }

    private void haltSyntaxInt() {
        printAppVersion();
        System.out.println("Syntax:        xt int");
        System.out.println("               Output the list of interrupts that are implemented by this");
        System.out.println("               version of XT for the run command.");
        System.exit(255);
    }

    private void run() {
        if (hostWorkingDir.isEmpty()) {
            hostWorkingDir = System.getProperty("user.dir");
        }
        if (!hostWorkingDir.endsWith(File.separator)) {
            hostWorkingDir += File.separator;
        }

        final ProgramRunner runner = new ProgramRunner(emulatedProgramPath, emulatedProgramArgs, hostWorkingDir,
                traceCPU, traceInterrupt, traceFile);
        runner.loadAndExecute();
    }

    private void parseInt() {
        final Interrupts interrupts = new Interrupts();
        interrupts.printInterrupts();
        System.exit(255);
    }

    private void parseTraceInterrupt() {
        traceInterrupt = true;
        final String argument = commandLine.next();
        if (argument == null) {
            haltSyntaxRun("expecting tracing host file argument");
        } else if (argument.startsWith("-")) {
            haltSyntaxRun(argument + " argument not recognized");
        } else {
            traceFile = argument;
        }
    }

    private void parseTraceCPU() {
        traceCPU = true;
        final String argument = commandLine.next();
        if (argument == null) {
            haltSyntaxRun("expecting -ti or tracing host file argument");
        } else if (argument.equals("-ti")) {
            parseTraceInterrupt();
        } else if (argument.startsWith("-")) {
            haltSyntaxRun(argument + " argument not recognized");
        } else {
            traceFile = argument;
        }
    }

    private void parseTraceOptions() {
        final String argument = commandLine.next();
        if (argument.equals("-tc")) {
            parseTraceCPU();
        } else if (argument.equals("-ti")) {
            parseTraceInterrupt();
        } else {
            haltSyntaxRun(argument + " argument not recognized");
        }
    }

    private void parseRootDirectory() {
        commandLine.next(); // skip over -c argument.
        String argument = commandLine.next();
        if (argument == null) {
            haltSyntaxRun("expecting host root directory argument");
        } else if (argument.startsWith("-")) {
            haltSyntaxRun("expecting host root directory argument");
        } else {
            hostWorkingDir = argument;
        }
    }

    private void parseRun() {
        if (!commandLine.hasNext()) {
            haltSyntaxRun("expecting program argument");
        }

        // Options.
        String argument = commandLine.peek();
        if (argument.startsWith("-t")) {
            parseTraceOptions();
        }

        if (!commandLine.hasNext()) {
            haltSyntaxRun("expecting program argument");
        }

        argument = commandLine.peek();
        if (argument.equals("-c")) {
            parseRootDirectory();
        }

        if (!commandLine.hasNext()) {
            haltSyntaxRun("expecting program argument");
        }

        // Program (mandatory).
        argument = commandLine.next();
        if (argument.startsWith("-")) {
            haltSyntaxRun(argument + " not recognized");
        } else {
            emulatedProgramPath = argument;
        }

        // Program args (optional).
        final StringBuilder buf = new StringBuilder();
        while (commandLine.hasNext()) {
            if (!buf.isEmpty()) {
                buf.append(' ');
            }
            buf.append(commandLine.next());
        }
        emulatedProgramArgs = buf.toString();

        run();

        System.exit(255);
    }

    private void parseHelp() {
        if (commandLine.hasNext()) {
            final String argument = commandLine.next();
            switch (argument) {
                case "run" -> haltSyntaxRun("");
                case "int" -> haltSyntaxInt();
                default -> haltSyntax(argument + " not recognized");
            }
        }
        haltSyntax("");
    }

    private void parse() {
        if (commandLine.hasNext()) {
            final String argument = commandLine.next();
            switch (argument) {
                case "help" -> parseHelp();
                case "int" -> parseInt();
                case "run" -> parseRun();
                default -> haltSyntax(argument + " not recognized");
            }
        }
        haltSyntax("");
    }

    public static void main(String[] args) {
        final Main main = new Main(new CommandLineParser(args));
        main.parse();
    }
}
