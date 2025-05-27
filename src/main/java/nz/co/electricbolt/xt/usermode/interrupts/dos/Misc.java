package nz.co.electricbolt.xt.usermode.interrupts.dos;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.cpu.SegOfs;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.DL;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.DS;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.DX;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.Interrupt;
import nz.co.electricbolt.xt.usermode.util.MemoryUtil;

public class Misc {

    @Interrupt(function = 0x33, subfunction = 0x01, description = "Set extended break checking state")
    public void setExtendedBreakChecking(final CPU cpu, final @DL boolean state) {
        // Do nothing, as the user can always terminate the app using ^C.
    }

    @Interrupt(function = 0x37, subfunction = 0x00, description = "Get switch character")
    public void getSwitchCharacter(final CPU cpu) {
        cpu.getReg().AL.setValue((byte) 0x00);
        cpu.getReg().DL.setValue((byte) '/');
    }

    @Interrupt(function = 0x30, description = "Get DOS version")
    public void getDOSVersion(final CPU cpu) {
        cpu.getReg().AX.setValue((short) 0x1606); // Report version DOS 6.22
        cpu.getReg().BX.setValue((short) 0x0);
        cpu.getReg().CX.setValue((short) 0x0);
    }

    @Interrupt(function = 0x38, description = "Get country specific information")
    public void getCountryInformation(final CPU cpu, @DS @DX SegOfs address) {
        MemoryUtil.fill(cpu.getMemory(), address, (short) 0x29, (byte) 0x00);

        // 00h WORD Date format. 0=USA.

        // 02h 5 BYTEs ASCIZ currency symbol string.
        address = new SegOfs(cpu.getReg().DS, (short) (cpu.getReg().DX.getValue() + 0x02));
        cpu.getMemory().writeByte(address, (byte) '$');

        // 07h 2 BYTEs ASCIZ thousands separator.
        address = new SegOfs(cpu.getReg().DS, (short) (cpu.getReg().DX.getValue() + 0x07));
        cpu.getMemory().writeByte(address, (byte) ',');

        // 09h 2 BYTEs ASCIZ decimal separator.
        address = new SegOfs(cpu.getReg().DS, (short) (cpu.getReg().DX.getValue() + 0x09));
        cpu.getMemory().writeByte(address, (byte) '.');

        // 0Bh 2 BYTEs ASCIZ date separator.
        address = new SegOfs(cpu.getReg().DS, (short) (cpu.getReg().DX.getValue() + 0x0B));
        cpu.getMemory().writeByte(address, (byte) '/');

        // 0Dh 2 BYTEs ASCIZ time separator.
        address = new SegOfs(cpu.getReg().DS, (short) (cpu.getReg().DX.getValue() + 0x0D));
        cpu.getMemory().writeByte(address, (byte) ':');

        // 0Fh BYTE currency format. 0=Currency symbol precedes value.

        // 10h 2 BYTE Number of digits.
        address = new SegOfs(cpu.getReg().DS, (short) (cpu.getReg().DX.getValue() + 0x10));
        cpu.getMemory().writeByte(address, (byte) 0x02);

        // 11h BYTE time format. 0=12 hour clock.

        // 12h DWORD address of case map routine (FAR call, AL = character to map to uppercase [>= 80h])

        // 16h ASCIZ data-list separator.
        address = new SegOfs(cpu.getReg().DS, (short) (cpu.getReg().DX.getValue() + 0x16));
        cpu.getMemory().writeByte(address, (byte) ',');

        // 18h 10 BYTEs reserved.
    }
}
