// BaseFile.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.filedevice;

import nz.co.electricbolt.xt.usermode.AccessMode;
import nz.co.electricbolt.xt.usermode.interrupts.dos.FileDateTime;
import nz.co.electricbolt.xt.usermode.SharingMode;

public class BaseFile {

    final AccessMode accessMode;
    final SharingMode sharingMode;
    final boolean inheritenceFlag;
    final String filename;
    short fileHandle;

    public BaseFile(final String filename, final AccessMode accessMode, final SharingMode sharingMode, final boolean inheritenceFlag) {
        this.filename = filename;
        this.accessMode = accessMode;
        this.sharingMode = sharingMode;
        this.inheritenceFlag = inheritenceFlag;
    }

    public void setFileHandle(final short fileHandle) {
        this.fileHandle = fileHandle;
    }

    public short getFileHandle() {
        return fileHandle;
    }

    public boolean create() {
        return false;
    }

    public boolean open() {
        return false;
    }

    /**
     * Closes the file.
     * @return true if the file should be removed from the file handle map (user file), or false if
     * it's a standard handle and should remain open.
     */
    public boolean close() {
        return false;
    }

    public short getDeviceInformationWord() {
        return 0;
    }

    public byte[] read(final int size) {
        return null;
    }

    public void write(byte[] buf) {
    }

    public int size() {
        return 0;
    }

    public void seek(final int pos) {
    }

    public int currentPos() {
        return 0;
    }

    public FileDateTime getDateTime() {
        return null;
    }

    public boolean delete() { return false; }
}
