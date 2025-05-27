package nz.co.electricbolt.xt.usermode.interrupts.dos;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.cpu.SegOfs;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.AL;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.Interrupt;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.DS;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.DX;

public class InterruptVectors {

    @Interrupt(function = 0x25, description = "Set interrupt vector")
    public void setInterruptVector(final CPU cpu, final @AL byte interrupt, final @DS @DX SegOfs address) {
        cpu.getMemory().setWord(new SegOfs((short) 0, (short) ((4 * (interrupt & 0xFF)) + 2)), address.getSegment());
        cpu.getMemory().setWord(new SegOfs((short) 0, (short) (4 * (interrupt & 0xFF))), address.getOffset());
    }

    @Interrupt(function = 0x35, description = "Get interrupt vector")
    public void getInterruptVector(final CPU cpu) {
        final byte interrupt = cpu.getReg().AL.getValue();
        cpu.getReg().ES.setValue((cpu.getMemory().getWord(new SegOfs((short) 0, (short) ((4 * (interrupt & 0xFF)) + 2)))));
        cpu.getReg().BX.setValue((cpu.getMemory().getWord(new SegOfs((short) 0, (short) (4 * (interrupt & 0xFF))))));
    }
}
