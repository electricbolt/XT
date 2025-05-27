// MemoryUtilTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.Memory;
import nz.co.electricbolt.xt.cpu.SegOfs;
import nz.co.electricbolt.xt.usermode.util.MemoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemoryUtilTests {

    @AfterEach
    public void teardown() {
        System.setErr(null);
    }

    @Test
    public void fillTests() {
        final Memory memory = new Memory(null);
        MemoryUtil.fill(memory, new SegOfs((short) 0x0000, (short) 0x0000), (short) 0, (byte) 0x55);
        assertEquals((byte) 0x00, memory.getByte(new SegOfs((short) 0x0000, (short) 0x0000)));

        MemoryUtil.fill(memory, new SegOfs((short) 0x0000, (short) 0x0000), (short) 1, (byte) 0x55);
        assertEquals((byte) 0x55, memory.getLinearByte(0));
        assertEquals((byte) 0x00, memory.getLinearByte(1));

        MemoryUtil.fill(memory, new SegOfs((short) 0x00A0, (short) 0x0010), (short) 300, (byte) 0xAA);
        assertEquals((byte) 0x00, memory.getLinearByte(2575));
        for (int i = 2576; i < 2876; i++) {
            assertEquals((byte) 0xAA, memory.getLinearByte(i));
        }
        assertEquals((byte) 0x00, memory.getLinearByte(2876));
    }

    @Test
    public void readStringZTests() {
        final Memory memory = new Memory(null);
        memory.putLinearData(2576, new byte[]{'A', 'B', '$', 'C', 'D'}, 2, 1);
        assertEquals((byte) 0x00, memory.getLinearByte(2575));
        assertEquals((byte) '$', memory.getLinearByte(2576));
        assertEquals((byte) 0x00, memory.getLinearByte(2577));
        assertEquals("", MemoryUtil.readStringZ(memory, new SegOfs((short) 0x00A0, (short) 0x0010), '$'));

        memory.putLinearData(2832, new byte[]{'A', 'B', '$', 'C', 'D'}, 1, 2);
        assertEquals((byte) 0x00, memory.getLinearByte(2831));
        assertEquals((byte) 'B', memory.getLinearByte(2832));
        assertEquals((byte) '$', memory.getLinearByte(2833));
        assertEquals((byte) 0x00, memory.getLinearByte(2834));
        assertEquals("B", MemoryUtil.readStringZ(memory, new SegOfs((short) 0x00B0, (short) 0x0010), '$'));

        memory.putLinearData(995920, new byte[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', '@'}, 1, 10);
        assertEquals((byte) 0x00, memory.getLinearByte(995919));
        assertEquals((byte) 'B', memory.getLinearByte(995920));
        assertEquals((byte) 'C', memory.getLinearByte(995921));
        assertEquals((byte) 'D', memory.getLinearByte(995922));
        assertEquals((byte) 'E', memory.getLinearByte(995923));
        assertEquals((byte) 'F', memory.getLinearByte(995924));
        assertEquals((byte) 'G', memory.getLinearByte(995925));
        assertEquals((byte) 'H', memory.getLinearByte(995926));
        assertEquals((byte) 'I', memory.getLinearByte(995927));
        assertEquals((byte) 'J', memory.getLinearByte(995928));
        assertEquals((byte) '@', memory.getLinearByte(995929));
        assertEquals((byte) 0x00, memory.getLinearByte(995930));
        assertEquals("BCDEFGHIJ", MemoryUtil.readStringZ(memory, new SegOfs((short) 0xF325, (short) 0x0000), '@'));
    }

    @Test
    public void readBufTests() {
        final Memory memory = new Memory(null);
        memory.putLinearData(2576, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07}, 0, 7);
        final byte[] buf = MemoryUtil.readBuf(memory, new SegOfs((short) 0x00A0, (short) 0x0011), (short) 5);
        assertEquals(5, buf.length);
        assertEquals((byte) 0x02, buf[0]);
        assertEquals((byte) 0x03, buf[1]);
        assertEquals((byte) 0x04, buf[2]);
        assertEquals((byte) 0x05, buf[3]);
        assertEquals((byte) 0x06, buf[4]);
    }

    @Test
    public void writeBufTests() {
        final Memory memory = new Memory(null);
        MemoryUtil.writeBuf(memory, new SegOfs((short) 0x00A0, (short) 0x0011), new byte[]{0x01, 0x02, 0x03, 0x04, 0x05});
        assertEquals((byte) 0x00, memory.getLinearByte(2576));
        assertEquals((byte) 0x01, memory.getLinearByte(2577));
        assertEquals((byte) 0x02, memory.getLinearByte(2578));
        assertEquals((byte) 0x03, memory.getLinearByte(2579));
        assertEquals((byte) 0x04, memory.getLinearByte(2580));
        assertEquals((byte) 0x05, memory.getLinearByte(2581));
        assertEquals((byte) 0x00, memory.getLinearByte(2582));
    }

    @Test
    public void dumpTests() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        System.setErr(ps);

        final Memory memory = new Memory(null);
        MemoryUtil.writeBuf(memory, new SegOfs((short) 0x00A0, (short) 0x0011), new byte[]{0x01, 0x02, 0x03, 0x04, 0x05});
        MemoryUtil.writeBuf(memory, new SegOfs((short) 0x00A0, (short) 0x0038), new byte[]{(byte) 0xF1, (byte) 0xE2, (byte) 0xD3, (byte) 0xC4, (byte) 0xB5});

        MemoryUtil.dump(memory, new SegOfs((short) 0x00A0, (short) 0x0011));

        assertEquals("00A0:0010: 00 01 02 03 04 05 00 00 00 00 00 00 00 00 00 00 ☐☐☐☐☐☐☐☐☐☐☐☐☐☐☐☐\n" +
                "00A0:0020: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ☐☐☐☐☐☐☐☐☐☐☐☐☐☐☐☐\n" +
                "00A0:0030: 00 00 00 00 00 00 00 00 F1 E2 D3 C4 B5 00 00 00 ☐☐☐☐☐☐☐☐☐☐☐☐☐☐☐☐\n" +
                "00A0:0040: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ☐☐☐☐☐☐☐☐☐☐☐☐☐☐☐☐\n", baos.toString());
    }
}