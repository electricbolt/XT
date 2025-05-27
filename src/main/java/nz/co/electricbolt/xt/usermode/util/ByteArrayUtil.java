// ByteArrayUtils.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.util;

public class ByteArrayUtil {

    private ByteArrayUtil() {}

    public static short getWord(final byte[] buf, final int srcPos) {
        return (short) ((buf[srcPos] & 0xFF) | ((buf[srcPos+1] & 0xFF) << 8));
    }

}
