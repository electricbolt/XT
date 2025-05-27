// FlagsTest.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FlagsTest {

    @Test
    void flagsAllTest() {
        final Flags flags = new Flags();
        assertEquals((short) 0xF002, flags.getValue16());
        flags.setValue16((short) 0xFFFF);
        assertEquals((short) 0xFFD7, flags.getValue16());
        assertEquals("FLAGS=OF DF IF TF SF ZF AF PF CF", flags.toString());
        assertTrue(flags.isCarry());
        assertTrue(flags.isParityEven());
        assertFalse(flags.isParityOdd());
        assertTrue(flags.isAuxiliaryCarry());
        assertFalse(flags.isNotAuxiliaryCarry());
        assertTrue(flags.isZero());
        assertFalse(flags.isNotZero());
        assertTrue(flags.isSignNegative());
        assertFalse(flags.isSignPositive());
        assertTrue(flags.isTrapEnabled());
        assertTrue(flags.isInterruptEnabled());
        assertFalse(flags.isInterruptDisabled());
        assertTrue(flags.isDirectionDown());
        assertFalse(flags.isDirectionUp());
        assertTrue(flags.isOverflow());
        assertFalse(flags.isNotOverflow());

        flags.setValue16((short) 0x0);
        assertEquals((short) 0xF002, flags.getValue16());
        assertEquals("FLAGS=", flags.toString());
    }

    @Test
    void flags8Test() {
        final Flags flags = new Flags();
        assertEquals((byte) 0x02, flags.getValue8());
        flags.setValue8((byte) 0xFF);
        assertEquals((short) 0xF0D7, flags.getValue16());
        assertEquals((byte) 0xD7, flags.getValue8());

        flags.setValue8((byte) 0x00);
        assertEquals((short) 0xF002, flags.getValue16());
        assertEquals((byte) 0x02, flags.getValue8());
    }

    @Test
    void flagsCarryTest() {
        final Flags flags = new Flags();
        flags.setValue16((short) 0x0000);

        assertFalse(flags.isCarry());
        assertTrue(flags.isNotCarry());
        flags.setValue16((short) 0x0001);
        assertEquals("FLAGS=CF", flags.toString());
        assertTrue(flags.isCarry());
        assertFalse(flags.isNotCarry());

        flags.setValue16((short) 0xFFFF);
        flags.setCarry(false);
        assertEquals((short) 0xFFD6, flags.getValue16());

        flags.setValue16((short) 0x0000);
        flags.setCarry(true);
        assertEquals((short) 0xF003, flags.getValue16());
    }

    @Test
    void flagsParityTest() {
        final Flags flags = new Flags();
        flags.setValue16((short) 0x0000);
        assertFalse(flags.isParityEven());
        assertTrue(flags.isParityOdd());
        flags.setValue16((short) 0x0004);
        assertEquals("FLAGS=PF", flags.toString());
        assertTrue(flags.isParityEven());
        assertFalse(flags.isParityOdd());

        flags.setValue16((short) 0xFFFF);
        flags.setParityEven(false);
        assertEquals((short) 0xFFD3, flags.getValue16());

        flags.setValue16((short) 0x0000);
        flags.setParityEven(true);
        assertEquals((short) 0xF006, flags.getValue16());
    }

    @Test
    void flagsAuxilaryCarryTest() {
        final Flags flags = new Flags();
        flags.setValue16((short) 0x0000);
        assertFalse(flags.isAuxiliaryCarry());
        assertTrue(flags.isNotAuxiliaryCarry());
        flags.setValue16((short) 0x0010);
        assertEquals("FLAGS=AF", flags.toString());
        assertTrue(flags.isAuxiliaryCarry());
        assertFalse(flags.isNotAuxiliaryCarry());

        flags.setValue16((short) 0xFFFF);
        flags.setAuxiliaryCarry(false);
        assertEquals((short) 0xFFC7, flags.getValue16());

        flags.setValue16((short) 0x0000);
        flags.setAuxiliaryCarry(true);
        assertEquals((short) 0xF012, flags.getValue16());
    }

    @Test
    void flagsZeroTest() {
        final Flags flags = new Flags();
        flags.setValue16((short) 0x0000);
        assertFalse(flags.isZero());
        assertTrue(flags.isNotZero());
        flags.setValue16((short) 0x0040);
        assertEquals("FLAGS=ZF", flags.toString());
        assertTrue(flags.isZero());
        assertFalse(flags.isNotZero());

        flags.setValue16((short) 0xFFFF);
        flags.setZero(false);
        assertEquals((short) 0xFF97, flags.getValue16());

        flags.setValue16((short) 0x0000);
        flags.setZero(true);
        assertEquals((short) 0xF042, flags.getValue16());
    }

    @Test
    void flagsSignTest() {
        final Flags flags = new Flags();
        flags.setValue16((short) 0x0000);
        assertFalse(flags.isSignNegative());
        assertTrue(flags.isSignPositive());
        flags.setValue16((short) 0x0080);
        assertEquals("FLAGS=SF", flags.toString());
        assertTrue(flags.isSignNegative());
        assertFalse(flags.isSignPositive());

        flags.setValue16((short) 0xFFFF);
        flags.setSignNegative(false);
        assertEquals((short) 0xFF57, flags.getValue16());

        flags.setValue16((short) 0x0000);
        flags.setSignNegative(true);
        assertEquals((short) 0xF082, flags.getValue16());
    }

    @Test
    void flagsTrapTest() {
        final Flags flags = new Flags();
        flags.setValue16((short) 0x0000);
        assertFalse(flags.isTrapEnabled());
        assertTrue(flags.isTrapDisabled());
        flags.setValue16((short) 0x0100);
        assertEquals("FLAGS=TF", flags.toString());
        assertTrue(flags.isTrapEnabled());
        assertFalse(flags.isTrapDisabled());

        flags.setValue16((short) 0xFFFF);
        flags.setTrapEnabled(false);
        assertEquals((short) 0xFED7, flags.getValue16());

        flags.setValue16((short) 0x0000);
        flags.setTrapEnabled(true);
        assertEquals((short) 0xF102, flags.getValue16());
    }

    @Test
    void flagsInterruptTest() {
        final Flags flags = new Flags();
        flags.setValue16((short) 0x0000);
        assertFalse(flags.isInterruptEnabled());
        assertTrue(flags.isInterruptDisabled());
        flags.setValue16((short) 0x0200);
        assertEquals("FLAGS=IF", flags.toString());
        assertTrue(flags.isInterruptEnabled());
        assertFalse(flags.isInterruptDisabled());

        flags.setValue16((short) 0xFFFF);
        flags.setInterruptEnabled(false);
        assertEquals((short) 0xFDD7, flags.getValue16());

        flags.setValue16((short) 0x0000);
        flags.setInterruptEnabled(true);
        assertEquals((short) 0xF202, flags.getValue16());
    }

    @Test
    void flagsDirectionTest() {
        final Flags flags = new Flags();
        flags.setValue16((short) 0x0000);
        assertFalse(flags.isDirectionDown());
        assertTrue(flags.isDirectionUp());
        flags.setValue16((short) 0x0400);
        assertEquals("FLAGS=DF", flags.toString());
        assertTrue(flags.isDirectionDown());
        assertFalse(flags.isDirectionUp());

        flags.setValue16((short) 0xFFFF);
        flags.setDirectionDown(false);
        assertEquals((short) 0xFBD7, flags.getValue16());

        flags.setValue16((short) 0x0000);
        flags.setDirectionDown(true);
        assertEquals((short) 0xF402, flags.getValue16());
    }

    @Test
    void flagsOverflowTest() {
        final Flags flags = new Flags();
        flags.setValue16((short) 0x0000);
        assertFalse(flags.isOverflow());
        assertTrue(flags.isNotZero());
        flags.setValue16((short) 0x0800);
        assertEquals("FLAGS=OF", flags.toString());
        assertTrue(flags.isOverflow());
        assertFalse(flags.isNotOverflow());

        flags.setValue16((short) 0xFFFF);
        flags.setOverflow(false);
        assertEquals((short) 0xF7D7, flags.getValue16());

        flags.setValue16((short) 0x0000);
        flags.setOverflow(true);
        assertEquals((short) 0xF802, flags.getValue16());
    }
}