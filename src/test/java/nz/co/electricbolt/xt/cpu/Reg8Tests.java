// Reg8Tests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Reg8Tests {

    @Test
    void setValueTests() {
        final Reg16 AX = new Reg16("AX");
        final Reg8 AL = new Reg8(AX, false);
        final Reg8 AH = new Reg8(AX, true);

        AX.setValue((short) 0);
        assertEquals((short) 0x0000, AX.getValue());
        assertEquals((byte) 0, AL.getValue());
        assertEquals((byte) 0, AH.getValue());
        AL.setValue((byte) 0x12);
        assertEquals((short) 0x0012, AX.getValue());
        assertEquals((byte) 0x12, AL.getValue());
        assertEquals((byte) 0, AH.getValue());
        AH.setValue((byte) 0x34);
        assertEquals((short) 0x3412, AX.getValue());
        assertEquals((byte) 0x12, AL.getValue());
        assertEquals((byte) 0x34, AH.getValue());

        AX.setValue((short) 1);
        assertEquals((short) 0x0001, AX.getValue());
        assertEquals((byte) 1, AL.getValue());
        assertEquals((byte) 0, AH.getValue());

        AX.setValue((short) 0x55AA);
        assertEquals((short) 0x55AA, AX.getValue());
        assertEquals((byte) 0xAA, AL.getValue());
        assertEquals((byte) 0x55, AH.getValue());

        AX.setValue((short) 0xFFFE);
        assertEquals((short) 0xFFFE, AX.getValue());
        assertEquals((byte) 0xFE, AL.getValue());
        assertEquals((byte) 0xFF, AH.getValue());

        AX.setValue((short) 0xFFFF);
        assertEquals((short) 0xFFFF, AX.getValue());
        assertEquals((byte) 0xFF, AL.getValue());
        assertEquals((byte) 0xFF, AH.getValue());
        AL.setValue((byte) 0x12);
        assertEquals((short) 0xFF12, AX.getValue());
        assertEquals((byte) 0x12, AL.getValue());
        assertEquals((byte) 0xFF, AH.getValue());
        AH.setValue((byte) 0x34);
        assertEquals((short) 0x3412, AX.getValue());
        assertEquals((byte) 0x12, AL.getValue());
        assertEquals((byte) 0x34, AH.getValue());

        AX.setValue((short) 0x10000);
        assertEquals((short) 0, AX.getValue());
        assertEquals((byte) 0, AL.getValue());
        assertEquals((byte) 0, AH.getValue());

        AX.setValue((short) 0x10001);
        assertEquals((short) 1, AX.getValue());
        assertEquals((byte) 1, AL.getValue());
        assertEquals((byte) 0, AH.getValue());

        AX.setValue((short) -1);
        assertEquals((short) 0xFFFF, AX.getValue());
        assertEquals((byte) 0xFF, AL.getValue());
        assertEquals((byte) 0xFF, AH.getValue());

        AX.setValue((short) -2);
        assertEquals((short) 0xFFFE, AX.getValue());
        assertEquals((byte) 0xFE, AL.getValue());
        assertEquals((byte) 0xFF, AH.getValue());
    }

    @Test
    void miscTests() {
        final Reg16 AX = new Reg16("AX", (short) 0x2459);
        final Reg8 AL = new Reg8(AX, false);
        final Reg8 AH = new Reg8(AX, true);
        assertEquals("AL", AL.getName());
        assertEquals("AH", AH.getName());
        assertEquals("AL=59", AL.toString());
        assertEquals("AH=24", AH.toString());

        final Reg16 BX = new Reg16("BX", (short) 0x1234);
        final Reg8 BL = BX.low();
        final Reg8 BH = BX.high();
        assertEquals(0x34, BL.getValue());
        assertEquals(0x12, BH.getValue());

        assertEquals(AL, AX.low());
        assertEquals(AH, AX.high());

        assertNotEquals(new Object(), AL);
    }
}
