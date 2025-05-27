// DirectoryUtilTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.usermode.util.DirectoryTranslation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectoryTranslationTests {

    @Test
    public void emulatedPathToHostPathTests() {
        final DirectoryTranslation directoryTranslation = new DirectoryTranslation("/Users/matthew/Documents/");
        assertEquals("/Users/matthew/Documents/", directoryTranslation.emulatedPathToHostPath("C:"));
        assertEquals("/Users/matthew/Documents/", directoryTranslation.emulatedPathToHostPath("C:\\"));
        assertEquals("/Users/matthew/Documents/Dev", directoryTranslation.emulatedPathToHostPath("C:\\Dev"));
        assertEquals("/Users/matthew/Documents/Dev/TP6", directoryTranslation.emulatedPathToHostPath("C:\\Dev\\TP6"));
        assertEquals("/Users/matthew/Documents/Dev/TP6/", directoryTranslation.emulatedPathToHostPath("C:\\Dev\\TP6\\"));
        assertEquals("/Users/matthew/Documents/Dev/TP6/TASM.EXE", directoryTranslation.emulatedPathToHostPath("C:\\Dev\\TP6\\TASM.EXE"));
    }

    @Test
    public void hostPathToEmulatedPathTests() {
        final DirectoryTranslation directoryTranslation = new DirectoryTranslation("/Users/matthew/Documents/");
        assertEquals("C:\\Random\\Folder\\Structure", directoryTranslation.hostPathToEmulatedPath("/Random/Folder/Structure"));
        assertEquals("C:\\Random\\Folder\\Structure\\", directoryTranslation.hostPathToEmulatedPath("/Random/Folder/Structure/"));
        assertEquals("C:\\Random\\Folder\\Structure\\", directoryTranslation.hostPathToEmulatedPath("Random/Folder/Structure/"));
        assertEquals("C:\\", directoryTranslation.hostPathToEmulatedPath("/Users/matthew/Documents/"));
        assertEquals("C:\\Dev", directoryTranslation.hostPathToEmulatedPath("/Users/matthew/Documents/Dev"));
        assertEquals("C:\\Dev\\", directoryTranslation.hostPathToEmulatedPath("/Users/matthew/Documents/Dev/"));
        assertEquals("C:\\Dev\\TP6", directoryTranslation.hostPathToEmulatedPath("/Users/matthew/Documents/Dev/TP6"));
        assertEquals("C:\\Dev\\TP6\\", directoryTranslation.hostPathToEmulatedPath("/Users/matthew/Documents/Dev/TP6/"));
        assertEquals("C:\\Dev\\TP6\\TASM.EXE", directoryTranslation.hostPathToEmulatedPath("/Users/matthew/Documents/Dev/TP6/TASM.EXE"));
    }
}