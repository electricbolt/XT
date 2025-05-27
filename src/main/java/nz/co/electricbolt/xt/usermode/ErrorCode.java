// ErrorCode.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

/// Error codes from PC-Interrupts 2nd edition, Chapter 24, MS-DOS Kernel.

public enum ErrorCode {
    NoError(0),
    FunctionNumberInvalid(1),
    FileNotFound(2),
    PathNotFound(3),
    TooManyOpenFiles(4),
    AccessDenied(5),
    InvalidHandle(6),
    MemoryControlBlockDestroyed(7),
    InsufficientMemory(8),
    MemoryBlockAddressInvalid(9),
    EnvironmentInvalid(10),
    FormatInvalid(11),
    AccessCodeInvalid(12),
    DataInvalid(13),
    Reserved1(14),
    InvalidDrive(15),
    AttemptedToRemoveCurrentDirectory(16),
    NotSameDevice(17),
    NoMoreFiles(18),
    DiskWriteProtected(19),
    UnknownUnit(20),
    DriveNotReady(21),
    UnknownCommand(22),
    DataErrorCRC(23),
    BadRequestStructureLength(24),
    SeekError(25),
    UnknownMediaType(26),
    SectorNotFound(27),
    PrinterOutOfPaper(28),
    WriteFault(29),
    ReadFault(30),
    GeneralFailure(31),
    SharingViolation(32),
    LockViolation(33),
    DiskChangeInvalid(34),
    FCBUnavailable(35),
    SharingBufferOverflow(36),
    CodePageMismatch(37),
    CannotCompleteFileOperation(38),
    InsufficientDiskSpace(39),
    FileExists(80),
    CannotMakeDirectory(82),
    FailOnInt24(83);

    public final short errorCode;

    private ErrorCode(final int errorCode) {
        this.errorCode = (short) errorCode;
    }
}
