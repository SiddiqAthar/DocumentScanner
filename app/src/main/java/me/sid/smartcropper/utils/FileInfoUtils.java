package me.sid.smartcropper.utils;

import java.io.File;
import java.util.Date;

public class FileInfoUtils {

    // GET PDF DETAILS

    /**
     * Gives a formatted last modified date for pdf ListView
     *
     * @param file file object whose last modified date is to be returned
     * @return String date modified in formatted form
     **/
    public static String getFormattedDate(File file) {
        Date lastModDate = new Date(file.lastModified());
        String[] formatDate = lastModDate.toString().split(" ");
        String time = formatDate[3];
        String[] formatTime = time.split(":");
        String date = formatTime[0] + ":" + formatTime[1];

        return formatDate[0] + ", " + formatDate[1] + " " + formatDate[2] + " at " + date;
    }

    /**
     * Gives a formatted size in MB for every pdf in pdf ListView
     *
     * @param file file object whose size is to be returned
     * @return String Size of pdf in formatted form
     */
    public static String getFormattedSize(File file) {
        String modifiedFileSize = null;
        double fileSize = 0.0;
        if (file.isFile()) {
            fileSize = (double) file.length();//in Bytes

            if (fileSize < 1024) {
                modifiedFileSize = String.valueOf(fileSize).concat("B");
            } else if (fileSize > 1024 && fileSize < (1024 * 1024)) {
                modifiedFileSize = String.valueOf(Math.round((fileSize / 1024 * 100.0)) / 100.0).concat("KB");
            } else {
                modifiedFileSize = String.valueOf(Math.round((fileSize / (1024 * 1204) * 100.0)) / 100.0).concat("MB");
            }
        } else {
            modifiedFileSize = "Unknown";
        }

        return modifiedFileSize;


//        return String.format("%.2f MB", (double) file.length() / (1024 * 1024));
    }


}
