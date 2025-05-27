// Trace.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.util;

import nz.co.electricbolt.xt.cpu.CPU;

import java.io.BufferedWriter;
import java.io.IOException;

public class Trace {

    private final CPU cpu;
    private final boolean traceCPU;
    private final boolean traceInterrupts;
    private BufferedWriter traceWriter;

    public Trace(final CPU cpu, final boolean traceCPU, final boolean traceInterrupts, final String traceFile) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (this.traceWriter != null) {
                    this.traceWriter.close();
                }
            } catch (IOException ignored) {
            }
        }));

        this.cpu = cpu;
        this.traceCPU = traceCPU;
        this.traceInterrupts = traceInterrupts;
        try {
            if (traceCPU || traceInterrupts) {
                this.traceWriter = new BufferedWriter(new java.io.FileWriter(traceFile));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(255);
        }
    }

    public void fetched8(final byte value, final long instructionCount) {
        if (traceCPU) {
            try {
                traceWriter.append(String.format("%-10d   %02X %s\n", instructionCount, value, cpu.getReg()));
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(255);
            }
        }
    }

    public void fetched16(final short value, final long instructionCount) {
        if (traceCPU) {
            try {
                traceWriter.append(String.format("%-10d %04X %s\n", instructionCount, value, cpu.getReg()));
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(255);
            }
        }
    }

    public void interrupt(final String message) {
        if (traceInterrupts) {
            try {
                traceWriter.append(message).append("\n");
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(255);
            }
        }
    }

    public void log(final String message) {
        if (traceCPU || traceInterrupts) {
            try {
                traceWriter.append(message).append("\n");
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(255);
            }
        }
    }

    public void flush() {
        try {
            traceWriter.flush();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
