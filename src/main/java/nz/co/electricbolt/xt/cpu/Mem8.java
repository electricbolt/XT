// Mem8.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Mem8 {

    private final SegOfs segOfs;
    private final Reg8 reg;
    private final Memory memory;

    Mem8(final SegOfs segOfs, final Memory memory) {
        this.segOfs = segOfs;
        this.memory = memory;
        this.reg = null;
    }

    Mem8(final Reg8 reg) {
        this.segOfs = null;
        this.memory = null;
        this.reg = reg;
    }

    public byte getValue() {
        if (reg != null) {
            return reg.getValue();
        } else {
            return memory.readByte(segOfs);
        }
    }

    public void setValue(final byte value) {
        if (reg != null) {
            reg.setValue(value);
        } else {
            memory.writeByte(segOfs, value);
        }
    }
}
