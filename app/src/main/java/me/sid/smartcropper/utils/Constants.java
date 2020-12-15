package me.sid.smartcropper.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

import me.sid.smartcropper.BuildConfig;

public class Constants {
    public static int READ_EXTERNAL_STORAGE = 101;
    public static int OPEN_CAMERA = 1001;
    public static int WRITE_EXTERNAL_STORAGE = 155;
    public static int HEADER_TYPE = 120;
    public static int BUTTON_TYPE = 1121;
    public static int RESULT_LOAD_IMG = 10121;
    public static int ITEM_TYPE = 1120;
    public static int EMPTY_VIEW = 71120;
    public static final String STORAGE_LOCATION = "storage_location";
    public static final String pdfDirectory = "/PDFfiles/";
    public static final String PATH_SEPERATOR = "/";
    public static final String pdfExtension = ".pdf";
    public static final String jpgExtension = ".jpg";
    public static final String textExtension = ".txt";
    public static final String excelExtension = ".xls";
    public static final String excelWorkbookExtension = ".xlsx";
    public static final String docExtension = ".doc";
    public static final String docxExtension = ".docx";
    public static final String tempDirectory = "temp";
    public static final String folderDirectory = "/Doc Scanner/";
    public static final String trashfolderDirectory = "/Doc Scanner/Trash";
    public static final String IMAGE_SCALE_TYPE_ASPECT_RATIO = "maintain_aspect_ratio";
    public static final String PG_NUM_STYLE_PAGE_X_OF_N = "pg_num_style_page_x_of_n";
    public static final String PG_NUM_STYLE_X_OF_N = "pg_num_style_x_of_n";
    public static final String DEFAULT_FONT_COLOR_TEXT = "DefaultFontColor";
    public static final int DEFAULT_FONT_COLOR = -16777216;
    public static final String DEFAULT_PAGE_COLOR_TTP = "DefaultPageColorTTP";
    public static final String DEFAULT_FONT_FAMILY_TEXT = "DefaultFontFamily";
    public static final String DEFAULT_FONT_FAMILY = "TIMES_ROMAN";
    public static final String DEFAULT_FONT_SIZE_TEXT = "DefaultFontSize";
    public static final int DEFAULT_FONT_SIZE = 11;
    public static final String DEFAULT_PAGE_SIZE_TEXT = "DefaultPageSize";
    public static final String DEFAULT_PAGE_SIZE = "A4";
    public static final String DEFAULT_PAGE_COLOR_ITP = "DefaultPageColorITP";
    public static final int DEFAULT_PAGE_COLOR = Color.WHITE;
    public static final String AUTHORITY_APP = "com.techlogix.pdftime";
    public static final  int mFileSelectCode = 0;

    public static void shareFile(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri(context, file));
        context.startActivity(Intent.createChooser(intent, "Share Sound File"));
    }

    public static Uri fileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT < 24)
            return Uri.fromFile(new File(file.getAbsolutePath()));
        return FileProvider.getUriForFile(
                context, "me.sid.smartcropper.fileProvider", new File(file.getAbsolutePath())
        );
    }

    public static void excelIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri(context, file), "application/vnd.ms-excel");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    public static void textFileIntent(Context context, File filee) {
        File file = new File(filee.getAbsolutePath());
        Uri uri = fileUri(context, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    public static void doxFileIntent(Context context, File file) {
        //Uri uri = Uri.parse("file://"+file.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "application/msword";
        intent.setDataAndType(fileUri(context,file), type);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }


}
