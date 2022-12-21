package com.udacity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.helpers.NotificationsHelper
import com.udacity.helpers.NotificationsHelper.FILE_NAME
import com.udacity.helpers.NotificationsHelper.STATUS
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        applyFileName_fileStatus()

        fab.setOnClickListener{
            onBackPressed()
        }
    }

    

    private fun applyFileName_fileStatus() {
        val fileName = intent.getStringExtra(FILE_NAME)
        val statusText = intent.getStringExtra(STATUS)

        file_name.text = fileName
        file_status.text = statusText

        if (statusText.equals(getString(R.string.success))) {
            file_status.setTextColor(Color.GREEN)
        } else {
            file_status.setTextColor(Color.RED)
        }
    }
}
