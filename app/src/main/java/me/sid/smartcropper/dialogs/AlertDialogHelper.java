package me.sid.smartcropper.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogHelper {

    public static void showAlert(Context context, final Callback callback, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onSucess(0);
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onSucess(-1);
                dialog.dismiss();
            }
        }).show();
    }

    public interface Callback {

        public void onSucess(int t);

    }

}