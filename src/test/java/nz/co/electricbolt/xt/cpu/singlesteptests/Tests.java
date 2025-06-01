// Tests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu.singlesteptests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.electricbolt.xt.cpu.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests implements CPUDelegate {

    final String PATH = "/Users/matthew/Development/8088/v2/";
    final String SUFFIX = ".json.gz";

    private final String opcode;
    private final String path;
    private ArrayList<TestDTO> testDTOs;
    private final short excludeFlags;
    private int interrupted;

    public Tests(final String opcode) {
        this(opcode, (short) 0);
    }

    public Tests(final String opcode, final short excludeFlags) {
        this.opcode = opcode;
        this.excludeFlags = excludeFlags;

        ClassLoader classLoader = getClass().getClassLoader();
        String resourcePath = "8088/v2/" + this.opcode + ".json.gz";

        try {
            File file = new File(classLoader.getResource(resourcePath).getFile());
            this.path = file.getAbsolutePath();
        } catch (NullPointerException e) {
            throw new RuntimeException(
                "Missing Single Step Tests for opcode " + this.opcode + ".\n" +
                "The required test resource '" + resourcePath + "' was not found.\n" +
                "Please see the README 'Extending/Developing XT' section for setup instructions."
            );
        }
    }

    public void load() {
        try (FileInputStream fis = new FileInputStream(path)) {
            GZIPInputStream gis = new GZIPInputStream(fis);
            ObjectMapper mapper = new ObjectMapper();
            testDTOs = mapper.readValue(gis, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int testCount() {
        return testDTOs.size();
    }

    public void test(final int index) {
        interrupted = -1;

        try {
            final TestDTO testDTO = testDTOs.get(index);
            if (testDTO.index != index) {
                assertEquals(testDTO.index, index, "Opcode " + opcode + " test " + index + " does not match file index " + testDTO.index);
            }

            final CPU cpu = new CPU(this);
            final RegSet reg = cpu.getReg();
            final Memory memory = cpu.getMemory();

            // Arrange
            reg.AX.setValue(testDTO.before.regs.AX.shortValue());
            reg.BX.setValue(testDTO.before.regs.BX.shortValue());
            reg.CX.setValue(testDTO.before.regs.CX.shortValue());
            reg.DX.setValue(testDTO.before.regs.DX.shortValue());
            reg.SP.setValue(testDTO.before.regs.SP.shortValue());
            reg.BP.setValue(testDTO.before.regs.BP.shortValue());
            reg.SI.setValue(testDTO.before.regs.SI.shortValue());
            reg.DI.setValue(testDTO.before.regs.DI.shortValue());
            reg.IP.setValue(testDTO.before.regs.IP.shortValue());
            reg.CS.setValue(testDTO.before.regs.CS.shortValue());
            reg.DS.setValue(testDTO.before.regs.DS.shortValue());
            reg.SS.setValue(testDTO.before.regs.SS.shortValue());
            reg.ES.setValue(testDTO.before.regs.ES.shortValue());
            reg.flags.setValue16(testDTO.before.regs.flags.shortValue());

            for (int i = 0; i < testDTO.before.memory.size(); i++) {
                final List<Integer> linearAddressValue = testDTO.before.memory.get(i);
                final int linearAddress = linearAddressValue.get(0);
                final byte value = linearAddressValue.get(1).byteValue();
                memory.setLinearByte(linearAddress, value);
            }

            int linearAddress = new SegOfs(reg.CS, reg.IP).toLinearAddress();
            for (int i = 0; i < testDTO.opcodes.length; i++) {
                memory.setLinearByte(linearAddress + i, testDTO.opcodes[i]);
            }

            // Act
            cpu.execute(1);

            // Assert
            if (interrupted == -1) {
                for (int i = 0; i < testDTO.after.memory.size(); i++) {
                    final List<Integer> linearAddressValue = testDTO.after.memory.get(i);
                    linearAddress = linearAddressValue.get(0);
                    final byte value1 = linearAddressValue.get(1).byteValue();
                    final byte value2 = memory.getLinearByte(linearAddress);
                    assertEquals(value1, value2, String.format("Opcode %s test %d memory[%d] expected %d (%02x) actual %d (%02x)", opcode, index, linearAddress, value1, value1, value2, value2));
                }
            }

            assertReg(index, testDTO.after.regs.AX, reg.AX);
            assertReg(index, testDTO.after.regs.BX, reg.BX);
            assertReg(index, testDTO.after.regs.CX, reg.CX);
            assertReg(index, testDTO.after.regs.DX, reg.DX);
            assertReg(index, testDTO.after.regs.SP, reg.SP);
            assertReg(index, testDTO.after.regs.BP, reg.BP);
            assertReg(index, testDTO.after.regs.SI, reg.SI);
            assertReg(index, testDTO.after.regs.DI, reg.DI);
            assertReg(index, testDTO.after.regs.IP, reg.IP);
            assertReg(index, testDTO.after.regs.CS, reg.CS);
            assertReg(index, testDTO.after.regs.DS, reg.DS);
            assertReg(index, testDTO.after.regs.SS, reg.SS);
            assertReg(index, testDTO.after.regs.ES, reg.ES);

            if (testDTO.after.regs.flags != null) {
                final short excludedAfterFlags = (short) (testDTO.after.regs.flags.shortValue() & ~excludeFlags);
                final short excludedActualFlags = (short) (reg.flags.getValue16() & ~excludeFlags);

                final Flags afterFlags = new Flags();
                afterFlags.setValue16(excludedAfterFlags);

                final Flags actualFlags = new Flags();
                actualFlags.setValue16(excludedActualFlags);

                assertEquals(excludedAfterFlags, excludedActualFlags, String.format("Opcode %s test %d flags expected %s actual %s", opcode, index, afterFlags, actualFlags));
            }
        } catch (Exception e) {
            System.out.println("Test index: " + index);
            throw e;
        }
    }

    private void assertReg(final int index, final Integer expected, final Reg16 actual) {
        if (expected != null && expected.shortValue() != actual.getValue()) {
            assertEquals(expected.shortValue(), actual.getValue(), String.format("Opcode %s test %d %s expected %d (%02x) actual %d (%02x)", opcode, index, actual.getName(), expected.shortValue(), expected.shortValue(), actual.getValue(), actual.getValue()));
        }
    }

    @Override
    public void fetched8(final byte value, final long instructionCount) {
    }

    @Override
    public void fetched16(final short value, final long instructionCount) {
    }

    @Override
    public void interrupt(final byte value) {
        interrupted = value;
    }

    @Override
    public void halt() {
    }

    @Override
    public byte portRead8(final short address) {
        return (byte) 0xFF;
    }

    @Override
    public void portWrite8(final short address, final byte value) {
    }

    @Override
    public short portRead16(final short address) {
        return (short) 0xFFFF;
    }

    @Override
    public void portWrite16(final short address, final short value) {
    }

    @Override
    public void invalidMemoryAccess(final SegOfs memoryAddress, final byte permissionMask) {
    }

    @Override
    public void invalidOpcode(final String message) {
    }
}