// ParityTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParityTests {

    @Test
    public void parityTest() {
        final Flags flags = new Flags();
        final ALU alu = new ALU(flags);
        assertTrue(Parity8.isEven(0b00000000));
        assertFalse(Parity8.isOdd(0b00000000));
        assertFalse(Parity8.isEven(0b00000001));
        assertTrue(Parity8.isOdd(0b00000001));
        assertTrue(Parity8.isEven(0b00000011));
        assertFalse(Parity8.isOdd(0b00000011));
        assertFalse(Parity8.isEven(0b00000111));
        assertTrue(Parity8.isEven(0b00001111));
        assertFalse(Parity8.isEven(0b00011111));
        assertTrue(Parity8.isEven(0b00111111));
        assertFalse(Parity8.isEven(0b01111111));
        assertTrue(Parity8.isEven(0b11111111));

        assertTrue(Parity8.isEven(0b00000101));
        assertFalse(Parity8.isEven(0b00010101));
        assertTrue(Parity8.isEven(0b01010101));

        assertFalse(Parity8.isEven(0b10000000));
        assertTrue(Parity8.isEven(0b11000000));
        assertFalse(Parity8.isEven(0b11100000));
        assertTrue(Parity8.isEven(0b11110000));
        assertFalse(Parity8.isEven(0b11111000));
        assertTrue(Parity8.isEven(0b11111100));
        assertFalse(Parity8.isEven(0b11111110));

        assertTrue(Parity8.isEven(0b10100000));
        assertFalse(Parity8.isEven(0b10101000));
        assertTrue(Parity8.isEven(0b10101010));

        assertTrue(Parity8.isEven(0b111110101010)); // confirms lower 8 bits only
        assertTrue(Parity8.isEven(0b001010101010)); // confirms lower 8 bits only
    }
}
