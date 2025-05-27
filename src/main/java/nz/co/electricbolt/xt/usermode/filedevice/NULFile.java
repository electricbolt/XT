// NULFile.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.filedevice;

import nz.co.electricbolt.xt.usermode.AccessMode;
import nz.co.electricbolt.xt.usermode.SharingMode;

public class NULFile extends BaseFile {

    public NULFile(final AccessMode accessMode, final SharingMode sharingMode, final boolean inheritenceFlag) {
        super("NUL", accessMode, sharingMode, inheritenceFlag);
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
        return (short) 0x0084;
    }
}
