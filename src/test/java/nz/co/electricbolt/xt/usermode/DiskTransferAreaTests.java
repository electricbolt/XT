// DiskTransferAreaTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.Memory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiskTransferAreaTests {

    @Test
    public void diskTransferAreaTests() {
        final Memory memory = new Memory(null);
        memory.setLinearByte(131071, (byte) 0xAA);
        memory.setLinearByte(131115, (byte) 0xAA);
        final DiskTransferArea dta = new DiskTransferArea(memory, (short) 0x2000, (short) 0x0000);
        dta.setInternalId((short) 0x1234);
        dta.writeFileDate((short) 0x5678);
        dta.writeFileTime((short) 0x5387);
        dta.writeFileSize(0x7ABCDEF);
        dta.writeFilename("HELOWRLD.EXE");

        assertEquals((short) 0x1234, dta.getInternalId());
        final byte[] buf = memory.getLinearData(131071, 45);
        final StringBuilder hexString = new StringBuilder();
        for (byte b : buf) {
            String hex = String.format("%02X", b & 0xFF);
            hexString.append(hex);
        }
        assertEquals("AA341200000000000000000000000000000000000000008753EFCDAB07000048454C4F57524C442E45584500AA", hexString.toString());
    }
}
