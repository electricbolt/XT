// RegSetTests.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegSetTests {

    @Test
    void setValueTests() {
        final RegSet reg = new RegSet();
        assertEquals("CS=F000 IP=FFF0 FLAGS= AX=0000 BX=0000 CX=0000 DX=0000 DS=0000 SI=0000 ES=0000 DI=0000 SS=0000 SP=0000 BP=0000", reg.toString());

        reg.flags.setValue16((short) 0xFFFF);
        reg.AX.setValue((short) 0x1234);
        reg.BX.setValue((short) 0x5678);
        reg.CX.setValue((short) 0x9ABC);
        reg.DX.setValue((short) 0xDEF0);
        reg.DS.setValue((short) 0x4321);
        reg.SI.setValue((short) 0x8765);
        reg.ES.setValue((short) 0xCBA9);
        reg.DI.setValue((short) 0x0FED);
        reg.SS.setValue((short) 0x1020);
        reg.SP.setValue((short) 0x3040);
        reg.BP.setValue((short) 0x5060);
        reg.CS.setValue((short) 0x9988);
        reg.IP.setValue((short) 0x7766);
        assertEquals("CS=9988 IP=7766 FLAGS=OF DF IF TF SF ZF AF PF CF AX=1234 BX=5678 CX=9ABC DX=DEF0 DS=4321 SI=8765 ES=CBA9 DI=0FED SS=1020 SP=3040 BP=5060", reg.toString());
    }
}
