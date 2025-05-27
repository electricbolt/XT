// SingleStepTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

import nz.co.electricbolt.xt.cpu.singlesteptests.Tests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
public class SingleStepTests {

    @Test
    public void add_00() {
        //  ADD r/m8,r8 - Add byte register to r/m byte.
        testOpcode("00");
    }

    @Test
    public void add_01() {
        // ADD r/m16,r16 - Add word register to r/m word.
        testOpcode("01");
    }

    @Test
    public void add_02() {
        // ADD r8,r/m8 - Add r/m byte to byte register.
        testOpcode("02");
    }

    @Test
    public void add_03() {
        // ADD r16,r/m16 - Add r/m word to word register.
        testOpcode("03");
    }

    @Test
    public void add_04() {
        // ADD AL,imm8 - Add immediate byte to AL
        testOpcode("04");
    }

    @Test
    public void add_05() {
        // ADD AX,imm16 - Add immediate word to AX
        testOpcode("05");
    }

    @Test
    public void push_06() {
        // PUSH ES - Push ES.
        testOpcode("06");
    }

    @Test
    public void pop_07() {
        // POP ES - Pop ES.
        testOpcode("07");
    }

    @Test
    public void or_08() {
        // OR r/m8,r8 - OR byte register to r/m byte.
        testOpcode("08");
    }

    @Test
    public void or_09() {
        // OR r/m16,r16 - OR word register to r/m word.
        testOpcode("09");
    }

    @Test
    public void or_0A() {
        // OR r8,r/m8 - OR r/m byte to byte register.
        testOpcode("0A");
    }

    @Test
    public void or_0B() {
        // OR r16,r/m16 - OR r/m word to word register.
        testOpcode("0B");
    }

    @Test
    public void or_0C() {
        // OR AL,imm8 - OR immediate byte to AL
        testOpcode("0C");
    }

    @Test
    public void or_0D() {
        // OR AX,imm16 - OR immediate word to AX
        testOpcode("0D");
    }

    @Test
    public void push_0E() {
        // PUSH CS - Push CS.
        testOpcode("0E");
    }

    // 0F - Undocumented POP CS.

    @Test
    public void adc_10() {
        // ADC r/m8,r8 - Add with carry byte register to r/m byte.
        testOpcode("10");
    }

    @Test
    public void adc_11() {
        // ADC r/m16,r16 - Add with carry word register to r/m word.
        testOpcode("11");
    }

    @Test
    public void adc_12() {
        // ADC r8,r/m8 - Add with carry r/m byte to byte register.
        testOpcode("12");
    }

    @Test
    public void adc_13() {
        // ADC r16,r/m16 - Add with carry r/m word to word register.
        testOpcode("13");
    }

    @Test
    public void adc_14() {
        // ADC AL,imm8 - Add with carry immediate byte to AL
        testOpcode("14");
    }

    @Test
    public void adc_15() {
        // ADC AX,imm16 - Add with carry immediate word to AX
        testOpcode("15");
    }

    @Test
    public void push_16() {
        // PUSH SS - Push SS.
        testOpcode("16");
    }

    @Test
    public void pop_17() {
        // POP SS - Pop SS.
        testOpcode("17");
    }

    @Test
    public void sbb_18() {
        // SBB r/m8,r8 - Subtract with borrow byte register from r/m byte.
        testOpcode("18");
    }

    @Test
    public void sbb_19() {
        // SBB r/m16,r16 - Subtract with borrow word register from r/m word.
        testOpcode("19");
    }

    @Test
    public void sbb_1A() {
        // SBB r8,r/m8 - Subtract with borrow word register from r/m byte.
        testOpcode("1A");
    }

    @Test
    public void sbb_1B() {
        // SBB r16,r/m16 - Subtract with borrow r/m word from word register.
        testOpcode("1B");
    }

    @Test
    public void sbb_1C() {
        // SBB AL,imm8 - Subtract with borrow immediate byte from AL.
        testOpcode("1C");
    }

    @Test
    public void sbb_1D() {
        // SBB AX,imm16 - Subtract with borrow immediate word from AX.
        testOpcode("1D");
    }

    @Test
    public void push_1E() {
        // PUSH DS - Push DS.
        testOpcode("1E");
    }

    @Test
    public void pop_1F() {
        // POP DS - Pop DS.
        testOpcode("1F");
    }

    @Test
    public void and_20() {
        // AND r/m8,r8 - AND byte register into r/m byte.
        testOpcode("20");
    }

    @Test
    public void and_21() {
        // AND r/m16,r16 - AND word register into r/m word.
        testOpcode("21");
    }

    @Test
    public void and_22() {
        // AND r8,r/m8 - AND r/m byte into byte register.
        testOpcode("22");
    }

    @Test
    public void and_23() {
        // AND r16,r/m16 - AND r/m word into word register.
        testOpcode("23");
    }

    @Test
    public void and_24() {
        // AND AL,imm8 - AND immediate byte to AL
        testOpcode("24");
    }

    @Test
    public void and_25() {
        // AND AX,imm16 - AND immediate word to AX
        testOpcode("25");
    }

    // 26 - ES segment override prefix. Tests included in other opcodes.

    @Test
    public void daa_27() {
        // DAA - Decimal adjust AL after addition.
        testOpcode("27");
    }

    @Test
    public void sub_28() {
        // SUB r/m8,r8 - Subtract byte register from r/m byte.
        testOpcode("28");
    }

    @Test
    public void sub_29() {
        // SUB r/m16,r16 - Subtract word register from r/m word.
        testOpcode("29");
    }

    @Test
    public void sub_2A() {
        // SUB r8,r/m8 - Subtract r/m byte from byte register.
        testOpcode("2A");
    }

    @Test
    public void sub_2B() {
        // SUB r16,r/m16 - Subtract r/m word from word register.
        testOpcode("2B");
    }

    @Test
    public void sub_2C() {
        // SUB AL,imm8 - Subtract immediate byte from AL.
        testOpcode("2C");
    }

    @Test
    public void sub_2D() {
        // SUB AX,imm16 - Subtract immediate byte from AX.
        testOpcode("2D");
    }

    // 2E - CS segment override prefix. Tests included in other opcodes.

    @Test
    public void das_2F() {
        // DAS - Decimal adjust AL after subtraction.
        testOpcode("2F");
    }

    @Test
    public void xor_30() {
        // XOR r/m8,r8 - Exclusive-OR byte register to r/m byte.
        testOpcode("30");
    }

    @Test
    public void xor_31() {
        // XOR r/m16,r16 - Exclusive-OR word register to r/m word.
        testOpcode("31");
    }

    @Test
    public void xor_32() {
        // XOR r8,r/m8 - Exclusive-OR r/m byte into byte register.
        testOpcode("32");
    }

    @Test
    public void sub_33() {
        // XOR r16,r/m16 - Exclusive-OR r/m word into word register.
        testOpcode("33");
    }

    @Test
    public void sub_34() {
        // XOR AL,imm8 - Exclusive-OR immediate byte to AL.
        testOpcode("34");
    }

    @Test
    public void sub_35() {
        // XOR AX,imm16 - Exclusive-OR immediate word to AX.
        testOpcode("35");
    }

    // 36 - SS segment override prefix. Tests included in other opcodes.

    @Test
    public void aaa_37() {
        // AAA - ASCII adjust after addition.
        testOpcode("37");
    }

    @Test
    public void cmp_38() {
        // CMP r/m8,r8 - Compare byte register to r/m byte.
        testOpcode("38");
    }

    @Test
    public void cmp_39() {
        // CMP r/m16,r16 - Compare word register to r/m word.
        testOpcode("39");
    }

    @Test
    public void cmp_3A() {
        // CMP r8,r/m8 - Compare r/m byte to byte register.
        testOpcode("3A");
    }

    @Test
    public void cmp_3B() {
        // CMP r16,r/m16 - Compare r/m word to word register.
        testOpcode("3B");
    }

    @Test
    public void cmp_3C() {
        // CMP AL,imm8 - Compare immediate byte to AL.
        testOpcode("3C");
    }

    @Test
    public void cmp_3D() {
        // CMP AX,imm8 - Compare immediate word to AX.
        testOpcode("3D");
    }

    // 3E - DS segment override prefix. Tests included in other opcodes.

    @Test
    public void aas_3F() {
        // AAS - ASCII adjust after subtraction.
        testOpcode("3F");
    }

    @Test
    public void inc_40() {
        // INC AX - Increment word register by 1.
        testOpcode("40");
    }

    @Test
    public void inc_41() {
        // INC CX - Increment word register by 1.
        testOpcode("41");
    }

    @Test
    public void inc_42() {
        // INC DX - Increment word register by 1.
        testOpcode("42");
    }

    @Test
    public void inc_43() {
        // INC BX - Increment word register by 1.
        testOpcode("43");
    }

    @Test
    public void inc_44() {
        // INC SP - Increment word register by 1.
        testOpcode("44");
    }

    @Test
    public void inc_45() {
        // INC BP - Increment word register by 1.
        testOpcode("45");
    }

    @Test
    public void inc_46() {
        // INC SI - Increment word register by 1.
        testOpcode("46");
    }

    @Test
    public void inc_47() {
        // INC DI - Increment word register by 1.
        testOpcode("47");
    }

    @Test
    public void dec_48() {
        // DEC AX - Decrement word register by 1.
        testOpcode("48");
    }

    @Test
    public void dec_49() {
        // DEC CX - Decrement word register by 1.
        testOpcode("49");
    }

    @Test
    public void dec_4A() {
        // DEC DX - Decrement word register by 1.
        testOpcode("4A");
    }

    @Test
    public void dec_4B() {
        // DEC BX - Decrement word register by 1.
        testOpcode("4B");
    }

    @Test
    public void dec_4C() {
        // DEC SP - Decrement word register by 1.
        testOpcode("4C");
    }

    @Test
    public void dec_4D() {
        // DEC BP - Decrement word register by 1.
        testOpcode("4D");
    }

    @Test
    public void dec_4E() {
        // DEC SI - Decrement word register by 1.
        testOpcode("4E");
    }

    @Test
    public void dec_4F() {
        // DEC DI - Decrement word register by 1.
        testOpcode("4F");
    }

    @Test
    public void push_50() {
        // PUSH AX - Push register word.
        testOpcode("50");
    }

    @Test
    public void push_51() {
        // PUSH CX - Push register word.
        testOpcode("51");
    }

    @Test
    public void push_52() {
        // PUSH DX - Push register word.
        testOpcode("52");
    }

    @Test
    public void push_53() {
        // PUSH BX - Push register word.
        testOpcode("53");
    }

    @Test
    public void push_54() {
        // PUSH SP - Push register word.
        testOpcode("54");
    }

    @Test
    public void push_55() {
        // PUSH BP - Push register word.
        testOpcode("55");
    }

    @Test
    public void push_56() {
        // PUSH SI - Push register word.
        testOpcode("56");
    }

    @Test
    public void push_57() {
        // PUSH DI - Push register word.
        testOpcode("57");
    }

    @Test
    public void pop_58() {
        // POP AX - Pop top of stack into word register.
        testOpcode("58");
    }

    @Test
    public void pop_59() {
        // POP CX - Pop top of stack into word register.
        testOpcode("59");
    }

    @Test
    public void pop_5A() {
        // POP DX - Pop top of stack into word register.
        testOpcode("5A");
    }

    @Test
    public void pop_5B() {
        // POP BX - Pop top of stack into word register.
        testOpcode("5B");
    }

    @Test
    public void pop_5C() {
        // POP SP - Pop top of stack into word register.
        testOpcode("5C");
    }

    @Test
    public void pop_5D() {
        // POP BP - Pop top of stack into word register.
        testOpcode("5D");
    }

    @Test
    public void pop_5E() {
        // POP SI - Pop top of stack into word register.
        testOpcode("5E");
    }

    @Test
    public void pop_5F() {
        // POP DI - Pop top of stack into word register.
        testOpcode("5F");
    }

    @Test
    public void jcc_70() {
        // JO rel8 - Jump short if overflow (OF=1)
        testOpcode("70");
    }

    @Test
    public void jcc_71() {
        // JNO rel8 - Jump short if not overflow (OF=0)
        testOpcode("71");
    }

    @Test
    public void jcc_72() {
        // JB/JNAE/JC rel8 - Jump short if carry/Jump short if not above or equal/Jump short if carry (CF=1)
        testOpcode("72");
    }

    @Test
    public void jcc_73() {
        // JNB/JAE/JNC rel8 - Jump short if not carry/Jump short if above or equal/Jump short if not carry (CF=0)
        testOpcode("73");
    }

    @Test
    public void jcc_74() {
        // JE/JZ rel8 - Jump short if equal/Jump short if zero (ZF=1)
        testOpcode("74");
    }

    @Test
    public void jcc_75() {
        // JNE/JNZ rel8 - Jump short if not equal/Jump short if not zero (ZF=0)
        testOpcode("75");
    }

    @Test
    public void jcc_76() {
        // JBE/JNA rel8 - Jump short if below or equal/Jump short if not above (CF=1 | ZF=1)
        testOpcode("76");
    }

    @Test
    public void jcc_77() {
        // JNBE/JA rel8 - Jump short if not below or equal/Jump short if above (CF=0 & ZF=0)
        testOpcode("77");
    }

    @Test
    public void jcc_78() {
        // JS rel8 - Jump short if sign (SF=1)
        testOpcode("78");
    }

    @Test
    public void jcc_79() {
        // JNS rel8 - Jump short if not sign (SF=0)
        testOpcode("79");
    }

    @Test
    public void jcc_7A() {
        // JP/JPE rel8 - Jump short if parity/Jump short if parity even (PF=1)
        testOpcode("7A");
    }

    @Test
    public void jcc_7B() {
        // JNP/JPO rel8 - Jump short if not parity/jump short if parity odd (PF=0)
        testOpcode("7B");
    }

    @Test
    public void jcc_7C() {
        // JL/JNGE rel8 - jump short if less/jump short if not greater than or equal (SF <> OE)
        testOpcode("7C");
    }

    @Test
    public void jcc_7D() {
        // JNL/JGE rel8 - jump short if not less/jump short if greater than or equal (SF == OE)
        testOpcode("7D");
    }

    @Test
    public void jcc_7E() {
        // JLE/JNG rel8 - Jump short if less than or equal/Not greater than
        testOpcode("7E");
    }

    @Test
    public void jcc_7F() {
        // JNLE/JG rel8 - Jump short if not less than or equal/Greater than (ZF=0 and SF=OF)
        testOpcode("7F");
    }

    @Test
    public void add_80() {
        // ADD r/m8,imm8 - Add immediate byte to r/m byte.
        testOpcode("80.0");
    }

    @Test
    public void or_80() {
        // OR r/m8,imm8 - OR immediate byte to r/m byte.
        testOpcode("80.1");
    }

    @Test
    public void adc_80() {
        // ADC r/m8,imm8 - Add with carry immediate byte to r/m byte.
        testOpcode("80.2");
    }

    @Test
    public void sbb_80() {
        // SBB r/m8,imm8 - Subtract with borrow immediate byte from r/m byte.
        testOpcode("80.3");
    }

    @Test
    public void and_80() {
        // AND r/m8,imm8 - AND immediate byte to r/m byte.
        testOpcode("80.4");
    }

    @Test
    public void sub_80() {
        // SUB r/m8,imm8 - Subtract immediate byte from r/m byte.
        testOpcode("80.5");
    }

    @Test
    public void xor_80() {
        // XOR r/m8,imm8 - Exclusive-OR immediate byte to r/m byte.
        testOpcode("80.6");
    }

    @Test
    public void cmp_80() {
        // CMP r/m8,imm8 - Compare immediate byte to r/m byte.
        testOpcode("80.7");
    }

    @Test
    public void add_81() {
        // ADD r/m16,imm16 - Add immediate word to r/m word.
        testOpcode("81.0");
    }

    @Test
    public void or_81() {
        // OR r/m16,imm16 - OR immediate word to r/m word.
        testOpcode("81.1");
    }

    @Test
    public void adc_81() {
        // ADC r/m16,imm16 - Add with carry immediate word to r/m word.
        testOpcode("81.2");
    }

    @Test
    public void sbb_81() {
        // SBB r/m16,imm16 - Subtract with borrow immediate word from r/m word.
        testOpcode("81.3");
    }

    @Test
    public void and_81() {
        // AND r/m16,imm16 - AND immediate word to r/m word.
        testOpcode("81.4");
    }

    @Test
    public void sub_81() {
        // SUB r/m16,imm16 - Subtract immediate word from r/m word.
        testOpcode("81.5");
    }

    @Test
    public void xor_81() {
        // XOR r/m16,imm16 - Exclusive-OR immediate word to r/m word.
        testOpcode("81.6");
    }

    @Test
    public void cmp_81() {
        // CMP r/m16,imm16 - Compare immediate word to r/m word.
        testOpcode("81.7");
    }

    @Test
    public void add_83() {
        // ADD r/m16,imm8 - Add signed-extended immediate byte to r/m word.
        testOpcode("83.0");
    }

    @Test
    public void or_83() {
        // OR r/m16,imm8 - OR signed-extended immediate byte to r/m word.
        testOpcode("83.1");
    }

    @Test
    public void adc_83() {
        // ADC r/m16,imm8 - Add with carry signed-extended immediate byte to r/m word.
        testOpcode("83.2");
    }

    @Test
    public void sbb_83() {
        // SBB r/m16,imm8 - Subtract with borrow signed-extended immediate byte from r/m word.
        testOpcode("83.3");
    }

    @Test
    public void and_83() {
        // AND r/m16,imm8 - AND signed-extended immediate byte to r/m word.
        testOpcode("83.4");
    }

    @Test
    public void sub_83() {
        // SUB r/m16,imm8 - Subtract signed-extended immediate byte from r/m word.
        testOpcode("83.5");
    }

    @Test
    public void xor_83() {
        // XOR r/m16,imm8 - Exclusive-OR signed-extended immediate byte to r/m word.
        testOpcode("83.6");
    }

    @Test
    public void cmp_83() {
        // CMP r/m16,imm8 - Compare signed-extended immediate byte to r/m word.
        testOpcode("83.7");
    }

    @Test
    public void test_84() {
        // TEST r/m8,r8 - AND byte register with r/m byte.
        testOpcode("84");
    }

    @Test
    public void test_85() {
        // TEST r/m16,r16 - AND word register with r/m word.
        testOpcode("85");
    }

    @Test
    public void xchg_86() {
        // XCHG r/m8,r8 - Exchange byte register with r/m byte.
        testOpcode("86");
    }

    @Test
    public void xchg_87() {
        // XCHG r/m16,r16 - Exchange word register with r/m word.
        testOpcode("87");
    }

    @Test
    public void mov_88() {
        // MOV r/m8,r8 - Move byte register into r/m byte.
        testOpcode("88");
    }

    @Test
    public void mov_89() {
        // MOV r/m16,r16 - Move word register into r/m word.
        testOpcode("89");
    }

    @Test
    public void mov_8A() {
        // MOV r8,r/m8 - Move r/m byte into byte register.
        testOpcode("8A");
    }

    @Test
    public void mov_8B() {
        // MOV Sreg,r/m16 - Move r/m word into word register.
        testOpcode("8B");
    }

    @Test
    public void mov_8C() {
        // MOV r/m16,Sreg - Move segment register to r/m register.
        testOpcode("8C");
    }

    @Test
    public void mov_8D() {
        // LEA r16,m - Store effective address for m in register 16.
        testOpcode("8D");
    }

    @Test
    public void mov_8E() {
        // MOV Sreg,r/m16 - Move r/m register to segment register.
        testOpcode("8E");
    }

    @Test
    public void pop_8F() {
        // POP m16 - Pop top of stack into memory word.
        testOpcode("8F");
    }

    @Test
    public void xchg_90() {
        // XCHG AX, AX - Exchange word register with AX
        testOpcode("90");
    }

    @Test
    public void xchg_91() {
        // XCHG AX, CX - Exchange word register with AX
        testOpcode("91");
    }

    @Test
    public void xchg_92() {
        // XCHG AX, DX - Exchange word register with AX
        testOpcode("92");
    }

    @Test
    public void xchg_93() {
        // XCHG AX, BX - Exchange word register with AX
        testOpcode("93");
    }

    @Test
    public void xchg_94() {
        // XCHG AX, SP - Exchange word register with AX
        testOpcode("94");
    }

    @Test
    public void xchg_95() {
        // XCHG AX, BP - Exchange word register with AX
        testOpcode("95");
    }

    @Test
    public void xchg_96() {
        // XCHG AX, SI - Exchange word register with AX
        testOpcode("96");
    }

    @Test
    public void xchg_97() {
        // XCHG AX, DI - Exchange word register with AX
        testOpcode("97");
    }

    @Test
    public void cbw_98() {
        // CBW - AX sign extend of AL.
        testOpcode("98");
    }

    @Test
    public void cwd_99() {
        // CWD - DX:AX <- sign-extend of AX.
        testOpcode("99");
    }

    @Test
    public void call_9A() {
        // CALL ptr16:16 - Call intersegment, to full pointer given (far call).
        testOpcode("9A");
    }

    @Test
    public void call_9B() {
        // WAIT - Wait until BUSY pin is inactive (HIGH).
        // No single step test.
    }

    @Test
    public void pushf_9C() {
        // PUSHF - Push FLAGS.
        testOpcode("9C");
    }

    @Test
    public void popf_9D() {
        // POPF - Pop top of stack into FLAGS.
        testOpcode("9D");
    }

    @Test
    public void sahf_9E() {
        // SAHF - Store AH flags SH ZF xx AF xx PF xx CF
        testOpcode("9E");
    }

    @Test
    public void lahf_9F() {
        // LAHF - Load: AH = flags SF ZF xx AF xx PF xx CF
        testOpcode("9F");
    }

    @Test
    public void mov_A0() {
        // MOV AL,moffs8 - Move byte at (seg:offset) to AL.
        testOpcode("A0");
    }

    @Test
    public void mov_A1() {
        // MOV AX,moffs16 - Move byte at (seg:offset) to AX.
        testOpcode("A1");
    }

    @Test
    public void mov_A2() {
        // MOV moffs8,AL - Move AL to (seg:offset).
        testOpcode("A2");
    }

    @Test
    public void mov_A3() {
        // MOV moffs16,AX - Move AX to (seg:offset).
        testOpcode("A3");
    }

    @Test
    public void movsb_A4() {
        // MOVSB - Move byte [DS:SI] to ES:[DI].
        testOpcode("A4");
    }

    @Test
    public void movsw_A5() {
        // MOVSW - Move word [DS:SI] to ES:[DI].
        testOpcode("A5");
    }

    @Test
    public void cmpsb_A6() {
        // CMPSB - Compare bytes [DS:SI] with ES:[DI].
        testOpcode("A6");
    }

    @Test
    public void cmpsw_A7() {
        // CMPSW - Compare words [DS:SI] with ES:[DI].
        testOpcode("A7");
    }

    @Test
    public void test_A8() {
        // TEST AL,imm8 - AND immediate byte with AL.
        testOpcode("A8");
    }

    @Test
    public void test_A9() {
        // TEST AX,imm16 - AND immediate word with AX.
        testOpcode("A9");
    }

    @Test
    public void stosb_AA() {
        // STOSB - Store AL in byte ES:[DI].
        testOpcode("AA");
    }

    @Test
    public void stosb_AB() {
        // STOSB - Store AX in word ES:[DI].
        testOpcode("AB");
    }

    @Test
    public void lodsb_AC() {
        // LODSB - Load byte DS:[SI] into AL.
        testOpcode("AC");
    }

    @Test
    public void lodsw_AD() {
        // LODSW - Load word DS:[SI] into AX.
        testOpcode("AD");
    }

    @Test
    public void scasb_AE() {
        // SCASB - Compare bytes AL - ES:[DI].
        testOpcode("AE");
    }

    @Test
    public void scasw_AF() {
        // SCASW - Compare words AX - ES:[DI].
        testOpcode("AF");
    }

    @Test
    public void mov_B0() {
        // MOV AL, imm8 - Move immediate byte to register.
        testOpcode("B0");
    }

    @Test
    public void mov_B1() {
        // MOV CL, imm8 - Move immediate byte to register.
        testOpcode("B1");
    }

    @Test
    public void mov_B2() {
        // MOV DL, imm8 - Move immediate byte to register.
        testOpcode("B2");
    }

    @Test
    public void mov_B3() {
        // MOV BL, imm8 - Move immediate byte to register.
        testOpcode("B3");
    }

    @Test
    public void mov_B4() {
        // MOV AH, imm8 - Move immediate byte to register.
        testOpcode("B4");
    }

    @Test
    public void mov_B5() {
        // MOV CH, imm8 - Move immediate byte to register.
        testOpcode("B5");
    }

    @Test
    public void mov_B6() {
        // MOV DH, imm8 - Move immediate byte to register.
        testOpcode("B6");
    }

    @Test
    public void mov_B7() {
        // MOV BH, imm8 - Move immediate byte to register.
        testOpcode("B7");
    }

    @Test
    public void mov_B8() {
        // MOV AX, imm16 - Move immediate word to register.
        testOpcode("B8");
    }

    @Test
    public void mov_B9() {
        // MOV CX, imm16 - Move immediate word to register.
        testOpcode("B9");
    }

    @Test
    public void mov_BA() {
        // MOV DX, imm16 - Move immediate word to register.
        testOpcode("BA");
    }

    @Test
    public void mov_BB() {
        // MOV BX, imm16 - Move immediate word to register.
        testOpcode("BB");
    }

    @Test
    public void mov_BC() {
        // MOV SP, imm16 - Move immediate word to register.
        testOpcode("BC");
    }

    @Test
    public void mov_BD() {
        // MOV BP, imm16 - Move immediate word to register.
        testOpcode("BD");
    }

    @Test
    public void mov_BE() {
        // MOV DI, imm16 - Move immediate word to register.
        testOpcode("BE");
    }

    @Test
    public void mov_BF() {
        // MOV SI, imm16 - Move immediate word to register.
        testOpcode("BF");
    }

    @Test
    public void ret_C2() {
        // RET imm16 - Return (near).
        testOpcode("C2");
    }

    @Test
    public void ret_C3() {
        // RET - Return (near).
        testOpcode("C3");
    }

    @Test
    public void les_C4() {
        // LES r16,m16:16 - Load ES:r16 with pointer from memory.
        testOpcode("C4");
    }

    @Test
    public void lds_C5() {
        // LDS r16,m16:16 - Load DS:r16 with pointer from memory.
        testOpcode("C5");
    }

    @Test
    public void mov_C6() {
        // MOV r/m8,imm8 - Move immediate byte to r/m byte.
        testOpcode("C6");
    }

    @Test
    public void mov_C7() {
        // MOV r/m16,imm16 - Move immediate word to r/m word.
        testOpcode("C7");
    }

    @Test
    public void ret_CA() {
        // RET - Return (far), popping off N additional bytes.
        testOpcode("CA");
    }

    @Test
    public void ret_CB() {
        // RET - Return (far).
        testOpcode("CB");
    }

    @Test
    public void int_CC() {
        // INT3 - Interrupt 3 -- trap to debugger.
        testOpcode("CC");
    }

    @Test
    public void int_CD() {
        // INT imm8 - Interrupt numbered by immediate byte.
        testOpcode("CD");
    }

    @Test
    public void int_CE() {
        // INTO - Interrupt 4 -- if overflow flag is 1.
        testOpcode("CE");
    }

    @Test
    public void iret_CF() {
        // IRET - Interrupt return (far return and pop flags).
        testOpcode("CF");
    }

    @Test
    public void rol_D0_0() {
        // ROL r/m8,1 - Rotate 8 bits r/m byte left once.
        testOpcode("D0.0");
    }

    @Test
    public void ror_D0_1() {
        // ROR r/m8,1 - Rotate 8 bits r/m byte right once.
        testOpcode("D0.1");
    }

    @Test
    public void rlc_D0_2() {
        // RCL r/m8,1 - Rotate 9 bits (CF,r/m byte) left once.
        testOpcode("D0.2");
    }

    @Test
    public void rrc_D0_3() {
        // RCR r/m8,1 - Rotate 9 bits (CF,r/m byte) right once.
        testOpcode("D0.3");
    }

    @Test
    public void sal_shr_D0_4() {
        // SAL/SHL r/m8,1 - Multiply r/m byte by 2 once.
        testOpcode("D0.4");
    }

    @Test
    public void shr_D0_5() {
        // SHR r/m8,1 - Unsigned divide r/m byte by 2 once.
        testOpcode("D0.5");
    }

    @Test
    public void sar_D0_7() {
        // SAR r/m8,1 - Signed divide r/m byte by 2 once.
        testOpcode("D0.7");
    }

    @Test
    public void rol_D1_0() {
        // ROL r/m16,1 - Rotate 16 bits r/m byte left once.
        testOpcode("D1.0");
    }

    @Test
    public void ror_D1_1() {
        // ROR r/m16,1 - Rotate 16 bits r/m byte right once.
        testOpcode("D1.1");
    }

    @Test
    public void rlc_D1_2() {
        // RCL r/m16,1 - Rotate 17 bits (CF,r/m byte) left once.
        testOpcode("D1.2");
    }

    @Test
    public void rrc_D1_3() {
        // RCR r/m16,1 - Rotate 17 bits (CF,r/m byte) right once.
        testOpcode("D1.3");
    }

    @Test
    public void sal_shr_D1_4() {
        // SAL/SHL r/m16,1 - Multiply r/m word by 2 once.
        testOpcode("D1.4");
    }

    @Test
    public void shr_D1_5() {
        // SHR r/m16,1 - Unsigned divide r/m word by 2 once.
        testOpcode("D1.5");
    }

    @Test
    public void sar_D1_7() {
        // SAR r/m16,1 - Signed divide r/m word by 2 once.
        testOpcode("D1.7");
    }

    @Test
    public void rol_D2_0() {
        // ROL r/m8,1 - Rotate 8 bits r/m byte left CL times.
        testOpcode("D2.0");
    }

    @Test
    public void ror_D2_1() {
        // ROR r/m8,1 - Rotate 8 bits r/m byte right CL times.
        testOpcode("D2.1");
    }

    @Test
    public void rlc_D2_2() {
        // RCL r/m8,1 - Rotate 9 bits (CF,r/m byte) left CL times.
        testOpcode("D2.2");
    }

    @Test
    public void rrc_D2_3() {
        // RCR r/m8,1 - Rotate 9 bits (CF,r/m byte) right CL times.
        testOpcode("D2.3");
    }

    @Test
    public void sal_shr_D2_4() {
        // SAL/SHL r/m8,1 - Multiply r/m byte by 2 CL times.
        testOpcode("D2.4");
    }

    @Test
    public void shr_D2_5() {
        // SHR r/m8,1 - Unsigned divide r/m byte by 2 CL times.
        testOpcode("D2.5");
    }

    @Test
    public void sar_D2_7() {
        // SAR r/m8,1 - Signed divide r/m byte by 2 CL times.
        testOpcode("D2.7");
    }

    @Test
    public void rol_D3_0() {
        // ROL r/m16,1 - Rotate 16 bits r/m byte left CL times.
        testOpcode("D3.0");
    }

    @Test
    public void ror_D3_1() {
        // ROR r/m16,1 - Rotate 16 bits r/m byte right CL times.
        testOpcode("D3.1");
    }

    @Test
    public void rlc_D3_2() {
        // RCL r/m16,1 - Rotate 17 bits (CF,r/m byte) left CL times.
        testOpcode("D3.2");
    }

    @Test
    public void rrc_D3_3() {
        // RCR r/m16,1 - Rotate 17 bits (CF,r/m byte) right CL times.
        testOpcode("D3.3");
    }

    @Test
    public void sal_shr_D3_4() {
        // SAL/SHL r/m16,1 - Multiply r/m word by 2 CL times.
        testOpcode("D3.4");
    }

    @Test
    public void shr_D3_5() {
        // SHR r/m16,1 - Unsigned divide r/m word by 2 CL times.
        testOpcode("D3.5");
    }

    @Test
    public void sar_D3_7() {
        // SAR r/m16,1 - Signed divide r/m word by 2 CL times.
        testOpcode("D3.7");
    }

    @Test
    public void aam_D4() {
        // AAM base - ASCII adjust after multiplication.
        testOpcode("D4");
    }

    @Test
    public void aad_D5() {
        // AAD base - ASCII adjust before division.
        testOpcode("D5");
    }

    // D6 - SALC. Undocumented. Not implemented.

    @Test
    public void xlat_D7() {
        // XLAT - Set AL to memory byte DS:[BX + unsigned AL].
        testOpcode("D7");
    }

    @Test
    public void loop_E0() {
        // LOOPNZ/LOOPNE - DEC Count; jump short if Count 0 and ZF = 0
        testOpcode("E0");
    }

    @Test
    public void loop_E1() {
        // LOOPZ/LOOPE - DEC Count; jump short if Count 0 and ZF=1
        testOpcode("E1");
    }

    @Test
    public void loop_E2() {
        // LOOP rel8 - DEC Count; jump short if Count 0
        testOpcode("E2");
    }

    @Test
    public void jcxz_E3() {
        // JCXZ rel8  - Jump short if CX register is 0.
        testOpcode("E3");
    }

    @Test
    public void in_E4() {
        // IN AL,imm8 - Input byte from immediate port into AL.
        testOpcode("E4");
    }

    @Test
    public void in_E5() {
        // IN AX,imm8 - Input word from immediate port into AX.
        testOpcode("E5");
    }

    @Test
    public void out_E6() {
        // OUT imm8,AL - Output byte AL to immediate port number.
        testOpcode("E6");
    }

    @Test
    public void out_E7() {
        // OUT imm16,AX - Output byte AX to immediate port number.
        testOpcode("E7");
    }

    @Test
    public void call_E8() {
        // CALL rel16 - Call near, displacement relative to next instruction.
        testOpcode("E8");
    }

    @Test
    public void call_E9() {
        // JMP rel16 - Jump short.
        testOpcode("E9");
    }

    @Test
    public void call_EA() {
        // JMP ptr16:16 - Jump intersegment, 4-byte immediate address.
        testOpcode("EA");
    }

    @Test
    public void call_EB() {
        // JMP rel8 - Jump short.
        testOpcode("EB");
    }

    @Test
    public void in_EC() {
        // IN AL,DX - Input byte from port DX into AL.
        testOpcode("EC");
    }

    @Test
    public void in_ED() {
        // IN AX,DX - Input byte from port DX into AL.
        testOpcode("ED");
    }

    @Test
    public void out_EE() {
        // OUT DX,AL - Output byte AL to port number in DX.
        testOpcode("EE");
    }

    @Test
    public void out_EF() {
        // OUT DX,AL - Output word AX to port number in DX.
        testOpcode("EF");
    }

    // F0 - LOCK - Assert LOCK# signal for the next instruction. No single step test.

    // F1 - LOCK - Undocumented. Not implemented.

    // F2 - REPNZ/REPNE - Repeat while not zero/repeat while not equal. No single step test.

    // F3 - REPZ/REPE - Repeat while zero/repeat while equal. No single step test.

    public void hlt_F4() {
        // HLT - Halt.
        // No single step test.
    }

    @Test
    public void cmc_F5() {
        // CMC - Complement carry flag.
        testOpcode("F5");
    }

    @Test
    public void test_F6_0() {
        // TEST r/m8,imm8 - And immediate byte with r/m byte.
        testOpcode("F6.0");
    }

    // F6.1 - TEST - Undocumented. Not implemented.

    @Test
    public void not_F6_2() {
        // NOT r/m8 - Reverse each bit of r/m byte.
        testOpcode("F6.2");
    }

    @Test
    public void neg_F6_3() {
        // NEG r/m8 - Two's complement negate r/m byte.
        testOpcode("F6.3");
    }

    @Test
    public void mul_F6_4() {
        // MUL r/m8 - Unsigned multiply (AX = AL * r/m byte)
        testOpcode("F6.4");
    }

    @Test
    public void mul_F6_5() {
        // MUL r/m8 - Signed multiply (AX = AL * r/m byte)
        testOpcode("F6.5");
    }

    @Test
    public void div_F6_6() {
        // DIV r/m8 - Unsigned divide AX by r/m byte (AL=QUO, AH=REM)
        // Without implementing the full microcode of an 8088 for the DIV instruction, it's not possible to calculate
        // the undocumented flags to pass the single step tests.
        testOpcode("F6.6", (short) (Flags.SIGN | Flags.ZERO | Flags.PARITY | Flags.AUX_CARRY | Flags.CARRY | Flags.OVERFLOW));
    }

    @Test
    public void idiv_F6_7() {
        // IDIV r/m8 - Signed divide AX by r/m byte (AL=QUO, AH=REM)
        // Without implementing the full microcode of an 8088 for the IDIV instruction, it's not possible to calculate
        // the undocumented flags to pass the single step tests.
        testOpcode("F6.7", (short) (Flags.SIGN | Flags.ZERO | Flags.PARITY | Flags.AUX_CARRY | Flags.CARRY | Flags.OVERFLOW));
    }

    @Test
    public void test_F7_0() {
        // TEST r/m16,imm16 - And immediate word with r/m word.
        testOpcode("F7.0");
    }

    // F7.1 - TEST - Undocumented. Not implemented.

    @Test
    public void not_F7_2() {
        // NOT r/m16 - Reverse each bit of r/m word.
        testOpcode("F7.2");
    }

    @Test
    public void neg_F7_3() {
        // NEG r/m16 - Two's complement negate r/m word.
        testOpcode("F7.3");
    }

    @Test
    public void mul_F7_4() {
        // MUL r/m16 - Unsigned multiply (DX:AX = AX * r/m word)
        testOpcode("F7.4");
    }

    @Test
    public void mul_F7_5() {
        // MUL r/m16 - Signed multiply (DX:AX = AX * r/m word)
        testOpcode("F7.5");
    }

    @Test
    public void div_F7_6() {
        // DIV r/m16 - Unsigned divide DX:AX by r/m word (AX=QUO, DX=REM)
        // Without implementing the full microcode of an 8088 for the DIV instruction, it's not possible to calculate
        // the undocumented flags to pass the single step tests.
        testOpcode("F7.6", (short) (Flags.SIGN | Flags.ZERO | Flags.PARITY | Flags.AUX_CARRY | Flags.CARRY | Flags.OVERFLOW));
    }

    @Test
    public void div_F7_7() {
        // IDIV r/m8 - Signed divide DX/AX by r/m word (AX=QUO, DX=REM)
        // Without implementing the full microcode of an 8088 for the IDIV instruction, it's not possible to calculate
        // the undocumented flags to pass the single step tests.
        testOpcode("F7.7", (short) (Flags.SIGN | Flags.ZERO | Flags.PARITY | Flags.AUX_CARRY | Flags.CARRY | Flags.OVERFLOW));
    }

    @Test
    public void clc_F8() {
        // CLC - Clear carry flag.
        testOpcode("F8");
    }

    @Test
    public void stc_F9() {
        // STC - Set carry flag.
        testOpcode("F9");
    }

    @Test
    public void cli_FA() {
        // CLI - Clear interrupt flag (disable interrupts).
        testOpcode("FA");
    }

    @Test
    public void sti_FB() {
        // STI - Set interrupt flag (enable interrupts).
        testOpcode("FB");
    }

    @Test
    public void cld_FC() {
        // CLD - Clear direction flag (down).
        testOpcode("FC");
    }

    @Test
    public void std_FD() {
        // STD - Set direction flag (up).
        testOpcode("FD");
    }

    @Test
    public void inc_FE_0() {
        // INC r/m8 - Increment r/m byte by 1.
        testOpcode("FE.0");
    }

    @Test
    public void dec_FE_1() {
        // DEC r/m8 - Decrement r/m byte by 1.
        testOpcode("FE.1");
    }

    // FE.2
    @Test
    public void inc_FF_0() {
        // INC r/m16 - Increment r/m word by 1.
        testOpcode("FF.0");
    }

    @Test
    public void dec_FF_1() {
        // DEC r/m16 - Decrement r/m word by 1.
        testOpcode("FF.1");
    }

    @Test
    public void call_FF_2() {
        // CALL r/m16 - Call near, register indirect/memory indirect.
        testOpcode("FF.2");
    }

    @Test
    public void call_FF_3() {
        // CALL m16:16 - Call intersegment address at r/m dword.
        testOpcode("FF.3");
    }

    @Test
    public void jmp_FF_4() {
        // JMP r/m16 - Jump near indirect.
        testOpcode("FF.4");
    }

    @Test
    public void jmp_FF_5() {
        // JMP m16:16 - Jump r/m16:16 indirect and intersegment.
        testOpcode("FF.5");
    }

    @Test
    public void push_FF_6() {
        // PUSH m16 - Push memory word.
        testOpcode("FF.6");
    }

    // FF.7 - PUSH - Undocumented. Not implemented.

    private void testOpcode(String opcode) {
        testOpcode(opcode, (short) 0);
    }

    private void testOpcode(String opcode, short excludeFlags) {
        final Tests tests = new Tests(opcode, excludeFlags);
        tests.load();
        for (int i = 0; i < tests.testCount(); i++) {
            tests.test(i);
        }
    }
}
