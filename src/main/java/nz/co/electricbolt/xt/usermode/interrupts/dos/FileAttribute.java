package nz.co.electricbolt.xt.usermode.interrupts.dos;

public class FileAttribute {
    public static final byte readOnly = 0x01;
    public static final byte hidden = 0x02;

    public static final byte system = 0x04;
    public static final byte volumeLabel = 0x08;
    public static final byte directory = 0x10;
    public static final byte archive = 0x20;

}
