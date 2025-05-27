// Parity8.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.cpu;

/*
* Parity calculations based upon https://gist.github.com/OhMeadhbh/3563a62daad9e10d7dae4233e73de14d website:
*/

/*
** After digging around,  I found this implementation  at Steven Brumme's
** site at:
**
**   http://bits.stephan-brumme.com/parity.html
**
** It's pretty  cool. It does the  shift and XOR trick  multiple times to
** get down to a  4 bit value. Then it shifts  a specially constructed 16
** bit  constant  by  that many  bits  and  as  if  by magic,  the  least
** significant bit is the parity of the 32 bit value.
**
** The  magic   here  comes   from  the   construction  of   the  special
** constant. Remember  that the shift and  XOR trick takes a  n-bit value
** and  produces  a   n/2-bit  value  with  the   same  parity.  Brumme's
** implementation does effectively the same  thing as the lookup table in
** the parity_lut implementation  above, but it uses the  16 bit constant
** as the look-up table. Each of the bits in the special constant encodes
** the parity of  the index's parity. Here's the table  from Brumme's web
** page redrawn:
**
**   Index      Parity
**   0 0000     0      --------------------------------------+
**   1 0001     1      ------------------------------------+ |
**   2 0010     1      ----------------------------------+ | |
**   3 0011     0      --------------------------------+ | | |
**                                                     | | | |
**   4 0100     1      ----------------------------+   | | | |
**   5 0101     0      --------------------------+ |   | | | |
**   6 0110     0      ------------------------+ | |   | | | |
**   7 0111     1      ----------------------+ | | |   | | | |
**                                           | | | |   | | | |
**   8 1000     1      ------------------+   | | | |   | | | |
**   9 1001     0      ----------------+ |   | | | |   | | | |
**   A 1010     0      --------------+ | |   | | | |   | | | |
**   B 1011     1      ------------+ | | |   | | | |   | | | |
**                                 | | | |   | | | |   | | | |
**   C 1100     0      --------+   | | | |   | | | |   | | | |
**   D 1101     1      ------+ |   | | | |   | | | |   | | | |
**   E 1110     1      ----+ | |   | | | |   | | | |   | | | |
**   F 1111     0      --+ | | |   | | | |   | | | |   | | | |
**                       | | | |   | | | |   | | | |   | | | |
**                       v v v v   v v v v   v v v v   v v v v
**   Magic Constant  ->  0 1 1 0   1 0 0 1   1 0 0 1   0 1 1 0
**   Magic Constant (in Hex) -> 0x6996
**
** I  don't know  if  Stephan  devised this  shift  look-up table  scheme
** himself or if  he's copying it from someone else,  but it's definitely
** clever. I'm not sure if it's going to be faster than the look-up table
** used in  parity_faster_xor() on a modern  CPU, but if you're  on a CPU
** with  a fast  shifter and  a (very)  small data  cache, I  wouldn't be
** surprised if it *was* faster.
*/

public class Parity8 {

    public static boolean isEven(final int value) {
        return !isOdd(value);
    }

    public static boolean isOdd(int value) {
        value &= 0xFF;
        value ^= value >> 4;
        value &= 0xF;
        return ((0x6996 >> value) & 1) == 1;
    }
}
