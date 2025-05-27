// Group3AInstructions.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Group3AInstructions {

    private final CPU cpu;

    Group3AInstructions(final CPU cpu) {
        this.cpu = cpu;
    }

    void decode() {
        final RegRM8 regRM = cpu.modRegRM.fetch8();
        switch (regRM.getRegValue()) {
            case 0: { // TEST r/m8,imm8 - And immediate byte with r/m byte.
                cpu.alu.and8(regRM.getMem8().getValue(), cpu.fetch8());
                break;
            }
            case 2: { // NOT r/m8 - Reverse each bit of r/m byte.
                regRM.getMem8().setValue((byte) ~regRM.getMem8().getValue());
                break;
            }
            case 3: { // NEG r/m8 - Two's complement negate r/m byte.
                regRM.getMem8().setValue(cpu.alu.sub8((byte) 0, regRM.getMem8().getValue(), false));
                break;
            }
            case 4: { // MUL r/m8 - Unsigned multiply (AX = AL * r/m byte)
                cpu.reg.AX.setValue(cpu.alu.mul8(cpu.reg.AL.getValue(), regRM.getMem8().getValue()));
                break;
            }
            case 5: { // IMUL r/m8 - Signed multiply (AX = AL * r/m byte)
                cpu.reg.AX.setValue(cpu.alu.imul8(cpu.reg.AL.getValue(), regRM.getMem8().getValue()));
                break;
            }
            case 6: { // DIV r/m8 - Unsigned divide AX by r/m byte (AL=QUO, AH=REM)
                try {
                    cpu.reg.AX.setValue(cpu.alu.div8(cpu.reg.AX.getValue(), regRM.getMem8().getValue()));
                } catch (ArithmeticException e) {
                    cpu.interrupt((byte) 0);
                }
                break;
            }
            case 7: { // IDIV r/m8 - Signed divide AX by r/m byte (AL=QUO, AH=REM)
                try {
                    final boolean negateQuotient = cpu.repeat;
                    cpu.reg.AX.setValue(cpu.alu.idiv8(cpu.reg.AX.getValue(), regRM.getMem8().getValue(), negateQuotient));
                } catch (ArithmeticException e) {
                    cpu.interrupt((byte) 0);
                }
                break;
            }
            default:
                cpu.delegate.invalidOpcode("Group 3A operation 1 invalid");
        }
    }
}
