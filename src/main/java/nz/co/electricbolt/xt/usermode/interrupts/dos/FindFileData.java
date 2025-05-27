package nz.co.electricbolt.xt.usermode.interrupts.dos;

import nz.co.electricbolt.xt.usermode.DiskTransferArea;

import java.io.File;

public record FindFileData(short internalId, File[] files, short fileIndex, DiskTransferArea diskTransferArea) {
}
