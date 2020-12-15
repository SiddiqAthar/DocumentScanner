package me.sid.smartcropper.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class ImageUtils {

    static public Bitmap loadCapturedBitmap(String fileName) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        int targetW = 1100;
        int targetH = 1100;

        int degree = getRotateDegreeFromExif(fileName);

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);/*from   w  w w.  j  a v  a2 s  .co  m*/

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(fileName, bmOptions);
        if (bitmap == null)
            return null;
        Bitmap rotatedImage = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        bitmap = null;
        return rotatedImage;

    }

    static private int getRotateDegreeFromExif(String filePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                degree = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                degree = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                degree = 270;
            }
            if (degree != 0) {
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                        "0");
                exifInterface.saveAttributes();
            }
        } catch (IOException e) {
            degree = -1;
            e.printStackTrace();
        }

        return degree;
    }

    static public Bitmap scaledBitmap(Bitmap fileName) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */
        /* Get the size of the ImageView */
        int targetW = 1100;
        int targetH = 1100;

        Bitmap bitmap = Bitmap.createScaledBitmap(fileName, targetW, targetH, true);

        return bitmap;

    }


    static public Bitmap rotatedBitmap(float angle,Bitmap bitmapOrg) {

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        int newWidth = 1100;
        int newHeight = 1100;

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // rotate the Bitmap
        matrix.postRotate(angle);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                width, height, matrix, true);
        return resizedBitmap;
    }


}
