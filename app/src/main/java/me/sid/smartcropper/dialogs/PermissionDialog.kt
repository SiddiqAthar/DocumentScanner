package me.sid.smartcropper.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.permission_dialog.*

import me.sid.smartcropper.R
import java.util.ArrayList

class PermissionDialog(internal var _activity: Activity,var permissionGranted: PermissionGranted) : Dialog(_activity) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_dialog)


        allowBtn.setOnClickListener {
            dismiss()
            permissionGranted.granted()
        }


    }

    interface PermissionGranted{
        fun granted()
    }



}