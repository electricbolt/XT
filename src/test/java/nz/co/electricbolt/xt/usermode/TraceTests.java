// TraceTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.cpu.CPUDelegate;
import nz.co.electricbolt.xt.cpu.SegOfs;
import nz.co.electricbolt.xt.usermode.util.Trace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TraceTests implements CPUDelegate {

    static File tempFile;

    @BeforeEach
    public void setup() throws IOException {
        tempFile = File.createTempFile("XT_", "_XT");
    }

    @AfterEach
    public void teardown() {
        boolean ignored = tempFile.delete();
    }

    @Test
    public void traceNotExist() {
        final CPU cpu = new CPU(this);
        final Trace trace = new Trace(cpu, false, false, null);
        trace.log("Should not be logged");
        trace.interrupt("Interrupt message");
        trace.fetched8((byte) 0xAB, 24651);

        System.out.println("Temp file: " + tempFile.getAbsolutePath() + " (" + tempFile.length() + " bytes)");
        assertEquals(0, tempFile.length());
    }

    @Test
    public void traceCPU() throws IOException {
        final CPU cpu = new CPU(this);
        final Trace trace = new Trace(cpu, true, false, tempFile.getAbsolutePath());
        trace.log("Log message");
        trace.interrupt("Interrupt message");
        trace.fetched8((byte) 0xAB, 24651);
        trace.flush();

        assertEquals(139, tempFile.length());

        final FileInputStream fis = new FileInputStream(tempFile);
        final InputStreamReader isr = new InputStreamReader(fis);
        final BufferedReader br = new BufferedReader(isr);
        assertEquals("Log message", br.readLine());
        assertEquals("24651        AB CS=F000 IP=FFF0 FLAGS= AX=0000 BX=0000 CX=0000 DX=0000 DS=0000 SI=0000 ES=0000 DI=0000 SS=0000 SP=0000 BP=0000", br.readLine());
    }

    @Test
    public void traceInterrupts() throws IOException {
        final CPU cpu = new CPU(this);
        final Trace trace = new Trace(cpu, false, true, tempFile.getAbsolutePath());
        trace.log("Log message");
        trace.interrupt("Interrupt message");
        trace.fetched8((byte) 0xAB, 24651);
        trace.flush();

        assertEquals(30, tempFile.length());

        final FileInputStream fis = new FileInputStream(tempFile);
        final InputStreamReader isr = new InputStreamReader(fis);
        final BufferedReader br = new BufferedReader(isr);
        assertEquals("Log message", br.readLine());
        assertEquals("Interrupt message", br.readLine());
    }

    @Test
    public void traceBoth() throws IOException {
        final CPU cpu = new CPU(this);
        final Trace trace = new Trace(cpu, true, true, tempFile.getAbsolutePath());
        trace.log("Log message");
        trace.interrupt("Interrupt message");
        trace.fetched8((byte) 0xAB, 24651);
        trace.flush();

        assertEquals(157, tempFile.length());

        final FileInputStream fis = new FileInputStream(tempFile);
        final InputStreamReader isr = new InputStreamReader(fis);
        final BufferedReader br = new BufferedReader(isr);
        assertEquals("Log message", br.readLine());
        assertEquals("Interrupt message", br.readLine());
        assertEquals("24651        AB CS=F000 IP=FFF0 FLAGS= AX=0000 BX=0000 CX=0000 DX=0000 DS=0000 SI=0000 ES=0000 DI=0000 SS=0000 SP=0000 BP=0000", br.readLine());
    }

    @Override
    public void fetched8(final byte value, final long instructionCount) {
    }

    @Override
    public void fetched16(final short value, final long instructionCount) {
    }

    @Override
    public void interrupt(final byte value) {
    }

    @Override
    public void halt() {
    }

    @Override
    public byte portRead8(final short address) {
        return 0;
    }

    @Override
    public void portWrite8(final short address, final byte value) {
    }

    @Override
    public short portRead16(final short address) {
        return 0;
    }

    @Override
    public void portWrite16(final short address, final short value) {

    }

    @Override
    public void invalidMemoryAccess(final SegOfs memoryAddress, final byte permissionMask) {
    }

    @Override
    public void invalidOpcode(final String message) {
    }
}