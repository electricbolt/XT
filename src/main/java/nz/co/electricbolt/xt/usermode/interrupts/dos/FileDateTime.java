package nz.co.electricbolt.xt.usermode.interrupts.dos;

import java.util.Calendar;

public class FileDateTime {

    byte hours;
    byte minutes;
    byte seconds;
    byte year;
    byte month;
    byte day;

    /**
     * @param hours 0-23 hour
     * @param minutes 0-59 minute
     * @param seconds 0-29 (seconds/2)
     * @param year year-1980
     * @param month 1-12 month
     * @param day 1-31 day
     */
    FileDateTime(final byte hours, final byte minutes, final byte seconds, final byte year, final byte month, final byte day) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public FileDateTime(final long lastModifiedTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastModifiedTime);
        this.hours = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        this.minutes = (byte) calendar.get(Calendar.MINUTE);
        this.seconds = (byte) (calendar.get(Calendar.SECOND) / 2);
        this.year = (byte) (calendar.get(Calendar.YEAR) - 1980);
        this.month = (byte) calendar.get(Calendar.MONTH);
        this.day = (byte) calendar.get(Calendar.DAY_OF_MONTH);
    }

    public short toDOSTime() {
        return FileDateTime.toDOSTime(hours, minutes, seconds);
    }

    public short toDOSDate() {
        return FileDateTime.toDOSDate(year, month, day);
    }

    public static short toDOSTime(final byte hours, final byte minutes, final byte seconds) {
        return (short) (((hours << 11) & 0b1111_1000_0000_0000) |
                ((minutes << 5) & 0b0000_0111_1110_0000) |
                (seconds & 0b0000_0000_0001_1111));
    }

    public static short toDOSDate(final byte year, final byte month, final byte day) {
        return (short) (((year << 9) & 0b1111_1110_0000_0000) |
                ((month << 5) & 0b0000_0001_1110_0000) |
                (day & 0b0000_0000_0001_1111));
    }
}
