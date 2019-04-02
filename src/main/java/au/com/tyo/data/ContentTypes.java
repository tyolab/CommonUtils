package au.com.tyo.data;

import java.util.Arrays;
import java.util.Collections;
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

    private static final String[] TEXT_EXTENSIONS = {
            "txt"
    };

    private static final List VIDEO_EXTENSION_LIST = Arrays.asList(VIDEO_EXTENSIONS);

    private static final String[] IMAGE_EXTENSIONS = {
            "png",
            "gif",
            "jpg",
            "jpeg",
            "tif",
            "tiff",
            "svg"
            // more here
    };

    private static final List IMAGE_EXTENSION_LIST = Arrays.asList(IMAGE_EXTENSIONS);

    public static String extensionCheck(String ext) {
        String newExt;

        int pos = ext.indexOf('.');
        if (pos > -1)
            newExt = ext.substring(pos + 1);
        else
            newExt = ext;

        return newExt;
    }

    /**
     *
     * @param ext
     * @return
     */
    public static boolean isVideo(String ext) {
        if (null == ext || ext.length() == 0)
            return false;
        return isType(VIDEO_EXTENSION_LIST, ext);
    }

    /**
     *
     * @param ext
     * @return
     */
    public static boolean isImage(String ext) {
        if (null == ext || ext.length() == 0)
            return false;

        return isType(IMAGE_EXTENSION_LIST, ext);
    }

    /**
     *
     * @param ext
     * @return
     */
    public static boolean isText(String ext) {
        if (null == ext || ext.length() == 0)
            return false;

        return isType(TEXT_EXTENSIONS, ext);
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

    private static boolean isType(String[] list, String ext) {
        if (null == ext || ext.length() == 0)
            return false;

        String fileExt = extensionCheck(ext).toLowerCase();

        return Arrays.binarySearch(list, ext, String::compareTo) > -1;
    }

    private static boolean isType(String type, String ext) {
        if (null == ext)
            return false;

        return type.equals(extensionCheck(ext).toLowerCase());
    }

    /**
     *
     * @param file
     * @return
     */
    public static String getExtension(String file) {
        int pos = file.lastIndexOf('.');
        if (pos > -1) {
            String ext = file.substring(pos);
            return ext;
        }
        return null;
    }

    public static boolean isSVG(String url) {
        String ext = getExtension(url);
        return isType("svg", ext);
    }

    public static String[] splitNameByExt(String file) {
        String[] pair = new String[2];
        int pos = file.lastIndexOf('.');
        if (pos > -1 && pos < (file.length() - 1)) {
            pair[1] = file.substring(pos + 1);
            pair[0] = file.substring(0, pos);
        }
        else
            pair[0] = file;
        return pair;
    }
}
