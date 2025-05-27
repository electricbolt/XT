// BCDInstructions.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class BCDInstructions {

    private final RegSet reg;

    public BCDInstructions(final RegSet reg) {
        this.reg = reg;
    }

    /***
     * AAA - ASCII adjust after addition.
     */
    public void aaa() {
        final int origal = reg.AL.getValue() & 0xFF;
        int adjal;

        if (((reg.AL.getValue() & 0xF) > 0x9) || reg.flags.isAuxiliaryCarry()) {
            reg.AH.setValue((byte) ((reg.AH.getValue() + 1) & 0xFF));
            adjal = (origal + 6) & 0xFF;
            reg.AL.setValue((byte) (adjal & 0xF));
            reg.flags.setAuxiliaryCarry(true);
            reg.flags.setCarry(true);
        } else {
            adjal = origal;
            reg.AL.setValue((byte) (adjal & 0xF));
            reg.flags.setAuxiliaryCarry(false);
            reg.flags.setCarry(false);
        }

        reg.flags.setZero(adjal == 0);
        reg.flags.setParityEven(Parity8.isEven(adjal));

        // Undocumented behaviour to allow Single Step Tests to pass.
        reg.flags.setOverflow(origal >= 0x7A && origal <= 0x7F);
        reg.flags.setSignNegative(origal >= 0x7A && origal <= 0xF9);
    }

    /**
     * AAS - ASCII adjust after subtraction.
     */
    public void aas() {
        final int origal = reg.AL.getValue() & 0xFF;
        int adjal;
        final boolean origAuxiliaryCarry = reg.flags.isAuxiliaryCarry();

        if (((reg.AL.getValue() & 0xF) > 0x9) || reg.flags.isAuxiliaryCarry()) {
            reg.AH.setValue((byte) ((reg.AH.getValue() - 1) & 0xFF));
            adjal = (origal - 6) & 0xFF;
            reg.AL.setValue((byte) (adjal & 0xF));
            reg.flags.setAuxiliaryCarry(true);
            reg.flags.setCarry(true);
        } else {
            adjal = origal;
            reg.AL.setValue((byte) (adjal & 0xF));
            reg.flags.setAuxiliaryCarry(false);
            reg.flags.setCarry(false);
        }

        reg.flags.setZero(adjal == 0);
        reg.flags.setParityEven(Parity8.isEven(adjal));

        // Undocumented behaviour to allow Single Step Tests to pass.
        if (origAuxiliaryCarry) {
            reg.flags.setOverflow((origal >= 0x80 && origal <= 0x85));
            reg.flags.setSignNegative(origal <= 0x05 || origal >= 0x86);
        } else {
            reg.flags.setOverflow(false);
            reg.flags.setSignNegative(origal >= 0x80);
        }
    }

    /**
     * DAA - Decimal adjust AL after addition.
     * Algorithm from https://www.righto.com/2023/01/understanding-x86s-decimal-adjust-after.html website.
     */
    public void daa() {
        int al = reg.AL.getValue() & 0xFF;
        final int origal = al;
        final int comp1 = reg.flags.isAuxiliaryCarry() ? 0x9F : 0x99;

        // Undocumented behaviour to allow Single Step Tests to pass.
        final int comp2 = reg.flags.isCarry() ? 0x1A : 0x7A;
        reg.flags.setOverflow((al >= comp2 && al <= 0x7F));

        if (((al & 0xF) > 0x9) || reg.flags.isAuxiliaryCarry()) {
            al += 0x6;
            reg.flags.setAuxiliaryCarry(true);
        } else {
            reg.flags.setAuxiliaryCarry(false);
        }

        if (((origal & 0xFF) > comp1) || reg.flags.isCarry()) {
            al += 0x60;
            reg.flags.setCarry(true);
        } else {
            reg.flags.setCarry(false);
        }

        reg.flags.setSignNegative((al & 0x80) == 0x80);
        reg.flags.setZero((al & 0xFF) == 0);
        reg.flags.setParityEven(Parity8.isEven(al));
        reg.AL.setValue((byte) al);
    }

    /**
     * DAS - Decimal adjust AL after subtraction.
     */
    public void das() {
        int al = reg.AL.getValue() & 0xFF;
        final int origal = al;
        final int comp1 = reg.flags.isAuxiliaryCarry() ? 0x9F : 0x99;

        // Undocumented behaviour to allow Single Step Tests to pass.
        reg.flags.setOverflow(switch (reg.flags.getValue16() & (Flags.AUX_CARRY | Flags.CARRY)) {
            case Flags.CARRY -> (al >= 0x80 && al <= 0xDF);
            case Flags.AUX_CARRY -> (al >= 0x80 && al <= 0x85) || (al >= 0xA0 && al <= 0xE5);
            case Flags.AUX_CARRY|Flags.CARRY -> (al >= 0x80 && al <= 0xE5);
            default -> (al >= 0x9A && al <= 0xDF);
        });

        if (((al & 0xF) > 0x9) || reg.flags.isAuxiliaryCarry()) {
            al -= 0x6;
            reg.flags.setAuxiliaryCarry(true);
        } else {
            reg.flags.setAuxiliaryCarry(false);
        }

        if (((origal & 0xFF) > comp1) || reg.flags.isCarry()) {
            al -= 0x60;
            reg.flags.setCarry(true);
        } else {
            reg.flags.setCarry(false);
        }

        reg.flags.setSignNegative((al & 0x80) == 0x80);
        reg.flags.setZero((al & 0xFF) == 0);
        reg.flags.setParityEven(Parity8.isEven(al));
        reg.AL.setValue((byte) al);
    }

    /**
     * AAM - ASCII adjust after multiplication.
     * @param base Normally base 10.
     * @return false if base is zero, otherwise true.
     */
    public boolean aam(final byte base) {
        // Undocumented behaviour to allow Single Step Tests to pass.
        reg.flags.setCarry(false);
        reg.flags.setAuxiliaryCarry(false);
        reg.flags.setOverflow(false);

        if (base == 0) {
            reg.flags.setSignNegative(false);
            reg.flags.setZero(true);
            reg.flags.setParityEven(true);
            return false;
        } else {
            reg.AH.setValue((byte) ((reg.AL.getValue() & 0xFF) / (base & 0xFF)));
            reg.AL.setValue((byte) ((reg.AL.getValue() & 0xFF) % (base & 0xFF)));

            reg.flags.setSignNegative((reg.AL.getValue() & 0x80) == 0x80);
            reg.flags.setZero((reg.AL.getValue() & 0xFF) == 0);
            reg.flags.setParityEven(Parity8.isEven(reg.AL.getValue()));
            return true;
        }
    }

    /**
     * AAD - ASCII adjust before division.
     * @param base Normally base 10.
     */
    public void aad(final byte base) {
        final byte al = reg.AL.getValue();
        final byte ah = reg.AH.getValue();
        final byte multiply = (byte) ((ah & 0xFF) * (base & 0xFF));
        final int result = (al & 0xFF) + (multiply & 0xFF);

        reg.AL.setValue((byte) result);
        reg.AH.setValue((byte) 0);

        reg.flags.setSignNegative((result & 0x80) == 0x80);
        reg.flags.setZero((result & 0xFF) == 0);
        reg.flags.setParityEven(Parity8.isEven(result));

        // Undocumented behaviour to allow Single Step Tests to pass.
        reg.flags.setOverflow(((al & 0x80) == (multiply & 0x80)) && (result & 0x80) != (al & 0x80));
        reg.flags.setAuxiliaryCarry(((al & 0xF) + (multiply & 0xF)) > 0xF);
        reg.flags.setCarry(result > 0xFF);
    }
}
