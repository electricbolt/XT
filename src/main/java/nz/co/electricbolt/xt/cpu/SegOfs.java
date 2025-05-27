// SegOfs.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class SegOfs {

    private short segment;
    private short offset;

    public SegOfs(final Reg16 segment, final Reg16 offset) {
        this.segment = segment.getValue();
        this.offset = offset.getValue();
    }

    public SegOfs(final Reg16 segment, final short offset) {
        this.segment = segment.getValue();
        this.offset = offset;
    }

    public SegOfs(final short segment, final short offset) {
        this.segment = segment;
        this.offset = offset;
    }

    public short getSegment() {
        return segment;
    }

    public void setSegment(final short segment) {
        this.segment = segment;
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(final short offset) {
        this.offset = offset;
    }

    public SegOfs copy() {
        return new SegOfs(segment, offset);
    }

    public void addOffset(final short value) {
        offset = (short) ((offset + value) & 0xFFFF);
    }

    public void increment() {
        addOffset((short) 1);
    }

    public void decrement() {
        addOffset((short) -1);
    }

    public String toString() {
        return String.format("%04X:%04X (%05X %d)", segment, offset, toLinearAddress(), toLinearAddress());
    }

    public int toLinearAddress() {
        int address = ((int) segment & 0xFFFF) << 4;
        address = address + (offset & 0xFFFF);
        address %= Memory.MEMORY_SIZE;
        return address;
    }
}
