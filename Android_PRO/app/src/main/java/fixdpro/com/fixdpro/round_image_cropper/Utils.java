package fixdpro.com.fixdpro.round_image_cropper;

import android.net.Uri;

import java.io.File;

/**
 * @author albin
 * @date 24/6/15
 */
public class Utils {

    public static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }
}
