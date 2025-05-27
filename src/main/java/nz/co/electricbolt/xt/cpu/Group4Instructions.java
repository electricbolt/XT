// Group4Instructions.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Group4Instructions {

    private final CPU cpu;

    Group4Instructions(final CPU cpu) {
        this.cpu = cpu;
    }

    void decode() {
        final RegRM8 regRM = cpu.modRegRM.fetch8();
        switch (regRM.getRegValue()) {
            case 0: { // INC r/m8 - Increment r/m byte by 1.
                final byte value = regRM.getMem8().getValue();
                final boolean origcarry = cpu.reg.flags.isCarry();
                final byte result = cpu.alu.add8(value, (byte) 1, false);
                regRM.getMem8().setValue(result);
                cpu.reg.flags.setCarry(origcarry);
                break;
            }
            case 1: { // DEC r/m8 - Decrement r/m byte by 1.
                final byte value = regRM.getMem8().getValue();
                final boolean origcarry = cpu.reg.flags.isCarry();
                final byte result = cpu.alu.sub8(value, (byte) 1, false);
                regRM.getMem8().setValue(result);
                cpu.reg.flags.setCarry(origcarry);
                break;
            }
            default:
                cpu.delegate.invalidOpcode("Group 4 operation " + (byte) regRM.getRegValue() + " invalid");
        }
    }
}
