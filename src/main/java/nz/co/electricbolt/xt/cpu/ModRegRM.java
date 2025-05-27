// ModRegRM.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

/**
 * <code>
 * +---+---+---+---+---+---+---+---+
 * | mod   | reg       | r/m       |
 * +---+---+---+---+---+---+---+---+
 * | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
 * +---+---+---+---+---+---+---+---+
 * <p>
 * +-----------------------+-----+-----+-----+-----+-----+-----+-----+-----+
 * | r8(/r)                | AL  | CL  | DL  | BL  | AH  | CH  | DH  | BH  |
 * | r16(/r)               | AX  | CX  | DX  | BX  | SP  | BP  | SI  | DI  |
 * | reg =                 | 000 | 001 | 010 | 011 | 100 | 101 | 110 | 111 |
 * +-----------------------+-----+-----+-----+-----+-----+-----+-----+-----+
 * <p>
 * +-------------+---------+-----+-----+-----+-----+-----+-----+-----+-----+
 * | Effective   |         | mod reg r/m values in hexadecimal:            |
 * | address     | mod r/m |                                               |
 * +-------------+---------+-----+-----+-----+-----+-----+-----+-----+-----+
 * | [BX+SI]     |     000 |  00 |  08 |  10 |  18 |  20 |  28 |  30 |  38 |
 * | [BX+DI]     |     001 |  01 |  09 |  11 |  19 |  21 |  29 |  31 |  39 |
 * | [BP+SI]     |     010 |  02 |  0A |  12 |  1A |  22 |  2A |  32 |  3A |
 * | [BP+DI]     | 00  011 |  03 |  0B |  13 |  1B |  23 |  2B |  33 |  3B |
 * | [SI]        |     100 |  04 |  0C |  14 |  1C |  24 |  2C |  34 |  3C |
 * | [DI]        |     101 |  05 |  0D |  15 |  1D |  25 |  2D |  35 |  3D |
 * | disp16      |     110 |  06 |  0E |  16 |  1E |  26 |  2E |  36 |  3E |
 * | [BX]        |     111 |  07 |  0F |  17 |  1F |  27 |  2F |  37 |  3F |
 * +-------------+---------+-----+-----+-----+-----+-----+-----+-----+-----+
 * | [BX+SI]+d8  |     000 |  40 |  48 |  50 |  58 |  60 |  68 |  70 |  78 |
 * | [BX+DI]+d8  |     001 |  41 |  49 |  51 |  59 |  61 |  69 |  71 |  79 |
 * | [BP+SI]+d8  |     010 |  42 |  4A |  52 |  5A |  62 |  6A |  72 |  7A |
 * | [BP+DI]+d8  | 01  011 |  43 |  4B |  53 |  5B |  63 |  6B |  73 |  7B |
 * | [SI]+d8     |     100 |  44 |  4C |  54 |  5C |  64 |  6C |  74 |  7C |
 * | [DI]+d8     |     101 |  45 |  4D |  55 |  5D |  65 |  6D |  75 |  7D |
 * | [BP]+d8     |     110 |  46 |  4E |  56 |  5E |  66 |  6E |  76 |  7E |
 * | [BX]+d8     |     111 |  47 |  4F |  57 |  5F |  67 |  6F |  77 |  7F |
 * +-------------+---------+-----+-----+-----+-----+-----+-----+-----+-----+
 * | [BX+SI]+d16 |     000 |  80 |  88 |  90 |  98 |  A0 |  A8 |  B0 |  B8 |
 * | [BX+DI]+d16 |     001 |  81 |  89 |  91 |  99 |  A1 |  A9 |  B1 |  B9 |
 * | [BP+SI]+d16 |     010 |  82 |  8A |  92 |  9A |  A2 |  AA |  B2 |  BA |
 * | [BP+DI]+d16 | 10  011 |  83 |  8B |  93 |  9B |  A3 |  AB |  B3 |  BB |
 * | [SI]+d16    |     100 |  84 |  8C |  94 |  9C |  A4 |  AC |  B4 |  BC |
 * | [DI]+d16    |     101 |  85 |  8D |  95 |  9D |  A5 |  AD |  B5 |  BD |
 * | [BP]+d16    |     110 |  86 |  8E |  96 |  9E |  A6 |  AE |  B6 |  BE |
 * | [BX]+d16    |     111 |  87 |  8F |  97 |  9F |  A7 |  AF |  B7 |  BF |
 * +-------------+---------+-----+-----+-----+-----+-----+-----+-----+-----+
 * | AX  /  AL   |     000 |  C0 |  C8 |  D0 |  D8 |  E0 |  E8 |  F0 |  F8 |
 * | CX  /  CL   |     001 |  C1 |  C9 |  D1 |  D9 |  E1 |  E9 |  F1 |  F9 |
 * | DX  /  DL   |     010 |  C2 |  CA |  D2 |  DA |  E2 |  EA |  F2 |  FA |
 * | BX  /  BL   | 11  011 |  C3 |  CB |  D3 |  DB |  E3 |  EB |  F3 |  FB |
 * | SP  /  AH   |     100 |  C4 |  CC |  D4 |  DC |  E4 |  EC |  F4 |  FC |
 * | BP  /  CH   |     101 |  C5 |  CD |  D5 |  DD |  E5 |  ED |  F5 |  FD |
 * | SI  /  DH   |     110 |  C6 |  CE |  D6 |  DE |  E6 |  EE |  F6 |  FE |
 * | DI  /  BH   |     111 |  C7 |  CF |  D7 |  DF |  E7 |  EF |  F7 |  FF |
 * +-------------+---------+-----+-----+-----+-----+-----+-----+-----+-----+
 * </code>
 */

public class ModRegRM {

    private final CPU cpu;

    public ModRegRM(final CPU cpu) {
        this.cpu = cpu;
    }

    public SegOfs fetchSegOfs() {
        final short offset = cpu.fetch16();
        final Reg16 segment = cpu.getSegmentOverride() == null ? cpu.reg.DS : cpu.getSegmentOverride();
        cpu.setSegmentOverride(null);
        return new SegOfs(segment, offset);
    }

    public RegRM8 fetch8() {
        final int value = cpu.fetch8() & 0xFF;
        final int modValue = value >> 6;
        final int regValue = (value >> 3) & 0x07;
        final int rmValue = value & 0x07;
        if (modValue == 3) {
            return new RegRM8(getReg8(regValue), getReg8(rmValue), regValue);
        } else {
            return new RegRM8(getReg8(regValue), effectiveAddress(modValue, rmValue), cpu.memory, regValue);
        }
    }

    public RegRM16 fetch16() {
        return fetch16(false);
    }

    public RegRM16 fetch16SReg() {
        return fetch16(true);
    }

    private RegRM16 fetch16(boolean SReg) {
        final int value = cpu.fetch8() & 0xFF;
        final int modValue = value >> 6;
        final int regValue = (value >> 3) & 0x07;
        final int rmValue = value & 0x07;
        if (modValue == 3) {
            return new RegRM16(SReg ? getSegReg(regValue) : getReg16(regValue), getReg16(rmValue), regValue);
        } else {
            return new RegRM16(SReg ? getSegReg(regValue) : getReg16(regValue), effectiveAddress(modValue, rmValue), cpu.memory, regValue);
        }
    }

    private SegOfs effectiveAddress(final int mod, final int rm) {
        int displacement = 0;
        Reg16 segment = cpu.getSegmentOverride();
        cpu.setSegmentOverride(null);

        if (mod == 0 && rm == 6) {
            displacement = cpu.fetch16();
            if (segment == null) {
                segment = cpu.reg.DS;
            }
        } else {
            if (mod == 1) {
                displacement = cpu.fetch8();
            } else if (mod == 2) {
                displacement = cpu.fetch16();
            }

            displacement += switch (rm) {
                case 0 -> (cpu.reg.BX.getValue() & 0xFFFF) + ((int) cpu.reg.SI.getValue() & 0xFFFF);
                case 1 -> (cpu.reg.BX.getValue() & 0xFFFF) + ((int) cpu.reg.DI.getValue() & 0xFFFF);
                case 2 -> (cpu.reg.BP.getValue() & 0xFFFF) + ((int) cpu.reg.SI.getValue() & 0xFFFF);
                case 3 -> (cpu.reg.BP.getValue() & 0xFFFF) + ((int) cpu.reg.DI.getValue() & 0xFFFF);
                case 4 -> (int) (cpu.reg.SI.getValue() & 0xFFFF);
                case 5 -> (int) (cpu.reg.DI.getValue() & 0xFFFF);
                case 6 -> (int) (cpu.reg.BP.getValue() & 0xFFFF);
                default -> (int) (cpu.reg.BX.getValue() & 0xFFFF);
            };
            if (segment == null) {
                if (rm == 2 || rm == 3 || rm == 6) {
                    segment = cpu.reg.SS;
                } else {
                    segment = cpu.reg.DS;
                }
            }
        }
        return new SegOfs(segment, (short) (displacement & 0xFFFF));
    }

    public Reg8 getReg8(final int reg) {
        return switch (reg) {
            case 0 -> cpu.reg.AL;
            case 1 -> cpu.reg.CL;
            case 2 -> cpu.reg.DL;
            case 3 -> cpu.reg.BL;
            case 4 -> cpu.reg.AH;
            case 5 -> cpu.reg.CH;
            case 6 -> cpu.reg.DH;
            default -> cpu.reg.BH;
        };
    }

    public Reg16 getReg16(final int reg) {
        return switch (reg) {
            case 0 -> cpu.reg.AX;
            case 1 -> cpu.reg.CX;
            case 2 -> cpu.reg.DX;
            case 3 -> cpu.reg.BX;
            case 4 -> cpu.reg.SP;
            case 5 -> cpu.reg.BP;
            case 6 -> cpu.reg.SI;
            default -> cpu.reg.DI;
        };
    }

    public Reg16 getSegReg(final int reg) {
        return switch (reg) {
            case 0 -> cpu.reg.ES;
            case 1 -> cpu.reg.CS;
            case 2 -> cpu.reg.SS;
            case 3 -> cpu.reg.DS;
            // Undocumented behaviour to allow single step tests to pass. Registers replicated 4-7.
            case 4 -> cpu.reg.ES;
            case 5 -> cpu.reg.CS;
            case 6 -> cpu.reg.SS;
            default -> cpu.reg.DS;
        };
    }
}
