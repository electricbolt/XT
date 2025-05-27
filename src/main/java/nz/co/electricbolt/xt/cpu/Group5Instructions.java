// Group5Instructions.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Group5Instructions {

    private final CPU cpu;

    Group5Instructions(final CPU cpu) {
        this.cpu = cpu;
    }

    void decode() {
        final RegRM16 regRM = cpu.modRegRM.fetch16();
        switch (regRM.getRegValue()) {
            case 0: { // INC r/m16 - Increment r/m word by 1.
                final boolean origcarry = cpu.reg.flags.isCarry();
                final short value = regRM.getMem16().getValue();
                final short result = cpu.alu.add16(value, (short) 1, false);
                regRM.getMem16().setValue(result);
                cpu.reg.flags.setCarry(origcarry);
                break;
            }
            case 1: { // DEC r/m16 - Decrement r/m word by 1.
                final boolean origcarry = cpu.reg.flags.isCarry();
                final short value = regRM.getMem16().getValue();
                final short result = cpu.alu.sub16(value, (short) 1, false);
                regRM.getMem16().setValue(result);
                cpu.reg.flags.setCarry(origcarry);
                break;
            }
            case 2: { // CALL r/m16 - Call near, register indirect/memory indirect.
                final short value = regRM.getMem16().getValue();
                cpu.push16(cpu.reg.IP.getValue());
                cpu.reg.IP.setValue(value);
                break;
            }
            case 3: { // CALL m16:16 - Call intersegment address at r/m dword.
                SegOfs segOfs = regRM.getMem16().getSegOfs();
                final short offset = cpu.memory.readWord(segOfs);
                segOfs.addOffset((short) 2);
                final short segment = cpu.memory.readWord(segOfs);
                cpu.push16(cpu.reg.CS.getValue());
                cpu.push16(cpu.reg.IP.getValue());
                cpu.reg.IP.setValue(offset);
                cpu.reg.CS.setValue(segment);
                break;
            }
            case 4: { // JMP r/m16 - Jump near indirect.
                final short value = regRM.getMem16().getValue();
                cpu.reg.IP.setValue(value);
                break;
            }
            case 5: { // JMP m16:16 - Jump r/m16:16 indirect and intersegment.
                SegOfs segOfs = regRM.getMem16().getSegOfs();
                final short offset = cpu.memory.readWord(segOfs);
                segOfs.addOffset((short) 2);
                final short segment = cpu.memory.readWord(segOfs);
                cpu.reg.IP.setValue(offset);
                cpu.reg.CS.setValue(segment);
                break;
            }
            case 6: { // PUSH m16 - Push memory word. (Also appears to be PUSH r/m16 - can be a register like AX).
                if (regRM.getMem16().getSegOfs() != null) {
                    final SegOfs segOfs = regRM.getMem16().getSegOfs();
                    final short value = cpu.memory.readWord(segOfs);
                    cpu.push16(value);
                } else {
                    final short value = regRM.getMem16().getValue();
                    if (regRM.getMem16().getReg() == cpu.reg.SP) {
                        // Push new value of SP instead of what SP was.
                        cpu.push16((short) ((short) (value - 2) & 0xFFFF));
                    } else {
                        cpu.push16(value);
                    }
                }
                break;
            }
            default:
                cpu.delegate.invalidOpcode("Group5 operation 7 invalid");
        }
    }
}
