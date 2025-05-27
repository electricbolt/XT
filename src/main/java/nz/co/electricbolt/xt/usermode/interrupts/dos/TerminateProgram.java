package nz.co.electricbolt.xt.usermode.interrupts.dos;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.AL;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.Interrupt;

public class TerminateProgram {

    @Interrupt(interrupt = 0x20, function = 0x00, description = "Terminate program")
    public void terminate1(final CPU cpu) {
        System.exit(0);
    }

    @Interrupt(function = 0x00, description = "Terminate program")
    public void terminate2(final CPU cpu) {
        System.exit(0);
    }

    @Interrupt(function = 0x4C, description = "Terminate program with exit code")
    public void terminate3(final CPU cpu, final @AL byte exitCode) {
        System.exit(cpu.getReg().AL.getValue());
    }
}
