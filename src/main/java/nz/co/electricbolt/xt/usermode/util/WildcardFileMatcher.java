// WildcardFileMatcher.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.util;

import java.util.regex.Pattern;

public class WildcardFileMatcher {

    private final Pattern pattern;

    public WildcardFileMatcher(String wildcard) {
        wildcard = wildcard.replace(".", "\\.").replace("?", ".").replace("*", ".*?");
        pattern = Pattern.compile(wildcard);
    }

    public boolean matches(final String filename) {
        return pattern.matcher(filename).matches();
    }
}
