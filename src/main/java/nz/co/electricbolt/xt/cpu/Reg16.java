// Reg16.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Reg16 {

    private final String name;
    private short value;
    private Reg8 high;
    private Reg8 low;

    Reg16(final String name) {
        this(name, (short) 0);
    }

    Reg16(final String name, final short initialValue) {
        this.name = name;
        setValue(initialValue);
    }

    public void add(final short value) {
        setValue((short) (getValue() + value));
    }

    public short getValue() {
        return value;
    }

    public void setValue(final short value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Reg16 copy() {
        return new Reg16(name, value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Reg16 other)) {
            return false;
        }
        return other.name.equals(name) && other.value == value;
    }

    @Override
    public String toString() {
        return name + "=" + String.format("%04X", value);
    }

    Reg8 high() {
        if (high == null) {
            high = new Reg8(this, true);
        }
        return high;
    }

    Reg8 low() {
        if (low == null) {
            low = new Reg8(this, false);
        }
        return low;
    }
}
