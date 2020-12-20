package me.sid.smartcropper.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.sid.smartcropper.views.activities.CropActivity;
import me.sid.smartcropper.views.activities.GernalCameraActivity;

public class ExtractImages {


    ArrayList<File> arrayListfile = new ArrayList<>();
    Context mContext;

   public ExtractImages(Context context) {
        this.mContext = context;
    }

    public void extract(String filepath) {

        PRStream prStream;


        PdfObject pdfObject;

        PdfImageObject pdfImageObject;


        try {

            // Create pdf reader

            PdfReader reader = new PdfReader(filepath);

            // Get number of objects in pdf document

            int numOfObject = reader.getXrefSize();

            for (int i = 0; i < numOfObject; i++) {

                // Get PdfObject

                pdfObject = reader.getPdfObject(i);

                if (pdfObject != null && pdfObject.isStream()) {

                    prStream = (PRStream) pdfObject; //cast object to stream

                    PdfObject type = prStream.get(PdfName.SUBTYPE); //get the object type

                    // Check if the object is the image type object

                    if (type != null && type.toString().equals(PdfName.IMAGE.toString())) {

                        pdfImageObject = new PdfImageObject(prStream);

                        byte[] imgdata = pdfImageObject.getImageAsBytes();
                        File photo = new File(Environment.getExternalStorageDirectory(), "image_to_pdf_" + System.currentTimeMillis() + ".jpg");

                        if (photo.exists()) {
                            photo.delete();
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(photo.getPath());

                            fos.write(imgdata);
                            fos.close();
                            arrayListfile.add(photo);
                        } catch (java.io.IOException e) {
                            Log.e("PictureDemo", "Exception in photoCallback", e);
                        }

//                        new SavePhotoTask(imgdata).execute();
                    }
                }
            }

            Intent intent = new Intent(mContext, CropActivity.class);
            intent.putExtra("FILES_TO_SEND", arrayListfile);
            mContext.startActivity(intent);
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }

    }


/*
    class SavePhotoTask extends AsyncTask<byte[], String, File> {

        byte[] imgesArray;

        SavePhotoTask(byte[] array) {
            this.imgesArray = array;
        }

        @Override
        protected File doInBackground(byte[]... jpeg) {

            File photo = new File(Environment.getExternalStorageDirectory(), "image_to_pdf_" + System.currentTimeMillis() + ".jpg");

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(photo.getPath());

                fos.write(imgesArray);
                fos.close();
            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
                return null;
            }

            return photo;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            arrayListfile.add(file);
        }
    }
*/

}
