// SegOfsTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SegOfsTests {

    @Test
    public void segOfsTests() {
        assertEquals(0x00000, new SegOfs((short) 0x0000, (short) 0x0000).toLinearAddress());
        assertEquals(0x00001, new SegOfs((short) 0x0000, (short) 0x0001).toLinearAddress());
        assertEquals(0x00011, new SegOfs((short) 0x0001, (short) 0x0001).toLinearAddress());
        assertEquals(0x4CDC9, new SegOfs((short) 0x400E, (short) 0xCCE9).toLinearAddress());
        assertEquals(0xB8000, new SegOfs((short) 0xB800, (short) 0x0000).toLinearAddress());
        assertEquals(0xB8001, new SegOfs((short) 0xB800, (short) 0x0001).toLinearAddress());
        assertEquals(0xC7FFE, new SegOfs((short) 0xB800, (short) 0xFFFE).toLinearAddress());
        assertEquals(0xC7FFF, new SegOfs((short) 0xB800, (short) 0xFFFF).toLinearAddress());
        assertEquals(0x0FFEF, new SegOfs((short) 0xFFFF, (short) 0xFFFF).toLinearAddress()); // Linear address wraps around to 0.

        assertEquals("B800:FFFE (C7FFE 819198)", new SegOfs((short) 0xB800, (short) 0xFFFE).toString());
    }

    @Test
    public void segOfsTestsWithOffset() {
        final SegOfs segOfs = new SegOfs((short) 0xB800, (short) 0x0000);
        segOfs.addOffset((short) 0x0000);
        assertEquals((short)0x0000, segOfs.getOffset());
        segOfs.addOffset((short) 0x0001);
        assertEquals((short)0x0001, segOfs.getOffset());
        segOfs.addOffset((short) 0x0001);
        assertEquals((short)0x0002, segOfs.getOffset());

        segOfs.addOffset((short) 0x0010);
        assertEquals((short)0x0012, segOfs.getOffset());

        segOfs.addOffset((short) 0x0010);
        assertEquals((short)0x0022, segOfs.getOffset());

        segOfs.setOffset((short) 0x0000);
        assertEquals((short)0x0000, segOfs.getOffset());
        segOfs.addOffset((short) -1);
        assertEquals((short)0xFFFF, segOfs.getOffset());
        segOfs.addOffset((short) -1);
        assertEquals((short) 0xFFFE, segOfs.getOffset());
        segOfs.addOffset((short) 3);
        assertEquals((short) 0x0001, segOfs.getOffset());

        segOfs.increment();
        assertEquals((short) 0x0002, segOfs.getOffset());

        segOfs.decrement();
        assertEquals((short) 0x0001, segOfs.getOffset());
    }
}
