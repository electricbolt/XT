// MemoryTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MemoryTests {

    @Test
    void putDataTests() {
        final CPU cpu = new CPU(null);
        final Memory memory = new Memory(cpu);

//        assertThrows(IllegalArgumentException.class, () -> {
//            memory.putLinearData(-1, null);
//        });
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            memory.putLinearData(0, new byte[]{});
//        });
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            memory.putLinearData(-1, new byte[]{});
//        });
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            memory.putLinearData(0, new byte[(1024*1024)+1]);
//        });

        memory.putLinearData(3, new byte[] {0x13, 0x24, 0x35, 0x7E, 0x7F, (byte) 0x80, (byte) 0xFE, (byte) 0xFF}, 0, 8);
        assertEquals((byte) 0, memory.buf[1]);
        assertEquals((byte) 0x13, memory.buf[3]);
        assertEquals((byte) 0x24, memory.buf[4]);
        assertEquals((byte) 0x35, memory.buf[5]);
        assertEquals((byte) 0x7E, memory.buf[6]);
        assertEquals((byte) 0x7F, memory.buf[7]);
        assertEquals((byte) 0x80, memory.buf[8]);
        assertEquals((byte) 0xFE, memory.buf[9]);
        assertEquals((byte) 0xFF, memory.buf[10]);
        assertEquals((byte) 0, memory.buf[11]);
    }

    @Test
    void byteTests() {
        final CPU cpu = new CPU(null);
        final Memory memory = new Memory(cpu);

        memory.putLinearData(3, new byte[] {0x13, 0x24, 0x35, 0x7E}, 0, 4);
        assertEquals((byte) 0x13, memory.buf[3]);
        assertEquals((byte) 0x24, memory.buf[4]);
        assertEquals((byte) 0x35, memory.buf[5]);
        assertEquals((byte) 0x7E, memory.buf[6]);

        final SegOfs _0000_0000 = new SegOfs((short) 0x0000, (short) 0x0000); // 0 first byte of addressable memory.
        memory.writeByte(_0000_0000, (byte) 0x69);
        assertEquals((byte) 0x69, memory.buf[0]);
        assertEquals((byte) 0x69, memory.readByte(_0000_0000));

        final SegOfs _B800_F319 = new SegOfs((short) 0xB800, (short) 0xF319);
        memory.writeByte(_B800_F319, (byte) 0x89);
        assertEquals((byte) 0x89, memory.buf[815897]);
        assertEquals((byte) 0x89, memory.readByte(_B800_F319));

        final SegOfs _FFFF_000F = new SegOfs((short) 0xFFFF, (short) 0x000F); // 1048575 last byte of addressable memory.
        memory.writeByte(_FFFF_000F, (byte) 0xFE);
        assertEquals((byte) 0xFE, memory.buf[1048575]);
        assertEquals((byte) 0xFE, memory.readByte(_FFFF_000F));

        final SegOfs _FFFF_0010 = new SegOfs((short) 0xFFFF, (short) 0x0010); // 1048576 -> 0 wraps around to first byte of addressable memory.
        memory.writeByte(_FFFF_0010, (byte) 0x9E);
        assertEquals((byte) 0x9E, memory.buf[0]);
        assertEquals((byte) 0x9E, memory.readByte(_FFFF_0010));
    }

    @Test
    void wordTests() {
        final CPU cpu = new CPU(null);
        final Memory memory = new Memory(cpu);

        memory.putLinearData(3, new byte[] {0x13, 0x24, 0x35, 0x7E}, 0, 4);
        assertEquals((byte) 0x13, memory.buf[3]);
        assertEquals((byte) 0x24, memory.buf[4]);
        assertEquals((byte) 0x35, memory.buf[5]);
        assertEquals((byte) 0x7E, memory.buf[6]);
        assertEquals((short) 0x2413, memory.readWord(new SegOfs((short) 0x0000, (short) 0x0003)));
        assertEquals((short) 0x7E35, memory.readWord(new SegOfs((short) 0x0000, (short) 0x0005)));

        final SegOfs _0000_593C = new SegOfs((short) 0x0000, (short) 0x593C);
        memory.writeWord(_0000_593C, (short) 0x593C);
        assertEquals((byte) 0x3C, memory.buf[22844]);
        assertEquals((byte) 0x59, memory.buf[22845]);
        assertEquals((short) 0x593C, memory.readWord(_0000_593C));

        final SegOfs _C800_4FE1 = new SegOfs((short) 0xC800, (short) 0x4FE1);
        memory.writeWord(_C800_4FE1, (short) 0xFE0A);
        assertEquals((byte) 0x0A, memory.buf[839649]);
        assertEquals((byte) 0xFE, memory.buf[839650]);
        assertEquals((short) 0xFE0A, memory.readWord(_C800_4FE1));

        final SegOfs _FFFF_000F = new SegOfs((short) 0xFFFF, (short) 0x000F); // 1048575 last byte of addressable memory.
        memory.writeWord(_FFFF_000F, (short) 0x5533);
        assertEquals((byte) 0x33, memory.buf[1048575]);
        assertEquals((byte) 0x55, memory.buf[0]);
        assertEquals((short) 0x5533, memory.readWord(_FFFF_000F));
    }
}