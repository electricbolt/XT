package nz.co.electricbolt.xt.usermode.interrupts.dos;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.Interrupt;

import java.util.Calendar;

public class TimeDate {

    @Interrupt(function = 0x2A, description = "Get system date")
    public void getSystemDate(final CPU cpu) {
        final Calendar calendar = Calendar.getInstance();
        cpu.getReg().CX.setValue((short) calendar.get(Calendar.YEAR)); // 1980-2099.
        cpu.getReg().DH.setValue((byte) calendar.get(Calendar.MONTH));
        cpu.getReg().DL.setValue((byte) calendar.get(Calendar.DAY_OF_MONTH));
        cpu.getReg().AL.setValue((byte) (calendar.get(Calendar.DAY_OF_WEEK) - 1)); // 00h=Sunday.
    }

    @Interrupt(function = 0x2C, description = "Get system time")
    public void getSystemTime(final CPU cpu) {
        final Calendar calendar = Calendar.getInstance();
        cpu.getReg().CH.setValue((byte) calendar.get(Calendar.HOUR_OF_DAY));
        cpu.getReg().CL.setValue((byte) calendar.get(Calendar.MINUTE));
        cpu.getReg().DH.setValue((byte) calendar.get(Calendar.SECOND));
        cpu.getReg().DL.setValue((byte) (calendar.get(Calendar.MILLISECOND) * 10));
    }
}
