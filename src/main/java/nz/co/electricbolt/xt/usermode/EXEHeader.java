// EXEHeader.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.SegOfs;
import static nz.co.electricbolt.xt.usermode.util.ByteArrayUtil.getWord;

public class EXEHeader {

    final short lastBlockSize;
    final short numberOfBlocks;
    final short numberOfRelocationEntries;
    final short numberOfParagraphsInHeader;
    final short numberOfParagraphsForUninitializedData;
    final short numberOfParagraphsToConstrainMemory;
    final short relativeSS;
    final short SP;
    final short checkSum;
    final short IP;
    final short relativeCS;
    final short offsetFirstRelocationItem;
    final short overlayNumber;
    final byte[] buf;

    public EXEHeader(final byte[] buf) {
        this.buf = buf;

        lastBlockSize = getWord(buf, 0x02);
        numberOfBlocks = getWord(buf, 0x04);
        numberOfRelocationEntries = getWord(buf, 0x06);
        numberOfParagraphsInHeader = getWord(buf, 0x08);
        numberOfParagraphsForUninitializedData = getWord(buf, 0x0A);
        numberOfParagraphsToConstrainMemory = getWord(buf, 0x0C);
        relativeSS = getWord(buf, 0x0E);
        SP = getWord(buf, 0x10);
        checkSum = getWord(buf, 0x12);
        IP = getWord(buf, 0x14);
        relativeCS = getWord(buf, 0x16);
        offsetFirstRelocationItem = getWord(buf, 0x18);
        overlayNumber = getWord(buf, 0x1A);
    }

    public SegOfs relocationItem(final int i) {
        final short ofs = getWord(buf, offsetFirstRelocationItem + (i * 4));
        final short seg = getWord(buf, offsetFirstRelocationItem + ((i * 4) + 2));
        return new SegOfs(seg, ofs);
    }
}
