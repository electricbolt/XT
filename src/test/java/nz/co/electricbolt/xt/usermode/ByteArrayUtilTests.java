// ByteArrayUtilTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.usermode.util.ByteArrayUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteArrayUtilTests {

    @Test
    public void getWordTests() {
        final byte[] buf = new byte[]{(byte) 0x80, 0x01, (byte) 0x82};
        assertEquals((short) 0x0180, ByteArrayUtil.getWord(buf, 0));
        assertEquals((short) 0x8201, ByteArrayUtil.getWord(buf, 1));
    }
}