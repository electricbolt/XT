package nz.co.electricbolt.xt.usermode.interrupts.dos;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.Interrupt;

public class Memory {

    @Interrupt(function = 0x4A, description = "Resize memory block")
    public void resizeMemoryBlock(final CPU cpu) {
        // We don't support resizing memory blocks, as we already allocate the entire address space to the app, so
        // we just return success.
        cpu.getReg().flags.setCarry(false);
    }
}
