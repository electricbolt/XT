// ProgramRunner.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.cpu.CPUDelegate;
import nz.co.electricbolt.xt.cpu.SegOfs;
import nz.co.electricbolt.xt.usermode.interrupts.Interrupts;
import nz.co.electricbolt.xt.usermode.interrupts.dos.FileIO;
import nz.co.electricbolt.xt.usermode.util.DirectoryTranslation;
import nz.co.electricbolt.xt.usermode.util.MemoryUtil;
import nz.co.electricbolt.xt.usermode.util.Trace;

public class ProgramRunner implements CPUDelegate {

    /*
    Memory layout of .COM executable:

    Linear from - to   Size       Description                             Seg:Ofs
    0x00000 - 0x003FF  1KB        Interrupt vector table.                 0x0000:0x0000
    0x00400 - 0x004FF  256 bytes  BIOS data area.                         0x0040:0x0000
    0x00500 - 0x008FF  1KB        Environment variables.                  0x0050:0x0000
    0x00900 - 0x009FF  256 bytes  Program Segment Prefix for loaded app.  0x0090:0x0000
    0x00A00 - 0x109FF  64KB-256   App+Data+Stack                          0x0090:0x0100-0x0090:0xFFFF
    */

    /*
    Memory layout of .EXE executable:

    Linear from - to   Size       Description                             Seg:Ofs
    0x00000 - 0x003FF  1KB        Interrupt vector table.                 0x0000:0x0000
    0x00400 - 0x004FF  256 bytes  BIOS data area.                         0x0040:0x0000
    0x00500 - 0x008FF  1KB        Environment variables.                  0x0050:0x0000
    0x00900 - 0x009FF  256 bytes  Program Segment Prefix for loaded app.  0x0090:0x0000
    0x00A00 - 0xEFFFF  957.5KB    App.                                    0x00A0:0x0000-0xEFFF:FFFF
    0xF0000 - 0xFFFFF  64KB       Stack.                                  0xF000:0xF000-0xF000:FFFF
    */

    private final String programPath;
    private final CPU cpu;
    private final String commandLine;
    private final Interrupts interrupts;
    private final DirectoryTranslation directoryTranslation;
    private final Trace trace;

    public ProgramRunner(final String programPath, final String commandLine, final String hostWorkingDirectory,
                         final boolean traceCPU, final boolean traceInterrupt, final String traceFile) {
        directoryTranslation = new DirectoryTranslation(hostWorkingDirectory);
        this.programPath = directoryTranslation.emulatedPathToHostPath(programPath);

        this.commandLine = commandLine;

        this.cpu = new CPU(this);
        this.interrupts = new Interrupts();
        this.trace = new Trace(cpu, traceCPU, traceInterrupt, traceFile);
    }

    public void loadAndExecute() {
        final EnvironmentVariables environment = new EnvironmentVariables(cpu.getMemory(), (short) 0x0050, (short) 0x0000);
        environment.writeVariable("PATH", "C:\\");
        environment.writeExecutablePath(directoryTranslation.hostPathToEmulatedPath(programPath));

        final ProgramSegmentPrefix psp = new ProgramSegmentPrefix(cpu.getMemory(), (short) 0x0090, (short) 0x0000);
        psp.writeProgramEnd((short) 0xF000);
        psp.writeEnvironment((short) 0x0050);
        psp.writeCommandLine(commandLine);

        String filename1 = "";
        String filename2 = "";
        if (!commandLine.isEmpty()) {
            String[] files = commandLine.split(" ");
            if (files.length >= 1) {
                filename1 = FileIO.getFilenameFromPath(files[0]).toUpperCase();
                if (files.length >= 2) {
                    filename2 = FileIO.getFilenameFromPath(files[1]).toUpperCase();
                }
            }
        }
        psp.writeFilename(1, 'C', filename1);
        psp.writeFilename(2, 'C', filename2);

        cpu.getReg().AX.setValue((short) 0x0000); // AL,AH = Drive numbers for the first and second FCB.
        cpu.getReg().DS.setValue((short) 0x0090); // Segment for start of PSP.
        cpu.getReg().ES.setValue((short) 0x0090); // Segment for start of PSP.

        final ProgramLoader programLoader = new ProgramLoader(cpu);
        programLoader.load(programPath);

        cpu.execute();
    }

    // region CPUDelegate
    @Override
    public void fetched8(final byte value, final long instructionCount) {
        trace.fetched8(value, instructionCount);
    }

    @Override
    public void fetched16(final short value, final long instructionCount) {
        trace.fetched16(value, instructionCount);
    }

    @Override
    public void interrupt(final byte interrupt) {
        interrupts.execute(cpu, interrupt, trace, directoryTranslation);
        cpu.getReg().IP.setValue(cpu.pop16());
        cpu.getReg().CS.setValue(cpu.pop16());
        cpu.pop16(); // flags should be passed through.
    }

    @Override
    public void halt() {
        trace.log("CPU halted");
        trace.log(cpu.getReg().toString());
        System.err.println("CPU halted");
        System.err.println(cpu.getReg().toString());
        System.exit(255);
    }

    @Override
    public byte portRead8(final short address) {
        return 0;
    }

    @Override
    public void portWrite8(final short address, byte value) {
    }

    @Override
    public short portRead16(final short address) {
        return 0;
    }

    @Override
    public void portWrite16(final short address, short value) {
    }

    /**
     * By default, this method will not be invoked - as all memory is marked read/write/execute.
     * When diagnosing new application behavior, it's useful to observe memory areas being read, written or executed
     * unexpectedly. Uncomment/customize the memory protection code in ProgramLoader.
     */
    @Override
    public void invalidMemoryAccess(final SegOfs memoryAddress, final byte permissionMask) {
        final String message = String.format("Invalid memory access %s - %s%n",
                cpu.getMemory().fromBitmask(permissionMask), memoryAddress.toString());
        trace.log(message);
        trace.log(cpu.getReg().toString());
        System.err.printf(message);
        System.err.println(cpu.getReg().toString());
        MemoryUtil.dump(cpu.getMemory(), memoryAddress);
        System.exit(255);
    }

    @Override
    public void invalidOpcode(final String message) {
        trace.log(message);
        trace.log(cpu.getReg().toString());
        System.err.println(message);
        System.err.println(cpu.getReg().toString());
        System.exit(255);
    }
    // endregion
}
