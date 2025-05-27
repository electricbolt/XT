// RegSet.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

public class RegSet {

    public final Flags flags = new Flags();
    public final Reg16 AX = new Reg16("AX"); // Accumulator
    public final Reg8 AL = AX.low();
    public final Reg8 AH = AX.high();
    public final Reg16 BX = new Reg16("BX"); // Base
    public final Reg8 BL = BX.low();
    public final Reg8 BH = BX.high();
    public final Reg16 CX = new Reg16("CX"); // Counting
    public final Reg8 CL = CX.low();
    public final Reg8 CH = CX.high();
    public final Reg16 DX = new Reg16("DX"); // Data
    public final Reg8 DL = DX.low();
    public final Reg8 DH = DX.high();
    public final Reg16 SP = new Reg16("SP");
    public final Reg16 BP = new Reg16("BP");
    public final Reg16 SI = new Reg16("SI");
    public final Reg16 DI = new Reg16("DI");
    public final Reg16 IP = new Reg16("IP", (short) 0xFFF0);
    public final Reg16 CS = new Reg16("CS",  (short) 0xF000); // Code segment
    public final Reg16 DS = new Reg16("DS"); // Data segment
    public final Reg16 SS = new Reg16("SS"); // Stack segment
    public final Reg16 ES = new Reg16("ES"); // Extra segment

    public String toString() {
        return CS +
                " " + IP +
                " " + flags +
                " " + AX +
                " " + BX +
                " " + CX +
                " " + DX +
                " " + DS +
                " " + SI +
                " " + ES +
                " " + DI +
                " " + SS +
                " " + SP +
                " " + BP;
    }
}