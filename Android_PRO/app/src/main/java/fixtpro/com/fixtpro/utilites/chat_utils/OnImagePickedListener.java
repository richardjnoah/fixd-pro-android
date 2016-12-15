package fixtpro.com.fixtpro.utilites.chat_utils;

import java.io.File;

public interface OnImagePickedListener {

    void onImagePicked(int requestCode, File file);

    void onImagePickError(int requestCode, Exception e);

    void onImagePickClosed(int requestCode);
}
