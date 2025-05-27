// EnvironmentVariablesTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.cpu.Memory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnvironmentVariablesTests {

    @Test
    public void environmentVariablesTests() {
        final Memory memory = new Memory(null);
        memory.setLinearByte(131071, (byte) 0xAA);
        memory.setLinearByte(131109, (byte) 0xAA);
        final EnvironmentVariables env = new EnvironmentVariables(memory, (short) 0x2000, (short) 0x0000);
        env.writeVariable("PATH", "C:\\TEMP");
        env.writeExecutablePath("C:\\TEMP\\HELOWRLD.EXE");

        final byte[] buf = memory.getLinearData(131071, 39);
        final StringBuilder hexString = new StringBuilder();
        for (byte b : buf) {
            String hex = String.format("%02X", b & 0xFF);
            hexString.append(hex);
        }
        assertEquals("AA504154483D433A5C54454D5000000100433A5C54454D505C48454C4F57524C442E45584500AA", hexString.toString());
    }
}