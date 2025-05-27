// MemoryUtil.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.util;

import nz.co.electricbolt.xt.cpu.Memory;
import nz.co.electricbolt.xt.cpu.SegOfs;

import java.nio.charset.Charset;

public class MemoryUtil {

    private MemoryUtil() {}

    public static void fill(final Memory memory, SegOfs to, final short length, final byte fill) {
        to = to.copy();
        for (short i = 0; i < length; i++) {
            memory.writeByte(to, fill);
            to.increment();
        }
    }

    public static String readStringZ(final Memory memory, SegOfs from, final char terminationChar) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            final char c = (char) memory.readByte(from);
            from.increment();
            if (c == terminationChar) {
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static void writeStringZ(final Memory memory, SegOfs to, final String string, final char terminationChar) {
        to = to.copy();
        for (short i = 0; i < string.length(); i++) {
            memory.writeByte(to, (byte) string.charAt(i));
            to.increment();
        }
        memory.writeByte(to, (byte) terminationChar);
    }

    public static byte[] readBuf(final Memory memory, SegOfs from, final short length) {
        from = from.copy();
        byte[] result = new byte[length];
        for (short i = 0; i < length; i++) {
            result[i] = memory.readByte(from);
            from.increment();
        }
        return result;
    }

    public static void writeBuf(final Memory memory, SegOfs to, final byte[] buf) {
        to = to.copy();
        for (byte b : buf) {
            memory.writeByte(to, b);
            to.increment();
        }
    }

    public static void dump(final Memory memory, final SegOfs address) {
        final short offset = (short) (address.getOffset() & 0xFFF0); // Align to 16 byte boundary.
        address.setOffset(offset);
        final StringBuilder ascii = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            if (i % 16 == 0) {
                // Start of line.
                System.err.printf("%04X:%04X: ", address.getSegment(), address.getOffset());
            }
            final byte b = memory.getByte(address);
            System.err.printf("%02X ", b);
            if (b >= 32) {
                final byte[] buf = new byte[1];
                buf[0] = b;
                ascii.append(new String(buf, Charset.forName("Cp437")));
            } else {
                ascii.append('☐');
            }

            if (i % 16 == 15) {
                System.err.println(ascii);
                ascii.setLength(0);
            }
            address.increment();
        }
    }
}
