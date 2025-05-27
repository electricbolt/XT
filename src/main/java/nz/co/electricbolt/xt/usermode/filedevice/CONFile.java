// CONFile.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.filedevice;

import nz.co.electricbolt.xt.usermode.AccessMode;
import nz.co.electricbolt.xt.usermode.SharingMode;

import java.nio.charset.Charset;

public class CONFile extends BaseFile {

    public CONFile(final AccessMode accessMode, final SharingMode sharingMode, final boolean inheritenceFlag) {
        super("CON", accessMode, sharingMode, inheritenceFlag);
    }

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public boolean close() {
        return false;
    }

    public short getDeviceInformationWord() {
        return (short) 0x80D3;
    }

    public void write(final byte[] buf) {
        System.out.print(new String(buf, Charset.forName("Cp437"))); // Original IBM PC/XT character set.
    }
}
