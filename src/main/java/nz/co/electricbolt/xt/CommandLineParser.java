// CommandLineParser.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt;

public class CommandLineParser {

    private final String[] args;
    private int index;

    public CommandLineParser(final String[] args) {
        this.args = args;
        this.index = 0;
    }

    public boolean hasNext() {
        return index < args.length;
    }

    public String peek() {
        if (hasNext()) {
            return args[index];
        } else {
            return null;
        }
    }

    public String next() {
        if (hasNext()) {
            return args[index++];
        } else {
            return null;
        }
    }
}
