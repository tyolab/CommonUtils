package au.com.tyo.data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 13/4/17.
 */

public class ContentTypes {
    
    private static final String[] VIDEO_EXTENSIONS = {
        "mov",
        "avi",
        "mp4",
        "qt",
        "ogv",
        "webm",
        "flv",
        "mkv",
        "vob",
        "mpg",
        "mpeg",
        "m4p",
        "m4v",
        "wmv"
    };

    private static final List VIDEO_EXTENSION_LIST = Arrays.asList(VIDEO_EXTENSIONS);

    private static final String[] IMAGE_EXTENSIONS = {
            "png",
            "gif",
            "jpg",
            "jpeg"
            // more here
    };

    private static final List IMAGE_EXTENSION_LIST = Arrays.asList(IMAGE_EXTENSIONS);

    private static String extensionCheck(String ext) {
        String fileExt = ext;
        if (ext.charAt(0) == '.')
            fileExt = ext.substring(1);
        return fileExt;
    }

    /**
     *
     * @param ext
     * @return
     */
    public static boolean isVideo(String ext) {
        return isType(VIDEO_EXTENSION_LIST, ext);
    }

    /**
     *
     * @param ext
     * @return
     */
    public static boolean isImage(String ext) {
        return isType(IMAGE_EXTENSION_LIST, ext);
    }

    /**
     *
     * @param list
     * @param ext
     * @return
     */
    private static boolean isType(List list, String ext) {
        if (null == ext || ext.length() == 0)
            return false;

        String fileExt = extensionCheck(ext).toLowerCase();

        return list.contains(fileExt);
    }
}
