// Memory.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class Memory {

    public static final byte PERMISSION_EXECUTE = 0x01;
    public static final byte PERMISSION_READ = 0x02;
    public static final byte PERMISSION_WRITE = 0x04;

    public static final int MEMORY_SIZE = 1024 * 1024;

    final byte[] buf = new byte[MEMORY_SIZE];
    final byte[] permissions = new byte[MEMORY_SIZE];
    final CPU cpu;

    public Memory(final CPU cpu) {
        this.cpu = cpu;
        applyPermission(0, MEMORY_SIZE, (byte) (PERMISSION_EXECUTE | PERMISSION_READ | PERMISSION_WRITE));
    }

    public String fromBitmask(final byte permissionBitmask) {
        StringBuilder buf = new StringBuilder();
        if ((permissionBitmask & Memory.PERMISSION_EXECUTE) == Memory.PERMISSION_EXECUTE) {
            buf.append("EXECUTE");
        }
        if ((permissionBitmask & Memory.PERMISSION_READ) == Memory.PERMISSION_READ) {
            buf.append("READ ");
        }
        if ((permissionBitmask & Memory.PERMISSION_WRITE) == Memory.PERMISSION_WRITE) {
            buf.append("WRITE ");
        }
        return buf.toString().trim();
    }

    public void applyPermission(final int linearAddress, final int size, final byte permissionBitmask) {
        for (int i = linearAddress; i < linearAddress + size; i++) {
            permissions[i] |= permissionBitmask;
        }
    }

    public void removePermission(final int linearAddress, final int size, final byte permissionBitmask) {
        for (int i = linearAddress; i < linearAddress + size; i++) {
            permissions[i] &= (byte) ~permissionBitmask;
        }
    }

    /**
     * Reads bytes directly from linear memory at the specified address 0x00000 - 0xFFFFF without any memory protection
     * bits. Should only be used by unit tests.
     *
     * @throws IndexOutOfBoundsException if linearAddress (+size) is not within the range above.
     */
    public byte[] getLinearData(final int linearAddress, final int size) {
        if (size <= 0 || size > MEMORY_SIZE) {
            throw new IllegalArgumentException("size argument (" + size + ") is not in range 0.." + +(MEMORY_SIZE - 1));
        }
        if (linearAddress < 0 || linearAddress + size >= MEMORY_SIZE) {
            throw new IllegalArgumentException("linearAddress argument (" + linearAddress + ") is not in range 0.." + (MEMORY_SIZE - 1));
        }
        final byte[] result = new byte[size];
        System.arraycopy(buf, linearAddress, result, 0, size);
        return result;
    }

    /**
     * Writes bytes directly to linear memory at the specified address 0x00000 - 0xFFFFF without any memory protection
     * bits. Should only be used by unit tests.
     *
     * @throws IndexOutOfBoundsException if linearAddress (+size) is not within the range above.
     */
    public void putLinearData(final int linearAddress, final byte[] data, final int srcPos, final int length) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        } else if (data.length == 0 || data.length > MEMORY_SIZE) {
            throw new IllegalArgumentException("data argument (" + data.length + ") is not in range 0.." + +(MEMORY_SIZE - 1));
        } else if (linearAddress < 0 || linearAddress >= MEMORY_SIZE) {
            throw new IllegalArgumentException("linearAddress " + linearAddress + " not in range 0.." + (MEMORY_SIZE - 1));
        }
        System.arraycopy(data, srcPos, buf, linearAddress, length);
    }

    /**
     * Reads a byte directly from linear memory at the specified address 0x00000 - 0xFFFFF without any memory protection
     * bits. Should only be used by unit tests.
     *
     * @throws IndexOutOfBoundsException if linearAddress is not within the range above.
     */
    public byte getLinearByte(final int linearAddress) {
        return buf[linearAddress];
    }

    /**
     * Writes a byte directly to linear memory at the specified address 0x00000 - 0xFFFFF without any memory protection
     * bits. Should only be used by unit tests.
     *
     * @throws IndexOutOfBoundsException if linearAddress is not within the range above.
     */
    public void setLinearByte(final int linearAddress, final byte value) {
        buf[linearAddress] = value;
    }

    /**
     * Gets an 8-bit byte from memory at the specified segment:offset without any memory protection bits.
     * If the segment:offset computes to a linear address of greater than 0xFFFFF, it is wrapped around to the beginning
     * of the address space at 0x00000.
     */
    public byte getByte(final SegOfs segOfs) {
        int address = segOfs.toLinearAddress();
        return buf[address];
    }

    /**
     * Sets an 8-bit byte to memory at the specified segment:offset without any memory protection bits.
     * If the segment:offset computes to a linear address of greater than 0xFFFFF, it is wrapped around to the beginning
     * of the address space at 0x00000.
     */
    public void setByte(final SegOfs segOfs, final byte value) {
        int address = segOfs.toLinearAddress();
        buf[address] = value;
    }

    /**
     * Gets a 16-bit instruction from memory at the specified segment:offset without any memory protection bits.
     * If the offset value is 0xFFFF, then the low byte uses offset 0xFFFF. The high byte wraps to the beginning of the
     * segment and uses offset 0x0000. If the segment:offset computes to a linear address of greater than 0xFFFFF, it is
     * wrapped around to the beginning of the address space at 0x00000.
     */
    public short getWord(SegOfs segOfs) {
        segOfs = segOfs.copy();
        byte lo = getByte(segOfs);
        segOfs.increment();
        byte hi = getByte(segOfs);
        return (short) ((hi & 0xFF) << 8 | lo & 0xFF);
    }

    /**
     * Sets a 16-bit word to memory at the specified segment:offset without any memory protection bits.
     * If the offset value is 0xFFFF, then the low byte uses offset 0xFFFF. The high byte wraps to the beginning of the
     * segment and uses offset 0x0000. If the segment:offset computes to a linear address of greater than 0xFFFFF, it is
     * wrapped around to the beginning of the address space at 0x00000.
     */
    public void setWord(SegOfs segOfs, short value) {
        segOfs = segOfs.copy();
        setByte(segOfs, (byte) value);
        segOfs.increment();
        setByte(segOfs, (byte) (value >> 8));
    }

    /**
     * Sets a 32-bit word to memory at the specified segment:offset without any memory protection bits.
     * Whilst writing the 4 bytes to memory, the offset value can wrap from 0xFFFF to 0x0000. If the segment:offset
     * computes to a linear address of greater than 0xFFFFF, it is wrapped around to the beginning of the address space
     * at 0x00000.
     */
    public void setDoubleWord(SegOfs segOfs, int value) {
        segOfs = segOfs.copy();
        setByte(segOfs, (byte) value);
        segOfs.increment();
        setByte(segOfs, (byte) (value >> 8));
        segOfs.increment();
        setByte(segOfs, (byte) (value >> 16));
        segOfs.increment();
        setByte(segOfs, (byte) (value >> 24));
    }

    /**
     * Reads an 8-bit byte from memory at the specified segment:offset.
     * If the segment:offset computes to a linear address of greater than 0xFFFFF, it is wrapped around to the beginning
     * of the address space at 0x00000. Invokes delegate.invalidMemoryAccess() if the memory address is not readable
     * (Memory.PERMISSION_READ).
     */
    public byte readByte(final SegOfs segOfs) {
        int address = segOfs.toLinearAddress();
        byte value = buf[address];
        if ((permissions[address] & Memory.PERMISSION_READ) == 0) {
            cpu.delegate.invalidMemoryAccess(segOfs, Memory.PERMISSION_READ);
        }
        return value;
    }

    /**
     * Reads an 8-bit instruction byte from memory at the specified segment:offset.
     * If the segment:offset computes to a linear address of greater than 0xFFFFF, it is wrapped around to the beginning
     * of the address space at 0x00000. Invokes delegate.invalidMemoryAccess() if the memory address is not executable
     * (Memory.PERMISSION_EXECUTE).
     */
    public byte fetchByte(final SegOfs segOfs) {
        int address = segOfs.toLinearAddress();
        byte value = buf[address];
        if ((permissions[address] & Memory.PERMISSION_EXECUTE) == 0) {
            cpu.delegate.invalidMemoryAccess(segOfs, Memory.PERMISSION_EXECUTE);
        }
        return value;
    }

    /**
     * Writes an 8-bit byte to memory at the specified segment:offset.
     * If the segment:offset computes to a linear address of greater than 0xFFFFF, it is wrapped around to the beginning
     * of the address space at 0x00000. Invokes delegate.invalidMemoryAccess() if the memory address is not writable
     * (Memory.PERMISSION_WRITE).
     */
    public void writeByte(final SegOfs segOfs, final byte value) {
        int address = segOfs.toLinearAddress();
        if ((permissions[address] & Memory.PERMISSION_WRITE) == 0) {
            cpu.delegate.invalidMemoryAccess(segOfs, Memory.PERMISSION_WRITE);
        }
        buf[address] = value;
    }

    /**
     * Reads a 16-bit word from memory at the specified segment:offset.
     * If the offset value is 0xFFFF, then the low byte uses offset 0xFFFF. The high byte wraps to the beginning of the
     * segment and uses offset 0x0000. If the segment:offset computes to a linear address of greater than 0xFFFFF, it is
     * wrapped around to the beginning of the address space at 0x00000. Invokes delegate.invalidMemoryAccess() if the
     * memory address is not readable (Memory.PERMISSION_READ).
     */
    public short readWord(SegOfs segOfs) {
        segOfs = segOfs.copy();
        byte lo = readByte(segOfs);
        segOfs.increment();
        byte hi = readByte(segOfs);
        return (short) ((hi & 0xFF) << 8 | lo & 0xFF);
    }

    /**
     * Reads a 16-bit instruction from memory at the specified segment:offset.
     * If the offset value is 0xFFFF, then the low byte uses offset 0xFFFF. The high byte wraps to the beginning of the
     * segment and uses offset 0x0000. If the segment:offset computes to a linear address of greater than 0xFFFFF, it is
     * wrapped around to the beginning of the address space at 0x00000. Invokes delegate.invalidMemoryAccess() if the
     * memory address is not executable (Memory.PERMISSION_EXECUTE).
     */
    public short fetchWord(SegOfs segOfs) {
        segOfs = segOfs.copy();
        byte lo = fetchByte(segOfs);
        segOfs.increment();
        byte hi = fetchByte(segOfs);
        return (short) ((hi & 0xFF) << 8 | lo & 0xFF);
    }

    /**
     * Writes a 16-bit word to memory at the specified segment:offset.
     * If the offset value is 0xFFFF, then the low byte uses offset 0xFFFF. The high byte wraps to the beginning of the
     * segment and uses offset 0x0000. If the segment:offset computes to a linear address of greater than 0xFFFFF, it is
     * wrapped around to the beginning of the address space at 0x00000. Invokes delegate.invalidMemoryAccess() if the
     * memory address is not writable (Memory.PERMISSION_WRITE).
     */
    public void writeWord(SegOfs segOfs, short value) {
        segOfs = segOfs.copy();
        writeByte(segOfs, (byte) value);
        segOfs.increment();
        writeByte(segOfs, (byte) (value >> 8));
    }
}
