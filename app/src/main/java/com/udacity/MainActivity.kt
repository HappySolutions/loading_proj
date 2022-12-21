package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.udacity.helpers.NotificationsHelper
import com.udacity.helpers.NotificationsHelper.createSampleDataNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var context: Context

    private var checkedIdV: Int? = null
//    private var status: NotificationsHelper.DownloadStatus? = null
    var status = NotificationsHelper.DownloadStatus.FAILURE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        context = custom_button.context

        //Register the notification channel
        createNotificationChannel()

        //Set the radio buttons on checked listener
        radioGroup.setOnCheckedChangeListener{ _, checkedId ->
            when (checkedId) {
                R.id.radio_glide -> {
                    checkedIdV = checkedId
                    URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
                }
                R.id.radio_udacity -> {
                    checkedIdV = checkedId
                    URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                }
                R.id.radio_retrofit -> {
                    checkedIdV = checkedId
                    URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
                }
            }
        }

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            when (checkedIdV) {
                null -> {
                    Toast.makeText(
                        this,
                        "Please select the file to download ",
                        Toast.LENGTH_LONG
                    ).show()
                }
                R.id.radio_glide -> {
                    download()
                    createSampleDataNotification(
                        this,
                        "Glide: An image loading and caching lib for Android",
                        "The project repository downloaded",
                        "The project repository downloaded",
                        false,
                        getFileName(),
                        status?.status.toString()
                    )
                }
                R.id.radio_udacity -> {
                    download()
                    createSampleDataNotification(
                        this,
                        "Udacity: Android Kotlin Nanodegree",
                        "The project 3 repository downloaded",
                        "The project 3 repository downloaded",
                        false,
                        getFileName(),
                        status?.status.toString())
                }
                R.id.radio_retrofit -> {
                    download()
                    createSampleDataNotification(
                        this,
                        "Retrofit: A Type-safe HTTP client for Android",
                        "The project repository downloaded",
                        "The project repository downloaded",
                        false,
                        getFileName(),
                        status?.status.toString())
                }
            }
        }
    }
    private fun setButtonState(buttonState: ButtonState) {
        custom_button.setNewButtonState(buttonState)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
             status = NotificationsHelper.DownloadStatus.FAILURE

            if (isDownComp(intent)) {
                status = queryDownloadStatus(intent)
            }
            setButtonState(ButtonState.Completed)
        }
    }

    private fun isDownComp(intent: Intent?): Boolean {
        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        return id == downloadID && intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE
    }

    //query to return download status
    private fun queryDownloadStatus(intent: Intent?): NotificationsHelper.DownloadStatus {
        var status = NotificationsHelper.DownloadStatus.FAILURE
        val query = DownloadManager.Query()

        intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)?.let { query.setFilterById(it) }

        val manager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val cursor: Cursor = manager.query(query)

        if (cursor.moveToFirst()) {
            if (cursor.count > 0) {
                val statusInt: Int =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (statusInt == DownloadManager.STATUS_SUCCESSFUL) {
                    status = NotificationsHelper.DownloadStatus.SUCCESS
                }
            }
        }
        return status
    }

    private fun createNotificationChannel() {
        NotificationsHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            false,
            getString(R.string.app_name),
            "App notification channel."
        )
    }

    private fun download() {
        setButtonState(ButtonState.Loading)

        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    URL.substring(URL.lastIndexOf("/") + 1)
                )

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    //fun to return file name
    private fun getFileName(): String {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.radio_glide -> getString(R.string.glid)
            R.id.radio_udacity -> getString(R.string.udacity)
            R.id.radio_retrofit -> getString(R.string.retrofit)
            else -> ""
        }
    }

    companion object {
        private var URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
