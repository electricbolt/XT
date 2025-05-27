// StringInstructions.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class StringInstructions {

    private interface StringFunction {
        void call();
    }

    private final CPU cpu;

    public StringInstructions(final CPU cpu) {
        this.cpu = cpu;
    }

    void repeat(StringFunction func) {
        if (cpu.repeat) {
            while (cpu.reg.CX.getValue() != 0) {
                func.call();
                cpu.reg.CX.add((short) -1);
                if (cpu.repeatFlag != null && cpu.reg.flags.isZero() != cpu.repeatFlag) {
                    break;
                }
            }
        } else {
            func.call();
        }
    }

    /**
     * SCASB - Compare bytes AL - ES:[DI].
     */
    void scan8() {
        repeat(() -> {
            final SegOfs dstSegOfs = new SegOfs(cpu.reg.ES, cpu.reg.DI);
            cpu.alu.sub8(cpu.reg.AL.getValue(),cpu.memory.readByte(dstSegOfs), false);
            cpu.reg.DI.add(cpu.reg.flags.isDirectionDown() ? (short) -1 : (short) 1);
        });
    }

    /**
     * SCASW - Compare words AX - ES:[DI].
     */
    void scan16() {
        repeat(() -> {
            final SegOfs dstSegOfs = new SegOfs(cpu.reg.ES, cpu.reg.DI);
            cpu.alu.sub16(cpu.reg.AX.getValue(), cpu.memory.readWord(dstSegOfs), false);
            cpu.reg.DI.add(cpu.reg.flags.isDirectionDown() ? (short) -2 : (short) 2);
        });
    }

    /**
     * LODSB - Load byte DS:[SI] into AL.
     */
    void load8() {
        cpu.repeatFlag = null;
        repeat(() -> {
            final SegOfs srcSegOfs = new SegOfs(cpu.segmentOverride == null ? cpu.reg.DS : cpu.segmentOverride, cpu.reg.SI);
            cpu.reg.AL.setValue(cpu.memory.readByte(srcSegOfs));
            cpu.reg.SI.add(cpu.reg.flags.isDirectionDown() ? (short) -1 : (short) 1);
        });
    }

    /**
     * LODSW - Load word DS:[SI] into AX.
     */
    void load16() {
        cpu.repeatFlag = null;
        repeat(() -> {
            final SegOfs srcSegOfs = new SegOfs(cpu.segmentOverride == null ? cpu.reg.DS : cpu.segmentOverride, cpu.reg.SI);
            cpu.reg.AX.setValue(cpu.memory.readWord(srcSegOfs));
            cpu.reg.SI.add(cpu.reg.flags.isDirectionDown() ? (short) -2 : (short) 2);
        });
    }

    /**
     * STOSB - Store AL in byte ES:[DI].
     */
    void store8() {
        cpu.repeatFlag = null;
        repeat(() -> {
            final SegOfs dstSegOfs = new SegOfs(cpu.reg.ES, cpu.reg.DI);
            cpu.memory.writeByte(dstSegOfs, cpu.reg.AL.getValue());
            cpu.reg.DI.add(cpu.reg.flags.isDirectionDown() ? (short) -1 : (short) 1);
        });
    }

    /**
     * STOSW - Store AX in word ES:[DI].
     */
    void store16() {
        cpu.repeatFlag = null;
        repeat(() -> {
            final SegOfs dstSegOfs = new SegOfs(cpu.reg.ES, cpu.reg.DI);
            cpu.memory.writeWord(dstSegOfs, cpu.reg.AX.getValue());
            cpu.reg.DI.add(cpu.reg.flags.isDirectionDown() ? (short) -2 : (short) 2);
        });
    }

    /**
     * CMPSB - Compare bytes DS:[SI] with ES:[DI].
     * Compare byte at DS:SI with ES:DI. DS can be overridden with a segment prefix.
     * SI and DI increment or decrement based upon the direction flag.
     */
    void compare8() {
        repeat(() -> {
            final SegOfs srcSegOfs = new SegOfs(cpu.segmentOverride == null ? cpu.reg.DS : cpu.segmentOverride, cpu.reg.SI);
            final SegOfs dstSegOfs = new SegOfs(cpu.reg.ES, cpu.reg.DI);
            cpu.alu.sub8(cpu.memory.readByte(srcSegOfs), cpu.memory.readByte(dstSegOfs), false);
            cpu.reg.DI.add(cpu.reg.flags.isDirectionDown() ? (short) -1 : (short) 1);
            cpu.reg.SI.add(cpu.reg.flags.isDirectionDown() ? (short) -1 : (short) 1);
        });
    }

    /**
     * CMPSW - Compare words DS:[SI] with ES:[DI].
     * Compare word at DS:SI with ES:DI. DS can be overridden with a segment prefix.
     * SI and DI increment or decrement based upon the direction flag.
     */
    void compare16() {
        repeat(() -> {
            final SegOfs srcSegOfs = new SegOfs(cpu.segmentOverride == null ? cpu.reg.DS : cpu.segmentOverride, cpu.reg.SI);
            final SegOfs dstSegOfs = new SegOfs(cpu.reg.ES, cpu.reg.DI);
            cpu.alu.sub16(cpu.memory.readWord(srcSegOfs), cpu.memory.readWord(dstSegOfs), false);
            cpu.reg.DI.add(cpu.reg.flags.isDirectionDown() ? (short) -2 : (short) 2);
            cpu.reg.SI.add(cpu.reg.flags.isDirectionDown() ? (short) -2 : (short) 2);
        });
    }

    /**
     * MOVSB - Move byte SS:[SI] to ES:[DI].
     * Move byte from DS:SI to ES:DI. DS can be overridden with a segment prefix.
     * SI and DI increment or decrement based upon the direction flag.
     */
    void move8() {
        cpu.repeatFlag = null;
        repeat(() -> {
            final SegOfs srcSegOfs = new SegOfs(cpu.segmentOverride == null ? cpu.reg.DS : cpu.segmentOverride, cpu.reg.SI);
            final SegOfs dstSegOfs = new SegOfs(cpu.reg.ES, cpu.reg.DI);
            cpu.memory.writeByte(dstSegOfs, cpu.memory.readByte(srcSegOfs));
            cpu.reg.DI.add(cpu.reg.flags.isDirectionDown() ? (short) -1 : (short) 1);
            cpu.reg.SI.add(cpu.reg.flags.isDirectionDown() ? (short) -1 : (short) 1);
        });
    }

    /**
     * MOVSW - Move word DS:[SI] to ES:[DI].
     * Move word from DS:SI to ES:DI. DS can be overridden with a segment prefix.
     * SI and DI increment or decrement based upon the direction flag.
     */
    void move16() {
        cpu.repeatFlag = null;
        repeat(() -> {
            final SegOfs srcSegOfs = new SegOfs(cpu.segmentOverride == null ? cpu.reg.DS : cpu.segmentOverride, cpu.reg.SI);
            final SegOfs dstSegOfs = new SegOfs(cpu.reg.ES, cpu.reg.DI);
            cpu.memory.writeWord(dstSegOfs, cpu.memory.readWord(srcSegOfs));
            cpu.reg.DI.add(cpu.reg.flags.isDirectionDown() ? (short) -2 : (short) 2);
            cpu.reg.SI.add(cpu.reg.flags.isDirectionDown() ? (short) -2 : (short) 2);
        });
    }
}