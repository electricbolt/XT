// EnvironmentVariables.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.Memory;
import nz.co.electricbolt.xt.cpu.SegOfs;

public class EnvironmentVariables {

    private final Memory memory;
    private final short segment;
    private short offset;

    public EnvironmentVariables(final Memory memory, final short segment, final short offset) {
        this.memory = memory;
        this.segment = segment;
        this.offset = offset;
    }

    public void writeVariable(final String key, final String value) {
        writeString(key);
        writeString("=");
        writeString(value);
        offset++; // add null terminator character (which memory will already be set to).
    }

    public void writeExecutablePath(final String path) {
        // Add second null terminator character (which memory will already be set to) indicating end of variables.
        offset++;

        // Executable path, number of items following - word.
        memory.setWord(new SegOfs(segment, offset), (byte) 0x01); // Always 0x0001
        offset += 2;
        writeString(path);
        offset++; // add null terminator character (which memory will already be set to).
    }

    private void writeString(final String str) {
        for (int i = 0; i < str.length(); i++) {
            memory.setByte(new SegOfs(segment, offset++), (byte) str.charAt(i));
        }
    }
}
