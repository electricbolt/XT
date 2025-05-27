package nz.co.electricbolt.xt.usermode.interrupts.dos;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.cpu.SegOfs;
import nz.co.electricbolt.xt.usermode.AccessMode;
import nz.co.electricbolt.xt.usermode.DiskTransferArea;
import nz.co.electricbolt.xt.usermode.ErrorCode;
import nz.co.electricbolt.xt.usermode.SharingMode;
import nz.co.electricbolt.xt.usermode.filedevice.NULFile;
import nz.co.electricbolt.xt.usermode.filedevice.BaseFile;
import nz.co.electricbolt.xt.usermode.filedevice.CONFile;
import nz.co.electricbolt.xt.usermode.filedevice.DiskFile;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.*;
import nz.co.electricbolt.xt.usermode.util.DirectoryTranslation;
import nz.co.electricbolt.xt.usermode.util.MemoryUtil;
import nz.co.electricbolt.xt.usermode.util.Trace;
import nz.co.electricbolt.xt.usermode.util.WildcardFileMatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.*;
import java.util.HashMap;
import java.util.Map;

public class FileIO {

    private final Map<Short, BaseFile> fileHandleMap = new HashMap<>();
    private final Map<Short, FindFileData> findFileMap = new HashMap<>();
    private short fileHandleCount = 0;
    private SegOfs diskTransferArea;
    private short findFileCount = 0;

    public FileIO() {
        fileHandleMap.put(fileHandleCount++, new CONFile(AccessMode.readOnly, SharingMode.denyWrite, true)); // STDIN - keyboard.
        fileHandleMap.put(fileHandleCount++, new CONFile(AccessMode.writeOnly, SharingMode.denyRead, true)); // STDOUT - screen.
        fileHandleMap.put(fileHandleCount++, new CONFile(AccessMode.writeOnly, SharingMode.denyNone, true)); // STDERR - screen.
        fileHandleMap.put(fileHandleCount++, new CONFile(AccessMode.writeOnly, SharingMode.denyRead, true)); // STDAUX - first serial port.
        fileHandleMap.put(fileHandleCount++, new CONFile(AccessMode.writeOnly, SharingMode.denyRead, true)); // STDPRN - first printer port.

        diskTransferArea = new SegOfs((short) 0x0090, (short) 0x0080);
    }

    @Interrupt(function = 0x1A, description = "Set disk transfer area address")
    public void setDiskTransferArea(final CPU cpu, final @DS @DX SegOfs address) {
        this.diskTransferArea = address;
    }

    @Interrupt(function = 0x2F, description = "Get disk transfer area address")
    public void getDiskTransferArea(final CPU cpu) {
        cpu.getReg().ES.setValue(diskTransferArea.getSegment());
        cpu.getReg().BX.setValue(diskTransferArea.getOffset());
    }

    @Interrupt(function = 0x3C, description = "Create or truncate file")
    public void createFile(final CPU cpu, final Trace trace, final DirectoryTranslation directoryTranslation,
                           final @CX short fileAttributes, @ASCIZ @DS @DX String filename) {
        // TODO: should use fileAttributes specified.
        filename = directoryTranslation.emulatedPathToHostPath(filename.toUpperCase());
        trace.interrupt("Host filename " + filename);
        final BaseFile baseFile = new DiskFile(filename, AccessMode.readWrite, SharingMode.compatibilityMode, false);
        if (!baseFile.create()) {
            trace.interrupt("Failed");
            setErrorResult(cpu, trace, ErrorCode.PathNotFound);
            return;
        }

        trace.interrupt("Returning filehandle " + fileHandleCount);
        baseFile.setFileHandle(fileHandleCount);
        fileHandleMap.put(fileHandleCount, baseFile);
        fileHandleCount++;
        cpu.getReg().flags.setCarry(false);
        cpu.getReg().AX.setValue(baseFile.getFileHandle());
    }

    @Interrupt(function = 0x3D, description = "Open existing file")
    public void openFile(final CPU cpu, final Trace trace, final DirectoryTranslation directoryTranslation,
                         @ASCIZ @DS @DX String filename, final @AL byte accessSharingMode, final @CL byte attributeMask) {
        AccessMode accessMode = null;
        byte _accessMode = 0;
        try {
            _accessMode = (byte) (accessSharingMode & 0x07);
            accessMode = AccessMode.values()[_accessMode];
        } catch (IndexOutOfBoundsException e) {
            trace.interrupt("Invalid access mode " + _accessMode);
            setErrorResult(cpu, trace, ErrorCode.AccessCodeInvalid);
            return;
        }

        SharingMode sharingMode = null;
        byte _sharingMode = 0;
        try {
            _sharingMode = (byte) ((accessSharingMode >>> 4) & 0x07);
            sharingMode = SharingMode.values()[_sharingMode];
        } catch (IndexOutOfBoundsException e) {
            trace.interrupt("Invalid sharing mode " + _sharingMode);
            setErrorResult(cpu, trace, ErrorCode.AccessCodeInvalid);
            return;
        }

        final boolean inheritenceFlag = (accessSharingMode & 0x80) == 0x80;

        BaseFile baseFile = null;
        if (filename.equals("NUL")) {
            trace.interrupt("Returning filehandle " + fileHandleCount);

            baseFile = new NULFile(accessMode, sharingMode, inheritenceFlag);
            baseFile.setFileHandle(fileHandleCount);
            fileHandleMap.put(fileHandleCount, baseFile);
            fileHandleCount++;

            cpu.getReg().flags.setCarry(false);
            cpu.getReg().AX.setValue(baseFile.getFileHandle());
        } else if (filename.equals("EMMXXXX0")) {
            trace.interrupt("EMM memory not implemented");
            setErrorResult(cpu, trace, ErrorCode.FileNotFound);
        } else {
            filename = directoryTranslation.emulatedPathToHostPath(filename.toUpperCase());
            trace.interrupt("Host filename " + filename);
            baseFile = new DiskFile(filename, accessMode, sharingMode, inheritenceFlag);
            if (!baseFile.open()) {
                trace.interrupt("Failed");
                setErrorResult(cpu, trace, ErrorCode.FileNotFound);
            } else {
                trace.interrupt("Returning filehandle " + fileHandleCount);

                baseFile.setFileHandle(fileHandleCount);
                fileHandleMap.put(fileHandleCount, baseFile);
                fileHandleCount++;

                cpu.getReg().flags.setCarry(false);
                cpu.getReg().AX.setValue(baseFile.getFileHandle());
            }
        }
    }

    @Interrupt(function = 0x3E, description = "Close file")
    public void closeFile(final CPU cpu, final Trace trace, final @BX short fileHandle) {
        final BaseFile baseFile = getFileHandleOrSetErrorResult(cpu, trace, fileHandle);
        if (baseFile != null) {
            if (baseFile.close()) {
                fileHandleMap.remove(fileHandle);
            }
            cpu.getReg().flags.setCarry(false);
        }
    }

    @Interrupt(function = 0x3F, description = "Read from file or device")
    public void readFile(final CPU cpu, final Trace trace, final @BX short fileHandle,
                         final @CX short numberOfBytesToRead, final @DS @DX SegOfs address) {
        final BaseFile baseFile = getFileHandleOrSetErrorResult(cpu, trace, fileHandle);
        if (baseFile != null) {
            final byte[] buf = baseFile.read(numberOfBytesToRead & 0xFFFF);
            cpu.getReg().flags.setCarry(false);
            if (buf == null) {
                cpu.getReg().AX.setValue((short) 0);
            } else {
                MemoryUtil.writeBuf(cpu.getMemory(), address, buf);
                cpu.getReg().AX.setValue((short) buf.length);
            }
        }
    }

    @Interrupt(function = 0x40, description = "Write to file or device")
    public void writeFile(final CPU cpu, final Trace trace, final @BX short fileHandle,
                          final @CX short numberOfBytesToWrite, final @DS @DX SegOfs address) {
        final BaseFile baseFile = getFileHandleOrSetErrorResult(cpu, trace, fileHandle);
        if (baseFile != null) {
            final byte[] buf = MemoryUtil.readBuf(cpu.getMemory(), address, numberOfBytesToWrite);
            baseFile.write(buf);
            cpu.getReg().flags.setCarry(false);
            cpu.getReg().AX.setValue((short) buf.length);
        }
    }

    @Interrupt(function = 0x41, description = "Delete file")
    public void deleteFile(final CPU cpu, final Trace trace, final @BX short fileHandle) {
        final BaseFile baseFile = getFileHandleOrSetErrorResult(cpu, trace, fileHandle);
        if (baseFile != null) {
            if (!baseFile.delete()) {
                setErrorResult(cpu, trace, ErrorCode.FileNotFound);
                return;
            }
            cpu.getReg().flags.setCarry(false);
            cpu.getReg().AL.setValue((byte) 0x03); // Documentation says "appears to be drive of deleted file".
        }
    }

    @Interrupt(function = 0x42, description = "Set current file position")
    public void seekFile(final CPU cpu, final Trace trace, final @BX short fileHandle, @CX @DX int offset,
                         final @AL byte origin) {
        final BaseFile baseFile = getFileHandleOrSetErrorResult(cpu, trace, fileHandle);
        if (baseFile != null) {
            switch (origin) {
                case 0x00: {
                    trace.interrupt("Seeking to beginning");
                    break;
                }
                case 0x01: {
                    trace.interrupt("Seeking from current");
                    offset += baseFile.currentPos();
                    break;
                }
                case 0x02: {
                    trace.interrupt("Seeking from end");
                    offset += baseFile.size();
                    break;
                }
                default: {
                    trace.interrupt("Invalid seek mode " + origin);
                    setErrorResult(cpu, trace, ErrorCode.FunctionNumberInvalid);
                    break;
                }
            }
            baseFile.seek(offset);
            cpu.getReg().flags.setCarry(false);
            cpu.getReg().DX.setValue((short) ((offset >>> 16) & 0xFFFF));
            cpu.getReg().AX.setValue((short) (offset & 0xFFFF));
        }
    }

    @Interrupt(function = 0x43, subfunction = 0x00, description = "Get file attributes")
    public void getFileAttributes(final CPU cpu, final Trace trace, final DirectoryTranslation directoryTranslation,
                                  @ASCIZ @DS @DX String filename) {
        filename = directoryTranslation.emulatedPathToHostPath(filename.toUpperCase());
        trace.interrupt("Host filename " + filename);
        final File file = new File(filename);
        try {
            short result = 0;
            // Check for Windows/DOS FAT file system.
            final DosFileAttributeView dosView = Files.getFileAttributeView(file.toPath(), DosFileAttributeView.class);
            if (dosView != null) {
                final DosFileAttributes attrs = dosView.readAttributes();
                if (attrs.isReadOnly())
                    result |= FileAttribute.readOnly;
                if (attrs.isSystem())
                    result |= FileAttribute.system;
                if (attrs.isHidden())
                    result |= FileAttribute.hidden;
                if (attrs.isDirectory())
                    result |= FileAttribute.directory;
                if (attrs.isArchive())
                    result |= FileAttribute.archive;
            } else {
                // Check for POSIX (Linux/Mac) file system.
                final PosixFileAttributeView posixView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
                if (posixView != null) {
                    PosixFileAttributes attrs = posixView.readAttributes();
                    if (!attrs.permissions().contains(PosixFilePermission.OWNER_WRITE))
                        result |= FileAttribute.readOnly;
                    if (!attrs.isDirectory())
                        result |= FileAttribute.directory;
                } else {
                    // Fall back to most basic attributes.
                    BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    if (attrs.isDirectory())
                        result |= FileAttribute.directory;
                }
            }
            cpu.getReg().flags.setCarry(false);
            cpu.getReg().AX.setValue(result);
        } catch (IOException e) {
            setErrorResult(cpu, trace, ErrorCode.FileNotFound);
        }
    }

    @Interrupt(function = 0x44, subfunction = 0x00, description = "IOCTL Get device information")
    public void getDeviceInformation(final CPU cpu, final Trace trace, final @BX short fileHandle) {
        final BaseFile baseFile = getFileHandleOrSetErrorResult(cpu, trace, fileHandle);
        if (baseFile != null) {
            cpu.getReg().flags.setCarry(false);
            cpu.getReg().AX.setValue(baseFile.getDeviceInformationWord());
        }
    }

    @Interrupt(function = 0x44, subfunction = 0x01, description = "IOCTL Set device information")
    public void setDeviceInformation(final CPU cpu, final Trace trace, final @BX short fileHandle,
                                     final @DX short newDeviceInformationWord) {
        final BaseFile baseFile = getFileHandleOrSetErrorResult(cpu, trace, fileHandle);
        if (baseFile != null) {
            cpu.getReg().flags.setCarry(false);
        }
    }

    @Interrupt(function = 0x44, subfunction = 0x07, description = "IOCTL Get input/output status")
    public void getOutputStatus(final CPU cpu, final Trace trace, final @BX short fileHandle) {
        final BaseFile baseFile = getFileHandleOrSetErrorResult(cpu, trace, fileHandle);
        if (baseFile != null) {
            cpu.getReg().flags.setCarry(false);
            cpu.getReg().AL.setValue((byte) 0xFF); // Ready.
        }
    }

    public static String getDirectoryFromPath(final String path) {
        final int index = path.lastIndexOf("\\");
        if (index != -1) {
            return path.substring(0, index);
        }
        return "";
    }

    public static String getFilenameFromPath(final String path) {
        final int index = path.lastIndexOf("\\");
        if (index != -1) {
            return path.substring(index + 1);
        }
        return path;
    }

    @Interrupt(function = 0x4E, description = "Find first matching file")
    public void findFirstMatchingFile(final CPU cpu, final Trace trace, final DirectoryTranslation directoryTranslation,
                                      final @AL byte appendFlag, final @ASCIZ @DS @DX String path,
                                      final @CX short attributeMask) {
        String directory = getDirectoryFromPath(path);
        final String filename = getFilenameFromPath(path);

        directory = directoryTranslation.emulatedPathToHostPath(directory.toUpperCase());
        trace.interrupt("Host path " + directory + " filename " + filename);

        final WildcardFileMatcher matcher = new WildcardFileMatcher(filename.toUpperCase());
        final File[] files = new File(directory).listFiles(file -> {
            return matcher.matches(file.getName());
        });

        if (files == null || files.length == 0) {
            setErrorResult(cpu, trace, ErrorCode.FileNotFound);
            return;
        }

        final DiskTransferArea dta = new DiskTransferArea(cpu.getMemory(), (short) 0x0090, (short) 0x0080);
        final FileDateTime fileDateTime = new FileDateTime(files[0].lastModified());
        dta.writeFileDate(fileDateTime.toDOSDate());
        dta.writeFileTime(fileDateTime.toDOSTime());
        dta.writeFileSize((int) files[0].length());
        dta.writeFilename(files[0].getName());

        final FindFileData data = new FindFileData(findFileCount, files, (short) 0, dta);
        findFileMap.put(findFileCount, data);
        dta.setInternalId(findFileCount++);
        cpu.getReg().flags.setCarry(false);
    }

    @Interrupt(function = 0x56, description = "Rename file")
    public void renameFile(final CPU cpu, final Trace trace, final DirectoryTranslation directoryTranslation,
                           final @ASCIZ @DS @DX String source, final @ASCIZ @ES @DI String dest) {
        final File sourceFile = new File(directoryTranslation.emulatedPathToHostPath(source.toUpperCase()));
        final File destFile = new File(directoryTranslation.emulatedPathToHostPath(dest.toUpperCase()));

        // TODO: test in real DOS what the actual error codes are based upon the scenarios of files/paths wrong.
        if (!sourceFile.exists()) {
            setErrorResult(cpu, trace, ErrorCode.FileNotFound);
            return;
        } else if (destFile.exists()) {
            setErrorResult(cpu, trace, ErrorCode.PathNotFound);
            return;
        }

        if (sourceFile.renameTo(destFile)) {
            cpu.getReg().flags.setCarry(false);
        } else {
            setErrorResult(cpu, trace, ErrorCode.PathNotFound);
        }
    }

    @Interrupt(function = 0x57, subfunction = 0x00, description = "Get file's date and time")
    public void getFileDateTime(final CPU cpu, final Trace trace, final @BX short fileHandle) {
        final BaseFile baseFile = getFileHandleOrSetErrorResult(cpu, trace, fileHandle);
        if (baseFile != null) {
            final FileDateTime fileDateTime = baseFile.getDateTime();
            if (fileDateTime == null) {
                setErrorResult(cpu, trace, ErrorCode.InvalidHandle);
                return;
            }

            cpu.getReg().flags.setCarry(false);
            cpu.getReg().CX.setValue(fileDateTime.toDOSTime());
            cpu.getReg().DX.setValue(fileDateTime.toDOSDate());
        }
    }

    /**
     * Retrieves the BaseFile that is associated with the filehandle specified. If the filehandle is invalid, AX will
     * be set to 06h (invalid file handle), the carry flag set and the function will return null. Otherwise the
     * BaseFile is returned.
     */
    private BaseFile getFileHandleOrSetErrorResult(final CPU cpu, final Trace trace, final short fileHandle) {
        final BaseFile baseFile = fileHandleMap.get(fileHandle);
        if (baseFile == null) {
            trace.interrupt("Invalid filehandle " + fileHandle);

            setErrorResult(cpu, trace, ErrorCode.InvalidHandle);
            return null;
        }
        return baseFile;
    }

    /**
     * Sets the AX register to the specified error code and sets the carry flag.
     */
    private void setErrorResult(final CPU cpu, final Trace trace, final ErrorCode errorCode) {
        trace.interrupt("Returning error " + errorCode.errorCode);

        cpu.getReg().flags.setCarry(true);
        cpu.getReg().AX.setValue(errorCode.errorCode);
    }
}
