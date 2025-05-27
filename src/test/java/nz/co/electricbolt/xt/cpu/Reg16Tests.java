// Reg16Tests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Reg16Tests {

    @Test
    void setValueTests() {
        final Reg16 RA = new Reg16("RA");
        assertEquals((short) 0, RA.getValue());
        RA.setValue((short) 0);
        assertEquals((short) 0x0000, RA.getValue());
        RA.setValue((short) 1);
        assertEquals((short) 0x0001, RA.getValue());
        RA.setValue((short) 0xFFFE);
        assertEquals((short) 0xFFFE, RA.getValue());
        RA.setValue((short) 0xFFFF);
        assertEquals((short) 0xFFFF, RA.getValue());
        RA.setValue((short) 0x10000);
        assertEquals((short) 0, RA.getValue());
        RA.setValue((short) 0x10001);
        assertEquals((short) 1, RA.getValue());
        RA.setValue((short) -1);
        assertEquals((short) 0xFFFF, RA.getValue());
        RA.setValue((short) -2);
        assertEquals((short) 0xFFFE, RA.getValue());
    }

    @Test
    void addValueTests() {
        final Reg16 RA = new Reg16("RA");
        RA.add((short) 1);
        assertEquals((short) 0x0001, RA.getValue());
        RA.add((short) 2);
        assertEquals((short) 0x0003, RA.getValue());
        RA.setValue((short) 0);
        RA.add((short) 0xFFFF);
        assertEquals((short) 0xFFFF, RA.getValue());
        RA.setValue((short) 0);
        RA.add((short) 0x10000);
        assertEquals((short) 0x0000, RA.getValue());
        RA.setValue((short) 0);
        RA.add((short) 0x12345);
        assertEquals((short) 0x2345, RA.getValue());

        RA.setValue((short) 0);
        RA.add((short) -1);
        assertEquals((short) 0xFFFF, RA.getValue());
        RA.add((short) -2);
        assertEquals((short) 0xFFFD, RA.getValue());

        RA.setValue((short) 0x1234);
        RA.add((short) -1);
        assertEquals((short) 0x1233, RA.getValue());
        RA.add((short) -2);
        assertEquals((short) 0x1231, RA.getValue());
    }

    @Test
    void miscTests() {
        final Reg16 RA = new Reg16("RA", (short) 0x2459);
        final Reg16 RB = RA.copy();
        assertEquals(RA, RB);
        assertEquals(RB.getValue(), RA.getValue());

        RA.setValue((short) 0x4567);
        assertEquals((short) 0x4567, RA.getValue());
        assertEquals((short) 0x2459, RB.getValue());
        assertEquals("RA=4567", RA.toString());
        assertEquals("RA", RA.getName());
        assertNotEquals(RB.getValue(), RA.getValue());

        assertNotEquals(new Object(), RA);
    }
}