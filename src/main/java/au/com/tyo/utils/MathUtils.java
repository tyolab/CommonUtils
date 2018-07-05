package au.com.tyo.utils;

public class MathUtils {

    /**
     *
     * @param value
     * @return
     */
    public static byte[] toBytes(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((value & 0xff000000) >> 24);
        bytes[1] = (byte) ((value & 0x00ff0000) >> 16);
        bytes[2] = (byte) ((value & 0x0000ff00) >> 8);
        bytes[3] = (byte) (value & 0x000000ff);
        return bytes;
    }

    public static int toUnsignedInt(byte b) {
        return (b + 256) % 256;
    }
}
