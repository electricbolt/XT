// DiskFile.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.filedevice;

import nz.co.electricbolt.xt.usermode.AccessMode;
import nz.co.electricbolt.xt.usermode.interrupts.dos.FileDateTime;
import nz.co.electricbolt.xt.usermode.SharingMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class DiskFile extends BaseFile {

    private File file;
    private RandomAccessFile randomAccessFile;

    public DiskFile(final String filename, final AccessMode accessMode, final SharingMode sharingMode, final boolean inheritenceFlag) {
        super(filename, accessMode, sharingMode, inheritenceFlag);
    }

    public boolean open() {
        try {
            file = new File(filename);
            final String mode = switch (accessMode) {
                case readOnly -> "r";
                case writeOnly -> "w";
                case readWrite -> "rw";
            };
            randomAccessFile = new RandomAccessFile(file, mode);
            return true;
        } catch (FileNotFoundException ignored) {
            return false;
        }
    }

    public boolean create() {
        try {
            file = new File(filename);
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.getChannel().truncate(0);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the file.
     *
     * @return true if the file should be removed from the file handle map (user file), or false if
     * it's a standard handle and should remain open.
     */
    public boolean close() {
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException ignored) {
            }
        }
        return true;
    }

    public byte[] read(final int size) {
        try {
            final byte[] buf = new byte[size];
            final int fileRead = randomAccessFile.read(buf);
            if (fileRead == -1) {
                return null;
            }
            if (fileRead != size) {
                final byte[] tmp = new byte[fileRead];
                System.arraycopy(buf, 0, tmp, 0, fileRead);
                return tmp;
            } else {
                return buf;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(final byte[] buf) {
        try {
            randomAccessFile.write(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void seek(final int pos) {
        try {
            randomAccessFile.seek(pos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        try {
            return (int) randomAccessFile.length();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int currentPos() {
        try {
            return (int) randomAccessFile.getFilePointer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileDateTime getDateTime() {
        try {
            final BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return new FileDateTime(attributes.lastModifiedTime().toMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete() {
        return file.delete();
    }
}
