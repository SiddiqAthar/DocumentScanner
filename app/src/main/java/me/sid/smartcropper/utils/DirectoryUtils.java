package me.sid.smartcropper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.sid.smartcropper.R;

import static me.sid.smartcropper.utils.Constants.STORAGE_LOCATION;
import static me.sid.smartcropper.utils.Constants.docExtension;
import static me.sid.smartcropper.utils.Constants.docxExtension;
import static me.sid.smartcropper.utils.Constants.excelExtension;
import static me.sid.smartcropper.utils.Constants.excelWorkbookExtension;
import static me.sid.smartcropper.utils.Constants.folderDirectory;
import static me.sid.smartcropper.utils.Constants.jpgExtension;
import static me.sid.smartcropper.utils.Constants.pdfDirectory;
import static me.sid.smartcropper.utils.Constants.pdfExtension;
import static me.sid.smartcropper.utils.Constants.textExtension;
import static me.sid.smartcropper.utils.Constants.trashfolderDirectory;

public class DirectoryUtils {

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;
    private ArrayList<String> mFilePaths;
    private ArrayList<File> fileArrayList = new ArrayList<>();
    private ArrayList<File> selectedFiles = new ArrayList<>();
    PdfDocument pdfDocument;

    public DirectoryUtils(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Used to search for PDF matching the search query
     *
     * @param query - Query from search bar
     * @return ArrayList containing all the pdf files matching the search query
     */
    ArrayList<File> searchPDF(String query) {
        ArrayList<File> searchResult = new ArrayList<>();
        final File[] files = getOrCreatePdfDirectory().listFiles();
        ArrayList<File> pdfs = searchPdfsFromPdfFolder(files);
        for (File pdf : pdfs) {
            String path = pdf.getPath();
            String[] fileName = path.split("/");
            String pdfName = fileName[fileName.length - 1].replace("pdf", "");
            if (checkChar(query, pdfName) == 1) {
                searchResult.add(pdf);
            }
        }
        return searchResult;
    }

    /**
     * Used in searchPDF to give the closest result to search query
     *
     * @param query    - Query from search bar
     * @param fileName - name of PDF file
     * @return 1 if the search query and filename has same characters , otherwise 0
     */
    private int checkChar(String query, String fileName) {
        query = query.toLowerCase();
        fileName = fileName.toLowerCase();
        Set<Character> q = new HashSet<>();
        Set<Character> f = new HashSet<>();
        for (char c : query.toCharArray()) {
            q.add(c);
        }
        for (char c : fileName.toCharArray()) {
            f.add(c);
        }

        if (q.containsAll(f) || f.containsAll(q))
            return 1;

        return 0;
    }

    // RETURNING LIST OF FILES OR DIRECTORIES

    /**
     * Returns pdf files from folder
     *
     * @param files list of files (folder)
     */
    ArrayList<File> getPdfsFromPdfFolder(File[] files) {
        ArrayList<File> pdfFiles = new ArrayList<>();
        if (files == null)
            return pdfFiles;
        for (File file : files) {
            if (isPDFAndNotDirectory(file))
                pdfFiles.add(file);
        }
        return pdfFiles;
    }

    private ArrayList<File> searchPdfsFromPdfFolder(File[] files) {
        ArrayList<File> pdfFiles = getPdfsFromPdfFolder(files);
        if (files == null)
            return pdfFiles;
        for (File file : files) {
            if (file.isDirectory()) {
                for (File dirFiles : file.listFiles()) {
                    if (isPDFAndNotDirectory(dirFiles))
                        pdfFiles.add(dirFiles);
                }
            }
        }
        return pdfFiles;
    }

    /**
     * Checks if a given file is PDF
     *
     * @param file - input file
     * @return tru - if condition satisfies, else false
     */
    private boolean isPDFAndNotDirectory(File file) {
        return !file.isDirectory() &&
                file.getName().endsWith(mContext.getString(R.string.pdf_ext));
    }

    /**
     * create PDF directory if directory does not exists
     */
    public File getOrCreatePdfDirectory() {
        File folder = new File(mSharedPreferences.getString(STORAGE_LOCATION,
                StringUtils.getInstance().getDefaultStorageLocation()));
        if (!folder.exists())
            folder.mkdir();
        return folder;
    }

    public File saveImageFile(Bitmap imageToSave, String fileName) {


        File directory = new File(Environment.getExternalStorageDirectory().toString(), folderDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, fileName + ".jpg");
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String filePath = file.getAbsolutePath() + File.separator + fileName;
        return file;
    }


    public File savePDFFile(Bitmap bitmap, String fileName) {

        File directory = new File(Environment.getExternalStorageDirectory().toString(), folderDirectory);


        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;


        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);



     /*   pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        canvas.drawBitmap(bitmap, 0, 0, null);
        pdfDocument.finishPage(page);*/


        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, fileName + ".pdf");
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            pdfDocument.writeTo(out);
            out.flush();
            out.close();
            pdfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String filePath = file.getAbsolutePath() + File.separator + fileName;
        return file;


        /*if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, fileName + ".pdf");
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            pdfDocument.writeTo(out);
            out.flush();
            out.close();
            pdfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String filePath = file.getAbsolutePath() + File.separator + fileName;
        return file;*/
    }

    /**
     * get the PDF files stored in directories other than home directory
     *
     * @return ArrayList of PDF files
     */
    public ArrayList<File> getPdfFromOtherDirectories() {
        mFilePaths = new ArrayList<>();
        walkDir(getOrCreatePdfDirectory());
        ArrayList<File> files = new ArrayList<>();
        for (String path : mFilePaths)
            files.add(new File(path));
        return files;
    }


    /**
     * gets a list of all the pdf files on the user device
     *
     * @return - list of file absolute paths
     */
    ArrayList<String> getAllPDFsOnDevice() {
        mFilePaths = new ArrayList<>();
        walkDir(Environment.getExternalStorageDirectory());
        return mFilePaths;
    }

    /**
     * Walks through given dir & sub directory, and append file path to mFilePaths
     *
     * @param dir - root directory
     */
    private void walkDir(File dir) {
        walkDir(dir, Collections.singletonList(pdfExtension));
    }

    /**
     * Walks through given dir & sub direc, and append file path to mFilePaths
     *
     * @param dir        - root directory
     * @param extensions - a list of file extensions to search for
     */
    private void walkDir(File dir, List<String> extensions) {
        File[] listFile = dir.listFiles();
        if (listFile != null) {
            for (File aListFile : listFile) {

                if (aListFile.isDirectory()) {
                    walkDir(aListFile, extensions);
                } else {
                    for (String extension : extensions) {
                        if (aListFile.getName().endsWith(extension)) {
                            //Do what ever u want
                            mFilePaths.add(aListFile.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    /**
     * gets a list of all the excel files on the user device
     *
     * @return - list of file absolute paths
     */
    ArrayList<String> getAllExcelDocumentsOnDevice() {
        mFilePaths = new ArrayList<>();
        walkDir(Environment.getExternalStorageDirectory(), Arrays.asList(excelExtension, excelWorkbookExtension));
        return mFilePaths;
    }

    /**
     * creates new folder for temp files
     */
    public static void makeAndClearTemp() {
        String dest = Environment.getExternalStorageDirectory().toString() +
                pdfDirectory + Constants.tempDirectory;
        File folder = new File(dest);
        boolean result = folder.mkdir();

        // clear all the files in it, if any
        if (result && folder.isDirectory()) {
            String[] children = folder.list();
            for (String child : children) {
                new File(folder, child).delete();
            }
        }
    }

    public ArrayList<File> searchDir(File dir) {
        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory() && FileList[i].getName().equals("")) {
                    searchDir(FileList[i]);
                } else {
                    if (FileList[i].getName().endsWith(pdfExtension) ||
                            FileList[i].getName().endsWith(textExtension) ||
                            FileList[i].getName().endsWith(jpgExtension)) {
                        //here you have that file.
                        fileArrayList.add(FileList[i]);

                    }
                }
            }
            return fileArrayList;
        }
        return null;
    }

    public ArrayList<File> getSelectedFiles(File file, String type) {
        File FileList[] = file.listFiles();
        String[] types = type.split(",");
        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {
//                if (!FileList[i].getAbsolutePath().equals("/storage/emulated/0/Doc Scanner/Trash"))
                if (!FileList[i].getAbsolutePath().equals(Environment.getExternalStorageDirectory() + trashfolderDirectory))
                {
                    if (FileList[i].isDirectory()) {
                        getSelectedFiles(FileList[i], type);
                    } else {
                        if (FileList[i].getName().endsWith(types[0]) || FileList[i].getName().endsWith(types[1])) {
                            //here you have that file.
                            selectedFiles.add(FileList[i]);

                        }
                    }
                }
            }
            return selectedFiles;
        }
        return null;
    }

    public void clearSelectedArray() {
        selectedFiles = new ArrayList<>();
    }

    public void clearFilterArray() {
        fileArrayList = new ArrayList<>();
    }


    public boolean renameFile(File file, String rename) {
        File from = new File(file.getAbsolutePath());
        File to = new File(file.getParent(), rename + "." + file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")).replace(".", ""));
        return from.renameTo(to);
    }

    public File createFolder(String folderName) {
        //create folder
        File file = new File(Environment.getExternalStorageDirectory().toString(), folderDirectory + folderName);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = file.getAbsolutePath() + File.separator + folderName;
        return file;
    }


    public File restorTrashFolder() {
        //create folder
        File file = new File(Environment.getExternalStorageDirectory().toString(), folderDirectory );
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = file.getAbsolutePath() + File.separator ;
        return file;
    }


    public ArrayList<File> getAllFolders() {
        ArrayList<File> allFolders = new ArrayList<>();
        File f = new File(Environment.getExternalStorageDirectory() + folderDirectory);

        File[] files = f.listFiles();
        if (files != null) {
            for (File inFile : files) {
                if (inFile.isDirectory() && !inFile.getName().equals("Trash")) {
                    allFolders.add(inFile);
                }
            }
        }

        return allFolders;
    }

    public boolean deleteFile(File file) {
        File file1 = new File(file.getAbsolutePath());
        if (file1.exists())
            return file.delete();
        return false;
    }

    public boolean moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath + inputFile);
            if (!dir.exists()) {
                dir.createNewFile();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(dir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
//            new File(inputPath).delete();
            deleteFile(new File(inputPath));
            return true;

        } catch (Exception fnfe1) {
            Log.e("tag", fnfe1.getMessage() + "");
            return false;
        }


    }


    public static String getDownloadFolderPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

}
