package com.example.mlapp

import android.Manifest
import android.R
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hololo.tutorial.library.PermissionStep
import com.hololo.tutorial.library.Step
import com.hololo.tutorial.library.TutorialActivity


class OnBoardingActivity : TutorialActivity() {
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Thread.sleep(2000)
        installSplashScreen()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        if (isReadExternalStoragePermissionGranted()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        addFragment(
            Step.Builder().setTitle("Extract text from images using Machine learning")
                .setContent("Recognise text in images with ML kit in Android")
                .setBackgroundColor(Color.parseColor("#9A9AEB")) // int background color
                .setDrawable(com.example.mlapp.R.drawable.text_detection1) // int top drawable
                .setSummary("You can use ML Kit to recognize text in images or video, such as the text of a street sign.")
                .build()
        )

        addFragment(
            Step.Builder().setTitle("Copy and reuse the extracted text from image in seconds.")
                .setContent("Recognise text in images with ML kit in Android")
                .setBackgroundColor(Color.parseColor("#9A9AEB")) // int background color
                .setDrawable(com.example.mlapp.R.drawable.text_detection_2) // int top drawable
                .setSummary("You can use ML Kit to recognize text in images or video, such as the text of a street sign.")
                .build()
        )

        addFragment(
            Step.Builder().setTitle("This is header")
                .setContent("Recognise text in images with ML kit in Android")
                .setBackgroundColor(Color.parseColor("#9A9AEB")) // int background color
                .setDrawable(com.example.mlapp.R.drawable.text_detection_3) // int top drawable
                .setSummary("You can use ML Kit to recognize text in images or video, such as the text of a street sign.")
                .build()
        )

        addFragment(
            Step.Builder().setTitle("Face recogition and sentiment ananlysis using ML kit in Android")
                .setContent("This is the content")
                .setBackgroundColor(Color.parseColor("#9A9AEB")) // int background color
                .setDrawable(com.example.mlapp.R.drawable.sentiment_analysis_1) // int top drawable
                .setSummary("This is summary")
                .build()
        )
        addFragment(
            Step.Builder().setTitle("Face recogition and sentiment ananlysis using ML kit in Android")
                .setContent("This is content")
                .setBackgroundColor(Color.parseColor("#9A9AEB")) // int background color
                .setDrawable(com.example.mlapp.R.drawable.sentiment_analysis_2) // int top drawable
                .setSummary("This is summary")
                .build()
        )

        addFragment(
            PermissionStep.Builder().setTitle(getString(com.example.mlapp.R.string.permission_title))
                .setContent(getString(com.example.mlapp.R.string.permission_detail))
                .setBackgroundColor(Color.parseColor("#7373E3"))
                .setDrawable(com.example.mlapp.R.drawable.permission)
                .setSummary(getString(com.example.mlapp.R.string.continue_and_learn))
                .setPermissions(
                    arrayOf<String>(
                       Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
                .build()
        )


    }

    override fun currentFragmentPosition(position: Int) {
        Toast.makeText(this, "Position : " + position, Toast.LENGTH_SHORT).show();
    }

    override fun finishTutorial() {
        if (isReadExternalStoragePermissionGranted()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            Toast.makeText(this, "Please grant permission", Toast.LENGTH_SHORT).show();
            requestReadExternalStoragePermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode,permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                showRationalDialogForPermissions()
            }
        }
    }

    // Fucntion to check if permissions are granted or not
    private fun isReadExternalStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Function to request permission
    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_REQUEST_CODE
        )
    }


    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
                finish()
            }.show()
    }


}