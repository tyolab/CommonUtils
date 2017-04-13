package au.com.tyo.data;

/**
 * Created by monfee on 13/4/17.
 */

public class ContentTypes {

    /**
     * Only for Java 7+
     *
     * @param ext
     * @return
     */
    public static boolean isVideo(String ext) {
        if (null == ext || ext.length() == 0)
            return false;
        String fileExt = ext;
        if (ext.charAt(0) == '.')
            fileExt = ext.substring(1);

        switch (fileExt.toLowerCase()) {
            case "mov":
            case "avi":
            case "mp4":
            case "qt":
            case "ogv":
            case "webm":
            case "flv":
            case "mkv":
            case "vob":
            case "mpg":
            case "mpeg":
            case "m4p":
            case "m4v":
                return true;
        }
        return false;
    }
}
