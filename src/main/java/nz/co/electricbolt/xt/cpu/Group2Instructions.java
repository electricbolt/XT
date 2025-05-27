// Group2Instructions.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Group2Instructions {

    private final CPU cpu;

    Group2Instructions(final CPU cpu) {
        this.cpu = cpu;
    }

    void rotate8(final int count) {
        final RegRM8 regRM = cpu.modRegRM.fetch8();
        byte value = regRM.getMem8().getValue();
        value = switch (regRM.getRegValue()) {
            case 0 -> rotateLeft8(value, count); // ROL r/m8,1 - Rotate 8 bits r/m byte count times.
            case 1 -> rotateRight8(value, count); // ROR r/m8,1 - Rotate 8 bits r/m byte count times.
            case 2 -> rotateLeft9(value, count); // RCL r/m8,1 - Rotate 9 bits (CF,r/m byte) count times.
            case 3 -> rotateRight9(value, count); // RCR r/m8,1 - Rotate 9 bits (CF,r/m byte) right count times.
            case 4 -> shiftLeft8(value, count); // SAL/SHL r/m8,1 - Multiply r/m byte by 2 count times.
            case 5 -> shiftRight8(value, count, false); // SHR r/m8,1 - Unsigned divide r/m byte by 2 count times.
            case 7 -> shiftRight8(value, count, true); // SAR r/m8,1 - Signed divide r/m byte by 2 count times.
            default -> {
                cpu.delegate.invalidOpcode("Unexpected value: " + regRM.getRegValue());
                yield 0;
            }
        };
        regRM.getMem8().setValue(value);
    }

    void rotate16(final int count) {
        final RegRM16 regRM = cpu.modRegRM.fetch16();
        short value = regRM.getMem16().getValue();
        value = switch (regRM.getRegValue()) {
            case 0 -> rotateLeft16(value, count); // ROL r/m16,1 - Rotate 16 bits r/m byte left count times.
            case 1 -> rotateRight16(value, count); // ROR r/m16,1 - Rotate 16 bits r/m byte right count times.
            case 2 -> rotateLeft17(value, count); // RCL r/m16,1 - Rotate 17 bits (CF,r/m byte) left count times.
            case 3 -> rotateRight17(value, count); // RCR r/m16,1 - Rotate 17 bits (CF,r/m byte) right count times.
            case 4 -> shiftLeft16(value, count); // SAL/SHL r/m16,1 - Multiply r/m byte by 2 count times.
            case 5 -> shiftRight16(value, count, false); // SHR r/m16,1 - Unsigned divide r/m byte by 2 count times.
            case 7 -> shiftRight16(value, count, true); // SAR r/m16,1 - Signed divide r/m byte by 2 count times.
            default -> {
                cpu.delegate.invalidOpcode("Unexpected value: " + regRM.getRegValue());
                yield 0;
            }
        };
        regRM.getMem16().setValue(value);
    }

    public byte shiftLeft8(byte value, final int count) {
        for (int i = 0; i < count; i++) {
            cpu.reg.flags.setCarry((value & 0x80) == 0x80);
            value = (byte) (value << 1);
        }
        cpu.reg.flags.setSignNegative((value & 0x80) == 0x80);
        cpu.reg.flags.setZero(value == 0x0);
        cpu.reg.flags.setParityEven(Parity8.isEven(value));
        cpu.reg.flags.setOverflow(cpu.reg.flags.isCarry() ^ ((value & 0x80) == 0x80));

        // Undocumented behaviour to allow single step tests to pass.
        cpu.reg.flags.setAuxiliaryCarry((value & 0x10) != 0);
        return value;
    }

    public byte shiftRight8(byte value, final int count, final boolean signed) {
        for (int i = 0; i < count; i++) {
            cpu.reg.flags.setCarry((value & 0x01) == 0x01);
            value = (byte) ((byte) (value >> 1) & (signed ? 0xFF : 0x7F));
        }
        cpu.reg.flags.setSignNegative((value & 0x80) == 0x80);
        cpu.reg.flags.setZero(value == 0x0);
        cpu.reg.flags.setParityEven(Parity8.isEven(value));
        cpu.reg.flags.setOverflow(((value & 0x80) == 0x80) ^ (value & 0x40) == 0x40);

        // Undocumented behaviour to allow single step tests to pass.
        cpu.reg.flags.setAuxiliaryCarry(false);
        return value;
    }

    public short shiftLeft16(short value, final int count) {
        for (int i = 0; i < count; i++) {
            cpu.reg.flags.setCarry((value & 0x8000) == 0x8000);
            value = (short) (value << 1);
        }
        cpu.reg.flags.setSignNegative((value & 0x8000) == 0x8000);
        cpu.reg.flags.setZero(value == 0x0);
        cpu.reg.flags.setParityEven(Parity8.isEven(value));
        cpu.reg.flags.setOverflow(cpu.reg.flags.isCarry() ^ ((value & 0x8000) == 0x8000));

        // Undocumented behaviour to allow single step tests to pass.
        cpu.reg.flags.setAuxiliaryCarry((value & 0x10) != 0);
        return value;
    }

    public short shiftRight16(short value, final int count, final boolean signed) {
        for (int i = 0; i < count; i++) {
            cpu.reg.flags.setCarry((value & 0x0001) == 0x0001);
            value = (short) ((short) (value >> 1) & (signed ? 0xFFFF : 0x7FFF));
        }
        cpu.reg.flags.setSignNegative((value & 0x8000) == 0x8000);
        cpu.reg.flags.setZero(value == 0x0);
        cpu.reg.flags.setParityEven(Parity8.isEven(value));
        cpu.reg.flags.setOverflow(((value & 0x8000) == 0x8000) ^ (value & 0x4000) == 0x4000);

        // Undocumented behaviour to allow single step tests to pass.
        cpu.reg.flags.setAuxiliaryCarry(false);
        return value;
    }

    public byte rotateLeft8(byte value, final int count) {
        for (int i = 0; i < count; i++) {
            cpu.reg.flags.setCarry((value & 0x80) == 0x80);
            byte value1 = (byte) (value << 1);
            byte value2 = (byte) ((value >> 7) & 0x01);
            value = (byte) (value1 | value2);
        }
        cpu.reg.flags.setOverflow(cpu.reg.flags.isCarry() ^ ((value & 0x80) == 0x80));
        return value;
    }

    public byte rotateRight8(byte value, final int count) {
        for (int i = 0; i < count; i++) {
            cpu.reg.flags.setCarry((value & 0x01) == 0x01);
            byte value1 = (byte) ((byte) (value >> 1) & 0x7F);
            byte value2 = (byte) ((byte) (value << 7) & 0x80);
            value = (byte) (value1 | value2);
        }
        cpu.reg.flags.setOverflow(((value & 0x80) == 0x80) ^ (value & 0x40) == 0x40);
        return value;
    }

    public byte rotateLeft9(byte value, final int count) {
        for (int i = 0; i < count; i++) {
            boolean origCarry = cpu.reg.flags.isCarry();
            byte value1 = (byte) (value << 1);
            cpu.reg.flags.setCarry(((value >> 7) & 0x01) == 0x01);
            value = (byte) (value1 | (origCarry ? (byte) 0x01 : (byte) 0x00));
        }
        cpu.reg.flags.setOverflow(cpu.reg.flags.isCarry() ^ ((value & 0x80) == 0x80));
        return value;
    }

    public byte rotateRight9(byte value, final int count) {
        for (int i = 0; i < count; i++) {
            boolean origCarry = cpu.reg.flags.isCarry();
            cpu.reg.flags.setCarry((value & 0x01) == 0x01);
            byte value1 = (byte) ((byte) (value >> 1) & 0x7F);
            value = (byte) (value1 | (origCarry ? (byte) 0x80 : (byte) 0x00));
        }
        cpu.reg.flags.setOverflow(((value & 0x80) == 0x80) ^ (value & 0x40) == 0x40);
        return value;
    }

    public short rotateLeft16(short value, final int count) {
        for (int i = 0; i < count; i++) {
            cpu.reg.flags.setCarry((value & 0x8000) == 0x8000);
            short value1 = (short) (value << 1);
            short value2 = (short) ((value >> 15) & 0x0001);
            value = (short) (value1 | value2);
        }
        cpu.reg.flags.setOverflow(cpu.reg.flags.isCarry() ^ ((value & 0x8000) == 0x8000));
        return value;
    }

    public short rotateRight16(short value, final int count) {
        for (int i = 0; i < count; i++) {
            cpu.reg.flags.setCarry((value & 0x0001) == 0x0001);
            short value1 = (short) ((short) (value >> 1) & 0x7FFF);
            short value2 = (short) ((short) (value << 15) & 0x8000);
            value = (short) (value1 | value2);
        }
        cpu.reg.flags.setOverflow(((value & 0x8000) == 0x8000) ^ (value & 0x4000) == 0x4000);
        return value;
    }

    public short rotateLeft17(short value, final int count) {
        for (int i = 0; i < count; i++) {
            boolean origCarry = cpu.reg.flags.isCarry();
            short value1 = (short) (value << 1);
            cpu.reg.flags.setCarry(((value >> 15) & 0x0001) == 0x0001);
            value = (short) (value1 | (origCarry ? (short) 0x0001 : (short) 0x0000));
        }
        cpu.reg.flags.setOverflow(cpu.reg.flags.isCarry() ^ ((value & 0x8000) == 0x8000));
        return value;
    }

    public short rotateRight17(short value, final int count) {
        for (int i = 0; i < count; i++) {
            boolean origCarry = cpu.reg.flags.isCarry();
            cpu.reg.flags.setCarry((value & 0x0001) == 0x0001);
            short value1 = (short) ((short) (value >> 1) & 0x7FFF);
            value = (short) (value1 | (origCarry ? (short) 0x8000 : (short) 0x0000));
        }
        cpu.reg.flags.setOverflow(((value & 0x8000) == 0x8000) ^ (value & 0x4000) == 0x4000);
        return value;
    }
}
