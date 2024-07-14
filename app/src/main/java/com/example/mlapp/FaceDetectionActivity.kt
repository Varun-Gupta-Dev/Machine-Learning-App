package com.example.mlapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mlapp.databinding.ActivityFaceDetectionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectionActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityFaceDetectionBinding
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
                startActivityForResult(intent, 183)
            }else{
                // something went wrong
                Toast.makeText(this, "OOPS, something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 183 && resultCode == RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as Bitmap
            detectFace(bitmap)
        }
    }

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
                            "Right Eye open : ${face.rightEyeOpenProbability?.times(100)}%\n"
                            "Face tracking Id : ${face.trackingId}\n\n"

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
}