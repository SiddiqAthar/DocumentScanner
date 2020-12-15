package me.sid.smartcropper.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class GetFilesUtility extends AsyncTask<String, Void, ArrayList<File>> {
    DirectoryUtils mDirectoryUtils;
    getFilesCallback callback;
    Context context;

    public GetFilesUtility(Context context, getFilesCallback getFiles) {
        this.context=context;
        this.callback = getFiles;
        mDirectoryUtils = new DirectoryUtils(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
     }

    @Override
    protected ArrayList<File> doInBackground(String... strings) {
        mDirectoryUtils.clearSelectedArray();
        return mDirectoryUtils.getSelectedFiles(Environment.getExternalStorageDirectory()
                , strings[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<File> files) {
        super.onPostExecute(files);
         callback.getFiles(files);
    }

    public interface getFilesCallback {
        public void getFiles(ArrayList<File> arrayList);
    }
}