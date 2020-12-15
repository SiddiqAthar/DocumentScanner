package me.sid.smartcropper.utils;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

public class PDFProcessing {

    PdfDocument pdfDocument;


    public PdfDocument makePDF(Bitmap bitmap, String filename) {
        pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page  =   pdfDocument.startPage(pageInfo);
        Canvas  canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        pdfDocument.finishPage(page);
        /* if (filename.endsWith(".pdf"))
            saveFile(filename);
        else
        {
            saveFile("$filename.pdf");
        }*/

        return pdfDocument;
    }


   /* void saveFile(String filename) {
        if (pdfDocument == null) {
            return;
        }
        val root = File(Environment.getExternalStorageDirectory().absolutePath, "Scanner")
        var isDirectoryCreated: Boolean = root.exists()
        if (!isDirectoryCreated) {
            isDirectoryCreated = root.mkdir()
        }
        if (checkFileName()) {
            val userInputFileName: String = filename
            val file = File(root, userInputFileName)
            try {
                val fileOutputStream = FileOutputStream(file)
                pdfDocument.writeTo(fileOutputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            pdfDocument.close()
        }

        val successMsg: String = "Successful! PATH: Internal Storage/${Environment.getExternalStorageDirectory().absolutePath}/Scanner"
    }

    private fun checkFileName(): Boolean {
        return true
    }*/


}

