package au.com.tyo.utils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 13/8/18.
 */
public class ByteUtils {

    /**
     * big endian
     *
     * @param bytes
     * @return
     */
    public static int toInteger(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    /**
     *
     * @param value
     * @return
     */
    public static byte[] toBytes(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value };
    }

    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     *
     * @param bytes
     * @return
     */
    public static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
