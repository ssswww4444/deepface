package au.edu.unimelb.eresearch.happypets.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.TransactionTooLargeException;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class GeneralUtils {
    private static final String TAG = "GeneralUtils";

    public static final int MAX_PARCELABLE_BYTES = 500000; // max is 500Kb
    public static final int MAX_IMG_BYTES = 3000000; // max is 3Mb
    public static final String TEMP_IMG_NAME = "happypets-temp-img";

    public static Uri tempImgPath;  // Stores path to image stored temporarily (i.e. camera intent)

    public static String compressAndEncodeToBase64(Bitmap image)
            throws TransactionTooLargeException {
        return compressAndEncodeToBase64(image, MAX_IMG_BYTES);
    }

    public static String compressAndEncodeToBase64(Bitmap image, int maxSize)
            throws TransactionTooLargeException {
        int imgSize = image.getRowBytes() * image.getHeight();
        int quality = 100;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        // Compress image until it is below specified maximum size in bytes
        while (imgSize > maxSize && quality > 0) {
            Log.d(TAG, "Compressing bitmap of size (bytes): " + imgSize);
            baos = new ByteArrayOutputStream();
            quality -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            imgSize = baos.size();
            Log.d(TAG, "Compressed bitmap into size (bytes): " + imgSize);
        }

        // Image is too large
        if (quality == 0) {
            throw new TransactionTooLargeException();
        }

        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap decodeToBitmap(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static Bitmap modifyOrientation(Context context, Bitmap bitmap, Uri contentUri) throws IOException {
        InputStream in = context.getContentResolver().openInputStream(contentUri);
        ExifInterface ei = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ei = new ExifInterface(in);
        } else {
            return bitmap;
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        in.close();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    private static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static File createTempImgFile(Context context) {
        try {
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = new File(storageDir, TEMP_IMG_NAME + ".jpg");
            return image;
        } catch (Exception e) {
            Log.d(TAG, "Failed to create temporary file for storing image: " + e.getMessage());
            return null;
        }
    }
}
