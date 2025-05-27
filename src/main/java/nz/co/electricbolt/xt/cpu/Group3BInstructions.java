// Group3BInstructions.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Group3BInstructions {

    private final CPU cpu;

    Group3BInstructions(final CPU cpu) {
        this.cpu = cpu;
    }

    void decode() {
        final RegRM16 regRM = cpu.modRegRM.fetch16();
        switch (regRM.getRegValue()) {
            case 0: { // TEST r/m16,imm16 - And immediate word with r/m word.
                cpu.alu.and16(regRM.getMem16().getValue(), cpu.fetch16());
                break;
            }
            case 2: { // NOT r/m16 - Reverse each bit of r/m word.
                regRM.getMem16().setValue((short) ~regRM.getMem16().getValue());
                break;
            }
            case 3: { // NEG r/m16 - Two's complement negate r/m word.
                regRM.getMem16().setValue(cpu.alu.sub16((short) 0, regRM.getMem16().getValue(), false));
                break;
            }
            case 4: { // MUL r/m16 - Unsigned multiply (DX:AX = AX * r/m word)
                final int result = cpu.alu.mul16(cpu.reg.AX.getValue(), regRM.getMem16().getValue());
                cpu.reg.DX.setValue((short) (result >>> 16));
                cpu.reg.AX.setValue((short) (result & 0xFFFF));
                break;
            }
            case 5: { // IMUL r/m8 - Signed multiply (DX:AX = AX * r/m word)
                final int result = cpu.alu.imul16(cpu.reg.AX.getValue(), regRM.getMem16().getValue());
                cpu.reg.DX.setValue((short) (result >>> 16));
                cpu.reg.AX.setValue((short) (result & 0xFFFF));
                break;
            }
            case 6: { // DIV r/m16 - Unsigned divide DX:AX by r/m word (AX=QUO, DX=REM)
                try {
                    final int dividend = ((cpu.reg.DX.getValue() & 0xFFFF) << 16) | (cpu.reg.AX.getValue() & 0xFFFF);
                    final int result = cpu.alu.div16(dividend, regRM.getMem16().getValue());
                    cpu.reg.DX.setValue((short) (result >>> 16));
                    cpu.reg.AX.setValue((short) (result & 0xFFFF));
                } catch (ArithmeticException e) {
                    cpu.interrupt((byte) 0);
                }
                break;
            }
            case 7: { // IDIV r/m8 - Signed divide DX/AX by r/m word (AX=QUO, DX=REM)
                try {
                    final boolean negateQuotient = cpu.repeat;
                    final int dividend = ((cpu.reg.DX.getValue() & 0xFFFF) << 16) | (cpu.reg.AX.getValue() & 0xFFFF);
                    final int result = cpu.alu.idiv16(dividend, regRM.getMem16().getValue(), negateQuotient);
                    cpu.reg.DX.setValue((short) (result >>> 16));
                    cpu.reg.AX.setValue((short) (result & 0xFFFF));
                } catch (ArithmeticException e) {
                    cpu.interrupt((byte) 0);
                }
                break;
            }
            default:
                cpu.delegate.invalidOpcode("Group 3B operation 1 invalid");
        }
    }
}
