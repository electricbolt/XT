// ALU.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

/**
 * Arithmetic logic unit performs arithmetic (addition, subtraction, multiplication, division) and logical operations
 * (and, or, not, xor).
 */

public class ALU {

    private final Flags flags;

    public ALU(final Flags flags) {
        this.flags = flags;
    }

    public byte add8(final byte a, final byte b, final boolean carry) {
        final int result = (a & 0xFF) + (b & 0xFF) + (carry ? 1 : 0);
        // The overflow flag is set when the most significant bit is changed by adding two numbers with the same sign.
        flags.setOverflow(((a & 0x80) == (b & 0x80)) && (result & 0x80) != (a & 0x80));
        flags.setSignNegative((result & 0x80) == 0x80);
        flags.setZero((result & 0xFF) == 0);
        flags.setAuxiliaryCarry((a & 0xF) + (b & 0xF) + (carry ? 1 : 0) > 0xF);
        flags.setParityEven(Parity8.isEven(result));
        flags.setCarry(result > 0xFF);
        return (byte) result;
    }

    public short add16(final short a, final short b, final boolean carry) {
        final int result = (a & 0xFFFF) + (b & 0xFFFF) + (carry ? 1 : 0);
        // The overflow flag is set when the most significant bit is changed by adding two numbers with the same sign.
        flags.setOverflow(((a & 0x8000) == (b & 0x8000)) && (result & 0x8000) != (a & 0x8000));
        flags.setSignNegative((result & 0x8000) == 0x8000);
        flags.setZero((result & 0xFFFF) == 0);
        flags.setAuxiliaryCarry((a & 0xF) + (b & 0xF) + (carry ? 1 : 0) > 0xF);
        flags.setParityEven(Parity8.isEven(result & 0xFF));
        flags.setCarry((result & 0xFFFFF) > 0xFFFF);
        return (short) result;
    }

    public byte sub8(final byte a, final byte b, final boolean carry) {
        final int result = (a & 0xFF) - (b & 0xFF) - (carry ? 1 : 0);
        // The overflow flag is set when the most significant bit is changed by subtracting two numbers with different
        // signs.
        flags.setOverflow(((a & 0x80) != (b & 0x80)) && (result & 0x80) != (a & 0x80));
        flags.setSignNegative((result & 0x80) == 0x80);
        flags.setZero((result & 0xFF) == 0);
        flags.setAuxiliaryCarry((a & 0xF) - (b & 0xF) - (carry ? 1 : 0) < 0);
        flags.setParityEven(Parity8.isEven(result));
        flags.setCarry((result & 0xFFF) > 0xFF);
        return (byte) result;
    }

    public short sub16(final short a, final short b, final boolean carry) {
        final int result = (a & 0xFFFF) - (b & 0xFFFF) - (carry ? 1 : 0);
        // The overflow flag is set when the most significant bit is changed by subtracting two numbers with different
        // signs.
        flags.setOverflow(((a & 0x8000) != (b & 0x8000)) && (result & 0x8000) != (a & 0x8000));
        flags.setSignNegative((result & 0x8000) == 0x8000);
        flags.setZero((result & 0xFFFF) == 0);
        flags.setAuxiliaryCarry((a & 0xF) - (b & 0xF) - (carry ? 1 : 0) < 0);
        flags.setParityEven(Parity8.isEven(result));
        flags.setCarry((result & 0xFFFFF) > 0xFFFF);
        return (short) result;
    }

    /**
     * Unsigned 8 bit multiply - https://www.righto.com/2023/03/8086-multiplication-microcode.html
     */
    public short mul8(final byte a, final byte b) {
        final int result = (a & 0xFF) * (b & 0xFF);
        flags.setOverflow(result >>> 8 != 0);
        flags.setCarry(result >>> 8 != 0);
        flags.setSignNegative((result & 0x8000) == 0x8000);
        flags.setZero(result >>> 8 == 0);
        flags.setAuxiliaryCarry(false);
        flags.setParityEven(Parity8.isEven(result >>> 8));
        return (short) (result & 0xFFFF);
    }

    /**
     * Signed 8 bit multiply - https://www.righto.com/2023/03/8086-multiplication-microcode.html
     * The overflow and carry flags are cleared when AL has been sign extended into AX. To calculate if AL
     * was sign extended, the IMUL instruction internally uses an ADC operation. The undocumented parity, sign,
     * auxiliary carry and zero flags are the result of this ADC operation. See IMULCOF section of the linked website.
     */
    public short imul8(final byte a, final byte b) {
        final int result = a * b;
        add8((byte) ((result >>> 8) & 0xFF), (byte) 0, ((result & 0x80) == 0x80));
        flags.setOverflow(flags.isNotZero());
        flags.setCarry(flags.isNotZero());
        return (short) (result & 0xFFFF);
    }

    /**
     * Unsigned 8 bit divide - https://www.righto.com/2023/04/reverse-engineering-8086-divide-microcode.html
     * Without implementing the full microcode of an 8088 for the DIV instruction, it's not possible to calculate
     * the undocumented flags to pass the single step tests.
     * @throws ArithmeticException if quotient overflows.
     */
    public short div8(final short dividend, final byte divisor) {
        final int quotient = ((dividend & 0xFFFF) / (divisor & 0xFF));
        if (quotient > 0xFF) {
            throw new ArithmeticException("Quotient overflow");
        }
        final int remainder = ((dividend & 0xFFFF) % (divisor & 0xFF));
        final short result = (short) ((remainder << 8) | (quotient & 0xFF));
        flags.setOverflow(false);
        flags.setCarry(false);
        flags.setSignNegative(false);
        flags.setAuxiliaryCarry(false);
        flags.setZero(false);
        flags.setParityEven(false);
        return (short) (result & 0xFFFF);
    }

    /**
     * Signed 8 bit divide - https://www.righto.com/2023/04/reverse-engineering-8086-divide-microcode.html
     * Without implementing the full microcode of an 8088 for the DIV instruction, it's not possible to calculate
     * the undocumented flags to pass the single step tests.
     * @param negateQuotient set to true if idiv8 was preceded by a REP instruction, which negates the quotient.
     * @throws ArithmeticException if quotient overflows.
     */
    public short idiv8(final short dividend, final byte divisor, final boolean negateQuotient) {
        int quotient = dividend / (int) divisor;
        if (quotient > 127 || quotient < -127) {
            throw new ArithmeticException("Quotient overflow");
        }
        if (negateQuotient) {
            quotient = -quotient;
        }
        final int remainder = dividend % (int) divisor;
        final short result = (short) ((remainder << 8) | (quotient & 0xFF));
        flags.setOverflow(false);
        flags.setCarry(false);
        flags.setSignNegative(false);
        flags.setAuxiliaryCarry(false);
        flags.setZero(false);
        flags.setParityEven(false);
        return (short) (result & 0xFFFF);
    }

    /**
     * Unsigned 16 bit divide.
     * https://www.righto.com/2023/04/reverse-engineering-8086-divide-microcode.html
     * Without implementing the full microcode of an 8088 for the DIV instruction, it's not possible to calculate
     * the undocumented flags to pass the single step tests.
     * @throws ArithmeticException if quotient overflows.
     */
    public int div16(final int dividend, final short divisor) {
        final long quotient = (((long) dividend & 0xFFFFFFFFL) / (divisor & 0xFFFF));
        if (quotient > 0xFFFF) {
            throw new ArithmeticException("Quotient overflow");
        }
        final long remainder = ((dividend & 0xFFFFFFFFL) % (divisor & 0xFFFF));
        final int result = (int) ((remainder << 16) | (quotient & 0xFFFF));
        flags.setOverflow(false);
        flags.setCarry(false);
        flags.setSignNegative(false);
        flags.setAuxiliaryCarry(false);
        flags.setZero(false);
        flags.setParityEven(false);
        return (int) (result & 0xFFFFFFFFL);
    }

    /**
     * Signed 16 bit divide.
     * https://www.righto.com/2023/04/reverse-engineering-8086-divide-microcode.html
     * Without implementing the full microcode of an 8088 for the DIV instruction, it's not possible to calculate
     * the undocumented flags to pass the single step tests.
     * @param negateQuotient set to true if idiv8 was preceded by a REP instruction, which negates the quotient.
     * @throws ArithmeticException if quotient overflows.
     */
    public int idiv16(final int dividend, final short divisor, final boolean negateQuotient) {
        long quotient = dividend / divisor;
        if (quotient > 32767 || quotient < -32767) {
            throw new ArithmeticException("Quotient overflow");
        }
        if (negateQuotient) {
            quotient = -quotient;
        }
        final long remainder = dividend % divisor;
        final int result = (int) ((remainder << 16) | (quotient & 0xFFFF));
        flags.setOverflow(false);
        flags.setCarry(false);
        flags.setSignNegative(false);
        flags.setAuxiliaryCarry(false);
        flags.setZero(false);
        flags.setParityEven(false);
        return (int) (result & 0xFFFFFFFFL);
    }

    /**
     * Unsigned 16 bit multiply.
     */
    public int mul16(final short a, final short b) {
        final long result = (long) (a & 0xFFFF) * (b & 0xFFFF);
        flags.setOverflow(result >>> 16 != 0);
        flags.setCarry(result >>> 16 != 0);
        flags.setSignNegative((result & 0x80000000L) == 0x80000000L);
        flags.setZero(result >>> 16 == 0);
        flags.setAuxiliaryCarry(false);
        flags.setParityEven(Parity8.isEven((int) (result >>> 16)));
        return (int) (result & 0xFFFFFFFFL);
    }

    /**
     * Signed 16 bit multiply.
     * The overflow and carry flags are cleared when AX has been sign extended into DX:AX. To calculate if AX
     * was sign extended, the IMUL instruction internally uses an ADC operation. The undocumented parity, sign,
     * auxiliary carry and zero flags are the result of this ADC operation. See
     * https://www.righto.com/2023/03/8086-multiplication-microcode.html IMULCOF section.
     */
    public int imul16(final short a, final short b) {
        final long result = a * b;
        add16((short) ((result >>> 16) & 0xFFFF), (short) 0, ((result & 0x8000) == 0x8000));
        flags.setOverflow(flags.isNotZero());
        flags.setCarry(flags.isNotZero());
        return (int) (result & 0xFFFFFFFFL);
    }

    public byte or8(final short a, final short b) {
        final int result = (a & 0xFF) | (b & 0xFF);
        flags.setOverflow(false);
        flags.setSignNegative((result & 0x80) == 0x80);
        flags.setZero((result & 0xFF) == 0);
        flags.setAuxiliaryCarry(false);
        flags.setParityEven(Parity8.isEven(result));
        flags.setCarry(false);
        return (byte) result;
    }

    public short or16(final short a, final short b) {
        final int result = (a & 0xFFFF) | (b & 0xFFFF);
        flags.setOverflow(false);
        flags.setSignNegative((result & 0x8000) == 0x8000);
        flags.setZero((result & 0xFFFF) == 0);
        flags.setAuxiliaryCarry(false);
        flags.setParityEven(Parity8.isEven(result));
        flags.setCarry(false);
        return (short) result;
    }

    public byte and8(final short a, final short b) {
        final int result = (a & 0xFF) & (b & 0xFF);
        flags.setOverflow(false);
        flags.setSignNegative((result & 0x80) == 0x80);
        flags.setZero((result & 0xFF) == 0);
        flags.setAuxiliaryCarry(false);
        flags.setParityEven(Parity8.isEven(result));
        flags.setCarry(false);
        return (byte) result;
    }

    public short and16(final short a, final short b) {
        final int result = (a & 0xFFFF) & (b & 0xFFFF);
        flags.setOverflow(false);
        flags.setSignNegative((result & 0x8000) == 0x8000);
        flags.setZero((result & 0xFFFF) == 0);
        flags.setAuxiliaryCarry(false);
        flags.setParityEven(Parity8.isEven(result));
        flags.setCarry(false);
        return (short) result;
    }

    public byte xor8(final short a, final short b) {
        final int result = (a & 0xFF) ^ (b & 0xFF);
        flags.setOverflow(false);
        flags.setSignNegative((result & 0x80) == 0x80);
        flags.setZero((result & 0xFF) == 0);
        flags.setAuxiliaryCarry(false);
        flags.setParityEven(Parity8.isEven(result));
        flags.setCarry(false);
        return (byte) result;
    }

    public short xor16(final short a, final short b) {
        final int result = (a & 0xFFFF) ^ (b & 0xFFFF);
        flags.setOverflow(false);
        flags.setSignNegative((result & 0x8000) == 0x8000);
        flags.setZero((result & 0xFFFF) == 0);
        flags.setAuxiliaryCarry(false);
        flags.setParityEven(Parity8.isEven(result));
        flags.setCarry(false);
        return (short) result;
    }
}
