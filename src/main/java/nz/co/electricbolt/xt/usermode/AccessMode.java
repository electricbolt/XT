// AccessMode.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode;

public enum AccessMode {
    readOnly(0),
    writeOnly(1),
    readWrite(2);

    int accessMode;

    AccessMode(final int accessMode) {
        this.accessMode = accessMode;
    }
}