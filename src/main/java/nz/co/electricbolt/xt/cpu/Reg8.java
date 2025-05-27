// Reg8.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Reg8 {
    private final boolean high;
    private final Reg16 reg;

    Reg8(final Reg16 reg, final boolean high) {
        super();
        this.reg = reg;
        this.high = high;
    }

    public void setValue(final byte value) {
        if (high) {
            reg.setValue((short) ((reg.getValue() & 0x00FF) | ((value & 0x00FF) << 8)));
        } else {
            reg.setValue((short) ((reg.getValue() & 0xFF00) | (value & 0x00FF)));
        }
    }

    public byte getValue() {
        if (high) {
            return (byte) ((reg.getValue() >> 8) & 0xFF);
        } else {
            return (byte) (reg.getValue() & 0x00FF);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Reg8 other)) {
            return false;
        }
        return other.getName().equals(getName()) && other.getValue() == getValue();
    }

    public String getName() {
        return reg.getName().charAt(0) + (high ? "H" : "L");
    }

    public String toString() {
        return getName() + "=" + String.format("%02X", getValue());
    }
}
