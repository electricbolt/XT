// Mem16.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Mem16 {

    private final SegOfs segOfs;
    private final Reg16 reg;
    private final Memory memory;

    Mem16(final SegOfs segOfs, final Memory memory) {
        this.segOfs = segOfs;
        this.memory = memory;
        this.reg = null;
    }

    Mem16(final Reg16 reg) {
        this.segOfs = null;
        this.memory = null;
        this.reg = reg;
    }

    public SegOfs getSegOfs() {
        if (segOfs != null) {
            return segOfs.copy();
        } else {
            return null;
        }
    }

    public Reg16 getReg() {
        return reg;
    }

    public short getValue() {
        if (reg != null) {
            return reg.getValue();
        } else {
            return memory.readWord(segOfs);
        }
    }

    public void setValue(final short value) {
        if (reg != null) {
            reg.setValue(value);
        } else {
            memory.writeWord(segOfs, value);
        }
    }
}
