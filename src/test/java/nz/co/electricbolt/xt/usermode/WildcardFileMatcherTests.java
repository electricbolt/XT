// WildcardFileMatcherTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

import nz.co.electricbolt.xt.usermode.util.WildcardFileMatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WildcardFileMatcherTests {

    @Test
    public void matchingTests() {
        WildcardFileMatcher m = new WildcardFileMatcher("*.exe");
        assertTrue(m.matches("cmd.exe"));
        assertFalse(m.matches("cmd."));
        assertFalse(m.matches("cmdexe"));
        assertTrue(m.matches(".exe"));

        m = new WildcardFileMatcher("*.*");
        assertTrue(m.matches("cmd.exe"));
        assertTrue(m.matches("cmd."));
        assertFalse(m.matches("cmdexe"));
        assertTrue(m.matches(".exe"));
        assertTrue(m.matches("."));

        m = new WildcardFileMatcher("*.???");
        assertTrue(m.matches("cmd.exe"));
        assertFalse(m.matches("cmd."));
        assertFalse(m.matches("cmdexe"));
        assertTrue(m.matches(".exe"));
        assertTrue(m.matches("abc.bat"));
        assertFalse(m.matches("abc.ba"));

        m = new WildcardFileMatcher("*.e?e");
        assertTrue(m.matches("cmd.exe"));
        assertFalse(m.matches("cmd."));
        assertFalse(m.matches("cmdexe"));
        assertTrue(m.matches(".exe"));
        assertTrue(m.matches("abc.eAe"));
        assertTrue(m.matches("abc.eBe"));
        assertFalse(m.matches("abc.ba"));

        m = new WildcardFileMatcher("h?l*.exe");
        assertFalse(m.matches("cmd.exe"));
        assertTrue(m.matches("hel.exe"));
        assertTrue(m.matches("help.exe"));
        assertTrue(m.matches("hellowld.exe"));
        assertTrue(m.matches("hAl.exe"));

        m = new WildcardFileMatcher("h??*.exe");
        assertFalse(m.matches("cmd.exe"));
        assertFalse(m.matches("he.exe"));
        assertTrue(m.matches("hel.exe"));
        assertTrue(m.matches("hAl.exe"));

        m = new WildcardFileMatcher("h??*p.exe");
        assertFalse(m.matches("cmd.exe"));
        assertFalse(m.matches("he.exe"));
        assertTrue(m.matches("help.exe"));
        assertTrue(m.matches("hAlcatp.exe"));
    }
}