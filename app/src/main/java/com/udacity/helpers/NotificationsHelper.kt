package com.udacity.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.udacity.DetailActivity
import com.udacity.MainActivity
import com.udacity.R
import com.udacity.services.GlobalBroadcastReceiver

object NotificationsHelper {
    const val FILE_NAME = "fileName"
    const val STATUS = "status"

    enum class DownloadStatus(val status: String) {
        SUCCESS("Success"),
        FAILURE("Fail")
    }

    fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        name: String,
        description: String
    ) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //Create a uniqe name for notification channel
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // Register the channel with the system
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }
    }

    //Create a new notification
    fun createSampleDataNotification(
        context: Context, title: String, message: String,
        bigText: String, autoCancel: Boolean, fileName: String,
        status: String
    ) {
        //Create the Intent for clicking on the notification
        val pendingIntent: PendingIntent?
        val intent = Intent(context, MainActivity::class.java)
        pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        //Create the Intent for clicking on the action button of notification
        val detailActivity = Intent(
            context,
            DetailActivity::class.java
        )

        detailActivity.action = context.getString(R.string.custom_action)
        detailActivity.putExtra("notification_id", 123123123)
        detailActivity.putExtra(FILE_NAME, fileName)
        detailActivity.putExtra(STATUS, status)

        val detailActivityPendingIntent =  PendingIntent.getActivity(
            context,
            0,
            detailActivity,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"
        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {

            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(autoCancel)
            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            priority = NotificationCompat.PRIORITY_DEFAULT
            addAction( R.drawable.ic_launcher_foreground,
                "Check the status",
                detailActivityPendingIntent)

            setContentIntent(pendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notificationBuilder.build())

    }
}