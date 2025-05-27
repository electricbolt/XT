// ProgramSegmentPrefixTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.Memory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProgramSegmentPrefixTests {

    @Test
    public void programSegmentPrefixTests() {
        final Memory memory = new Memory(null);
        final ProgramSegmentPrefix psp = new ProgramSegmentPrefix(memory, (short) 0x2000, (short) 0x0000);
        psp.writeProgramEnd((short) 0xF000);
        psp.writeEnvironment((short) 0x0050);
        psp.writeCommandLine("C:\\TEMP\\PROGRAM.EXE MYAPP1.PAS ANOTHER.TXT");
        psp.writeFilename(1, 'C', "FIRSTFIL.BAT");
        psp.writeFilename(2, 'C', "SCNDFILE.TXT");

        final byte[] buf = memory.getLinearData(131072, 256);
        final StringBuilder hexString = new StringBuilder();
        for (byte b : buf) {
            String hex = String.format("%02X", b & 0xFF);
            hexString.append(hex);
        }
        assertEquals("CD2000F0000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00500000000000000000000000000000000000000000000000000000000000000000000000CD21CB00000000000000000002" +
                "464952535446494C424154000000000253434E4446494C4554585400000000000000002B20433A5C54454D505C50524F4752" +
                "414D2E455845204D59415050312E50415320414E4F544845522E5458540D0000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000", hexString.toString());
    }
}
