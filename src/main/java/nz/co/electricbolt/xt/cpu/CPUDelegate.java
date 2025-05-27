// CPUDelegate.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public interface CPUDelegate {

    /**
     * Invoked when a byte has been fetched from memory for use in instruction decoding and execution.
     */
     void fetched8(final byte value, final long instructionCount);

    /**
     * Invoked when a word was fetched from memory for use in instruction decoding and execution.
     */
    void fetched16(final short value, final long instructionCount);

    /**
     * Invoked when an interrupt occurs due to 'software' INT, INT3, INTO instructions or as a result of 'hardware'
     * error e.g. divide by zero, quotient overflow or AAM with base 0. Flags, CS:IP have been pushed onto the stack
     * and CS:IP was updated to the interrupt vector. If the delegate wants to return from the interrupt, it must call
     * {@code cpu.iret()} method or pop the stack manually.
     *
     * @param value byte 0x00 - 0xFF interrupt number.
     */
    void interrupt(final byte value);

    /**
     * Invoked when the CPU is halted by the HLT instruction.
     */
    void halt();

    /**
     * Invoked when a byte is requested from the IO port specified.
     *
     * @param address port number 0x0 - 0xFF.
     */
    byte portRead8(final short address);

    /**
     * Invoked when a byte is written to the IO port specified.
     *
     * @param address port number 0x0 - 0xFF.
     * @param value   to write to port specified.
     */
    void portWrite8(final short address, final byte value);

    /**
     * Invoked when a word is requested from the IO port specified.
     *
     * @param address port number 0x0 - 0xFFFF.
     */
    short portRead16(final short address);

    /**
     * Invoked when a word is written to the IO port specified.
     *
     * @param address port number 0x0 - 0xFFFF.
     * @param value   to write to port specified.
     */
    void portWrite16(final short address, final short value);

    /**
     * Invoked when a fetch, read or write memory access is attempted that is not permitted by the current permission
     * mask.
     *
     * @param memoryAddress  address of the memory location being accessed.
     * @param permissionMask permission bitmask that isn't allowed for the memory location.
     */
    void invalidMemoryAccess(final SegOfs memoryAddress, final byte permissionMask);

    /**
     * Invoked when an undocumented (unimplemented) opcode is encountered.
     *
     * @param message message to be displayed to the user.
     */
    void invalidOpcode(final String message);
}
