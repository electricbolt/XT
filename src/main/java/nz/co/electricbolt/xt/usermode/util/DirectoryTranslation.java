// DirectoryTranslation.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.util;

import java.io.File;

public class DirectoryTranslation {

    private final String workingDirectory;

    public DirectoryTranslation(final String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Converts an emulated MS-DOS program path to a host path rooted from the working directory. macOS example:
     * <pre>
     * Working directory = /Users/matthew/Documents
     * Emulated path = C:\Dev\TASM.EXE
     * Result = /Users/matthew/Documents/Dev/TASM.EXE
     * </pre>
     */
    public String emulatedPathToHostPath(String path) {
        if (path.startsWith("C:")) {
            path = path.substring(2);
        }
        path = path.replace('\\', File.separatorChar);
        if (path.startsWith(File.separator)) {
            path = path.substring(1);
        }
        path = workingDirectory + path;
        return path;
    }

    /**
     * Converts a host path to an emulated MS-DOS program path. macOS example:
     * <pre>
     * Working directory = /Users/matthew/Documents
     * Host path = /Users/matthew/Documents/Dev/TASM.EXE
     * Result = C:\Dev\TASM.EXE
     * </pre>
     */
    public String hostPathToEmulatedPath(String path) {
        if (path.startsWith(workingDirectory)) {
            path = path.substring(workingDirectory.length());
        }
        path = path.replace(File.separatorChar, '\\');
        if (!path.startsWith("\\")) {
            path = "\\" + path;
        }
        path = "C:" + path;
        return path;
    }
}
