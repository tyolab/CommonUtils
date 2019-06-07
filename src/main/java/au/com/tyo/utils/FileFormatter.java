/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.utils;

public class FileFormatter {

    public static String unitPb = "PB";
    public static String unitTb = "TB";
    public static String unitGb = "GB";
    public static String unitMb = "MB";
    public static String unitKb = "KB";
    public static String unitBytes = "bytes";

    public static final long SIZE_KB = 1024;
    public static final long SIZE_MB = SIZE_KB * 1024;
    public static final long SIZE_GB = SIZE_MB * 1024;

    /**
     * FIXME
     *
     * probably it is too big to fit using a long data type
     */
    public static final long SIZE_TB = SIZE_GB * 1024;
    public static final long SIZE_PB = SIZE_TB * 1024;
    public static final long SIZE_EB = SIZE_PB * 1024;

    public static long byteSizeToMegabyte(long size) {
        return size / 1000000;
    }

    public static float byteSizeToMegabyte(float size, long precision) {
        return ((long) (size / (SIZE_KB / (precision * 10)))) / ((float)(precision * 10));
    }

    public static float byteSizeToUnit(long unitThreshold, float size, int precision) {
        return (float) ((long) (size / (unitThreshold / (precision * 10)))) / (precision * 10);
    }

    public static String byteSizeToMegabyteString(long size) {
        return "" + byteSizeToMegabyte(size) + "Mb";
    }

    public static String byteSizeToMegabyteString(float size) {
        return byteSizeToMegabyteString((long) size);
    }

    public static String byteSizeToMegabyteString(float size, int precision) {
        return "" + byteSizeToMegabyte(size, precision) + "Mb";
    }

    public static String byteSizeToString(long size, int precision) {
        if (size <= SIZE_KB)
            return "" + size + ' ' + unitBytes;
        else if (size <= SIZE_MB)
            return "" + byteSizeToUnit(SIZE_KB, size, precision) + ' ' + unitKb;
        else if (size <= SIZE_GB)
            return "" + byteSizeToUnit(SIZE_MB, size, precision) + ' ' + unitMb;
        else if (size <= SIZE_TB)
            return "" + byteSizeToUnit(SIZE_GB, size, precision) + ' ' + unitGb;
        else if (size <= SIZE_PB)
            return "" + byteSizeToUnit(SIZE_TB, size, precision) + ' ' + unitTb;
        return "" + size;
    }
}
