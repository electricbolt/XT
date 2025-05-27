// Group1Instructions.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Group1Instructions {

    private final CPU cpu;

    Group1Instructions(final CPU cpu) {
        this.cpu = cpu;
    }

    void imm8() {
        final RegRM8 regRM8 = cpu.modRegRM.fetch8();
        final Mem8 mem8 = regRM8.getMem8();
        switch (regRM8.getRegValue()) {
            case 0: // ADD r/m8,imm8 - Add immediate byte to r/m byte.
                mem8.setValue(cpu.alu.add8(mem8.getValue(), cpu.fetch8(), false));
                break;
            case 1: // OR r/m8,imm8 - OR immediate byte to r/m byte.
                mem8.setValue(cpu.alu.or8(mem8.getValue(), cpu.fetch8()));
                break;
            case 2: // ADC r/m8,imm8 - Add with carry immediate byte to r/m byte.
                mem8.setValue(cpu.alu.add8(mem8.getValue(), cpu.fetch8(), cpu.reg.flags.isCarry()));
                break;
            case 3: // SBB r/m8,imm8 - Subtract with borrow immediate byte from r/m byte.
                mem8.setValue(cpu.alu.sub8(mem8.getValue(), cpu.fetch8(), cpu.reg.flags.isCarry()));
                break;
            case 4: // AND r/m8,imm8 - AND immediate byte to r/m byte.
                mem8.setValue(cpu.alu.and8(mem8.getValue(), cpu.fetch8()));
                break;
            case 5: // SUB r/m8,imm8 - Subtract immediate byte from r/m byte.
                mem8.setValue(cpu.alu.sub8(mem8.getValue(), cpu.fetch8(), false));
                break;
            case 6: // XOR r/m8,imm8 - Exclusive-OR immediate byte to r/m byte.
                mem8.setValue(cpu.alu.xor8(mem8.getValue(), cpu.fetch8()));
                break;
            default: // CMP r/m8,imm8 - Compare immediate byte to r/m byte.
                cpu.alu.sub8(mem8.getValue(), cpu.fetch8(), false);
                break;
        }
    }

    void imm16(final boolean signExtendedByte) {
        final RegRM16 regRM16 = cpu.modRegRM.fetch16();
        final Mem16 mem16 = regRM16.getMem16();
        final short imm16 = signExtendedByte ? (short) cpu.fetch8() : cpu.fetch16();
        switch (regRM16.getRegValue()) {
            case 0: // ADD r/m16,imm16/imm8
                mem16.setValue(cpu.alu.add16(mem16.getValue(), imm16, false));
                break;
            case 1: // OR r/m16,imm16/imm8
                mem16.setValue(cpu.alu.or16(mem16.getValue(), imm16));
                break;
            case 2: // ADC r/m16,imm16/imm8
                mem16.setValue(cpu.alu.add16(mem16.getValue(), imm16, cpu.reg.flags.isCarry()));
                break;
            case 3: // SBB r/m16,imm16/imm8
                mem16.setValue(cpu.alu.sub16(mem16.getValue(), imm16, cpu.reg.flags.isCarry()));
                break;
            case 4: // AND r/m16,imm16/imm8
                mem16.setValue(cpu.alu.and16(mem16.getValue(), imm16));
                break;
            case 5: // SUB r/m16,imm16/imm8
                mem16.setValue(cpu.alu.sub16(mem16.getValue(), imm16, false));
                break;
            case 6: // XOR r/m16,imm16/imm8
                mem16.setValue(cpu.alu.xor16(mem16.getValue(), imm16));
                break;
            default: // CMP r/m16,imm16/imm8
                cpu.alu.sub16(mem16.getValue(), imm16, false);
                break;
        }
    }
}
