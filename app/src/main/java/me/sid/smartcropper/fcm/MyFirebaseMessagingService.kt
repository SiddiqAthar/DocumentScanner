package me.sid.smartcropper.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import me.sid.smartcropper.BuildConfig
import me.sid.smartcropper.R
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from)
        val data = remoteMessage.data
        if (data != null && data.isNotEmpty()) {
            val iconURL = data["icon"].toString()
            val shortDesc = data["short_desc"].toString()
            val feature = data["feature"].toString()
            val appURL = data["app_url"].toString()
        }
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            sendNotification(
                data["icon"],
                data["title"], data["short_desc"].toString(),
                data["long_desc"], data["app_url"].toString(), data["feature"], 1)
            if ( /* Check if data needs to be processed by long running job */true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(
                TAG, "Message Notification Body: " + remoteMessage.notification!!
                    .body
            )
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]
    // [START on_new_token]
    /**
     * Called if FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve
     * the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]
    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     *
     */
    private fun sendNotification(
        iconUrl: String?,
        title: String?,
        short_desc: String,
        long_desc: String?,
        url: String,
        feature: String?,
        NotificationId: Int)
    {
        val uri = Uri.parse(url) // missing 'http://' will cause crashed
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)
        val channelId = BuildConfig.APPLICATION_ID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val remoteViews = RemoteViews(packageName, R.layout.custom_notification_layout)
        val icon = getBitmapFromURL(iconUrl)
        val ImageUrl = getBitmapFromURL(feature)
        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_short_desc, short_desc)
        remoteViews.setTextViewText(R.id.tv_long_desc, long_desc)
        remoteViews.setImageViewBitmap(R.id.iv_icon, icon)
        remoteViews.setImageViewBitmap(R.id.iv_feature, ImageUrl)
        if (long_desc != null && !long_desc.isEmpty()) {
            remoteViews.setViewVisibility(R.id.tv_long_desc, View.VISIBLE)
        } else {
            remoteViews.setViewVisibility(R.id.tv_long_desc, View.GONE)
        }
        if (feature != null && !feature.isEmpty()) {
            remoteViews.setViewVisibility(R.id.iv_feature, View.VISIBLE)
        } else {
            remoteViews.setViewVisibility(R.id.iv_feature, View.GONE)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setSound(defaultSoundUri)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notification)
    }

    fun getBitmapFromURL(strURL: String?): Bitmap? {
        try {
            val url = URL(strURL)
            val `in` = url.openConnection().getInputStream()
            val bis = BufferedInputStream(`in`, 1024 * 8)
            val out = ByteArrayOutputStream()
            var len = 0
            val buffer = ByteArray(1024)
            while (bis.read(buffer).also { len = it } != -1) {
                out.write(buffer, 0, len)
            }
            out.close()
            bis.close()
            val data = out.toByteArray()
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}