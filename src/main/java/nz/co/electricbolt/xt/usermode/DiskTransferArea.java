// DiskTransferArea.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.Memory;
import nz.co.electricbolt.xt.cpu.SegOfs;

public class DiskTransferArea {

    private static final short INTERNAL_ID = (short) 0x00; // byte.
    private static final short FILE_ATTRIBUTE = (byte) 0x15; // byte.
    private static final short FILE_TIME = (short) 0x16; // word.
    private static final short FILE_DATE = (short) 0x17; // word.
    private static final short FILE_SIZE = (short) 0x18; // dword.
    private static final short FILE_NAME = (short) 0x1E; // ASCIIZ filename (13 bytes).

    private final Memory memory;
    private final short segment;
    private final short offset;

    public DiskTransferArea(final Memory memory, final short segment, final short offset) {
        this.memory = memory;
        this.segment = segment;
        this.offset = offset;
    }

    public short getInternalId() {
        return getWord(INTERNAL_ID);
    }

    public void setInternalId(final short internalId) {
        setWord(INTERNAL_ID, internalId);
    }

    public void writeFileAttribute(final byte fileAttribute) {
        setWord(FILE_ATTRIBUTE, fileAttribute);
    }

    public void writeFileTime(final short fileTime) {
        setWord(FILE_TIME, fileTime);
    }

    public void writeFileDate(final short fileDate) {
        setWord(FILE_DATE, fileDate);
    }

    public void writeFileSize(final int fileSize) {
        setDoubleWord(FILE_SIZE, fileSize);
    }

    public void writeFilename(final String filename) {
        for (int i = 0; i < filename.length(); i++) {
            final char c = filename.charAt(i);
            setByte((short) (FILE_NAME + i), (byte) c);
        }
        setByte((short) (FILE_NAME + filename.length()), (byte) 0x00);
    }

    private short getWord(final short dtaOfs) {
        return memory.readWord(new SegOfs(segment, (short) (offset + dtaOfs)));
    }

    private void setByte(final short dtaOfs, final byte value) {
        memory.setByte(new SegOfs(segment, (short) (offset + dtaOfs)), value);
    }

    private void setWord(final short dtaOfs, final short value) {
        memory.setWord(new SegOfs(segment, (short) (offset + dtaOfs)), value);
    }

    private void setDoubleWord(final short dtaOfs, final int value) {
        memory.setDoubleWord(new SegOfs(segment, (short) (offset + dtaOfs)), value);
    }
}
