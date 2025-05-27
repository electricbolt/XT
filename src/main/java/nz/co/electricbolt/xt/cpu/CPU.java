// CPU.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class CPU {

    final RegSet reg = new RegSet();
    final Memory memory = new Memory(this);
    final ModRegRM modRegRM = new ModRegRM(this);
    final ALU alu = new ALU(reg.flags);
    final BCDInstructions bcd = new BCDInstructions(reg);
    final StringInstructions string = new StringInstructions(this);
    final Group1Instructions group1 = new Group1Instructions(this);
    final Group2Instructions group2 = new Group2Instructions(this);
    final Group3AInstructions group3A = new Group3AInstructions(this);
    final Group3BInstructions group3B = new Group3BInstructions(this);
    final Group4Instructions group4 = new Group4Instructions(this);
    final Group5Instructions group5 = new Group5Instructions(this);
    final CPUDelegate delegate;
    Reg16 segmentOverride;
    boolean repeat;
    Boolean repeatFlag;
    long instructionCount;

    public CPU(CPUDelegate delegate) {
        this.delegate = delegate;
    }

    public Reg16 getSegmentOverride() {
        return segmentOverride;
    }

    public void setSegmentOverride(final Reg16 segmentOverride) {
        this.segmentOverride = segmentOverride;
    }

    public Memory getMemory() {
        return memory;
    }

    public RegSet getReg() {
        return reg;
    }

    /**
     *  Executes the CPU forever, until the delegate terminates the execution.
     */
    public void execute() {
        while (true) {
            repeat = false;
            repeatFlag = null;
            segmentOverride = null;
            step();
        }
    }

    /**
     *  Executes the CPU for a maximum of maxSteps, or until the delegate terminates the execution.
     *  A step is defined as a single instruction (including any segment prefix overrides and REP opcodes).
     */
    public void execute(int maxSteps) {
        while (maxSteps-- > 0) {
            repeat = false;
            repeatFlag = null;
            segmentOverride = null;
            step();
        }
    }

    /**
     * Opcode descriptions from Turbo Assembler Quick Reference Guide v3.2.
     */
    private void step() {
        instructionCount++;
        byte opcode = fetch8();

        switch (opcode) {
            // region SEGMENT OVERRIDE
            case (byte) 0x26:   // ES segment override prefix.
                segmentOverride = reg.ES;
                step();
                break;
            case (byte) 0x2E:   // CS segment override prefix.
                segmentOverride = reg.CS;
                step();
                break;
            case (byte) 0x36:   // SS segment override prefix.
                segmentOverride = reg.SS;
                step();
                break;
            case (byte) 0x3E:   // DS segment override prefix.
                segmentOverride = reg.DS;
                step();
                break;
            // endregion

            // region REP
            case (byte) 0xF2:   // REPNZ/REPNE - Repeat while not zero/repeat while not equal.
                repeat = true;
                repeatFlag = Boolean.FALSE;
                step();
                break;
            case (byte) 0xF3:   // REPZ/REPE - Repeat while zero/repeat while equal.
                repeat = true;
                repeatFlag = Boolean.TRUE;
                step();
                break;
            // endregion

            // region ADD/ADC.
            case (byte) 0x00:   // ADD r/m8,r8 - Add byte register to r/m byte.
            case (byte) 0x02:   // ADD r8,r/m8 - Add r/m byte to byte register.
            case (byte) 0x10:   // ADC r/m8,r8 - Add with carry byte register to r/m byte.
            case (byte) 0x12: { // ADC r8,r/m8 - Add with carry r/m byte to byte register.
                final boolean carry = (opcode == 0x10 || opcode == 0x12) && reg.flags.isCarry();
                final RegRM8 regRM = modRegRM.fetch8();
                final byte result = alu.add8(regRM.getMem8().getValue(), regRM.getReg8().getValue(), carry);
                if (opcode == 0x02 || opcode == 0x12) {
                    regRM.getReg8().setValue(result);
                } else {
                    regRM.getMem8().setValue(result);
                }
                break;
            }
            case (byte) 0x04:   // ADD AL,imm8 - Add immediate byte to AL.
            case (byte) 0x14: { // ADC AL,imm8 - Add with carry immediate byte to AL.
                final boolean carry = opcode == 0x14 && reg.flags.isCarry();
                reg.AL.setValue(alu.add8(fetch8(), reg.AL.getValue(), carry));
                break;
            }
            case (byte) 0x01:   // ADD r/m16,r16 - Add word register to r/m word.
            case (byte) 0x03:   // ADD r16,r/m16 - Add r/m word to word register.
            case (byte) 0x11:   // ADC r/m16,r16 - Add with carry word register to r/m word.
            case (byte) 0x13: { // ADC r16,r/m16 - Add with carry r/m word to word register.
                final boolean carry = (opcode == 0x11 || opcode == 0x13) && reg.flags.isCarry();
                final RegRM16 regRM = modRegRM.fetch16();
                final short result = alu.add16(regRM.getMem16().getValue(), regRM.getReg16().getValue(), carry);
                if (opcode == 0x03 || opcode == 0x13) {
                    regRM.getReg16().setValue(result);
                } else {
                    regRM.getMem16().setValue(result);
                }
                break;
            }
            case (byte) 0x05:   // ADD AX,imm16 - Add immediate word to AX.
            case (byte) 0x15: { // ADC AX,imm16 - Add with carry immediate word to AX.
                final boolean carry = opcode == 0x15 && reg.flags.isCarry();
                reg.AX.setValue(alu.add16(fetch16(), reg.AX.getValue(), carry));
                break;
            }
            // endregion

            // region PUSH/POP.
            case (byte) 0x06:   // PUSH ES - Push ES.
                push16(reg.ES.getValue());
                break;
            case (byte) 0x07:   // POP ES - Pop ES.
                reg.ES.setValue(pop16());
                break;
            case (byte) 0x0E:   // PUSH CS - Push CS.
                push16(reg.CS.getValue());
                break;
            case (byte) 0x16:   // PUSH SS - Push SS.
                push16(reg.SS.getValue());
                break;
            case (byte) 0x17:   // POP SS - Pop SS.
                reg.SS.setValue(pop16());
                break;
            case (byte) 0x1E:   // PUSH DS - Push DS.
                push16(reg.DS.getValue());
                break;
            case (byte) 0x1F:   // POP DS - Pop DS.
                reg.DS.setValue(pop16());
                break;
            case (byte) 0x50:   // PUSH AX - Push register word.
            case (byte) 0x51:   // PUSH CX - Push register word.
            case (byte) 0x52:   // PUSH DX - Push register word.
            case (byte) 0x53:   // PUSH BX - Push register word.
            case (byte) 0x54:   // PUSH SP - Push register word.
            case (byte) 0x55:   // PUSH BP - Push register word.
            case (byte) 0x56:   // PUSH SI - Push register word.
            case (byte) 0x57: { // PUSH DI - Push register word.
                final Reg16 reg16 = modRegRM.getReg16(opcode & 0x7);
                push16((short) (reg16.getValue() - (opcode == 0x54 ? 2 : 0)));
                break;
            }
            case (byte) 0x58:   // POP AX - Pop top of stack into word register.
            case (byte) 0x59:   // POP CX - Pop top of stack into word register.
            case (byte) 0x5A:   // POP DX - Pop top of stack into word register.
            case (byte) 0x5B:   // POP BX - Pop top of stack into word register.
            case (byte) 0x5C:   // POP SP - Pop top of stack into word register.
            case (byte) 0x5D:   // POP BP - Pop top of stack into word register.
            case (byte) 0x5E:   // POP SI - Pop top of stack into word register.
            case (byte) 0x5F: { // POP DI - Pop top of stack into word register.
                final Reg16 reg16 = modRegRM.getReg16(opcode & 0x7);
                reg16.setValue(pop16());
                break;
            }
            case (byte) 0x8F: { // POP m16 - Pop top of stack into memory word.
                final RegRM16 regRM = modRegRM.fetch16();
                regRM.getMem16().setValue(pop16());
                break;
            }
            // endregion

            // region FLAGS.
            case (byte) 0x9C:   // PUSHF - Push FLAGS.
                push16(reg.flags.getValue16());
                break;
            case (byte) 0x9D: { // POPF - Pop top of stack into FLAGS.
                popf();
                break;
            }
            case (byte) 0x9E: { // SAHF - Store AH flags SH ZF 0 AF 0 PF 1 CF
                reg.flags.setValue8(reg.AH.getValue());
                break;
            }
            case (byte) 0x9F: { // LAHF - Load: AH = flags SF ZF 0 AF 0 PF 1 CF
                reg.AH.setValue(reg.flags.getValue8());
                break;
            }
            case (byte) 0xF5:   // CMC - Complement carry flag.
                reg.flags.setCarry(!reg.flags.isCarry());
                break;
            case (byte) 0xF8:   // CLC - Clear carry flag.
                reg.flags.setCarry(false);
                break;
            case (byte) 0xF9:   // STC - Set carry flag.
                reg.flags.setCarry(true);
                break;
            case (byte) 0xFA:
                // CLI - Clear interrupt flag (disable interrupts).
                reg.flags.setInterruptEnabled(false);
                break;
            case (byte) 0xFB:
                // STI - Set interrupt flag (enable interrupts).
                reg.flags.setInterruptEnabled(true);
                break;
            case (byte) 0xFC:
                // CLD - Clear direction flag (up).
                reg.flags.setDirectionDown(false);
                break;
            case (byte) 0xFD:
                // STD - Set direction flag (down).
                reg.flags.setDirectionDown(true);
                break;
            // endregion

            // region OR.
            case (byte) 0x08: { // OR r/m8,r8 - OR byte register to r/m byte.
                final RegRM8 regRM = modRegRM.fetch8();
                final byte result = alu.or8(regRM.getMem8().getValue(), regRM.getReg8().getValue());
                regRM.getMem8().setValue(result);
                break;
            }
            case (byte) 0x09: { // OR r/m16,r16 - OR word register to r/m word.
                final RegRM16 regRM = modRegRM.fetch16();
                final short result = alu.or16(regRM.getMem16().getValue(), regRM.getReg16().getValue());
                regRM.getMem16().setValue(result);
                break;
            }
            case (byte) 0x0A: { // OR r8,r/m8 - OR r/m byte to byte register.
                final RegRM8 regRM = modRegRM.fetch8();
                final byte result = alu.or8(regRM.getMem8().getValue(), regRM.getReg8().getValue());
                regRM.getReg8().setValue(result);
                break;
            }
            case (byte) 0x0B: { // OR r16,r/m16 - OR r/m word to word register.
                final RegRM16 regRM = modRegRM.fetch16();
                final short result = alu.or16(regRM.getMem16().getValue(), regRM.getReg16().getValue());
                regRM.getReg16().setValue(result);
                break;
            }
            case (byte) 0x0C: { // OR AL,imm8 - OR immediate byte to AL.
                reg.AL.setValue(alu.or8(fetch8(), reg.AL.getValue()));
                break;
            }
            case (byte) 0x0D: { // OR AX,imm16 - OR immediate word to AX.
                reg.AX.setValue(alu.or16(fetch16(), reg.AX.getValue()));
                break;
            }
            // endregion

            // region SUB/SBB.
            case (byte) 0x18:   // SBB r/m8,r8 - Subtract with borrow byte register from r/m byte.
            case (byte) 0x28: { // SUB r/m8,r8 - Subtract byte register from r/m byte.
                final boolean carry = (opcode == 0x18) && reg.flags.isCarry();
                final RegRM8 regRM = modRegRM.fetch8();
                final byte result = alu.sub8(regRM.getMem8().getValue(), regRM.getReg8().getValue(), carry);
                regRM.getMem8().setValue(result);
                break;
            }
            case (byte) 0x1A:   // SBB r8,r/m8 - Subtract with borrow word register from r/m byte.
            case (byte) 0x2A: { // SUB r8,r/m8 - Subtract r/m byte from byte register.
                final boolean carry = opcode == 0x1A && reg.flags.isCarry();
                final RegRM8 regRM = modRegRM.fetch8();
                final byte result = alu.sub8(regRM.getReg8().getValue(), regRM.getMem8().getValue(), carry);
                regRM.getReg8().setValue(result);
                break;
            }
            case (byte) 0x1C:   // SBB AL,imm8 - Subtract with borrow immediate byte from AL.
            case (byte) 0x2C: { // SUB AL,imm8 - Subtract immediate byte from AL.
                final boolean carry = opcode == 0x1C && reg.flags.isCarry();
                reg.AL.setValue(alu.sub8(reg.AL.getValue(), fetch8(), carry));
                break;
            }
            case (byte) 0x19:   // SBB r/m16,r16 - Subtract with borrow word register from r/m word.
            case (byte) 0x29: { // SUB r/m16,r16 - Subtract word register from r/m word.
                final boolean carry = (opcode == 0x19) && reg.flags.isCarry();
                final RegRM16 regRM = modRegRM.fetch16();
                final short result = alu.sub16(regRM.getMem16().getValue(), regRM.getReg16().getValue(), carry);
                regRM.getMem16().setValue(result);
                break;
            }
            case (byte) 0x1B:   // SBB r16,r/m16 - Subtract with borrow r/m word from word register.
            case (byte) 0x2B: { // SUB r16,r/m16 - Subtract r/m word from word register.
                final boolean carry = (opcode == 0x1B) && reg.flags.isCarry();
                final RegRM16 regRM = modRegRM.fetch16();
                final short result = alu.sub16(regRM.getReg16().getValue(), regRM.getMem16().getValue(), carry);
                regRM.getReg16().setValue(result);
                break;
            }
            case (byte) 0x1D:   // SBB AX,imm16 - Subtract with borrow immediate word from AX.
            case (byte) 0x2D: { // SUB AX,imm16 - Subtract immediate byte from AX.
                final boolean carry = opcode == 0x1D && reg.flags.isCarry();
                reg.AX.setValue(alu.sub16(reg.AX.getValue(), fetch16(), carry));
                break;
            }
            // endregion

            // region AND.
            case (byte) 0x20: {  // AND r/m8,r8 - AND byte register into r/m byte.
                final RegRM8 regRM = modRegRM.fetch8();
                final byte result = alu.and8(regRM.getMem8().getValue(), regRM.getReg8().getValue());
                regRM.getMem8().setValue(result);
                break;
            }
            case (byte) 0x21: { // AND r/m16,r16 - AND word register into r/m word.
                final RegRM16 regRM = modRegRM.fetch16();
                final short result = alu.and16(regRM.getMem16().getValue(), regRM.getReg16().getValue());
                regRM.getMem16().setValue(result);
                break;
            }
            case (byte) 0x22: { // AND r8,r/m8 - AND r/m byte into byte register.
                final RegRM8 regRM = modRegRM.fetch8();
                final byte result = alu.and8(regRM.getMem8().getValue(), regRM.getReg8().getValue());
                regRM.getReg8().setValue(result);
                break;
            }
            case (byte) 0x23: { // AND r16,r/m16 - AND r/m word into word register.
                final RegRM16 regRM = modRegRM.fetch16();
                final short result = alu.and16(regRM.getMem16().getValue(), regRM.getReg16().getValue());
                regRM.getReg16().setValue(result);
                break;
            }
            case (byte) 0x24: { // AND AL,imm8 - AND immediate byte to AL.
                reg.AL.setValue(alu.and8(fetch8(), reg.AL.getValue()));
                break;
            }
            case (byte) 0x25: { // AND AX,imm16 - AND immediate word to AX.
                reg.AX.setValue(alu.and16(fetch16(), reg.AX.getValue()));
                break;
            }
            // endregion

            // region BCD
            case (byte) 0x27:   // DAA - Decimal adjust AL after addition.
                bcd.daa();
                break;
            case (byte) 0x2F:   // DAS - Decimal adjust AL after subtraction.
                bcd.das();
                break;
            case (byte) 0x37:   // AAA - ASCII adjust after addition.
                bcd.aaa();
                break;
            case (byte) 0x3F:   // AAS - ASCII adjust after subtraction.
                bcd.aas();
                break;
            case (byte) 0xD4: { // AAM base - ASCII adjust after multiplication.
                final byte base = fetch8();
                if (!bcd.aam(base)) {
                    interrupt((byte) 0);
                }
                break;
            }
            case (byte) 0xD5: { // AAD base - ASCII adjust before division.
                final byte base = fetch8();
                bcd.aad(base);
                break;
            }
            // endregion

            // region XOR.
            case (byte) 0x30: {  // XOR r/m8,r8 - Exclusive-OR byte register to r/m byte.
                final RegRM8 regRM = modRegRM.fetch8();
                final byte result = alu.xor8(regRM.getMem8().getValue(), regRM.getReg8().getValue());
                regRM.getMem8().setValue(result);
                break;
            }
            case (byte) 0x31: {  // XOR r/m16,r16 - Exclusive-OR word register to r/m word.
                final RegRM16 regRM = modRegRM.fetch16();
                final short result = alu.xor16(regRM.getMem16().getValue(), regRM.getReg16().getValue());
                regRM.getMem16().setValue(result);
                break;
            }
            case (byte) 0x32: { // XOR r8,r/m8 - Exclusive-OR r/m byte into byte register.
                final RegRM8 regRM = modRegRM.fetch8();
                final byte result = alu.xor8(regRM.getMem8().getValue(), regRM.getReg8().getValue());
                regRM.getReg8().setValue(result);
                break;
            }
            case (byte) 0x33: { // XOR r16,r/m16 - Exclusive-OR r/m word into word register.
                final RegRM16 regRM = modRegRM.fetch16();
                final short result = alu.xor16(regRM.getMem16().getValue(), regRM.getReg16().getValue());
                regRM.getReg16().setValue(result);
                break;
            }
            case (byte) 0x34: { // XOR AL,imm8 - Exclusive-OR immediate byte to AL.
                reg.AL.setValue(alu.xor8(fetch8(), reg.AL.getValue()));
                break;
            }
            case (byte) 0x35: { // XOR AX,imm16 - Exclusive-OR immediate word to AX.
                reg.AX.setValue(alu.xor16(fetch16(), reg.AX.getValue()));
                break;
            }
            // endregion

            // region CMP.
            case (byte) 0x38: { // CMP r/m8,r8 - Compare byte register to r/m byte.
                final RegRM8 regRM = modRegRM.fetch8();
                alu.sub8(regRM.getMem8().getValue(), regRM.getReg8().getValue(), false);
                break;
            }
            case (byte) 0x39: { // CMP r/m16,r16 - Compare word register to r/m word.
                final RegRM16 regRM = modRegRM.fetch16();
                alu.sub16(regRM.getMem16().getValue(), regRM.getReg16().getValue(), false);
                break;
            }
            case (byte) 0x3A: { // CMP r8,r/m8 - Compare r/m byte to byte register.
                final RegRM8 regRM = modRegRM.fetch8();
                alu.sub8(regRM.getReg8().getValue(), regRM.getMem8().getValue(), false);
                break;
            }
            case (byte) 0x3B: { // CMP r16,r/m16 - Compare r/m word to word register.
                final RegRM16 regRM = modRegRM.fetch16();
                alu.sub16(regRM.getReg16().getValue(), regRM.getMem16().getValue(), false);
                break;
            }
            case (byte) 0x3C:  // CMP AL,imm8 - Compare immediate byte to AL.
                alu.sub8(reg.AL.getValue(), fetch8(), false);
                break;
            case (byte) 0x3D:  // CMP AX,imm8 - Compare immediate word to AX.
                alu.sub16(reg.AX.getValue(), fetch16(), false);
                break;
            // endregion

            // region INC/DEC.
            case (byte) 0x40:   // INC AX - Increment word register by 1.
            case (byte) 0x41:   // INC CX - Increment word register by 1.
            case (byte) 0x42:   // INC DX - Increment word register by 1.
            case (byte) 0x43:   // INC BX - Increment word register by 1.
            case (byte) 0x44:   // INC SP - Increment word register by 1.
            case (byte) 0x45:   // INC BP - Increment word register by 1.
            case (byte) 0x46:   // INC SI - Increment word register by 1.
            case (byte) 0x47: { // INC DI - Increment word register by 1.
                final Reg16 reg16 = modRegRM.getReg16(opcode & 0x7);
                final boolean origCarry = reg.flags.isCarry();
                reg16.setValue(alu.add16(reg16.getValue(), (short) 1, false));
                reg.flags.setCarry(origCarry);
                break;
            }
            case (byte) 0x48:   // DEC AX - Decrement word register by 1.
            case (byte) 0x49:   // DEC CX - Decrement word register by 1.
            case (byte) 0x4A:   // DEC DX - Decrement word register by 1.
            case (byte) 0x4B:   // DEC BX - Decrement word register by 1.
            case (byte) 0x4C:   // DEC SP - Decrement word register by 1.
            case (byte) 0x4D:   // DEC BP - Decrement word register by 1.
            case (byte) 0x4E:   // DEC SI - Decrement word register by 1.
            case (byte) 0x4F: { // DEC DI - Decrement word register by 1.
                final Reg16 reg16 = modRegRM.getReg16(opcode & 0x7);
                final boolean origCarry = reg.flags.isCarry();
                reg16.setValue(alu.sub16(reg16.getValue(), (short) 1, false));
                reg.flags.setCarry(origCarry);
                break;
            }
            case (byte) 0xFE:   // INC/DEC r/m8.
                group4.decode();
                break;
            // endregion

            // region Jcc.
            case (byte) 0x70:   // JO rel8 - Jump short if overflow (OF=1).
                jcc(reg.flags.isOverflow());
                break;
            case (byte) 0x71:   // JNO rel8 - Jump short if not overflow (OF=0).
                jcc(reg.flags.isNotOverflow());
                break;
            case (byte) 0x72:   // JB/JNAE/JC rel8 - Jump short if carry/Jump short if not above or equal/Jump short if carry (CF=1).
                jcc(reg.flags.isCarry());
                break;
            case (byte) 0x73:   // JNB/JAE/JNC rel8 - Jump short if not carry/Jump short if above or equal/Jump short if not carry (CF=0).
                jcc(reg.flags.isNotCarry());
                break;
            case (byte) 0x74:   // JE/JZ rel8 - Jump short if equal/Jump short if zero (ZF=1).
                jcc(reg.flags.isZero());
                break;
            case (byte) 0x75:   // JNE/JNZ rel8 - Jump short if not equal/Jump short if not zero (ZF=0).
                jcc(reg.flags.isNotZero());
                break;
            case (byte) 0x76:   // JBE/JNA rel8 - Jump short if below or equal/Jump short if not above (CF=1 | ZF=1).
                jcc(reg.flags.isCarry() || reg.flags.isZero());
                break;
            case (byte) 0x77:   // JNBE/JA rel8 - Jump short if not below or equal/Jump short if above (CF=0 & ZF=0).
                jcc(reg.flags.isNotCarry() && reg.flags.isNotZero());
                break;
            case (byte) 0x78:   // JS rel8 - Jump short if sign (SF=1).
                jcc(reg.flags.isSignNegative());
                break;
            case (byte) 0x79:   // JNS rel8 - Jump short if not sign (SF=0).
                jcc(reg.flags.isSignPositive());
                break;
            case (byte) 0x7A:   // JP/JPE rel8 - Jump short if parity/Jump short if parity even (PF=1).
                jcc(reg.flags.isParityEven());
                break;
            case (byte) 0x7B:   // JNP/JPO rel8 - Jump short if not parity/jump short if parity odd (PF=0).
                jcc(reg.flags.isParityOdd());
                break;
            case (byte) 0x7C:   // JL/JNGE rel8 - jump short if less/jump short if not greater than or equal (SF <> OE).
                jcc(reg.flags.isSignPositive() != reg.flags.isNotOverflow());
                break;
            case (byte) 0x7D:   // JNL/JGE rel8 - jump short if not less/jump short if greater than or equal (SF == OE).
                jcc(reg.flags.isSignNegative() == reg.flags.isOverflow());
                break;
            case (byte) 0x7E:   // JLE/JNG rel8 - Jump short if less than or equal/Not greater than
                jcc(reg.flags.isZero() || (reg.flags.isSignNegative() != reg.flags.isOverflow())); // (ZF=1 and SF=OF).
                break;
            case (byte) 0x7F:   // JNLE/JG rel8 - Jump short if not less than or equal/Greater than (ZF=0 and SF=OF).
                jcc(reg.flags.isNotZero() && (reg.flags.isSignNegative() == reg.flags.isOverflow()));
                break;
            case (byte) 0xE3:   // JCXZ rel8  - Jump short if CX register is 0.
                jcc(reg.CX.getValue() == 0);
                break;
            // endregion

            // region Group 1 - ADD/ADC/AND/CMP/OR/SBB/SUB/XOR.
            case (byte) 0x80:   // op r/m8, imm8.
                group1.imm8();
                break;
            case (byte) 0x81:   // op r/m16, imm16.
                group1.imm16(false);
                break;
            case (byte) 0x83:   // op r/m16, imm8 - sign-extended immediate byte to r/m word.
                group1.imm16(true);
                break;
            // endregion

            // region TEST.
            case (byte) 0x84: { // TEST r/m8,r8 - AND byte register with r/m byte.
                final RegRM8 regRM = modRegRM.fetch8();
                alu.and8(regRM.getMem8().getValue(), regRM.getReg8().getValue());
                break;
            }
            case (byte) 0x85: { // TEST r/m16,r16 - AND word register with r/m word.
                final RegRM16 regRM = modRegRM.fetch16();
                alu.and16(regRM.getMem16().getValue(), regRM.getReg16().getValue());
                break;
            }
            case (byte) 0xA8: { // TEST AL,imm8 - AND immediate byte with AL.
                final byte imm8 = fetch8();
                alu.and8(reg.AL.getValue(), imm8);
                break;
            }
            case (byte) 0xA9: { // TEST AX,imm16 - AND immediate word with AX.
                final short imm16 = fetch16();
                alu.and16(reg.AX.getValue(), imm16);
                break;
            }
            // endregion

            // region XCHG.
            case (byte) 0x86: { // XCHG r/m8,r8 - Exchange byte register with r/m byte.
                final RegRM8 regRM = modRegRM.fetch8();
                final byte temp = regRM.getMem8().getValue();
                regRM.getMem8().setValue(regRM.getReg8().getValue());
                regRM.getReg8().setValue(temp);
                break;
            }
            case (byte) 0x87: { // XCHG r/m16,r16 - Exchange word register with r/m word.
                final RegRM16 regRM = modRegRM.fetch16();
                final short temp = regRM.getMem16().getValue();
                regRM.getMem16().setValue(regRM.getReg16().getValue());
                regRM.getReg16().setValue(temp);
                break;
            }
            case (byte) 0x90:   // XCHG AX, AX - Exchange word register with AX/NOP - No operation.
            case (byte) 0x91:   // XCHG AX, CX - Exchange word register with AX.
            case (byte) 0x92:   // XCHG AX, DX - Exchange word register with AX.
            case (byte) 0x93:   // XCHG AX, BX - Exchange word register with AX.
            case (byte) 0x94:   // XCHG AX, SP - Exchange word register with AX.
            case (byte) 0x95:   // XCHG AX, BP - Exchange word register with AX.
            case (byte) 0x96:   // XCHG AX, SI - Exchange word register with AX.
            case (byte) 0x97: { // XCHG AX, DI - Exchange word register with AX.
                final Reg16 reg16 = modRegRM.getReg16(opcode & 0x7);
                final short temp = reg16.getValue();
                reg16.setValue(reg.AX.getValue());
                reg.AX.setValue(temp);
                break;
            }
            // endregion

            // region MOV.
            case (byte) 0x88: { // MOV r/m8,r8 - Move byte register into r/m byte.
                final RegRM8 regRM = modRegRM.fetch8();
                regRM.getMem8().setValue(regRM.getReg8().getValue());
                break;
            }
            case (byte) 0x89: { // MOV r/m16,r16 - Move word register into r/m word.
                final RegRM16 regRM = modRegRM.fetch16();
                regRM.getMem16().setValue(regRM.getReg16().getValue());
                break;
            }
            case (byte) 0x8A: { // MOV r8,r/m8 - Move r/m byte into byte register.
                final RegRM8 regRM = modRegRM.fetch8();
                regRM.getReg8().setValue(regRM.getMem8().getValue());
                break;
            }
            case (byte) 0x8B: { // MOV r8,r/m16 - Move r/m word into word register.
                final RegRM16 regRM = modRegRM.fetch16();
                regRM.getReg16().setValue(regRM.getMem16().getValue());
                break;
            }
            case (byte) 0x8C: { // MOV r/m16,Sreg - Move segment register to r/m register.
                final RegRM16 regRM = modRegRM.fetch16SReg();
                regRM.getMem16().setValue(regRM.getReg16().getValue());
                break;
            }
            case (byte) 0x8D: { // LEA r16,m - Store effective address for m in register 16.
                final RegRM16 regRM = modRegRM.fetch16();
                regRM.getReg16().setValue(regRM.getMem16().getSegOfs().getOffset());
                break;
            }
            case (byte) 0x8E: { // MOV Sreg,r/m16 - Move r/m register to segment register.
                final RegRM16 regRM = modRegRM.fetch16SReg();
                regRM.getReg16().setValue(regRM.getMem16().getValue());
                break;
            }
            case (byte) 0xA0: { // MOV AL,moffs8 - Move byte at (seg:offset) to AL.
                final SegOfs segOfs = modRegRM.fetchSegOfs();
                reg.AL.setValue(memory.readByte(segOfs));
                break;
            }
            case (byte) 0xA1: { // MOV AX,moffs16 - Move byte at (seg:offset) to AX.
                final SegOfs segOfs = modRegRM.fetchSegOfs();
                reg.AX.setValue(memory.readWord(segOfs));
                break;
            }
            case (byte) 0xA2: { // MOV moffs8,AL - Move AL to (seg:offset).
                final SegOfs segOfs = modRegRM.fetchSegOfs();
                memory.writeByte(segOfs, reg.AL.getValue());
                break;
            }
            case (byte) 0xA3: { // MOV moffs16,AX - Move AX to (seg:offset).
                final SegOfs segOfs = modRegRM.fetchSegOfs();
                memory.writeWord(segOfs, reg.AX.getValue());
                break;
            }
            case (byte) 0xB0:   // MOV AL, imm8 - Move immediate byte to register.
            case (byte) 0xB1:   // MOV CL, imm8 - Move immediate byte to register.
            case (byte) 0xB2:   // MOV DL, imm8 - Move immediate byte to register.
            case (byte) 0xB3:   // MOV BL, imm8 - Move immediate byte to register.
            case (byte) 0xB4:   // MOV AH, imm8 - Move immediate byte to register.
            case (byte) 0xB5:   // MOV CH, imm8 - Move immediate byte to register.
            case (byte) 0xB6:   // MOV DH, imm8 - Move immediate byte to register.
            case (byte) 0xB7: { // MOV BH, imm8 - Move immediate byte to register.
                final Reg8 reg8 = modRegRM.getReg8(opcode & 0x7);
                reg8.setValue(fetch8());
                break;
            }
            case (byte) 0xB8:   // MOV AX, imm16 - Move immediate word to register.
            case (byte) 0xB9:   // MOV CX, imm16 - Move immediate word to register.
            case (byte) 0xBA:   // MOV DX, imm16 - Move immediate word to register.
            case (byte) 0xBB:   // MOV BX, imm16 - Move immediate word to register.
            case (byte) 0xBC:   // MOV SP, imm16 - Move immediate word to register.
            case (byte) 0xBD:   // MOV BP, imm16 - Move immediate word to register.
            case (byte) 0xBE:   // MOV SI, imm16 - Move immediate word to register.
            case (byte) 0xBF: { // MOV DI, imm16 - Move immediate word to register.
                final Reg16 reg16 = modRegRM.getReg16(opcode & 0x7);
                reg16.setValue(fetch16());
                break;
            }
            case (byte) 0xC6: { // MOV r/m8,imm8 - Move immediate byte to r/m byte.
                final RegRM8 regRM = modRegRM.fetch8();
                regRM.getMem8().setValue(fetch8());
                break;
            }
            case (byte) 0xC7: { // MOV r/m16,imm16 - Move immediate word to r/m word.
                final RegRM16 regRM = modRegRM.fetch16();
                regRM.getMem16().setValue(fetch16());
                break;
            }
            // endregion

            // region STRING.
            case (byte) 0xA4:   // MOVSB - Move byte SS:[SI] to ES:[DI].
                string.move8();
                break;
            case (byte) 0xA5:   // MOVSW - Move word DS:[SI] to ES:[DI].
                string.move16();
                break;
            case (byte) 0xA6:   // CMPSB - Compare bytes DS:[SI] with ES:[DI].
                string.compare8();
                break;
            case (byte) 0xA7:   // CMPSW - Compare words DS:[SI] with ES:[DI].
                string.compare16();
                break;
            case (byte) 0xAA:   // STOSB - Store AL in byte ES:[DI].
                string.store8();
                break;
            case (byte) 0xAB:   // STOSW - Store AX in word ES:[DI].
                string.store16();
                break;
            case (byte) 0xAC:   // LODSB - Load byte DS:[SI] into AL.
                string.load8();
                break;
            case (byte) 0xAD:   // LODSW - Load word DS:[SI] into AX.
                string.load16();
                break;
            case (byte) 0xAE:   // SCASB - Compare bytes AL - ES:[DI].
                string.scan8();
                break;
            case (byte) 0xAF:   // SCASW - Compare words AX - ES:[DI].
                string.scan16();
                break;
            // endregion

            // region CBW/CWD.
            case (byte) 0x98: { // CBW - AX sign extend of AL.
                reg.AX.setValue(reg.AL.getValue());
                break;
            }
            case (byte) 0x99: { // CWD - DX:AX <- sign-extend of AX.
                if ((reg.AX.getValue() & 0x8000) == 0x8000) {
                    reg.DX.setValue((short) 0xFFFF);
                } else {
                    reg.DX.setValue((short) 0);
                }
                break;
            }
            // endregion

            // region CALL/RET.
            case (byte) 0x9A: { // CALL ptr16:16 - Call intersegment, to full pointer given (far call).
                final short offset = fetch16();
                final short segment = fetch16();
                push16(reg.CS.getValue());
                push16(reg.IP.getValue());
                reg.CS.setValue(segment);
                reg.IP.setValue(offset);
                break;
            }
            case (byte) 0xC2: { // RET imm16 - Return (near), popping off N additional bytes.
                final short additionalPopBytes = fetch16();
                reg.IP.setValue(pop16());
                reg.SP.add(additionalPopBytes);
                break;
            }
            case (byte) 0xC3: { // RET - Return (near).
                reg.IP.setValue(pop16());
                break;
            }
            case (byte) 0xCA: { // RETF - Return (far), popping off N additional bytes.
                final short additionalPopBytes = fetch16();
                reg.IP.setValue(pop16());
                reg.CS.setValue(pop16());
                reg.SP.add(additionalPopBytes);
                break;
            }
            case (byte) 0xCB: { // RETF - Return (far).
                reg.IP.setValue(pop16());
                reg.CS.setValue(pop16());
                break;
            }
            // endregion

            // region LOAD SEG.
            case (byte) 0xC4:   // LES r16,m16:16 - Load ES:r16 with pointer from memory.
                loadSeg(reg.ES);
                break;
            case (byte) 0xC5:   // LDS r16,m16:16 - Load DS:r16 with pointer from memory.
                loadSeg(reg.DS);
                break;
            // endregion

            // region MISC.
            case (byte) 0x9B:   // WAIT - Wait until BUSY pin is inactive (HIGH).
                // ignore.
                break;
            case (byte) 0xF0:   // LOCK - Assert LOCK# signal for the next instruction.
                // ignore.
                break;
            case (byte) 0xF4:   // HLT - Halt.
                delegate.halt();
                break;
            // endregion

            // region INTERRUPTS.
            case (byte) 0xCC:   // INT3 - Interrupt 3 -- trap to debugger.
                interrupt((byte) 3);
                break;
            case (byte) 0xCD:   // INT imm8 - Interrupt numbered by immediate byte.
                interrupt(fetch8());
                break;
            case (byte) 0xCE:   // INTO - Interrupt 4 -- if overflow flag is 1.
                if (reg.flags.isOverflow())
                    interrupt((byte) 4);
                break;
            case (byte) 0xCF:   // IRET - Interrupt return (far return and pop flags).
                iret();
                break;
            // endregion

            // region Group 2 - RCL/RCR/ROL/ROR/SHL/SHR/SAL/SAR.
            case (byte) 0xD0:   // Rotate byte 1 bit.
                group2.rotate8(1);
                break;
            case (byte) 0xD1:   // Rotate word 1 bit.
                group2.rotate16(1);
                break;
            case (byte) 0xD2:   // Rotate byte by CL bits.
                group2.rotate8(reg.CL.getValue());
                break;
            case (byte) 0xD3:   // Rotate word by CL bits.
                group2.rotate16(reg.CL.getValue());
                break;
            // endregion

            // region XLAT.
            case (byte) 0xD7: { // XLAT - Set AL to memory byte DS:[BX + unsigned AL].
                final SegOfs segOfs = new SegOfs(segmentOverride == null ? reg.DS : segmentOverride, (short) (reg.BX.getValue() + (reg.AL.getValue() & 0xFF)));
                reg.AL.setValue(memory.readByte(segOfs));
                segmentOverride = null;
                break;
            }
            // endregion

            // region ESC
            case (byte) 0xD8:   // ESC - Escape to co-processor.
            case (byte) 0xD9:
            case (byte) 0xDA:
            case (byte) 0xDB:
            case (byte) 0xDC:
            case (byte) 0xDD:
            case (byte) 0xDE:
            case (byte) 0xDF:
                modRegRM.fetch16();
                break;
            // endregion

            // region LOOP.
            case (byte) 0xE0: { // LOOPNZ/LOOPNE - DEC Count; jump short if Count 0 and ZF=0.
                loop(reg.flags.isNotZero());
                break;
            }
            case (byte) 0xE1: { // LOOPZ/LOOPE - DEC Count; jump short if Count 0 and ZF=1.
                loop(reg.flags.isZero());
                break;
            }
            case (byte) 0xE2: { // LOOP rel8 - DEC Count; jump short if Count 0.
                loop(true);
                break;
            }
            // endregion

            // region IN/OUT.
            case (byte) 0xE4: { // IN AL,imm8 - Input byte from immediate port into AL.
                final byte address = fetch8();
                reg.AL.setValue(delegate.portRead8(address));
                break;
            }
            case (byte) 0xE5: { // IN AX,imm8 - Input word from immediate port into AX.
                final short address = fetch8();
                reg.AX.setValue(delegate.portRead16(address));
                break;
            }
            case (byte) 0xE6: { // OUT imm8,AL - Output byte AL to immediate port number.
                final short address = fetch8();
                delegate.portWrite8(address, reg.AL.getValue());
                break;
            }
            case (byte) 0xE7: { // OUT imm16,AX - Output byte AX to immediate port number.
                final short address = fetch8();
                delegate.portWrite16(address, reg.AX.getValue());
                break;
            }
            case (byte) 0xEC: { // IN AL,DX - Input byte from port DX into AL.
                reg.AL.setValue(delegate.portRead8(reg.DX.getValue()));
                break;
            }
            case (byte) 0xED: { // IN AX,DX - Input byte from port DX into AL.
                reg.AX.setValue(delegate.portRead16(reg.DX.getValue()));
                break;
            }
            case (byte) 0xEE: { // OUT DX,AL - Output byte AL to port number in DX.
                delegate.portWrite8(reg.DX.getValue(), reg.AL.getValue());
                break;
            }
            case (byte) 0xEF: { // OUT DX,AL - Output word AX to port number in DX.
                delegate.portWrite16(reg.DX.getValue(), reg.AX.getValue());
                break;
            }
            // endregion

            // region CALL/JMP.
            case (byte) 0xE8: { // CALL rel16 - Call near, displacement relative to next instruction.
                final short offset = fetch16();
                push16(reg.IP.getValue());
                reg.IP.setValue((short) (reg.IP.getValue() + offset));
                break;
            }
            case (byte) 0xE9: { // JMP rel16 - Jump short.
                final short offset = fetch16();
                reg.IP.setValue((short) (reg.IP.getValue() + offset));
                break;
            }
            case (byte) 0xEA: { // JMP ptr16:16 - Jump intersegment, 4-byte immediate address.
                final short ip = fetch16();
                final short cs = fetch16();
                reg.IP.setValue(ip);
                reg.CS.setValue(cs);
                break;
            }
            case (byte) 0xEB: { // JMP rel8 - Jump short.
                final byte offset = fetch8();
                reg.IP.setValue((short) (reg.IP.getValue() + offset));
                break;
            }
            // endregion

            // region Group 3A - TEST/NOT/NEG/MUL/IMUL/DIV/IDIV.
            case (byte) 0xF6:   // 8-bit instructions.
                group3A.decode();
                break;
            case (byte) 0xF7:   // 16-bit instructions.
                group3B.decode();
                break;
            // endregion

            // region Group 5 - 16-bit instructions.
            case (byte) 0xFF:
                group5.decode();
                break;
            // endregion

            default:
                delegate.invalidOpcode("Invalid opcode");
        }
    }

    /**
     * Fetches a byte from the address at CS:IP, then increments IP by 1. If IP was 0xFFFF it will wrap to 0x0.
     */
    byte fetch8() {
        final byte result = memory.fetchByte(new SegOfs(reg.CS, reg.IP));
        delegate.fetched8(result, instructionCount);
        reg.IP.add((short) 1);
        return result;
    }

    /**
     * Fetches a word from the address at CS:IP, then increments IP by 2. If IP was 0xFFFE or 0xFFFF it will wrap
     * around to 0x0 or 0x1.
     */
    short fetch16() {
        final short result = memory.fetchWord(new SegOfs(reg.CS, reg.IP));
        delegate.fetched16(result, instructionCount);
        reg.IP.add((short) 2);
        return result;
    }

    /**
     * LOOP instruction. Decrements CX and jumps short if CX is 0, or the optional flag evaluates to true.
     */
    void loop(boolean flag) {
        final byte relOfs = fetch8();
        reg.CX.add((short) -1);
        if (reg.CX.getValue() != 0 && flag) {
            reg.IP.add(relOfs);
        }
    }

    /**
     * Pops a word from the stack into the flags register.
     */
    void popf() {
        reg.flags.setValue16(pop16());
    }

    /**
     * IRET instruction which returns from an interrupt routine.
     */
    public void iret() {
        reg.IP.setValue(pop16());
        reg.CS.setValue(pop16());
        popf();
    }

    /**
     * Interrupt handler, which is called by 'software' INT, INT3, INTO instructions or as a result of a 'hardware'
     * error e.g. divide by zero, quotient overflow or AAM with base 0. To pass Single Step Tests, we must update the
     * registers. The {@code CPUDelegate} will be invoked. If the CPUDelegate wants to continue execution, it must
     * invoke the {@code iret} method or pop the stack manually.
     */
    public void interrupt(byte interrupt) {
        push16(reg.flags.getValue16());
        push16(reg.CS.getValue());
        push16(reg.IP.getValue());

        reg.flags.setTrapEnabled(false);
        reg.flags.setInterruptEnabled(false);
        reg.CS.setValue((memory.getWord(new SegOfs((short) 0, (short) ((4 * (interrupt & 0xFF)) + 2)))));
        reg.IP.setValue((memory.getWord(new SegOfs((short) 0, (short) (4 * (interrupt & 0xFF))))));
        delegate.interrupt(interrupt);
    }

    /**
     * LES or LDS instruction. Loads the DS or ES segment register with a word from memory.
     */
    void loadSeg(Reg16 segReg) {
        final RegRM16 regRM = modRegRM.fetch16();
        SegOfs segOfs = regRM.getMem16().getSegOfs();
        regRM.getReg16().setValue(memory.readWord(segOfs));
        segOfs.addOffset((short) 2);
        segReg.setValue(memory.readWord(segOfs));
    }

    /**
     * Push a word value onto the stack.
     */
    void push16(short value) {
        reg.SP.add((short) -2);
        memory.writeWord(new SegOfs(reg.SS, reg.SP), value);
    }

    /**
     * Pops a word value from the top of the stack and returns it.
     */
    public short pop16() {
        final short result = memory.readWord(new SegOfs(reg.SS, reg.SP));
        reg.SP.add((short) 2);
        return result;
    }

    /**
     * Jump short to relative address if condition code is true.
     * @param flag the condition code flag.
     */
    void jcc(boolean flag) {
        final byte offset = fetch8();
        if (flag) {
            reg.IP.setValue((short) (reg.IP.getValue() + offset));
        }
    }

    @Override
    public String toString() {
        return reg.toString();
    }
}
