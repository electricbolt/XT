// EXEHeaderTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EXEHeaderTests {

    /*
    INFOEXE.EXE  v1.00   (c) 1989 Fabrice BELLARD
    Information about an EXE file

    Examining DPMIINST.EXE

    Decimal            Hex

    Length on disk (bytes)                       36724       00008F74
    Length of code in the EXE (bytes)            36724       00008F74

    Min. memory requested (paragraphs)              41           0029
    Max. memory requested (paragraphs)           65535           FFFF

    SS:SP                                                   0858:0080
    CS:IP                                                   0000:0000

    Number of relocation entries                   435           01B3
    Start of relocation table                       62           003E
    Length of header (paragraphs)                  160           00A0

    Num. of overlays                                 0           0000
    */

    @Test
    public void exeTests() {
        final byte[] buf = new byte[] {0x4D, 0x5A, 0x74, 0x01, 0x48, 0x00, (byte) 0xB3, 0x01, (byte) 0xA0, 0x00, 0x29, 0x00, (byte) 0xFF, (byte) 0xFF, 0x58, 0x08, (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x3E, 0x00, 0x00, 0x00, 0x01, 0x00, (byte) 0xFB, 0x30, 0x6A, 0x72, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        final EXEHeader header = new EXEHeader(buf);
        assertEquals((short) 372, header.lastBlockSize);
        assertEquals((short) 72, header.numberOfBlocks);
        assertEquals((short) 435, header.numberOfRelocationEntries);
        assertEquals((short) 41, header.numberOfParagraphsForUninitializedData);
        assertEquals((short) 0xFFFF, header.numberOfParagraphsToConstrainMemory);
        assertEquals((short) 0x0858, header.relativeSS);
        assertEquals((short) 0x0080, header.SP);
        assertEquals((short) 0, header.checkSum);
        assertEquals((short) 0, header.IP);
        assertEquals((short) 0, header.relativeCS);
        assertEquals((short) 62, header.offsetFirstRelocationItem);
        assertEquals((short) 0, header.overlayNumber);
    }
}
