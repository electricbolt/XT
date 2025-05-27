// ProgramSegmentPrefix.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.Memory;
import nz.co.electricbolt.xt.cpu.SegOfs;

public class ProgramSegmentPrefix {

    private static final short INT20 = (short) 0x00; // 2 bytes.
    private static final short PROGRAM_END_SEG = (short) 0x02; // 1 word.
    private static final short CALL_DOSDISPATCH = (short) 0x05; // 5 bytes.
    private static final short PROGRAM_TERMINATION_SEGOFS = (short) 0x0A; // 2 words.
    private static final short BREAKPOINT_HANDLER_SEGOFS = (short) 0x0E; // 2 words.
    private static final short CRITICAL_ERROR_HANDLER_SEGOFS = (short) 0x12; // 2 words.
    private static final short ENVIRONMENT_SEG = (short) 0x2C; // 1 word.
    private static final short INT21_RETF = (short) 0x50; // 3 bytes.
    private static final short FILE_CONTROL_BLOCK1 = (short) 0x5C; // 16 bytes.
    private static final short FILE_CONTROL_BLOCK2 = (short) 0x6C; // 20 bytes.
    private static final short COMMANDLINE_LENGTH = (short) 0x80; // 1 byte.
    private static final short COMMANDLINE = (short) 0x81; // 127 bytes.

    private static final short FILE_CONTROL_BLOCK_DRIVE = (short) 0x00;
    private static final short FILE_CONTROL_BLOCK_FILENAME = (short) 0x01;
    private static final short FILE_CONTROL_BLOCK_EXTENSION = (short) 0x09;

    private final Memory memory;
    private final short segment;
    private final short offset;

    public ProgramSegmentPrefix(final Memory memory, final short segment, final short offset) {
        this.memory = memory;
        this.segment = segment;
        this.offset = offset;

        setByte((byte) INT20, (byte) 0xCD);            // INT instruction.
        setByte((byte) (INT20 + 1), (byte) 0x20);      // 20 value.

        setByte((byte) INT21_RETF, (byte) 0xCD);       // INT instruction.
        setByte((byte) (INT21_RETF + 1), (byte) 0x21); // 21 value.
        setByte((byte) (INT21_RETF + 2), (byte) 0xCB); // RETF instruction.
    }

    public void writeProgramEnd(final short segment) {
        setWord(PROGRAM_END_SEG, segment);
    }

    public void writeEnvironment(final short segment) {
        setWord(ENVIRONMENT_SEG, segment);
    }

    public void writeFilename(final int fcb, final char drive, final String file) {
        String filename = "";
        String extension = "";
        int dot = file.indexOf('.');
        if (dot != -1) {
            filename = file.substring(0, dot);
            extension = file.substring(dot + 1);
        } else {
            filename = file;
        }
        if (filename.length() > 8) {
            filename = filename.substring(0, 8);
        }
        if (extension.length() > 3) {
            extension = extension.substring(0, 3);
        }
        if (filename.length() < 8) {
            filename = filename + " ".repeat(8 - filename.length());
        }
        if (extension.length() < 3) {
            extension = extension + " ".repeat(3 - extension.length());
        }
        short offset = (fcb == 1 ? FILE_CONTROL_BLOCK1 : FILE_CONTROL_BLOCK2);
        if (!file.isEmpty()) {
            setByte(offset, (byte) (drive - 'A'));
        }
        offset++;
        for (int i = 0; i < filename.length(); i++) {
            setByte(offset, (byte) filename.charAt(i));
            offset++;
        }
        for (int i = 0; i < extension.length(); i++) {
            setByte(offset, (byte) extension.charAt(i));
            offset++;
        }
    }

    public void writeCommandLine(String commandLine) {
        commandLine = commandLine.trim();
        if (!commandLine.isEmpty()) {
            commandLine = " " + commandLine;
        }
        if (commandLine.length() > 127) {
            throw new IllegalArgumentException("Command line length exceeds 127");
        }
        setByte(COMMANDLINE_LENGTH, (byte) commandLine.length());
        for (int i = 0; i < commandLine.length(); i++) {
            setByte((short) (COMMANDLINE + i), (byte) commandLine.charAt(i));
        }
        setByte((short) (COMMANDLINE + commandLine.length()), (byte) 0x0D);
    }

    private void setByte(final short pspOfs, final byte value) {
        memory.writeByte(new SegOfs(segment, (short) (offset + pspOfs)), value);
    }

    private void setWord(final short pspOfs, final short value) {
        memory.writeWord(new SegOfs(segment, (short) (offset + pspOfs)), value);
    }
}
