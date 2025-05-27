// TimeDate.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.interrupts.bios;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.Interrupt;
import nz.co.electricbolt.xt.usermode.util.Trace;

public class TimeDate {

    private final long systemTimeClockTicksStart = System.currentTimeMillis();

    @Interrupt(interrupt = 0x1A, function = 0x00, description = "Get system time clock ticks")
    public void getSystemTimeClockTicks(final CPU cpu, final Trace trace) {
        // There are ~18.2 clock ticks/second.
        final long systemTimeClockTicksNow = (System.currentTimeMillis() - systemTimeClockTicksStart) / 182;
        cpu.getReg().CX.setValue((short) ((systemTimeClockTicksNow >>> 16) & 0xFFFF));
        cpu.getReg().DX.setValue((short) (systemTimeClockTicksNow & 0xFFFF));
        cpu.getReg().AL.setValue((byte) 0); // past midnight.
    }
}