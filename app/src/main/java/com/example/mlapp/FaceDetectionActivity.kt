package com.example.mlapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mlapp.databinding.ActivityFaceDetectionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException

class FaceDetectionActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityFaceDetectionBinding
    private val READ_STORAGE_PERMISSION_CODE = 1
    private val PICK_IMAGE_REQUEST_CODE = 2
    private val CLICK_IMAGE_REQUEST_CODE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.dark_lavendar)
        }

        var btnClickImage = findViewById<Button>(R.id.btn_click_image)
        var tvFaceDetectResult = findViewById<TextView>(R.id.tv_face_detect_result)

        btnClickImage.setOnClickListener {
            // open up the camera and store the image
            // Upon clicking an image, we will run the ML algo to detect text out of it
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager) != null){
                // i want to recieve the image and send it for result extraction
                startActivityForResult(intent, CLICK_IMAGE_REQUEST_CODE)
            }else{
                // something went wrong
                Toast.makeText(this, "OOPS, something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSelectImageGallery.setOnClickListener {

            //1. Checks the permissiom to read external storage
            //2. Provides functionality to select an image
            if(ContextCompat.checkSelfPermission(
                    this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ){
                // Show image chooser
                showImageChooser()

            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Todo show image chooser
                showImageChooser()
            }
        }else{
            Toast.makeText(this,
                "You just denied permission for storage. You can enable it from settings.",
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CLICK_IMAGE_REQUEST_CODE  && resultCode == RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as Bitmap
            detectFace(bitmap)
        }else if(requestCode ==PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            val imageUri = data?.data // Get the image URI from the Intent
            if(imageUri != null){
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    detectFace(bitmap)
                }catch (e: IOException){
                    Log.e("FaceDetectionActivity", "Failed to load image", e)
                }
            }

        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun detectFace(bitmap: Bitmap) {
        // High-accuracy landmark detection and face classification
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
        // Real-time contour detection
        val realTimeOpts = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)
        val detectorRealTime = FaceDetection.getClient(realTimeOpts)
        val image = InputImage.fromBitmap(bitmap, 0)
        var resultText = ""

        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully

                var i = 1
                for (face in faces) {
                    resultText += "Face number: $i\n"+
                            "Smile : ${face.smilingProbability?.times(100)}%\n"+
                            "Left Eye open : ${face.leftEyeOpenProbability?.times(100)}%\n"+
                            "Right Eye open : ${face.rightEyeOpenProbability?.times(100)}%\n\n"

                    i++
                }
                if(faces.isEmpty()){
                    resultText = "No face detected"
                }else{
                    binding.tvFaceDetectResult.text = resultText
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Toast.makeText(this, "OOPS, something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showImageChooser(){
            var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
}