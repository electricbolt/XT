package nz.co.electricbolt.xt.usermode.interrupts.dos;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.*;

public class ConsoleIO {

    @Interrupt(function = 0x02, description = "Write character to standard output")
    public void writeCharacter(final CPU cpu, final @DL char c) {
        System.out.print(c);
    }

    @Interrupt(function = 0x09, description = "Write string to standard output")
    public void writeString(final CPU cpu, final @ASCIZ(terminationChar = '$') @DS @DX String s) {
        System.out.print(s);
        cpu.getReg().AL.setValue((byte) '$');
    }
}
