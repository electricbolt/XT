// RegRM16.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class RegRM16 {

    private final Reg16 reg;
    private final Mem16 mem;
    private final int regValue;

    public RegRM16(final Reg16 reg, final Reg16 RMReg, final int regValue) {
        this.reg = reg;
        this.mem = new Mem16(RMReg);
        this.regValue = regValue;
    }

    public RegRM16(final Reg16 reg, final SegOfs RMMem, Memory memory, final int regValue) {
        this.reg = reg;
        this.mem = new Mem16(RMMem, memory);
        this.regValue = regValue;
    }

    public Reg16 getReg16() {
        return reg;
    }

    public Mem16 getMem16() {
        return mem;
    }

    public int getRegValue() {
        return regValue;
    }
}
