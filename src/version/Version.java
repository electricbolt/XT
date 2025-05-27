public class Version {

    public static void main(String[] args) {
        String[] versionElements = System.getProperty("java.version").split("\\.");
        int version = Integer.parseInt(versionElements[0]);
        if (version < 17)
            System.exit(1);
    }
}