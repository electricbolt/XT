// ProgramLoader.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.cpu.Memory;
import nz.co.electricbolt.xt.cpu.SegOfs;

import java.io.FileInputStream;
import java.io.IOException;

public class ProgramLoader {

    private final CPU cpu;

    public ProgramLoader(final CPU cpu) {
        this.cpu = cpu;
    }

    public void load(final String path) {
        try (final FileInputStream fis = new FileInputStream(path)) {
            final byte[] buf = fis.readAllBytes();

            if ((buf.length < 0x1C) || buf[0x00] != 'M' || buf[0x01] != 'Z') {
                if (buf.length == 0) {
                    throw new IOException("File " + path + " is not executable (length 0)");
                }

                // COM file.
                final int startLinearAddress = new SegOfs((short) 0x0090, (short) 0x0100).toLinearAddress();
                cpu.getMemory().putLinearData(startLinearAddress, buf, 0, buf.length);
                cpu.getMemory().removePermission(0, startLinearAddress, Memory.PERMISSION_EXECUTE);
                cpu.getMemory().removePermission(startLinearAddress + buf.length, Memory.MEMORY_SIZE - startLinearAddress - buf.length, Memory.PERMISSION_EXECUTE);

                cpu.getReg().SP.setValue((short) 0xFFFF);
                cpu.getReg().SS.setValue((short) 0x0090);
                cpu.getReg().IP.setValue((short) 0x0100);
                cpu.getReg().CS.setValue((short) 0x0090);
            } else {
                // EXE file.
                final EXEHeader header = new EXEHeader(buf);

                int totalFileSize;
                if (header.lastBlockSize == 0) {
                    totalFileSize = header.numberOfBlocks * 512;
                } else {
                    totalFileSize = ((header.numberOfBlocks - 1) * 512) + header.lastBlockSize;
                }
                final int headerSize = header.numberOfParagraphsInHeader * 16;
                final int codeSize = totalFileSize - headerSize;

                final int startLinearAddress = new SegOfs((short) 0x00A0, (short) 0x0000).toLinearAddress();
                cpu.getMemory().putLinearData(startLinearAddress, buf, headerSize, codeSize);

                // Relocations.
                for (int i = 0; i < header.numberOfRelocationEntries; i++) {
                    SegOfs segOfs = header.relocationItem(i);
                    segOfs = new SegOfs((short) (segOfs.getSegment() + 0x00A0), segOfs.getOffset());
                    final short value = cpu.getMemory().readWord(segOfs);
                    cpu.getMemory().writeWord(segOfs, (short) (value + 0x00A0));
                }

                // Uncomment the following when diagnosing new program behavior, it can be useful to observe memory
                // areas being read, written or executed unexpectedly:
                // cpu.getMemory().removePermission(0, startLinearAddress, (byte) (Memory.PERMISSION_EXECUTE | Memory.PERMISSION_WRITE | Memory.PERMISSION_READ));
                // cpu.getMemory().applyPermission(0x00902, 2, Memory.PERMISSION_READ); // PSP PROGRAM_END_SEG
                // cpu.getMemory().applyPermission(0x00980, 128, Memory.PERMISSION_READ); // PSP COMMANDLINE_LENGTH..COMMANDLINE
                // cpu.getMemory().removePermission(startLinearAddress + buf.length, Memory.MEMORY_SIZE - startLinearAddress - buf.length, Memory.PERMISSION_EXECUTE);

                cpu.getReg().SP.setValue((short) header.SP);
                cpu.getReg().SS.setValue((short) (header.relativeSS + 0x00A0));
                cpu.getReg().IP.setValue((short) header.IP);
                cpu.getReg().CS.setValue((short) (header.relativeCS + 0x00A0));
            }
        } catch (IOException e) {
            System.out.println("The program " + path + " could not be read.");
            System.exit(255);
        }
    }
}
